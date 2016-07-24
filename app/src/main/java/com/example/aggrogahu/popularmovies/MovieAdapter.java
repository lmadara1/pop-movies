package com.example.aggrogahu.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Leonard Madarang on 6/12/2016.
 * Using concepts from https://github.com/udacity/android-custom-arrayadapter/blob/master/app/src/main/java/demo/example/com/customarrayadapter/AndroidFlavorAdapter.java
 */
public class MovieAdapter extends ArrayAdapter<Movie>{
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Activity context, List<Movie> movies){
        super(context,0,movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
        }

        // Set values of the views
        //TextView movTitleView = (TextView) convertView.findViewById(R.id.list_item_title);
        //movTitleView.setText(movie.title);

        //TextView rDateView = (TextView) convertView.findViewById(R.id.list_item_date);
        //rDateView.setText(movie.releaseDate);

        // TODO: Set image with Picasso
        ImageView view = (ImageView) convertView;
        if (view == null){
            view = new ImageView(getContext());
        }
        String url = getItem(position).poster;

        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg").into(view);//Picasso.with(getContext()).load(url).into(view);



        //TextView plotSynView = (TextView) convertView.findViewById(R.id.list_item_plot);
        //plotSynView.setText(movie.plot);

        return convertView;
    }
}


