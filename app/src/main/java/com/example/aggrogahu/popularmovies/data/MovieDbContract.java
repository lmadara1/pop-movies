package com.example.aggrogahu.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Leonard on 1/17/2018.
 */

public class MovieDbContract {

    // Authority
    public static final String AUTHORITY = "com.example.aggrogahu.popularmovies";

    // Base content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Paths
    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns{

        // content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        // table and column names
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_DATE = "date";
        public static final String COLUMN_MOVIE_POSTER = "poster";
        public static final String COLUMN_MOVIE_PLOT = "plot";
        public static final String COLUMN_MOVIE_VOTE = "vote";

    }
}
