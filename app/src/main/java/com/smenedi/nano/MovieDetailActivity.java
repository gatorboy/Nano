package com.smenedi.nano;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;

public class MovieDetailActivity extends AppCompatActivity {

    Toolbar mToolbar;

    @Bind(R.id.favorite)
    FloatingActionButton mFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
//        setAnimations();

        if (savedInstanceState == null) {
            //if the details are displayed in a separate activity
            final Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.DETAIL_URI, getIntent().getData());

            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                                       .add(R.id.movie_detail_container, movieDetailFragment)
                                       .commit();
        }

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        // We are using the tool bar as a replacement for action, setup it up as such
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setElevation(0f);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getIntent().getStringExtra(Movie.TITLE_FIELD_NAME));
    }

    public void setFavoriteButton(boolean isFavorite) {
        if(isFavorite) {
            mFavorite.setImageDrawable(getResources().getDrawable(R.drawable.favorite));
        } else {
            mFavorite.setImageDrawable(getResources().getDrawable(R.drawable.nofavorite));
        }
    }

    private void setAnimations() {
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.transitions));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.moviesdetailactivity, menu);
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
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.favorite)
    public void onFavorite() {
        MovieDetailFragment movieDetailFragment = (MovieDetailFragment) getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);
        if(mFavorite!=null) {
            mFavorite.setImageDrawable(getResources().getDrawable((!movieDetailFragment.isFavorite) ? R.drawable.favorite : R.drawable.nofavorite ));
        }
        movieDetailFragment.onFavorite(!movieDetailFragment.isFavorite);
    }
}
