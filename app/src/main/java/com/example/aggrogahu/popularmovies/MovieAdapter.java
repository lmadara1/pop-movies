package com.example.aggrogahu.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by aggrogahu on 10/10/2016.
 * Concepts from https://developer.android.com/guide/topics/ui/layout/gridview.html
 */
public class MovieAdapter extends BaseAdapter{
    private Context mContext;
    private List<Movie> mMovieList;

    public MovieAdapter(Context c, List<Movie> movieList) {
        mContext = c;
        mMovieList = movieList;
    }

    public int getCount() {
        return mMovieList.size();
    }

    public Object getItem(int position) {
        return mMovieList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext).load(mMovieList.get(position).poster).into(imageView);
        return imageView;
    }

    // dummy array
//
//    Movie[] movies = {
//            new Movie("Interstellar","July","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","plot"),
//            new Movie("Interstellar","July","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","plot"),
//            new Movie("Interstellar","July","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","plot"),
//            new Movie("Interstellar","July","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","plot"),
//            new Movie("Interstellar","July","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","plot"),
//            new Movie("Interstellar","July","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","plot"),
//            new Movie("Interstellar","July","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","plot"),
//            new Movie("Interstellar","July","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","plot")
//    };
}
