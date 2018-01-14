package com.example.aggrogahu.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by aggrogahu on 10/10/2016.
 * Concepts from Sunshine App Udacity Project
 */

public class MoviesFragment extends Fragment {

    private final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private MovieAdapter mMovieAdapter;
    private ArrayList<Movie> movieArrayList;    //List that adapter will read data from
    private boolean apicall = false;            //Flag to indicate if api call succeeded or not

    private void updateMovies(String sort){
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute(sort);
    }

//    TODO (5) make sure sorting preference persists after opening details and navigating back, as well as through rotating device
    public void onStart(){
        super.onStart();
        updateMovies("popular");
    }

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        movieArrayList = new ArrayList<Movie>();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.moviefragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_popular_sort){
            updateMovies("popular");
            Log.v(LOG_TAG,"popular");
            return true;
        }
        if (id == R.id.action_rating_sort){
            updateMovies("top_rated");
            Log.v(LOG_TAG,"rop_tated");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Find Grid View
        GridView myGridView = rootView.findViewById(R.id.gridview_movies);

        // Setup adapter
        mMovieAdapter = new MovieAdapter(getActivity(),movieArrayList);
        myGridView.setAdapter(mMovieAdapter);

        mMovieAdapter.notifyDataSetChanged();

        // Launch Movie Details
        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getActivity(),mForecastAdapter.getItem(i).toString(), Toast.LENGTH_SHORT).show();
                Movie movie = (Movie) mMovieAdapter.getItem(i);
                Intent detailIntent = new Intent(getActivity(),MovieDetailActivity.class)
                        .putExtra("Movie",movie);
                startActivity(detailIntent);
            }
        });
        return rootView;
    }
//  TODO (8) prolly should factor the FetchMoviesTask and AsyncTask to separate java file too
    public class FetchMoviesTask extends AsyncTask<String,Void,Movie[]>{

        private String sort;

        private Movie[] getMovieDataFromJson(String movieJsonStr, String sort)
                throws JSONException{

            final String TMDB_RESULTS = "results";
            final String TMDB_TITLE = "original_title";
            final String TMDB_DATE = "release_date";
            final String TMDB_POSTER = "poster_path";
            final String TMDB_PLOT = "overview";
            final String TMDB_ID = "id";
            final String TMDB_VOTEAV = "vote_average";
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
                int mid;
                Long voteAverage;
                String baseURL = "http://image.tmdb.org/t/p/w185";

                // Get the JSON object representing the movie
                JSONObject movieInfo = movieJsonArray.getJSONObject(i);

                title = movieInfo.getString(TMDB_TITLE);
                releaseDate = movieInfo.getString(TMDB_DATE);
                poster = baseURL + movieInfo.getString(TMDB_POSTER);
                plot = movieInfo.getString(TMDB_PLOT);
                mid = movieInfo.getInt(TMDB_ID);
                voteAverage = movieInfo.getLong(TMDB_VOTEAV);

                // Debug to make sure we're getting the right information from the JSON
                // Log.v(LOG_TAG, title + releaseDate + poster + plot);
                resultMovs[i] = new Movie(title,releaseDate,poster,plot,mid,voteAverage);
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
            String moviesJsonStr;

            // URL parameters to create API call.
            String apikey = ""; // Since sharing API keys publicly on github is frowned upon, I have removed the api key, insert your own here to ensure app works
            String format = "json";
            this.sort = params[0];
            Log.v("Sorting: ", sort);
//            String sort = sort;//either "popular" or "top_rated"

            try {
                // Construct the URL for the API query
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(sort)
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
                apicall = true;
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
                return getMovieDataFromJson(moviesJsonStr,sort);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies){
            //TODO (?) update adapter
            if (apicall){
                Toast.makeText(getActivity(),"API call failed, please insert your own API key, see README for details",Toast.LENGTH_SHORT).show();
            }
            movieArrayList.clear();
            if(movies != null){
            for (Movie m : movies){
                movieArrayList.add(m);
            }
            }
            mMovieAdapter.notifyDataSetChanged();
        }
    }
}
