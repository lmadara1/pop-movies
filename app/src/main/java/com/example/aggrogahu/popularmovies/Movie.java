package com.example.aggrogahu.popularmovies;

/**
 * Created by Leonard Madarang on 6/12/2016.
 * Using concepts from https://github.com/udacity/android-custom-arrayadapter/blob/master/app/src/main/java/demo/example/com/customarrayadapter/AndroidFlavor.java
 */
public class Movie {
    String title;
    String releaseDate;
    String poster;
    String plot;

    public Movie(String tit, String rDate, String post, String plot){
        this.title = tit;
        this.releaseDate = rDate;
        this.poster = post;
        this.plot = plot;
    }
}
