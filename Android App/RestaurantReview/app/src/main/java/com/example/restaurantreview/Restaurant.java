package com.example.restaurantreview;

import java.util.ArrayList;

public class Restaurant {
    private int restaurant_id;
    private String name;
    private String website;
    private String tel_no;
    private String address;
    private String description;
    private ArrayList<String> tags;
    private ArrayList<String> services;
    private String thumbnail;
    private int num_review;
    private int rating;

    public Restaurant(int restaurant_id, String name, String website, String tel_no, String address, String description, ArrayList<String> tags, ArrayList<String> services, String thumbnail,int num_review,int rating) {
        this.restaurant_id = restaurant_id;
        this.name = name;
        this.website = website;
        this.tel_no = tel_no;
        this.address = address;
        this.description = description;
        this.tags = tags;
        this.services = services;
        this.thumbnail = thumbnail;
        this.num_review=num_review;
        this.rating=rating;
    }

    public int getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTel_no() {
        return tel_no;
    }

    public void setTel_no(String tel_no) {
        this.tel_no = tel_no;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getServices() {
        return services;
    }

    public void setServices(ArrayList<String> services) {
        this.services = services;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getNum_review() {
        return num_review;
    }

    public void setNum_review(int num_review) {
        this.num_review = num_review;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
