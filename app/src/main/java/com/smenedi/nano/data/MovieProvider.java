package com.smenedi.nano.data;

import com.smenedi.nano.data.MovieContract.MovieDetailEntry;
import com.smenedi.nano.data.MovieContract.MovieEntry;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by smenedi on 10/7/15.
 */
public class MovieProvider extends ContentProvider {

    static final int MOVIE_LIST = 1;
    static final int MOVIE_DETAIL = 2;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    public static final String sMovieSelection = MovieContract.MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_MOVIE_ID + " = ?";
    private static final SQLiteQueryBuilder sQueryBuilder;

    static {
        sQueryBuilder = new SQLiteQueryBuilder();
        sQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);

        sQueryBuilder.setTables(
                MovieEntry.TABLE_NAME + " LEFT JOIN " +
                MovieDetailEntry.TABLE_NAME +
                " ON " + MovieEntry.TABLE_NAME +
                "." + MovieEntry.COLUMN_MOVIE_ID +
                " = " + MovieDetailEntry.TABLE_NAME +
                "." + MovieDetailEntry.COLUMN_MOVIE_ID);

    }

    private MovieDbHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE_LIST);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_DETAIL);
        return matcher;
    }

    private Cursor queryMovieList(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return mOpenHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
        case MOVIE_LIST:
            retCursor = queryMovieList(uri, projection, selection, selectionArgs, sortOrder);
            break;
        case MOVIE_DETAIL:
            retCursor = queryMovieDetail(uri, projection);
            break;

        default:
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }

    private Cursor queryMovieDetail(Uri uri, String[] projection) {
//        return sQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, sMovieSelection, new String[] { uri.getLastPathSegment() }, null, null, sortOrder);
//        return mOpenHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME, projection, sMovieSelection, new String[] { uri.getLastPathSegment() }, null, null, sortOrder);
        return sQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, sMovieSelection, new String[] { uri.getLastPathSegment() }, null, null, null);
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
        case MOVIE_LIST:
            return MovieEntry.CONTENT_TYPE;
        case MOVIE_DETAIL:
            return MovieEntry.CONTENT_ITEM_TYPE;
        default:
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
        case MOVIE_LIST: {
            long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
            if (_id > 0) {
                returnUri = MovieContract.MovieEntry.buildMovieListUri();
            } else {
                throw new android.database.SQLException("Failed to insert row into " + uri);
            }
            break;
        }
        case MOVIE_DETAIL: {
            long _id = db.insert(MovieContract.MovieDetailEntry.TABLE_NAME, null, values);
            returnUri = MovieContract.MovieEntry.buildMovieDetailUri(_id);
            break;
        }
        default:
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
//        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) {
            selection = "1";
        }
        switch (match) {
        case MOVIE_LIST:
            rowsDeleted = db.delete(
                    MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
            break;
        case MOVIE_DETAIL:
            rowsDeleted = db.delete(
                    MovieContract.MovieDetailEntry.TABLE_NAME, selection, selectionArgs);
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
//            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
        case MOVIE_LIST:
            rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
            break;
        case MOVIE_DETAIL:
            rowsUpdated = db.update(MovieContract.MovieDetailEntry.TABLE_NAME, values, selection, selectionArgs);
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
//            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
        case MOVIE_LIST:
            db.beginTransaction();
            int returnCount = 0;
            try {
                for (ContentValues value : values) {
                    long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                    if (_id != -1) {
                        returnCount++;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return returnCount;
        default:
            return super.bulkInsert(uri, values);
        }
    }

}
