package com.smenedi.nano;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.drawee.view.SimpleDraweeView;
import com.smenedi.nano.data.MovieContract;
import com.smenedi.nano.data.MovieContract.MovieDetailEntry;
import com.smenedi.nano.data.MovieContract.MovieEntry;
import com.smenedi.nano.data.MovieProvider;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import butterknife.Bind;
import butterknife.ButterKnife;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final int MOVIE_DETAIL_LOADER_ID = R.id.movie_detail_loader_id;

    public static final String DETAIL_URI = "URI";
    //Columns
    static final int COLUMN_ORIGINAL_TITLE = 0;
    static final int COLUMN_POSTER_PATH = 1;
    static final int COLUMN_RELEASE_DATE = 2;
    static final int COLUMN_RATING = 3;
    static final int COLUMN_OVERVIEW = 4;
    static final int COLUMN_BACKDROP_PATH = 5;
    static final int COLUMN_FAVORITE = 6;
    static final int COLUMN_REVIEWS = 7;
    static final int COLUMN_TRAILERS = 8;

    //Projection
    private static final String[] MOVIE_DETAIL_PROJECTION = {
            MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_RATING,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_BACKDROP_PATH,
            MovieEntry.COLUMN_FAVORITE,
            MovieDetailEntry.COLUMN_REVIEWS,
            MovieDetailEntry.COLUMN_TRAILERS,
    };


    @Bind(R.id.poster)
    SimpleDraweeView mPoster;
    @Bind(R.id.release_year)
    TextView mYearOfRelease;
    @Bind(R.id.rating)
    TextView mRating;
    @Bind(R.id.overview)
    TextView mOverview;
    @Bind(R.id.trailersLayout)
    LinearLayout mTrailersLayout;
    @Bind(R.id.reviewsLayout)
    LinearLayout mReviewsLayout;
    @Bind(R.id.reviews_card)
    CardView mReviewsCard;
    @Bind(R.id.trailers_card)
    CardView mTrailersCard;

    private String mShareMovieDetails;
    private Uri mMovieDetailUri;

    private static final String DATE_FORMAT = "yyyy-mm-dd";
    public boolean isFavorite;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mMovieDetailUri = bundle.getParcelable(DETAIL_URI);
        }
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            startActivity(Intent.createChooser(createShareForecastIntent(), "Share to"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareMovieDetails);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mMovieDetailUri) {
            return new CursorLoader(getActivity(), mMovieDetailUri, MOVIE_DETAIL_PROJECTION, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            setViews(data);
        }
    }

    private void setViews(Cursor cursor) {
        mShareMovieDetails = "Movie: " + cursor.getString(COLUMN_ORIGINAL_TITLE) + " Overview: " + cursor.getString(COLUMN_OVERVIEW);
        mPoster.setImageURI(ApiRequests.getPosterUri(cursor.getString(COLUMN_POSTER_PATH)));
        mYearOfRelease.setText(String.valueOf(getYearOfRelease(cursor.getString(COLUMN_RELEASE_DATE))));
        mRating.setText(getActivity().getString(R.string.format_rating, cursor.getDouble(COLUMN_RATING)));
        final String overview = cursor.getString(COLUMN_OVERVIEW);
        if (!TextUtils.isEmpty(overview)) {
            mOverview.setText(overview);
        }

        //Set Fav button
        int favorite = cursor.getInt(COLUMN_FAVORITE);
        isFavorite = favorite == 1;
        ((MovieDetailActivity) getActivity()).setFavoriteButton(favorite == 1);
        //get trailers and reviews
        String trailers = cursor.getString(COLUMN_TRAILERS);
        String reviews = cursor.getString(COLUMN_REVIEWS);
        setTrailersAndReviews(trailers, reviews);

        if (getActivity().findViewById(R.id.backdrop) != null) {
            ((SimpleDraweeView) getActivity().findViewById(R.id.backdrop)).setImageURI(ApiRequests.getBackdropUri(cursor.getString(COLUMN_BACKDROP_PATH)));
        }
    }


    private void setTrailersAndReviews(String trailers, String reviews) {
        if (TextUtils.isEmpty(trailers) || TextUtils.isEmpty(reviews)) {
            if (NetworkUtil.isConnected(getActivity())) {
                try {
                    Call call = ApiRequests.getRequestClient(ApiRequests.getMovieDetailUrl(Long.valueOf(mMovieDetailUri.getLastPathSegment())));
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            //TODO: show error
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                Log.e(LOG_TAG, "Unexpected code " + response);
                            } else {
                                final String responseBody = response.body().string();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        saveMovieDetails(responseBody);
                                    }
                                });
                            }
                        }
                    });
                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, "Exception: " + e.getMessage());
                }
            } else {
                NetworkUtil.showNoInternetMessage(getActivity());
            }
        } else {
            try {
                setTrailersView(new JSONArray(trailers));
                setReviewsView(new JSONArray(reviews));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Exception: " + e.getMessage());
            }
        }

    }


    private void saveMovieDetails(String movieJsonStr) {
        ContentValues movieValues = new ContentValues();
        JSONObject moviesJSON;
        try {
            Log.d(LOG_TAG, movieJsonStr);
            moviesJSON = new JSONObject(movieJsonStr);
            //retrieve trailers
            final String TRAILERS = "trailers.youtube";
            final JSONArray trailers = JSONUtil.optArrayFromPath(moviesJSON, TRAILERS);
            if (trailers != null) {
                movieValues.put(MovieDetailEntry.COLUMN_TRAILERS, trailers.toString());
            }

            //retrieve reviews
            final String REVIEWS = "reviews.results";
            final JSONArray reviews = JSONUtil.optArrayFromPath(moviesJSON, REVIEWS);
            if (reviews != null) {
                movieValues.put(MovieDetailEntry.COLUMN_REVIEWS, reviews.toString());
            }

            if (getActivity() != null) {
                setTrailersView(trailers);
                setReviewsView(reviews);
            }
            //insert to db
            Movie movie = new Movie(moviesJSON);
            movieValues.put(MovieEntry.COLUMN_MOVIE_ID, movie.getId());

            if (getActivity() != null) {
                getActivity().getContentResolver().insert(mMovieDetailUri, movieValues);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing movie detail", e);
        }
    }

    private void setTrailersView(JSONArray trailers) {
        Log.d(LOG_TAG, "No. of trailers=" + trailers.length());
        if (trailers.length() == 0) {
            mTrailersCard.setVisibility(View.GONE);
        }
        for (int i = 0; i < trailers.length(); i++) {
            final JSONObject trailerJsonObject = trailers.optJSONObject(i);
            final Trailer trailer = new Trailer(getActivity(), trailerJsonObject);
            mTrailersLayout.addView(trailer.getView(mTrailersLayout));
        }
    }

    private void setReviewsView(JSONArray reviews) {
        Log.d(LOG_TAG, "No. of reviews=" + reviews.length());
        if (reviews.length() == 0) {
            mReviewsCard.setVisibility(View.GONE);
        }
        for (int i = 0; i < reviews.length(); i++) {
            final JSONObject reviewsJsonObject = reviews.optJSONObject(i);
            final Review review = new Review(getActivity(), reviewsJsonObject);
            mReviewsLayout.addView(review.getView(mReviewsLayout));
        }
    }


    private int getYearOfRelease(String dateString) {
        if (dateString == null || dateString.length() == 0) {
            return 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        Date date;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Invalid release date: " + dateString);
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onFavorite(boolean isFavorite) {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_FAVORITE, isFavorite ? 1 : 0);
        this.isFavorite = isFavorite;
        getActivity().getContentResolver()
                     .update(MovieContract.MovieEntry.buildMovieListUri(), movieValues, MovieProvider.sMovieSelection, new String[] { mMovieDetailUri.getLastPathSegment() });
    }


}
