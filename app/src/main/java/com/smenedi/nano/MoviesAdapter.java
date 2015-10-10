package com.smenedi.nano;

import java.util.List;

import com.facebook.drawee.view.SimpleDraweeView;
import com.smenedi.nano.MoviesAdapter.MovieViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by smenedi on 9/12/15.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder> {
    public static final String KEY_MOVIE = "com.smenedi.nano.movie";

    Context mContext;
    private List<Movie> mMovies;

    public MoviesAdapter(Context context, List<Movie> movies) {
        mContext = context;
        mMovies = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new MovieViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_movies, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(MovieViewHolder viewHolder, int position) {
        final Movie movie = mMovies.get(position);
        viewHolder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.poster)
        SimpleDraweeView mPoster;

        public MovieViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        public void bind(Movie movie) {
            mPoster.setImageURI(movie.getPosterPathUri());
        }

        @Override
        public void onClick(View v) {
            final Intent intent = new Intent(mContext, MovieDetailActivity.class);
//            intent.putExtra()
            mContext.startActivity(intent);
        }
    }

}
