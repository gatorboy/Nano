package com.smenedi.nano.data;

import com.smenedi.nano.data.MovieContract.MovieEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by smenedi on 10/4/15.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "movies.db";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                                              MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                                              // the ID of the location entry associated with this weather data
                                              MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                                              MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                                              MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +

                                              MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL," +
                                              MovieEntry.COLUMN_RATING + " REAL NOT NULL," +

                                              MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                                              MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +

                                              MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                                              MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +

                                              " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT IGNORE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
