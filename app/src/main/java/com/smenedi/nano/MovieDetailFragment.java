package com.smenedi.nano;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.facebook.drawee.view.SimpleDraweeView;
import com.smenedi.nano.data.MovieContract.MovieEntry;

import butterknife.Bind;
import butterknife.ButterKnife;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final int MOVIE_DETAIL_LOADER_ID = R.id.movie_detail_loader_id;

    //Projection
    private static final String[] MOVIE_DETAIL_PROJECTION = {
            MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_RATING,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_BACKDROP_PATH
    };

    static final int COLUMN_ORIGINAL_TITLE = 0;
    static final int COLUMN_POSTER_PATH = 1;
    static final int COLUMN_RELEASE_DATE = 2;
    static final int COLUMN_RATING = 3;
    static final int COLUMN_OVERVIEW = 4;
    static final int COLUMN_BACKDROP_PATH = 5;

    private static final String DATE_FORMAT = "yyyy-mm-dd";
    public static final String DETAIL_URI = "URI";

    private String mShareMovieDetails;
    private Uri mMovieDetailUri;

    @Bind(R.id.poster)
    SimpleDraweeView mPoster;
    @Bind(R.id.release_year)
    TextView mYearOfRelease;
    @Bind(R.id.rating)
    TextView mRating;
    @Bind(R.id.overview)
    TextView mOverview;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(bundle!=null){
            mMovieDetailUri = bundle.getParcelable(DETAIL_URI);
        }
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_share) {
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
        if(data.moveToFirst()){
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

        if (getActivity().findViewById(R.id.backdrop) != null) {
            ((SimpleDraweeView) getActivity().findViewById(R.id.backdrop)).setImageURI(ApiRequests.getBackdropUri(cursor.getString(COLUMN_BACKDROP_PATH)));
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

}
