package com.example.aggrogahu.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by aggrogahu on 10/10/2016.
 * Concepts from Sunshine App Udacity Project
 */

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private Movie mMovie;
        private String mTitle;
        private String mReleaseDate;
        private String mPoster;
        private String mPlot;
        private Long mVoteAverage;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("Movie")) {
                mMovie = intent.getExtras().getParcelable("Movie");
                mTitle = mMovie.title;
                mReleaseDate = mMovie.releaseDate;
                mPoster = mMovie.poster;
                mPlot = mMovie.plot;
                mVoteAverage = mMovie.voteAverage;

                ((TextView) rootView.findViewById(R.id.movie_title))
                        .setText(mTitle);
                ((TextView) rootView.findViewById(R.id.movie_date))
                        .setText("Released: " + mReleaseDate);
                ImageView imageView = ((ImageView) rootView.findViewById(R.id.movie_poster));
                Picasso.with(getContext()).load(mPoster).resize(500,750).into(imageView);
                ((TextView) rootView.findViewById(R.id.movie_description))
                        .setText(mPlot);
                ((TextView) rootView.findViewById(R.id.movie_rating))
                        .setText("Rated " + mVoteAverage + " out of 10");
            }

            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        }

    }

}
