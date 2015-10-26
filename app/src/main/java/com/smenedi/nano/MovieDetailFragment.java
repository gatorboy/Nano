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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
            MovieEntry.COLUMN_OVERVIEW
    };

    static final int COLUMN_ORIGINAL_TITLE = 0;
    static final int COLUMN_POSTER_PATH = 1;
    static final int COLUMN_RELEASE_DATE = 2;
    static final int COLUMN_RATING = 3;
    static final int COLUMN_OVERVIEW = 4;
    private static final String DATE_FORMAT = "yyyy-mm-dd";
    public static final String DETAIL_URI = "URI";

    private String mShareMovieDetails;
    private Uri mMovieDetailUri;
//    private ShareActionProvider mShareActionProvider;

    //views
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.poster)
    SimpleDraweeView mPoster;
    @Bind(R.id.release_year)
    TextView mYearOfRelease;
    @Bind(R.id.duration)
    TextView mDuration;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.moviedetailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        /*// Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }*/
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

            /*// If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }*/
        }

    }

    private void setViews(Cursor cursor) {
        mShareMovieDetails = cursor.getString(COLUMN_POSTER_PATH);
        mTitle.setText(cursor.getString(COLUMN_ORIGINAL_TITLE));
        Log.d(LOG_TAG, "" + ApiRequests.getPosterUri(cursor.getString(COLUMN_POSTER_PATH)));
        mPoster.setImageURI(ApiRequests.getPosterUri(cursor.getString(COLUMN_POSTER_PATH)));
        mYearOfRelease.setText(String.valueOf(getYearOfRelease(cursor.getString(COLUMN_RELEASE_DATE))));
        mRating.setText(getActivity().getString(R.string.format_rating, cursor.getDouble(COLUMN_RATING)));
//        mOverview.setText(cursor.getString(COLUMN_OVERVIEW));
        mOverview.setText("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.");
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
