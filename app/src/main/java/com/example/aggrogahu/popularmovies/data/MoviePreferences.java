package com.example.aggrogahu.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.aggrogahu.popularmovies.R;

/**
 * Created by Leonard on 1/18/2018.
 */

public final class MoviePreferences {

    public static String getSorting(Context context){
        // COMPLETED (3) extract sorting preference
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String sortingKey = context.getString(R.string.sorting_key);
        return sp.getString(sortingKey, "popular");
    }
}
