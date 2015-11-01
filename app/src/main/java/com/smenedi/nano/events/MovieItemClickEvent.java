package com.smenedi.nano.events;

import android.view.View;

/**
 * Created by smenedi on 10/11/15.
 */
public class MovieItemClickEvent {
    public final long movieId;
    public final int position;
    public final String movieName;
    public final View mView;

    public MovieItemClickEvent(View v, long movieId, String movieName, int position) {
        this.movieId = movieId;
        this.movieName = movieName;
        this.position = position;
        mView = v;
    }
}
