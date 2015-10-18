package com.smenedi.nano;

import com.smenedi.nano.data.MovieContract.MovieEntry;
import com.smenedi.nano.service.MovieService;

import butterknife.Bind;
import butterknife.ButterKnife;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment implements LoaderCallbacks<Cursor> {


    private static final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = R.id.movie_loader_id;

    //Projection
    private static final String[] MOVIE_LIST_PROJECTION = {
            MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_ORIGINAL_TITLE
    };

    static final int COLUMN_ID = 0;
    static final int COLUMN_POSTER_PATH = 1;
    static final int COLUMN_ORIGINAL_TITLE = 2;


    private static final String SORT_ORDER_FORMAT = "%s.desc";
    private static final String SELECTED_KEY = "selected_position";

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    MoviesAdapter mMoviesAdapter;

    private int mSelectedPosition;

    public MoviesFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviesfragment, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
//        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getActivity());
//        fetchMoviesTask.execute(getSortOrder());
        MovieService.startActionUpdateMovies(getActivity(), getSortOrder());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.bind(this, layout);
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mSelectedPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        setUpRecylerView();
        return layout;
    }

    private void setUpRecylerView() {

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

//        Uri movieListUri = MovieEntry.buildMovieListUri();
//        Cursor cur = getActivity().getContentResolver().query(movieListUri, null, null, null, getSqlSortOrder());
//        Vector<ContentValues> cVVector = new Vector<>(cur.getCount());
//        if (cur.moveToFirst()) {
//            do {
//                ContentValues cv = new ContentValues();
//                DatabaseUtils.cursorRowToContentValues(cur, cv);
//                cVVector.add(cv);
//            } while (cur.moveToNext());
//        }
//        mMovieList = convertContentValuesToUXFormat(cVVector);
        mMoviesAdapter = new MoviesAdapter(getContext());
        mRecyclerView.setAdapter(mMoviesAdapter);
    }


    private String getSortOrder() {
        return String.format(SORT_ORDER_FORMAT, PreferenceManager.getDefaultSharedPreferences(getContext())
                                                                 .getString(getString(R.string.key_pref_sort_order), getString(R.string.value_pref_sort_order_default)));
    }

    private String getSqlSortOrder() {
        if (getSortOrder().equals("popularity.desc")) {
            return MovieEntry.COLUMN_POPULARITY + " DESC";
        } else {
            return MovieEntry.COLUMN_RATING + " DESC";
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieListUri = MovieEntry.buildMovieListUri();
        return new CursorLoader(getActivity(), movieListUri, MOVIE_LIST_PROJECTION, null, null, getSqlSortOrder());
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesAdapter.swapCursor(data);
        //scroll to last scrolled position
        if (mSelectedPosition != RecyclerView.NO_POSITION) {
            mRecyclerView.smoothScrollToPosition(mSelectedPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        int position = (mMoviesAdapter != null) ? mMoviesAdapter.getSelectedPosition() : RecyclerView.NO_POSITION;
        //Save scrolling position
        if (position != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY, position);
        }
        super.onSaveInstanceState(outState);
    }
}
