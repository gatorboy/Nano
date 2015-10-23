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
    private static final String API_KEY_QUERY_PARAMETER = "api_key";
    private static final String PAGE_KEY_QUERY_PARAMETER = "page";


    public static Uri getPosterUri(String imagePath) {
        final Uri.Builder builder = new Uri.Builder().scheme(BuildConfig.IMAGE_HOST_SCHEME)
                                                     .authority(BuildConfig.IMAGE_HOST)
                                                     .appendEncodedPath(IMAGES_API_PATH)
                                                     .appendEncodedPath(imagePath);
        return builder.build();
    }

    public static URL getMoviesUrl(String sortBy, int page) throws MalformedURLException {
        final Uri.Builder builder = new Uri.Builder().scheme(BuildConfig.SERVICE_HOST_SCHEME)
                                                     .authority(BuildConfig.SERVICE_HOST)
                                                     .appendEncodedPath(MOVIES_API_PATH)
                                                     .appendQueryParameter(SORT_BY_QUERY_PARAMETER, sortBy)
                                                     .appendQueryParameter(PAGE_KEY_QUERY_PARAMETER, String.valueOf(page))
                                                     .appendQueryParameter(API_KEY_QUERY_PARAMETER, BuildConfig.API_KEY);
        return new URL(builder.build().toString());
    }

}
