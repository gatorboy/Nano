package com.smenedi.nano;

import com.smenedi.nano.data.MovieContract.MovieEntry;
import com.smenedi.nano.events.MovieItemClickEvent;
import com.smenedi.nano.sync.MoviesSyncAdapter;

import de.greenrobot.event.EventBus;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MoviesActivity extends AppCompatActivity {
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private final String LOG_TAG = MoviesActivity.class.getSimpleName();
    private boolean mTwoPane;
    private String mSortOrder;

    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        // We are using the tool bar as a replacement for action, setup it up as such
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        setAnimations();

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
//            View v = getFragmentManager().findFragmentById(R.id.fragment_movies).getfindViewById(R.id.poster);
//            Log.d(LOG_TAG, "view found :"+ (v==null));
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, event.mView, getString(R.string.poster_transition));
            final Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.setData(MovieEntry.buildMovieDetailUri(event.movieId));
            startActivity(intent, optionsCompat.toBundle());
        }
    }

    private void setAnimations() {
        if(VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.transitions));
        }
    }


}
