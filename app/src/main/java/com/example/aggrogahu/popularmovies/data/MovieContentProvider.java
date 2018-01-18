package com.example.aggrogahu.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.aggrogahu.popularmovies.data.MovieDbContract.MovieEntry.TABLE_NAME;

/**
 * Created by Leonard on 1/17/2018.
 * With Udacity Lesson 09 To do list as reference
 */

public class MovieContentProvider extends ContentProvider {

    // Int constants
    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;

    // Uri matcher
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Global DbHelper
    private MovieDbHelper mMovieDbHelper;

    public static UriMatcher buildUriMatcher(){
        // Initialize urimatcher
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Add paths
        uriMatcher.addURI(MovieDbContract.AUTHORITY, MovieDbContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieDbContract.AUTHORITY, MovieDbContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Log.d("ContentProvider", "right");
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }

//    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String s, String[] strings1, String sortOrder) {
        // get database
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        // get uri match code and declare cursor
        int match = sUriMatcher.match(uri);
        Cursor cursor;

        // query
        switch (match) {
            // query for entire movie directory
            case MOVIES:
                cursor = db.query(TABLE_NAME,
                        projection,
                        s,
                        strings1,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIES_WITH_ID:
                cursor = db.query(TABLE_NAME,
                        projection,
                        s,
                        strings1,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // set notification uri on cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // not yet implemented
        return null;
    }

//    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        // get db
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        // get uri matching code
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIES:
                // insert into database
                long id = db.insert(TABLE_NAME, null, contentValues);
                if( id > 0){
                    returnUri = ContentUris.withAppendedId(MovieDbContract.MovieEntry.CONTENT_URI,id);
                } else {
                    throw new SQLException("Insert failed: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        // get db
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        // get url match code
        int match = sUriMatcher.match(uri);
        Log.d("CP del","match: " + match);
        int tasksDeleted;

        // delete entries
        switch (match) {
            case MOVIES_WITH_ID:
                // get movie index from URI path
                String id = uri.getPathSegments().get(1);
                // filter for id
//                tasksDeleted = db.delete(TABLE_NAME,"_id=?", new String[]{id});
                tasksDeleted = db.delete(TABLE_NAME,"movie_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // notify contentresolver
        if (tasksDeleted !=0 ){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
