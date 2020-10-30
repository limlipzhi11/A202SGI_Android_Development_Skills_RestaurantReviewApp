package com.example.restaurantreview;

public class Api {
    private static String API_ROOT="http://192.168.0.173/Restaurant_Review/index.php?route=api/review_api/";

    public final static String API_LOGIN=API_ROOT+"login";
    public final static String API_REGISTER=API_ROOT+"register";
    public final static String API_RESTAURANTS=API_ROOT+"getAllRestaurants";
    public final static String API_FEATURED=API_ROOT+"getFeatured";
    public final static String API_DISHES=API_ROOT+"getDishes";
    public final static String API_RESTAURANT_RATING=API_ROOT+"getRestaurantRating";
    public final static String API_RESTAURANT_REVIEW=API_ROOT+"giveRestaurantReview";
    public final static String API_RESTAURANT_REVIEWS=API_ROOT+"getRestaurantReview";
    public final static String API_DISH_REVIEWS=API_ROOT+"getDishReview";
    public final static String API_DISH_REVIEW=API_ROOT+"giveDishReview";
    public final static String API_DISH_RATING=API_ROOT+"getDishRating";
}
