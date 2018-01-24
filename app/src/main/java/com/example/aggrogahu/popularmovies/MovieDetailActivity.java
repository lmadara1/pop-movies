package com.example.aggrogahu.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;

/**
 * Created by aggrogahu on 10/10/2016.
 * Concepts from Sunshine App Udacity Project
 */

//TODO (8) MovieDetailActivity might not be needed

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieDetailFragment())
                    .commit();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//
        int id = item.getItemId();
        Log.d("Detail", "item id: " + id + " home: " + R.id.home + " or " + R.id.homeAsUp);
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.home) {
//            Log.d("Detail", "why");
//            NavUtils.navigateUpFromSameTask(this);
////            startActivity(new Intent(this, SettingsActivity.class));
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


}
