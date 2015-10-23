package com.smenedi.nano;

import com.smenedi.nano.data.MovieContract.MovieEntry;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by smenedi on 10/18/15.
 */
public class Utility {
    private static final String SORT_ORDER_FORMAT = "%s.desc";

    public static String getSortOrder(Context context) {
        return String.format(SORT_ORDER_FORMAT, PreferenceManager.getDefaultSharedPreferences(context)
                                                                 .getString(context.getString(R.string.key_pref_sort_order), context.getString(R.string.value_pref_sort_order_default)));
    }

    public static String getSqlSortOrder(Context context) {
        if (getSortOrder(context).equals("popularity.desc")) {
            return MovieEntry.COLUMN_POPULARITY + " DESC";
        } else {
            return MovieEntry.COLUMN_RATING + " DESC";
        }
    }

    public static int getPageNumber(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                         .getInt(context.getString(R.string.key_pref_page_number), 1);
    }
}
