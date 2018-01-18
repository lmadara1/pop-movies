package com.example.aggrogahu.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
//import android.widget.ListAdapter;
//import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.aggrogahu.popularmovies.data.MovieDbContract;
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
import java.util.ArrayList;

/**
 * Fragment for movie details
 */
//    COMPLETED (3) Create Content Provider
//COMPLETED (3) movie reviews
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

//    private TrailerAdapter mTrailerAdapter;
    private LinearLayout trailerLinearList;
    private LinearLayout reviewLinearList;
//    private ArrayList<Trailer> trailerArrayList;

    public MovieDetailFragment() {
//        setHasOptionsMenu(true);
    }

    public void addFav(){
        // create contentValues with movID
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieDbContract.MovieEntry.COLUMN_MOVIE_ID, movID);
        // Insert the via ContentResolver
        Uri uri = getActivity().getContentResolver().insert(MovieDbContract.MovieEntry.CONTENT_URI, contentValues);

        if(uri != null) {
//            Toast.makeText(getActivity(), uri.toString(), Toast.LENGTH_LONG).show();
            Log.d("add Fav", "uri: " + uri);
        }
    }

    public void deleteFav(){
        String movieId = Integer.toString(movID);
        Uri uri = MovieDbContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();
        Log.d("delete fav", "uri: " + uri);
        getActivity().getContentResolver().delete(uri, null, null);
    }

    private void fetchTrailersReviews(){
        FetchTrailerTask trailerTask = new FetchTrailerTask();
        FetchReviewTask reviewTask = new FetchReviewTask();
        trailerTask.execute();
        reviewTask.execute();

    }

    private void addTrailer(final Trailer trailer){
        // limit to 5
        if(trailerLinearList.getChildCount() > 4){
            return;
        }
        // inflate list_item_trailer template
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_trailer, trailerLinearList, false);
        // set the trailer_title
        TextView trailerName = view.findViewById(R.id.trailer_title);
        trailerName.setText(trailer.title);
        // set onClickListener to open YouTube
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getActivity(),trailer.title,Toast.LENGTH_SHORT).show();
                String key = trailer.youTubeKey;
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key));
                try {
                      getActivity().startActivity(appIntent);
                  } catch (ActivityNotFoundException e) {
                      getActivity().startActivity(browserIntent);
                  }

            }
        });
        //add view to list

        trailerLinearList.addView(view);
    }
    public void addReview(final Review review){
        // limit to 5
        if(reviewLinearList.getChildCount() > 4) {
            return;
        }

        /* not yet implemented */
        // inflate list_item_trailer template
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_review, reviewLinearList, false);
        // set the username and content
        TextView userName = view.findViewById(R.id.review_author);
        TextView content = view.findViewById(R.id.review);
        TextView readMore = view.findViewById(R.id.read_more);
        userName.setText(review.user + " says:");
        content.setText(review.review);
        // set onClickListener to open YouTube
        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getActivity(),trailer.title,Toast.LENGTH_SHORT).show();
                String url = review.url;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getActivity().startActivity(browserIntent);

            }
        });
        //add view to list
        reviewLinearList.addView(view);


    }

    public void onStart() {
        super.onStart();
        fetchTrailersReviews();
    }



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        fetchTrailersReviews();
//        trailerArrayList = new ArrayList<Trailer>();
    }

    //    TODO (8) use Data Binding to clean up findViewById calls
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for the movie detail fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // COMPLETED (3) change from ListView to LinearLayout
        // Find list view
        trailerLinearList = rootView.findViewById(R.id.trailer_list);
        reviewLinearList = rootView.findViewById(R.id.review_list);

        // Setup adapter
//        mTrailerAdapter = new TrailerAdapter(getActivity(),trailerArrayList);
//        myListView.setAdapter(mTrailerAdapter);

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
//            ((TextView) rootView.findViewById(R.id.movie_id))
//                    .setText("ID: " + movID);
            ((TextView) rootView.findViewById(R.id.movie_rating))
                    .setText(mVoteAverage + "/10");
        }

        // Setup add favorite button
        ToggleButton favButton = rootView.findViewById(R.id.favoriteButton);
        favButton.setChecked(true);
        favButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    addFav();
                } else {
                    deleteFav();
                }
            }
        });


        // Launch Youtube app via intent
//        myLinearList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                              @Override
//                                              public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                                                  Trailer trailer = (Trailer) mTrailerAdapter.getItem(i);
//                                                  String key = trailer.youTubeKey;
//                                                  Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
//                                                  Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key));
//
//                                                  try {
//                                                      getActivity().startActivity(appIntent);
//                                                  } catch (ActivityNotFoundException e) {
//                                                      getActivity().startActivity(browserIntent);
//                                                  }
//                                              }
//                                          }
//
//
//        );

        return rootView;
    }

//    TODO (8) do something with settings for Movie Detail screen
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }


    public class FetchTrailerTask extends AsyncTask<Void,Void,Trailer[]> {

        private Trailer[] getTrailersFromJson(String trailerJsonString)
                throws JSONException {

            final String TMDB_RESULTS = "results";

            int numMovs;

            JSONObject trailersJson = new JSONObject(trailerJsonString);
            JSONArray trailersJsonJSONArray = trailersJson.getJSONArray(TMDB_RESULTS);
            numMovs = trailersJsonJSONArray.length();

            Trailer[] resultTrailers = new Trailer[numMovs];

            for(int i = 0; i < trailersJsonJSONArray.length(); i++){

                // Get the JSON object representing the trailers
                JSONObject movieInfo = trailersJsonJSONArray.getJSONObject(i);

                // Debug to make sure we're getting the right information from the JSON
                // Log.v(LOG_TAG, title + releaseDate + poster + plot);

                // Get the trailer name and key from JSON object
                String key = movieInfo.getString("key");
                String title = movieInfo.getString("name");
                resultTrailers[i] = new Trailer(title,key);
            }

            return resultTrailers;
        }

        @Override
        protected Trailer[] doInBackground(Void... voids) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr;

            // TODO (8) factor out api key so I don't have to manually remove/replace it when committing to github
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

        //        COMPLETED (3) onPostExecute should update the ListView via notifying adapter
        @Override
        protected void onPostExecute(Trailer[] trailers) {
            super.onPostExecute(trailers);
            trailerLinearList.removeAllViews();
            // limit to 5
            int tSize;
            if (trailers.length > 5){
                tSize = 5;
            } else {
                tSize = trailers.length;
            }
//            fetchTrailersReviews();
            for (int i=0; i<tSize; i++){
                addTrailer(trailers[i]);
//                trailerArrayList.add(trailers[i]);
            }
//            mTrailerAdapter.notifyDataSetChanged();
            //generate LinearLayout list

        }
    }
    public class FetchReviewTask extends AsyncTask<Void,Void,Review[]> {

        private Review[] getReviewsFromJson(String reviewJsonString)
                throws JSONException {

            final String TMDB_RESULTS = "results";

            int numRevs;

            JSONObject reviewsJson = new JSONObject(reviewJsonString);
            JSONArray reviewsJsonJSONArray = reviewsJson.getJSONArray(TMDB_RESULTS);
            numRevs = reviewsJsonJSONArray.length();

            Review[] resultReviews = new Review[numRevs];

            for(int i = 0; i < reviewsJsonJSONArray.length(); i++){

                // Get the JSON object representing the trailers
                JSONObject reviewInfo = reviewsJsonJSONArray.getJSONObject(i);

                // Debug to make sure we're getting the right information from the JSON
                // Log.v(LOG_TAG, title + releaseDate + poster + plot);

                // Get the review username and content from JSON object
                String user = reviewInfo.getString("author");
                String review = reviewInfo.getString("content");
                String url = reviewInfo.getString("url");
                resultReviews[i] = new Review(user,review, url);
            }

            return resultReviews;
        }

        @Override
        protected Review[] doInBackground(Void... voids) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String reviewsJsonStr;

            // TODO (8) factor out api key so I don't have to manually remove/replace it when committing to github
            // URL parameters to create API call.
            String apikey = "787cb2f1e39c2dcc6fa5ae0612284c06"; // Since sharing API keys publicly on github is frowned upon, I have removed the api key, insert your own here to ensure app works
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
                        .appendPath("reviews")
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
                reviewsJsonStr = buffer.toString();
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
                return getReviewsFromJson(reviewsJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        //        COMPLETED (3) onPostExecute should update the ListView via notifying adapter
        @Override
        protected void onPostExecute(Review[] reviews) {
            super.onPostExecute(reviews);
            //limit to 5
            reviewLinearList.removeAllViews();
            int rSize;
            if (reviews.length > 5){
                rSize = 5;
            } else {
                rSize = reviews.length;
            }

//            fetchTrailersReviews();
            for (int i=0; i<rSize; i++){
                addReview(reviews[i]);
//                trailerArrayList.add(trailers[i]);
            }
//            mTrailerAdapter.notifyDataSetChanged();
            //generate LinearLayout list

        }
    }
}
