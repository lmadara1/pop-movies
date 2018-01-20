package com.example.aggrogahu.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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

import com.example.aggrogahu.popularmovies.data.MovieDbContract;

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
import java.util.Collection;
import java.util.Collections;

import static com.example.aggrogahu.popularmovies.data.MoviePreferences.getSorting;

/**
 * Created by aggrogahu on 10/10/2016.
 * Concepts from Sunshine App Udacity Project
 */

public class MoviesFragment extends Fragment {

    private final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private MovieAdapter mMovieAdapter;         //Adapter
    private ArrayList<Movie> movieArrayList;    //List that adapter will read data from
    private boolean apicall = false;            //Flag to indicate if api call succeeded or not


    /**
     * called from onStart and whenever GridView needs to be updated with new movies to display
     * @param sort popular, top_rated, or favorites; first two use an api call, favorites just reads from content provider
     */
    private void updateMovies(String sort){
        if (sort.equals("favorites")){
            showFavoriteMovies();
        }
        else {
            FetchMoviesTask moviesTask = new FetchMoviesTask();
            moviesTask.execute(sort);
        }
    }

    public void onStart(){
        super.onStart();
        // Use MoviePreferences class function to retrieve sorting preference
        String sort = getSorting(getContext());
        updateMovies(sort);
    }

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        movieArrayList = new ArrayList<>();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.moviefragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // get preference editor to make changes
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sp.edit();
        String sortingKey = getContext().getString(R.string.sorting_key);

        int id = item.getItemId();
        // popular
        if (id == R.id.action_popular_sort){
            editor.putString(sortingKey, "popular");
            editor.apply();
            updateMovies("popular");
            return true;
        }
        // top rated
        if (id == R.id.action_rating_sort){
            editor.putString(sortingKey, "top_rated");
            editor.apply();
            updateMovies("top_rated");
            return true;
        }
        // favorites
        if (id == R.id.action_favorite_sort){
            editor.putString(sortingKey, "favorites");
            editor.apply();
            updateMovies("favorites");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Find Grid View
        GridView myGridView = rootView.findViewById(R.id.gridview_movies);

        // Setup adapter
        mMovieAdapter = new MovieAdapter(getActivity(),movieArrayList);
        myGridView.setAdapter(mMovieAdapter);

        mMovieAdapter.notifyDataSetChanged();

        // Setup click listener to launch movie details fragment
        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = (Movie) mMovieAdapter.getItem(i);
                Intent detailIntent = new Intent(getActivity(),MovieDetailActivity.class)
                        .putExtra("Movie",movie);
                startActivity(detailIntent);
            }
        });
        return rootView;
    }

    /**
     * convert Json from themoviedb api call to Movie[]
     * @param movieJsonStr Json string to parse for movies
     * @return Movie[] containing movies from api call results
     * @throws JSONException when something goes wrong
     */
    private Movie[] getMovieDataFromJson(String movieJsonStr)
            throws JSONException{

        final String TMDB_RESULTS = "results";
        final String TMDB_ID = "id";
        int numMovies;

        JSONObject moviesJson = new JSONObject(movieJsonStr);
        JSONArray movieJsonArray = moviesJson.getJSONArray(TMDB_RESULTS);
        numMovies = movieJsonArray.length();

        Movie[] resultMovies = new Movie[numMovies];



        for(int i = 0; i < movieJsonArray.length(); i++){
            String baseURL = "http://image.tmdb.org/t/p/w185";

            // Get the JSON object representing the movie
            JSONObject movieInfo = movieJsonArray.getJSONObject(i);

            String title = movieInfo.getString(MovieDbContract.MovieEntry.COLUMN_MOVIE_TITLE);
            String releaseDate = movieInfo.getString(MovieDbContract.MovieEntry.COLUMN_MOVIE_DATE);
            String poster = baseURL + movieInfo.getString(MovieDbContract.MovieEntry.COLUMN_MOVIE_POSTER);
            String plot = movieInfo.getString(MovieDbContract.MovieEntry.COLUMN_MOVIE_PLOT);
            int mid = movieInfo.getInt(TMDB_ID);
            int voteAverage = movieInfo.getInt(MovieDbContract.MovieEntry.COLUMN_MOVIE_VOTE);

            // Debug to make sure we're getting the right information from the JSON
            // Log.v(LOG_TAG, title + releaseDate + poster + plot);
            resultMovies[i] = new Movie(title,releaseDate,poster,plot,mid,voteAverage);
        }

        return resultMovies;
    }

    /**
     * update main GridView to display favorite movies
     */
    private void showFavoriteMovies(){
            // retrieve cursor
            Cursor cursor = getActivity().getContentResolver()
                    .query(MovieDbContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

            // initialize array
            int mSize = cursor.getCount();
            cursor.moveToFirst();

            // create array from cursor
            int titleIndex = cursor.getColumnIndex(MovieDbContract.MovieEntry.COLUMN_MOVIE_TITLE);
            int dateIndex = cursor.getColumnIndex(MovieDbContract.MovieEntry.COLUMN_MOVIE_DATE);
            int posterIndex = cursor.getColumnIndex(MovieDbContract.MovieEntry.COLUMN_MOVIE_POSTER);
            int plotIndex = cursor.getColumnIndex(MovieDbContract.MovieEntry.COLUMN_MOVIE_PLOT);
            int mIdIndex = cursor.getColumnIndex(MovieDbContract.MovieEntry.COLUMN_MOVIE_ID);
            int voteIndex = cursor.getColumnIndex(MovieDbContract.MovieEntry.COLUMN_MOVIE_VOTE);

            movieArrayList.clear();
            for (int i = 0; i < mSize; i++){
                String title = cursor.getString(titleIndex);
                String date = cursor.getString(dateIndex);
                String poster = cursor.getString(posterIndex);
                String plot = cursor.getString(plotIndex);
                int mId = cursor.getInt(mIdIndex);
                int vote = cursor.getInt(voteIndex);

                Log.d("showFavoriteMovies", "title: " + title);
                movieArrayList.add(new Movie(title,date,poster,plot,mId,vote));
                cursor.moveToNext();
            }
            mMovieAdapter.notifyDataSetChanged();
            // TODO (8) if there are no favorites saved, show a prompt as a view instead of a toast
            if(movieArrayList.size()==0){
                Toast.makeText(getContext(), "You have no favorites saved; choose a different sorting option from the menu", Toast.LENGTH_LONG).show();
            }
            cursor.close();
    }

//  TODO (8) prolly should factor the FetchMoviesTask and AsyncTask to separate java file too
    public class FetchMoviesTask extends AsyncTask<String,Void,Movie[]>{

        private String sort;

        @Override
        protected Movie[] doInBackground(String... params){
            // Making api call...
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr;

            // URL parameters to create API call.
            String apiKey = ""; // Since sharing API keys publicly on github is frowned upon, I have removed the api key, insert your own here to ensure app works
            this.sort = params[0];

            try {
                // Construct the URL for the API query
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(sort)
                        .appendQueryParameter("api_key",apiKey)
                ;
                Log.d("Moviez", builder.build().toString());
                URL url = new URL(builder.build().toString());

                // Create the request and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                // TODO (8) change to StringBuilder
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
            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies){
            // update array and adapter
            if (apicall){
                Toast.makeText(getActivity(),"API call failed",Toast.LENGTH_SHORT).show();
            }

            movieArrayList.clear();
            if(movies != null){
                Collections.addAll(movieArrayList,movies);
            }
            mMovieAdapter.notifyDataSetChanged();
        }
    }
}
