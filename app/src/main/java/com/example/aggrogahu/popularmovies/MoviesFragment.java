package com.example.aggrogahu.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MoviesFragment extends Fragment {

    private final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private MovieAdapter mMovieAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private void updateMovies(){
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute("herp","derp");
    }

    public void onStart(){
        super.onStart();
        updateMovies();
    }

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // TODO: Initialize adapter
//        mMovieAdapter = new MovieAdapter(getContext());//(getActivity(),R.layout.grid_item_movie,R.id.grid_item_movie_imageview,new ArrayList<ImageView>());

        // Find Grid View
        GridView myGridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        // TODO: Setup adapter
        mMovieAdapter = new MovieAdapter(getActivity());
        myGridView.setAdapter(mMovieAdapter);

        // Dummy data to test
//        ImageView iv = new ImageView(getContext());
//        iv.setImageResource(R.drawable.interstellar);
//        mMovieAdapter.add(iv);

        // Picasso test
//        ImageView iv = new ImageView(getActivity());
//        Picasso.with(getActivity()).load("http://i.imgur.com/DvpvklR.png").into(iv);
//        mMovieAdapter.add(iv);
//        Log.v("Adapter Count", "Count" + mMovieAdapter.getCount());
        mMovieAdapter.notifyDataSetChanged();

        // Launch Movie Details
        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getActivity(),mForecastAdapter.getItem(i).toString(), Toast.LENGTH_SHORT).show();
                String forecast = mMovieAdapter.getItem(i).toString();
                Intent detailIntent = new Intent(getActivity(),MovieDetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT,5);//forecast went where 5 is
                startActivity(detailIntent);
            }
        });



        // TODO: create intent and launch movie details page
        //return inflater.inflate(R.layout.movie_detail, container, false);
        return rootView;
    }

    public class FetchMoviesTask extends AsyncTask<String,Void,Movie[]>{
        private Movie[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException{

            final String TMDB_RESULTS = "results";
            final String TMDB_TITLE = "original_title";
            final String TMDB_DATE = "release_date";
            final String TMDB_POSTER = "poster_path";
            final String TMDB_PLOT = "overview";
            int numMovs;

            JSONObject moviesJson = new JSONObject(movieJsonStr);
            JSONArray movieJsonArray = moviesJson.getJSONArray(TMDB_RESULTS);
            numMovs = movieJsonArray.length();

            Movie[] resultMovs = new Movie[numMovs];

            for(int i = 0; i < movieJsonArray.length(); i++){
                String title;
                String releaseDate;
                String poster;
                String plot;
                String baseURL = "http://image.tmdb.org/t/p/w185";

                // Get the JSON object representing the movie
                JSONObject movieInfo = movieJsonArray.getJSONObject(i);

                title = movieInfo.getString(TMDB_TITLE);
                releaseDate = movieInfo.getString(TMDB_DATE);
                poster = baseURL + movieInfo.getString(TMDB_POSTER);
                plot = movieInfo.getString(TMDB_PLOT);

                // Debug to make sure we're getting the right information from the JSON
                // Log.v(LOG_TAG, title + releaseDate + poster + plot);
                resultMovs[i] = new Movie(title,releaseDate,poster,plot);
            }

            return resultMovs;
        }

        @Override
        protected Movie[] doInBackground(String... params){

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            // URL parameters to create API call.
            String format = "json";
            String sort = "popular";//either "popular" or "top_rated"

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(sort)
                        .appendQueryParameter("api_key","900d602e0797810c4581d1a74e5d030c")
                ;
                //Log.d("Moviez", builder.build().toString());
                URL url = new URL(builder.build().toString());

                // Create the request to OpenWeatherMap, and open the connection
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
                // If the code didn't successfully get the weather data, there's no point in attemping
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
                //Log.v("Array [0]", getWeatherDataFromJson(forecastJsonStr,days)[0]);
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies){
            //TODO update adapter
//            mMovieAdapter
        }
    }
}
