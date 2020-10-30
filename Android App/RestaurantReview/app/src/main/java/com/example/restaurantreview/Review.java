package com.example.restaurantreview;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Review {
    private int review_id;
    private String name;
    private String review;
    private int rating;
    private Date date_added;

    public Review(int review_id, String name, String review, int rating, String date_added) {
        this.review_id = review_id;
        this.name = name;
        this.review = review;
        this.rating = rating;
        Log.e("test",date_added);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.date_added = format.parse(date_added.substring(0,10));
            Log.e("test",this.date_added.toString());
        }
        catch (ParseException e){
            Log.e("test",e.getMessage());
        }
    }

    public int getReview_id() {
        return review_id;
    }

    public void setReview_id(int review_id) {
        this.review_id = review_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Date getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        SimpleDateFormat format=new SimpleDateFormat("dd MM YYYY");
        try {
            this.date_added = format.parse(date_added);
        }
        catch (ParseException e){
            e.printStackTrace();
        }
    }
}
