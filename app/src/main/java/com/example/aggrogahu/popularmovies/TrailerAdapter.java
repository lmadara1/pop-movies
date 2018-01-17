package com.example.aggrogahu.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Leonard on 1/10/2018.
 */

public class TrailerAdapter extends BaseAdapter{
    private Context tContext;
    private List<Trailer> trailerList;


    public TrailerAdapter( Context context,  List<Trailer> objects) {

        tContext = context;
        trailerList = objects;
    }

    @Override
    public int getCount() {
        return trailerList.size();
    }

    @Override
    public Trailer getItem(int i) {
        return trailerList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null){
            view = LayoutInflater.from(tContext).inflate(R.layout.list_item_trailer,viewGroup,false);
        }

        TextView trailerName = view.findViewById(R.id.trailer_title);

        trailerName.setText(trailerList.get(i).title);

        return view;
    }
}
