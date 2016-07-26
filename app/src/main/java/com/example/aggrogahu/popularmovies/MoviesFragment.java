package com.example.aggrogahu.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;


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




        // TODO: create intent and launch movie details page
        //return inflater.inflate(R.layout.fragment_detail, container, false);
        return rootView;
    }
}
