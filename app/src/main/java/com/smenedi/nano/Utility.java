package com.smenedi.nano;

import com.smenedi.nano.data.MovieContract.MovieEntry;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

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
               .putBoolean(context.getString(R.string.key_pref_is_favorites), isFavorites).commit();
        return isFavorites;
    }

    public static String getSqlSortOrder(Context context) {
        if (getSortOrder(context).equals("popularity.desc")) {
            return MovieEntry.COLUMN_POPULARITY + " DESC";
        } else {
            return MovieEntry.COLUMN_RATING + " DESC";
        }
    }

    public static void showSnackbar(Activity activity, String message) {
        final View coordinatorLayoutView = activity.findViewById(R.id.snackbar);
        Snackbar.make(coordinatorLayoutView, message, Snackbar.LENGTH_SHORT).show();
    }

    public void setFavoriteButton(FloatingActionButton favorite, boolean isFavorite) {
        if(isFavorite) {
            favorite.setImageDrawable(favorite.getResources().getDrawable(R.drawable.favorite));
        } else {
            favorite.setImageDrawable(favorite.getResources().getDrawable(R.drawable.nofavorite));
        }
    }
}
