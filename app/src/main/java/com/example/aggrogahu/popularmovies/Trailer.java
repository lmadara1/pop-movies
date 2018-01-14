package com.example.aggrogahu.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Leonard on 1/11/2018.
 */

public class Trailer implements Parcelable{
    String title;
    String youTubeKey;

    public Trailer(String title, String key){
        this.title = title;
        this.youTubeKey = key;
    }

    private Trailer(Parcel in){
        title = in.readString();
        youTubeKey = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(youTubeKey);
    }

}
