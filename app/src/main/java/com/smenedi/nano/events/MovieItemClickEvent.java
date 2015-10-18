package com.smenedi.nano.events;

/**
 * Created by smenedi on 10/11/15.
 */
public class MovieItemClickEvent {
    public final long movieId;
    public final int position;

    public MovieItemClickEvent(long movieId, int position) {
        this.movieId = movieId;
        this.position = position;
    }
}
