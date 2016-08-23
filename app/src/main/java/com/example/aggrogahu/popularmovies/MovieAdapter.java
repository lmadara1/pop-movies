package com.example.aggrogahu.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by aggrogahu on 7/23/2016.
 * Concepts from https://developer.android.com/guide/topics/ui/layout/gridview.html
 */
public class MovieAdapter extends BaseAdapter{
    private Context mContext;
//    private List<Movie> mMovieList;

    public MovieAdapter(Context c, List<Movie> movieList) {
        mContext = c;
//        mMovieList = movieList;
    }

    public int getCount() {
        return movies.length;
    }

    public Object getItem(int position) {
        return movies[position];
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
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

//        Picasso.with(mContext).load("http://i.imgur.com/DvpvklR.png").into(imageView);
        Picasso.with(mContext).load(movies[position].poster).into(imageView);
//        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // dummy array

    Movie[] movies = {
            new Movie("Interstellar","July","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","plot"),
            new Movie("Interstellar","July","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","plot"),
            new Movie("Interstellar","July","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","plot"),
            new Movie("Interstellar","July","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","plot"),
            new Movie("Interstellar","July","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","plot"),
            new Movie("Interstellar","July","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","plot"),
            new Movie("Interstellar","July","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","plot"),
            new Movie("Interstellar","July","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","plot")
    };
}
