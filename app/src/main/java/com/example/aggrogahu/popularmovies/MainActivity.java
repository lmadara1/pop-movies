package com.example.aggrogahu.popularmovies;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.common.android.FragmentCompatUtil;

public class MainActivity extends AppCompatActivity {

    private static final String FRAGMENT_STATE = "fragment";
    private android.support.v4.app.Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.d("Main", "onCreate, fragments: " + getSupportFragmentManager().getFragments().size());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, new MoviesFragment())
                    .commit();
        } else {
            Log.d("M", "something saved");
            mFragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_STATE);
            if (mFragment.isAdded()) {
                return;
            }else {
                Log.d("M", "else");
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_container, mFragment)
                        .commit();
            }
        }

         /* "ctrl+/" on this line to turn on Stetho debugger
        final Context context = this;
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                        .build());//*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_popular_sort) {
//            //startActivity(new Intent(this, SettingsActivity.class));
//            return true;
//        }
//        if (id == R.id.action_rating_sort) {
//            //startActivity(new Intent(this, SettingsActivity.class));
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        getSupportFragmentManager().putFragment(outState, FRAGMENT_STATE, mFragment);
    }
}
