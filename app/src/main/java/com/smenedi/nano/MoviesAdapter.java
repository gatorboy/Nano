package com.smenedi.nano;

import com.facebook.drawee.view.SimpleDraweeView;
import com.smenedi.nano.MoviesAdapter.MovieViewHolder;
import com.smenedi.nano.events.MovieItemClickEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by smenedi on 9/12/15.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder> {
    public static final String KEY_MOVIE = "com.smenedi.nano.movie";
    private static final String LOG_TAG = MoviesAdapter.class.getSimpleName();

    Context mContext;
    private Cursor mCursor;
    private int mSelectedPosition = RecyclerView.NO_POSITION;

    public MoviesAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_movies, viewGroup, false);
        view.setFocusable(true);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);
        viewHolder.bind(mCursor, position);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) {
            return 0;
        }
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }
    public Cursor getCursor() {
        return mCursor;
    }

    Movie convertContentValuesToUXFormat(Cursor cursor) {
        ContentValues cv = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, cv);
        return new Movie(cv);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.poster)
        SimpleDraweeView mPoster;
        Long movieId;
        int position;

        public MovieViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
        }

        public void bind(Cursor cursor, int pos) {
//            Movie movie = convertContentValuesToUXFormat(cursor);
//            mPoster.setImageURI(movie.getPosterPathUri());
            final Uri posterPath = Movie.getPosterPathUri(cursor.getString(MoviesFragment.COLUMN_POSTER_PATH));
            mPoster.setImageURI(posterPath);
            mPoster.setContentDescription(cursor.getString(MoviesFragment.COLUMN_ORIGINAL_TITLE));
            movieId = cursor.getLong(MoviesFragment.COLUMN_ID);
            position = pos;
            Log.d("MovieAdapter", posterPath.toString());
        }

        @Override
        public void onClick(View v) {
            EventBus.getDefault().post(new MovieItemClickEvent(movieId, position));
            mSelectedPosition = position;
            /*final Intent intent = new Intent(mContext, MovieDetailActivity.class);
            intent.setData(MovieEntry.buildMovieDetailUri(movieId));
            mContext.startActivity(intent);*/
        }
    }



}
