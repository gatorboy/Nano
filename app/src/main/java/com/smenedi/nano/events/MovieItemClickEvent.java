package com.smenedi.nano.events;

import android.view.View;

/**
 * Created by smenedi on 10/11/15.
 */
public class MovieItemClickEvent {
    public final long movieId;
    public final int position;
    public final View mView;

    public MovieItemClickEvent(View v, long movieId, int position) {
        this.movieId = movieId;
        this.position = position;
        mView = v;
    }
}
