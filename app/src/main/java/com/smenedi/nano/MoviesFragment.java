package com.smenedi.nano;

import com.smenedi.nano.data.MovieContract;
import com.smenedi.nano.data.MovieContract.MovieEntry;
import com.smenedi.nano.service.MovieService;
import com.smenedi.nano.sync.MoviesSyncAdapter;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment implements LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = R.id.movie_loader_id;

    static final int COLUMN_ID = 0;
    static final int COLUMN_POSTER_PATH = 1;
    static final int COLUMN_ORIGINAL_TITLE = 2;

    //Projection
    private static final String[] MOVIE_LIST_PROJECTION = {
            MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_ORIGINAL_TITLE
    };
    private static final String SELECTED_KEY = "selected_position";
    private static final int THRESHOLD_ITEM_COUNT = 5;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    MoviesAdapter mMoviesAdapter;
    private int mSelectedPosition;
    private boolean isListLoading = true;
    private int lastLoadedItemCount = 0;

    public MoviesFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setHasOptionsMenu(true);
        updateMovies();
    }

    void onSortChanged() {
        Log.d(LOG_TAG, "Sort Changed. Update Movies");
        //Clear DB
        getActivity().getContentResolver().delete(MovieContract.MovieEntry.buildMovieListUri(), null, null);
        //set page to 1
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt(getString(R.string.key_pref_page_number), 1).apply();
        MovieService.startActionUpdateMovies(getActivity(), Utility.getSortOrder(getActivity()), 1);
        lastLoadedItemCount = 0;
//        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    private void updateMovies() {
//        MovieService.startActionUpdateMovies(getActivity(), getSortOrder());
        MoviesSyncAdapter.syncImmediately(getActivity());
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
        /*if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mSelectedPosition = savedInstanceState.getInt(SELECTED_KEY);
        }*/
        setUpRecylerView();
        return layout;
    }

    private void setUpRecylerView() {
        Log.d(LOG_TAG, "setUpRecylerView");
        mRecyclerView.setHasFixedSize(true);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.grid_width));
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mMoviesAdapter = new MoviesAdapter(getContext());
        mRecyclerView.setAdapter(mMoviesAdapter);
        /*mRecyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final int totalItemCount = gridLayoutManager.getItemCount();
                final int lastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition();

                if (isListLoading) {
                    if (totalItemCount > lastLoadedItemCount) {
                        isListLoading = false;
                        lastLoadedItemCount = totalItemCount;
                    }
                } else if (lastVisibleItemPosition >= totalItemCount - THRESHOLD_ITEM_COUNT) {
                    isListLoading = true;
                    int lastLoadedPageNumber = Utility.getPageNumber(getActivity());
                    loadMorePages(++lastLoadedPageNumber);
                }
            }
        });*/
    }

    private void loadMorePages(int page) {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt(getString(R.string.key_pref_page_number), page).apply();
        MovieService.startActionUpdateMovies(getActivity(), Utility.getSortOrder(getActivity()), page);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieListUri = MovieEntry.buildMovieListUri();
        String selection;
        switch (id) {
        case 1:
            selection = MovieEntry.COLUMN_FAVORITE + " = '1'";
            break;
        default:
            selection = null;
            break;
        }
        return new CursorLoader(getActivity(), movieListUri, MOVIE_LIST_PROJECTION, selection, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, "onLoadFinished");
        if (data != null) {
            mMoviesAdapter.swapCursor(mMoviesAdapter.getItemCount(), data);
        }
        /*//scroll to last scrolled position
        if(mRecyclerView!= null) {
//            mRecyclerView.scrollToPosition(currentPosition);
        }
        if (mSelectedPosition != RecyclerView.NO_POSITION) {
//            mRecyclerView.smoothScrollToPosition(mSelectedPosition);
        }*/
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(MoviesAdapter.RESET_COUNT, null);
    }

    public void onFavorites(boolean isFavorites) {
        if (getLoaderManager() != null) {
            getLoaderManager().restartLoader(isFavorites ? 1 : 0, null, this);
        }
    }

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        int position = (mMoviesAdapter != null) ? mMoviesAdapter.getSelectedPosition() : RecyclerView.NO_POSITION;
        //Save scrolling position
        if (position != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY, position);
        }
        super.onSaveInstanceState(outState);
    }*/
}
