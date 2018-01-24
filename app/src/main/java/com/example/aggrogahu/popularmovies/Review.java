package com.example.aggrogahu.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Leonard on 1/11/2018.
 */

public class Review implements Parcelable{
    String user;
    String review;
    String url;

    public Review(String user, String review, String url){
        this.user = user;
        this.review = review;
        this.url = url;
    }

    private Review(Parcel in){
        user = in.readString();
        review = in.readString();
        url = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(user);
        parcel.writeString(review);
        parcel.writeString(url);
    }

}
