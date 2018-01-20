package com.example.aggrogahu.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

/**
 * Fragment for movie details
 */
public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private String mTitle;
    private String mReleaseDate;
    private String mPoster;
    private String mPlot;
    private int movID;
    private int mVoteAverage;

    private LinearLayout trailerLinearList;
    private LinearLayout reviewLinearList;

    public MovieDetailFragment() {
//        setHasOptionsMenu(true);
    }

    /**
     * add a movie to favorites
     */
    public void addFav(){
        // create contentValues with movie information
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieDbContract.MovieEntry.COLUMN_MOVIE_ID, movID);
        contentValues.put(MovieDbContract.MovieEntry.COLUMN_MOVIE_TITLE, mTitle);
        contentValues.put(MovieDbContract.MovieEntry.COLUMN_MOVIE_DATE, mReleaseDate);
        contentValues.put(MovieDbContract.MovieEntry.COLUMN_MOVIE_POSTER, mPoster);
        contentValues.put(MovieDbContract.MovieEntry.COLUMN_MOVIE_PLOT, mPlot);
        contentValues.put(MovieDbContract.MovieEntry.COLUMN_MOVIE_VOTE, mVoteAverage);

        // Insert via ContentResolver
        getActivity().getContentResolver().insert(MovieDbContract.MovieEntry.CONTENT_URI, contentValues);

    }

    /**
     * delete a movie from favorites
     */
    public void deleteFav(){
        String movieId = Integer.toString(movID);
        Uri uri = MovieDbContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();
        getActivity().getContentResolver().delete(uri, null, null);
    }

    /**
     * get trailers and review for the selected movie
     */
    private void fetchTrailersReviews(){
        FetchTrailerTask trailerTask = new FetchTrailerTask();
        FetchReviewTask reviewTask = new FetchReviewTask();
        trailerTask.execute();
        reviewTask.execute();

    }

    /**
     * Add trailer view to the trailer LinearLayout
     * @param trailer the trailer to be added to the list
     */
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

    /**
     * Add review to the review LinearLayout
     * @param review the review to be added to the list
     */
    public void addReview(final Review review){
        // limit to 5
        if(reviewLinearList.getChildCount() > 4) {
            return;
        }

        // inflate list_item_trailer template
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_review, reviewLinearList, false);
        // set the username and content
        TextView userName = view.findViewById(R.id.review_author);
        TextView content = view.findViewById(R.id.review);
        TextView readMore = view.findViewById(R.id.read_more);
        //TODO (5) use string resource for says text
        userName.setText(review.user + " says:");
        content.setText(review.review);
        // set onClickListener to open YouTube
        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        // populate trailers and reviews lists
        fetchTrailersReviews();
    }



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    //    TODO (8) use Data Binding to clean up findViewById calls
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for the movie detail fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // Find list view
        trailerLinearList = rootView.findViewById(R.id.trailer_list);
        reviewLinearList = rootView.findViewById(R.id.review_list);

        // The detail Activity called via intent.  Inspect the intent for movie data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("Movie")) {
            Movie mMovie = intent.getExtras().getParcelable("Movie");
            mTitle = mMovie.title;
            mReleaseDate = mMovie.releaseDate;
            mPoster = mMovie.poster;
            mPlot = mMovie.plot;
            movID = mMovie.mID;
            mVoteAverage = mMovie.voteAverage;

            ((TextView) rootView.findViewById(R.id.movie_title))
                    .setText(mTitle);
            ((TextView) rootView.findViewById(R.id.movie_date))
                    .setText("Released: " + mReleaseDate); //TODO use string resource for release text
            ImageView imageView = (rootView.findViewById(R.id.movie_poster));
            Picasso.with(getContext()).load(mPoster).resize(500,750).into(imageView);
            ((TextView) rootView.findViewById(R.id.movie_description))
                    .setText(mPlot);
            ((TextView) rootView.findViewById(R.id.movie_rating))
                    .setText(mVoteAverage + "/10");
        }

        // Setup add favorite button
        ToggleButton favButton = rootView.findViewById(R.id.favoriteButton);

        // Check to see if the movie is already saved as a favorite
        String movieId = Integer.toString(movID);
        Cursor cursor = getActivity().getContentResolver()
                .query(MovieDbContract.MovieEntry.CONTENT_URI,
                        null,
                        "movie_id" + " = " + DatabaseUtils.sqlEscapeString(movieId),
                        null,
                        null);
        if (cursor.getCount() != 0) {
            favButton.setChecked(true);
        }
        cursor.close();

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

        return rootView;
    }

//    TODO (8) do something with settings for Movie Detail screen
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

//    TODO (5) consolidate into one api call using append_to_response
    public class FetchTrailerTask extends AsyncTask<Void,Void,Trailer[]> {

        private Trailer[] getTrailersFromJson(String trailerJsonString)
                throws JSONException {

            final String TMDB_RESULTS = "results";

            int numTrailers;

            JSONObject trailersJson = new JSONObject(trailerJsonString);
            JSONArray trailersJsonJSONArray = trailersJson.getJSONArray(TMDB_RESULTS);
            numTrailers = trailersJsonJSONArray.length();

            Trailer[] resultTrailers = new Trailer[numTrailers];

            for(int i = 0; i < trailersJsonJSONArray.length(); i++){

                // Get the JSON object representing the trailers
                JSONObject movieInfo = trailersJsonJSONArray.getJSONObject(i);

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
            String apiKey = ""; // Since sharing API keys publicly on github is frowned upon, I have removed the api key, insert your own here to ensure app works

            try {
                // Construct the URL for the API query
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(String.valueOf(movID))
                        .appendPath("videos")
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
            try {
                return getTrailersFromJson(moviesJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

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
            for (int i=0; i<tSize; i++){
                addTrailer(trailers[i]);
            }

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
            String apikey = ""; // Since sharing API keys publicly on github is frowned upon, I have removed the api key, insert your own here to ensure app works

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

            try {
                return getReviewsFromJson(reviewsJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

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
            }


        }
    }
}
