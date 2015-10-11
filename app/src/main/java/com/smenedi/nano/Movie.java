package com.smenedi.nano;

import org.json.JSONObject;

import com.smenedi.nano.data.MovieContract.MovieEntry;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by smenedi on 9/12/15.
 */
public class Movie implements Parcelable {
    public static final String MOVIE_ID_FIELD_NAME = "id";
    public static final String BACKDROP_PATH_FIELD_NAME = "backdrop_path";
    public static final String GENRE_IDS_FIELD_NAME = "genre_ids";
    public static final String ORIGINAL_LANGUAGE_FIELD_NAME = "original_language";
    public static final String ORIGINAL_TITLE_FIELD_NAME = "original_title";
    public static final String OVERVIEW_FIELD_NAME = "overview";
    public static final String POPULARITY_FIELD_NAME = "popularity";
    public static final String POSTERPATH_FIELD_NAME = "poster_path";
    public static final String RELEASE_DATE_FIELD_NAME = "release_date";
    public static final String TITLE_FIELD_NAME = "title";
    public static final String VIDEO_FIELD_NAME = "video";
    public static final String VOTE_AVG_FIELD_NAME = "vote_average";
    public static final String VOTE_COUNT_FIELD_NAME = "vote_count";


    //Movie fields
    private final int mId;
    private final String mBackdropPath;
    private final String mOriginalTitle;
    private final String mOverview;
    private final double mPopularity;
    private final String mPosterPath;
    private final String mReleaseDate;
    private final String mTitle;
    private final double mRating;

    public Movie(JSONObject jsonObject) {
        mId = jsonObject.optInt(MOVIE_ID_FIELD_NAME);
        mBackdropPath = jsonObject.optString(BACKDROP_PATH_FIELD_NAME);
        mOriginalTitle = jsonObject.optString(ORIGINAL_TITLE_FIELD_NAME);
        mOverview = jsonObject.optString(OVERVIEW_FIELD_NAME);
        mPopularity = jsonObject.optDouble(POPULARITY_FIELD_NAME);
        mPosterPath = jsonObject.optString(POSTERPATH_FIELD_NAME);
        mReleaseDate = jsonObject.optString(RELEASE_DATE_FIELD_NAME);
        mTitle = jsonObject.optString(TITLE_FIELD_NAME);
        mRating = jsonObject.optDouble(VOTE_AVG_FIELD_NAME);
    }


    public Movie(ContentValues contentValues) {
        mId = contentValues.getAsInteger(MovieEntry.COLUMN_MOVIE_ID);
        mBackdropPath = contentValues.getAsString(MovieEntry.COLUMN_BACKDROP_PATH);
        mOriginalTitle = contentValues.getAsString(MovieEntry.COLUMN_ORIGINAL_TITLE);
        mOverview = contentValues.getAsString(MovieEntry.COLUMN_OVERVIEW);
        mPopularity = contentValues.getAsDouble(MovieEntry.COLUMN_POPULARITY);
        mPosterPath = contentValues.getAsString(MovieEntry.COLUMN_POSTER_PATH);
        mReleaseDate = contentValues.getAsString(MovieEntry.COLUMN_RELEASE_DATE);
        mTitle = contentValues.getAsString(MovieEntry.COLUMN_TITLE);
        mRating = contentValues.getAsDouble(MovieEntry.COLUMN_RATING);
    }

    public Movie(Parcel in) {
        mId = in.readInt();
        mBackdropPath = in.readString();
        mOriginalTitle = in.readString();
        mOverview = in.readString();
        mPopularity = in.readDouble();
        mPosterPath = in.readString();
        mReleaseDate = in.readString();
        mTitle = in.readString();
        mRating = in.readDouble();
    }

    public int getId() {
        return mId;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public double getPopularity() {
        return mPopularity;
    }

    public static Uri getPosterPathUri(String posterPath) {
        return ApiRequests.getPosterUri(posterPath);
    }

    public String getPosterPath() {
        return mPosterPath;
    }
    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public double getRating() {
        return mRating;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mBackdropPath);
        dest.writeString(mOriginalTitle);
        dest.writeString(mOverview);
        dest.writeDouble(mPopularity);
        dest.writeString(mPosterPath);
        dest.writeString(mReleaseDate);
        dest.writeString(mTitle);
    }

    public final Parcelable.Creator CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
