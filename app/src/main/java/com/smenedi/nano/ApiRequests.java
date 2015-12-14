package com.smenedi.nano;

import java.net.MalformedURLException;
import java.net.URL;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import android.net.Uri;

/**
 * Created by smenedi on 9/12/15.
 */
public class ApiRequests {
    // Movies Rest API constants
    private static final String MOVIES_API_PATH = "3/discover/movie";
    private static final String MOVIES_DETAIL_API_PATH = "3/movie";

    private static final String IMAGES_API_PATH = "t/p";
    private static final String IMAGES_WIDTH_185 = "w185";
    private static final String IMAGES_WIDTH_500 = "w500";
    private static final String TRAILERS_AND_REVIEWS = "trailers,reviews";

    private static final String SORT_BY_QUERY_PARAMETER = "sort_by";
    private static final String API_KEY_QUERY_PARAMETER = "api_key";
    private static final String PAGE_KEY_QUERY_PARAMETER = "page";
    private static final String APPEND_TO_QUERY_PARAMETER = "append_to_response";


    public static Uri getBackdropUri(String imagePath) {
        return getImageUri(imagePath, IMAGES_WIDTH_500);

    }

    public static Uri getPosterUri(String imagePath) {
        return getImageUri(imagePath, IMAGES_WIDTH_185);
    }

    public static Uri getImageUri(String imagePath, String widthPath) {
        final Uri.Builder builder = new Uri.Builder().scheme(BuildConfig.IMAGE_HOST_SCHEME)
                                                     .authority(BuildConfig.IMAGE_HOST)
                                                     .appendEncodedPath(IMAGES_API_PATH)
                                                     .appendEncodedPath(widthPath)
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

    public static String getMovieDetailUrl(long movieId) throws MalformedURLException {
        final Uri.Builder builder = new Uri.Builder().scheme(BuildConfig.SERVICE_HOST_SCHEME)
                                                     .authority(BuildConfig.SERVICE_HOST)
                                                     .appendEncodedPath(MOVIES_DETAIL_API_PATH)
                                                     .appendEncodedPath(String.valueOf(movieId))
                                                     .appendQueryParameter(API_KEY_QUERY_PARAMETER, BuildConfig.API_KEY)
//                                                     .appendQueryParameter(PAGE_KEY_QUERY_PARAMETER, String.valueOf(page))
                                                     .appendQueryParameter(APPEND_TO_QUERY_PARAMETER, TRAILERS_AND_REVIEWS);

        return builder.build().toString();
    }

    public static Call getRequestClient(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        return client.newCall(request);
    }

}
