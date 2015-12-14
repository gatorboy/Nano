package com.smenedi.nano.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by smenedi on 10/4/15.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.smenedi.nano";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movieList";
    public static final String PATH_MOVIE_DETAIL = "movieDetail";

    /* Inner class that defines the table contents of the Movie table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_OVERVIEW = "overview";

        //popularity and rating
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_RATING = "vote_average";

        //titles
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_TITLE = "title";

        //image paths
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_POSTER_PATH = "poster_path";

        //favorite
        public static final String COLUMN_FAVORITE = "favorite";


        public static Uri buildMovieDetailUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieListUri() {
            return CONTENT_URI;
        }

        public static Uri buildFromBaseUri(final String path) {
            Uri.Builder uriBuilder = BASE_CONTENT_URI.buildUpon();
            if (path != null && path.length() != 0) {
                uriBuilder.path(path);
            }
            return uriBuilder.build();
        }

    }

    /* Inner class that defines the table contents of the movie detail table */
    public static final class MovieDetailEntry implements BaseColumns {

/*
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_DETAIL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_DETAIL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_DETAIL;
*/

        // Table name
        public static final String TABLE_NAME = "moviedetail";

        //foreign key to the movie id in the movie table
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_TRAILERS = "trailers";
        public static final String COLUMN_REVIEWS = "reviews";

    }
}
