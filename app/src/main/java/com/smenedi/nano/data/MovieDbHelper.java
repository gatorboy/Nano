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
                                              // Why AutoIncrement here, and not above?
                                              // Unique keys will be auto-generated in either case.  But for weather
                                              // forecasting, it's reasonable to assume the user will want information
                                              // for a certain date and all dates *following*, so the forecast data
                                              // should be sorted accordingly.
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

                                              // To assure the application have just one weather entry per day
                                              // per location, it's created a UNIQUE constraint with REPLACE strategy
                                              " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
