package com.smenedi.nano;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smenedi.nano.data.MovieContract.MovieEntry;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by smenedi on 10/7/15.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private List<Movie> mMovieList;
    private Context mContext;

    public FetchMoviesTask(Context context, List<Movie> movieList, RecyclerView recyclerView) {
        mContext = context;
        mMovieList = movieList;
        mRecyclerView = recyclerView;
    }

    @Override
    protected List<Movie> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        try {
            URL url = ApiRequests.getMoviesUrl(params[0]);
            Log.d(LOG_TAG, "Built URI: " + url.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("FetchMoviesTask", "Error " + e.getMessage());
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("FetchMoviesTask", "Error closing stream", e);
                }
            }
        }
        try {
            return getMovieDataFromJson(movieJsonStr, params[0]);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Movie> results) {
        mMovieList.clear();
        if (results != null && results.size() != 0) {
            mMovieList.addAll(results);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private List<Movie> getMovieDataFromJson(String movieJsonStr, String sortOrder) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        JSONObject moviesJSON = new JSONObject(movieJsonStr);
        JSONArray moviesArray = moviesJSON.getJSONArray(RESULTS);
        final int len = moviesArray.length();
        Vector<ContentValues> cVVector = new Vector<>(moviesArray.length());

        for (int i = 0; i < len; i++) {
            final Movie movie = new Movie(moviesArray.getJSONObject(i));
            //insert to db
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            movieValues.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            movieValues.put(MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
            movieValues.put(MovieEntry.COLUMN_RATING, movie.getRating());
            movieValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
            movieValues.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
            movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
            movieValues.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());

            cVVector.add(movieValues);
        }


        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
        }

        // Sort order:  Ascending, by date.
        String movieSortOrder;
        if(sortOrder.equals("popularity.desc")) {
            movieSortOrder = MovieEntry.COLUMN_POPULARITY + " DESC";
        } else {
            movieSortOrder = MovieEntry.COLUMN_RATING + " DESC";
        }
        Uri movieListUri = MovieEntry.buildMovieListUri();
        Cursor cur = mContext.getContentResolver().query(movieListUri, null, null, null, movieSortOrder);
        cVVector = new Vector<>(cur.getCount());
        if ( cur.moveToFirst() ) {
            do {
                ContentValues cv = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cur, cv);
                cVVector.add(cv);
            } while (cur.moveToNext());
        }
        return convertContentValuesToUXFormat(cVVector);
    }

    List<Movie> convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
        final List<Movie> movies = new ArrayList<>();
        // return strings to keep UI functional for now
        for ( int i = 0; i < cvv.size(); i++ ) {
            ContentValues movieValues = cvv.elementAt(i);
            movies.add(new Movie(movieValues));
        }
        return movies;
    }

    long addMovie(int movieId, String releaseDate, String overView, double popularity, double rating, String originalTitle, String title, String backdropPath, String posterPath) {
        long movieEntryId;
        Cursor movieCursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI, new String[]{ MovieEntry._ID}, MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[movieId], null);
        if(movieCursor.moveToFirst()) {
            movieEntryId = movieCursor.getLong(movieCursor.getColumnIndex(MovieEntry._ID));
        } else {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieEntry.COLUMN_MOVIE_ID, movieId);
            movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(MovieEntry.COLUMN_OVERVIEW, overView);
            movieValues.put(MovieEntry.COLUMN_POPULARITY, popularity);
            movieValues.put(MovieEntry.COLUMN_RATING, rating);
            movieValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
            movieValues.put(MovieEntry.COLUMN_TITLE, title);
            movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, backdropPath);
            movieValues.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);

            Uri insertedUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, movieValues);
            movieEntryId = ContentUris.parseId(insertedUri);

        }
        movieCursor.close();
        return movieEntryId;
    }
}
