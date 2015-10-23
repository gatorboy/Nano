package com.smenedi.nano.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smenedi.nano.ApiRequests;
import com.smenedi.nano.Movie;
import com.smenedi.nano.data.MovieContract.MovieEntry;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MovieService extends IntentService {
    private static final String LOG_TAG = MovieService.class.getSimpleName();
    private static final String ACTION_UPDATE_MOVIES = "com.smenedi.nano.service.action.UPDATE_MOVIES";
    private static final String EXTRA_SORT_ORDER = "com.smenedi.nano.service.extra.SORT_ORDER";
    private static final String EXTRA_PAGE_NUMBER = "com.smenedi.nano.service.extra.PAGE_NUMBER";

    /**
     * Starts this service to perform action Update Movies with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateMovies(Context context, String sortOrder, int page) {
        Intent intent = new Intent(context, MovieService.class);
        intent.setAction(ACTION_UPDATE_MOVIES);
        intent.putExtra(EXTRA_SORT_ORDER, sortOrder);
        intent.putExtra(EXTRA_PAGE_NUMBER, page);
        context.startService(intent);
    }


    public MovieService() {
        super("MovieService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_MOVIES.equals(action)) {
                final String sortOrder = intent.getStringExtra(EXTRA_SORT_ORDER);
                final int page = intent.getIntExtra(EXTRA_PAGE_NUMBER, 1);
                handleActionUpdateMovies(sortOrder, page);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateMovies(String sortOrder, int page) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr;

        try {
//            for (int page = 1; page <= pages; page++) {

                URL url = ApiRequests.getMoviesUrl(sortOrder, page);
                Log.d(LOG_TAG, "Built URI: " + url.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return;
                }
                movieJsonStr = buffer.toString();
                getMovieDataFromJson(movieJsonStr);
//            }

        } catch (IOException | JSONException e) {
            Log.e(LOG_TAG, "Error " + e.getMessage());
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return;
    }

    private void getMovieDataFromJson(String movieJsonStr) throws JSONException {
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

        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
        }
        Log.d(LOG_TAG, "Movies fetching Complete. " + inserted + " Inserted");

 /*       // Sort order:  Ascending, by date.
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
        return convertContentValuesToUXFormat(cVVector);*/
    }

}
