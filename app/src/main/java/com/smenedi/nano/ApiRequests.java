package com.smenedi.nano;

import java.net.MalformedURLException;
import java.net.URL;

import android.net.Uri;

/**
 * Created by smenedi on 9/12/15.
 */
public class ApiRequests {
    // Movies Rest API constants
    private static final String MOVIES_API_PATH = "3/discover/movie";

    private static final String IMAGES_API_PATH = "t/p/w185";

    private static final String SORT_BY_QUERY_PARAMETER = "sort_by";
    public static final String SORT_BY_POPULARITY = "popularity.desc";
    public static final String SORT_BY_RATING = "vote_average.desc";
    private static final String API_KEY_QUERY_PARAMETER = "api_key";


    static Uri getPosterUri(String imagePath) {
        final Uri.Builder builder = new Uri.Builder().scheme(BuildConfig.IMAGE_HOST_SCHEME)
                                                     .authority(BuildConfig.IMAGE_HOST)
                                                     .appendEncodedPath(IMAGES_API_PATH)
                                                     .appendEncodedPath(imagePath);
        return builder.build();
    }

    static URL getMoviesUrl(String sortBy) throws MalformedURLException {
        final Uri.Builder builder = new Uri.Builder().scheme(BuildConfig.SERVICE_HOST_SCHEME)
                                                     .authority(BuildConfig.SERVICE_HOST)
                                                     .appendEncodedPath(MOVIES_API_PATH)
                                                     .appendQueryParameter(SORT_BY_QUERY_PARAMETER, sortBy)
                                                     .appendQueryParameter(API_KEY_QUERY_PARAMETER, BuildConfig.API_KEY);
        return new URL(builder.build().toString());
    }

}
