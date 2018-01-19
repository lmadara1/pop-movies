package com.example.aggrogahu.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.aggrogahu.popularmovies.Movie;
import com.example.aggrogahu.popularmovies.data.MovieDbContract.MovieEntry;

/**
 * Created by Leonard on 1/17/2018.
 * With Udacity Lesson 09 To do list as reference
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    // Database name
    private static final String DATABASE_NAME = "moviesDb.db";

    // Database version
    private static final int VERSION = 1;

    MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("Helper onCreate", "what now");
        //Create movies table
        final String CREATE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                        MovieEntry._ID             + " INTEGER PRIMARY KEY, " +
                        MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_MOVIE_DATE + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_MOVIE_PLOT + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_MOVIE_VOTE + " INTEGER NOT NULL);";


        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
