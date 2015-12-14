package com.smenedi.nano;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by smenedi on 10/18/15.
 */
public class Utility {
    private static final String SORT_ORDER_FORMAT = "%s.desc";

    public static String getSortOrder(Context context) {
        return String.format(SORT_ORDER_FORMAT, PreferenceManager.getDefaultSharedPreferences(context)
                                                                 .getString(context.getString(R.string.key_pref_sort_order),
                                                                            context.getString(R.string.value_pref_sort_order_default)));
    }

    public static int getPageNumber(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                                .getInt(context.getString(R.string.key_pref_page_number), 1);
    }

    public static boolean isFavorites(Context context) {
        return context.getSharedPreferences(MoviesApplication.APP_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                                .getBoolean(context.getString(R.string.key_pref_is_favorites), false);
    }

    public static boolean setFavorites(Context context, boolean isFavorites) {
        context.getSharedPreferences(MoviesApplication.APP_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
                      .putBoolean(context.getString(R.string.key_pref_is_favorites), isFavorites).apply();
        return isFavorites;
    }

}
