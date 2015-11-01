/*
 * Copyright 2015 salesforce.com.
 * All Rights Reserved.
 * Company Confidential.
 */

package com.smenedi.nano;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * NetworkUtil to check the network connectivity
 *
 * @author smenedi
 */
public class NetworkUtil {
    /**
     * Checks if the app is connected to internet
     *
     * @param context
     *         to use to check for network connectivity.
     *
     * @return true if connected, false otherwise.
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Shows Snackbar with a "No Internet Connection" text.
     *
     * @param activity
     *         activity in which snackbar message is displayed
     */
    public static void showNoInternetMessage(Activity activity) {
        final View coordinatorLayoutView = activity.findViewById(R.id.snackbar);
        Snackbar.make(coordinatorLayoutView, activity.getResources().getString(R.string.internet_unavailable), Snackbar.LENGTH_SHORT).show();
    }
}
