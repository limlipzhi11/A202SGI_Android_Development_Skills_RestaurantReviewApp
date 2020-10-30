package com.example.restaurantreview;

public class Dish {
    private int dish_id;
    private int restaurant_id;
    private String name;
    private String image;
    private int rating;
    private int num_review;

    public Dish(int dish_id, int restaurant_id, String name, String image,int rating,int num_review) {
        this.dish_id = dish_id;
        this.restaurant_id = restaurant_id;
        this.name = name;
        this.image = image;
        this.rating=rating;
        this.num_review=num_review;
    }

    public int getDish_id() {
        return dish_id;
    }

    public void setDish_id(int dish_id) {
        this.dish_id = dish_id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getNum_review() {
        return num_review;
    }

    public void setNum_review(int num_review) {
        this.num_review = num_review;
    }
}
