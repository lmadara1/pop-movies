package com.example.aggrogahu.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Fragment for movie details
 */
//    COMPLETED factor fragment out into separate java file
public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private Movie mMovie;
    private String mTitle;
    private String mReleaseDate;
    private String mPoster;
    private String mPlot;
    private int movID;
    private Long mVoteAverage;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // The detail Activity called via intent.  Inspect the intent for movie data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("Movie")) {
            mMovie = intent.getExtras().getParcelable("Movie");
            mTitle = mMovie.title;
            mReleaseDate = mMovie.releaseDate;
            mPoster = mMovie.poster;
            mPlot = mMovie.plot;
            movID = mMovie.mID;
            mVoteAverage = mMovie.voteAverage;

            ((TextView) rootView.findViewById(R.id.movie_title))
                    .setText(mTitle);
            ((TextView) rootView.findViewById(R.id.movie_date))
                    .setText("Released: " + mReleaseDate);
            ImageView imageView = (rootView.findViewById(R.id.movie_poster));
            Picasso.with(getContext()).load(mPoster).resize(500,750).into(imageView);
            ((TextView) rootView.findViewById(R.id.movie_description))
                    .setText(mPlot);
            ((TextView) rootView.findViewById(R.id.movie_id))
                    .setText("ID: " + movID);
            ((TextView) rootView.findViewById(R.id.movie_rating))
                    .setText("Rated " + mVoteAverage + " out of 10");
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }
    public class FetchTrailerTask extends AsyncTask<Void,Void,String[]> {

        private String[] getTrailersFromJson(String trailerJsonString)
                throws JSONException {

            final String TMDB_RESULTS = "results";

            int numMovs;

            JSONObject trailersJson = new JSONObject(trailerJsonString);
            JSONArray trailersJsonJSONArray = trailersJson.getJSONArray(TMDB_RESULTS);
            numMovs = trailersJsonJSONArray.length();

            String[] resultTrailers = new String[numMovs];

            for(int i = 0; i < trailersJsonJSONArray.length(); i++){

                // Get the JSON object representing the trailers
                JSONObject movieInfo = trailersJsonJSONArray.getJSONObject(i);

                // Debug to make sure we're getting the right information from the JSON
                // Log.v(LOG_TAG, title + releaseDate + poster + plot);

                // Get the trailers from JSON object into String Array
                resultTrailers[i] = movieInfo.getString("key");
            }

            return resultTrailers;
        }

        @Override
        protected String[] doInBackground(Void... voids) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr;

            // URL parameters to create API call.
            String apikey = ""; // Since sharing API keys publicly on github is frowned upon, I have removed the api key, insert your own here to ensure app works
//                String format = "json";
//                this.sort = params[0];
//                Log.v("Sorting: ", sort);
//            String sort = sort;//either "popular" or "top_rated"

            try {
                // Construct the URL for the API query
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(String.valueOf(movID))
                        .appendPath("videos")
                        .appendQueryParameter("api_key",apikey)
                ;
                Log.d("Moviez", builder.build().toString());
                URL url = new URL(builder.build().toString());

                // Create the request and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
//                    apicall = true;
                // If the code didn't successfully get the data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            //Debug to make sure the api call worked correctly, should return json with desired information
            //Log.v(LOG_TAG, moviesJsonStr);
            try {
                return getTrailersFromJson(moviesJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
