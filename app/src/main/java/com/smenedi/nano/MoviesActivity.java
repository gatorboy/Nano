package com.smenedi.nano;

import com.smenedi.nano.data.MovieContract.MovieEntry;
import com.smenedi.nano.events.MovieItemClickEvent;

import de.greenrobot.event.EventBus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MoviesActivity extends AppCompatActivity {
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private final String LOG_TAG = MoviesActivity.class.getSimpleName();
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                                           .replace(R.id.movie_detail_container, new MovieDetailFragment(), DETAILFRAGMENT_TAG)
                                           .commit();
            }
        } else {
            mTwoPane = false;
            if(getSupportActionBar()!=null){
                getSupportActionBar().setElevation(0f);
            }
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
        if(mTwoPane) {
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
            startActivity(intent);
        }
    }
}
