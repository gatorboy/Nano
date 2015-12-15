package com.smenedi.nano;

import com.smenedi.nano.data.MovieContract.MovieEntry;
import com.smenedi.nano.events.MovieItemClickEvent;
import com.smenedi.nano.sync.MoviesSyncAdapter;

import de.greenrobot.event.EventBus;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MoviesActivity extends AppCompatActivity {
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private final String LOG_TAG = MoviesActivity.class.getSimpleName();
    Toolbar mToolbar;
    private boolean mTwoPane;
    private String mSortOrder;

    MenuItem mFavoriteMenu;

    FloatingActionButton mFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        mFavorite = (FloatingActionButton) findViewById(R.id.favorite);
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        // We are using the tool bar as a replacement for action, setup it up as such
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

//        setAnimations();

        mSortOrder = Utility.getSortOrder(this);
        if (findViewById(R.id.movie_detail_container) != null) {
            Log.d(LOG_TAG, "Set details in a two pane layout");
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                                           .replace(R.id.movie_detail_container, new MovieDetailFragment(), DETAILFRAGMENT_TAG)
                                           .commit();
            }
        } else {
            mTwoPane = false;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setElevation(0f);
            }
        }

        MoviesSyncAdapter.initializeSyncAdapter(this);

        if (!NetworkUtil.isConnected(this)) {
            NetworkUtil.showNoInternetMessage(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        String sortOrder = Utility.getSortOrder(this);
        if (sortOrder != null && !sortOrder.equals(mSortOrder)) {
            mSortOrder = sortOrder;
            Log.d(LOG_TAG, "Updated Sort Order in onResume");
            MoviesFragment ff = (MoviesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
            if (null != ff) {
                ff.onSortChanged();
            }
        }

        MoviesFragment moviesFragment = (MoviesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
        if (moviesFragment != null) {
            moviesFragment.queryProvider();
        }


    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.moviesactivity, menu);
        mFavoriteMenu = menu.findItem(R.id.show_favorites);
        boolean isFavorites = Utility.isFavorites(this);
        mFavoriteMenu.setIcon(getResources().getDrawable((isFavorites) ? R.drawable.favorite : R.drawable.nofavorite));
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.show_favorites) {

            MovieDetailFragment movieDetailFragment = (MovieDetailFragment) getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);
            if (movieDetailFragment != null) {
                getSupportFragmentManager().beginTransaction().remove(movieDetailFragment).commit();
            }
            MoviesFragment moviesFragment = (MoviesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
            boolean isFavorites = Utility.isFavorites(this);
            if (null != moviesFragment) {
                mFavoriteMenu.setIcon(getResources().getDrawable((!isFavorites) ? R.drawable.favorite : R.drawable.nofavorite));
                moviesFragment.onFavorites(Utility.setFavorites(this, !isFavorites));
            }


        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unused") // This is actually used via Event Bus
    public void onEventMainThread(MovieItemClickEvent event) {
        if (mTwoPane) {
            final Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.DETAIL_URI, MovieEntry.buildMovieDetailUri(event.movieId));

            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.movie_detail_container, movieDetailFragment, DETAILFRAGMENT_TAG)
                                       .commit();
        } else {
            final Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.setData(MovieEntry.buildMovieDetailUri(event.movieId));
            intent.putExtra(Movie.TITLE_FIELD_NAME, event.movieName);
            startActivity(intent);
        }
    }

    private void setAnimations() {
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.transitions));
        }
    }

    public void setFavoriteButton(boolean isFavorite) {
        mFavorite.setVisibility(View.VISIBLE);
        if(isFavorite) {
            mFavorite.setImageDrawable(getResources().getDrawable(R.drawable.favorite));
        } else {
            mFavorite.setImageDrawable(getResources().getDrawable(R.drawable.nofavorite));
        }
    }


    public void onFavorite(View view) {
        MovieDetailFragment movieDetailFragment = (MovieDetailFragment) getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);
        if(mFavorite!=null) {
            mFavorite.setImageDrawable(getResources().getDrawable((!movieDetailFragment.isFavorite) ? R.drawable.favorite : R.drawable.nofavorite ));
        }
        movieDetailFragment.onFavorite(!movieDetailFragment.isFavorite);
    }



}
