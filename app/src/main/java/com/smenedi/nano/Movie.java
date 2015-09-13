package com.smenedi.nano;

import org.json.JSONObject;

import android.net.Uri;

/**
 * Created by smenedi on 9/12/15.
 */
public class Movie {
    public static final String ID_FIELD_NAME = "id";
    public static final String ADULT_FIELD_NAME = "adult";
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
    private final boolean mIsAdultMovie;
    private final String mBackdropPath;
    private final String mOriginalTitle;
    private final String mOverview;
    private final double mPopularity;
    private final String mPosterPath;
    private final String mReleaseDate;
    private final String mTitle;

    public Movie(JSONObject jsonObject) {
        mId = jsonObject.optInt(ID_FIELD_NAME);
        mIsAdultMovie = jsonObject.optBoolean(ADULT_FIELD_NAME);
        mBackdropPath = jsonObject.optString(BACKDROP_PATH_FIELD_NAME);
        mOriginalTitle = jsonObject.optString(ORIGINAL_TITLE_FIELD_NAME);
        mOverview = jsonObject.optString(OVERVIEW_FIELD_NAME);
        mPopularity = jsonObject.optDouble(POPULARITY_FIELD_NAME);
        mPosterPath = jsonObject.optString(POSTERPATH_FIELD_NAME);
        mReleaseDate = jsonObject.optString(RELEASE_DATE_FIELD_NAME);
        mTitle = jsonObject.optString(TITLE_FIELD_NAME);
    }

    public int getId() {
        return mId;
    }

    public boolean isAdultMovie() {
        return mIsAdultMovie;
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

    public Uri getPosterPath() {
        return ApiRequests.getPosterUri(mPosterPath);
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getTitle() {
        return mTitle;
    }
}
