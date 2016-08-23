package com.example.aggrogahu.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Leonard Madarang on 6/12/2016.
 * Using concepts from https://github.com/udacity/android-custom-arrayadapter/blob/master/app/src/main/java/demo/example/com/customarrayadapter/AndroidFlavor.java
 */
public class Movie implements Parcelable {
    String title;
    String releaseDate;
    String poster;
    String plot;

    public Movie(String tit, String rDate, String postr, String plot){
        this.title = tit;
        this.releaseDate = rDate;
        this.poster = postr;
        this.plot = plot;
    }

    private Movie(Parcel in){
        title = in.readString();
        releaseDate = in.readString();
        poster = in.readString();
        plot = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(releaseDate);
        parcel.writeString(poster);
        parcel.writeString(plot);
    }

    public final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel parcel){
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i){
            return new Movie[i];
        }
    };
}
