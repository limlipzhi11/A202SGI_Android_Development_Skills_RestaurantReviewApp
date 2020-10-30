package com.example.restaurantreview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder> {
    private ArrayList<Restaurant> mRestaurant=new ArrayList<>();
    private Context context;
    private OnRestaurantListener onRestaurantListener;

    public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private TextView tv_restaurant_name;
        private TextView tv_restaurant_num_review;
        private TextView tv_restaurant_tags;
        private ImageView img_restaurant_thumbnail;
        private RatingBar rb_restaurant_rating;
        OnRestaurantListener onRestaurantListener;

        public RestaurantViewHolder(View itemView,OnRestaurantListener onRestaurantListener){
            super(itemView);
            context=itemView.getContext();
            tv_restaurant_name=itemView.findViewById(R.id.tv_restaurant_name);
            tv_restaurant_num_review=itemView.findViewById(R.id.tv_restaurant_num_review);
            tv_restaurant_tags=itemView.findViewById(R.id.tv_restaurant_tags);
            img_restaurant_thumbnail=itemView.findViewById(R.id.img_restaurant_thumbnail);
            rb_restaurant_rating=itemView.findViewById(R.id.rb_restaurant_rating);

            this.onRestaurantListener=onRestaurantListener;
            itemView.setOnClickListener(this);
        }

        public void bindRestaurant(Restaurant restaurant){
            tv_restaurant_name.setText(restaurant.getName());
            String tags="";
            for(int i=0;i<restaurant.getTags().size();i++){
                if(i!=0)
                    tags+=","+restaurant.getTags().get(i);
                else
                    tags+=restaurant.getTags().get(i);
            }
            tv_restaurant_tags.setText(tags);
            tv_restaurant_num_review.setText("("+restaurant.getNum_review()+")");

            LoadImage loadImage=new LoadImage(img_restaurant_thumbnail);
            loadImage.execute(restaurant.getThumbnail());

            rb_restaurant_rating.setRating(restaurant.getRating());
        }

        @Override
        public void onClick(View view){
            onRestaurantListener.onRestaurantClick(getAdapterPosition(),mRestaurant.get(getAdapterPosition()));
        }
    }

    public RestaurantListAdapter(Context context,ArrayList<Restaurant> restaurants,OnRestaurantListener onRestaurantListener){
        this.context=context;
        mRestaurant.addAll(restaurants);
        this.onRestaurantListener=onRestaurantListener;
    }

    public RestaurantListAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item,parent,false);
        RestaurantViewHolder viewHolder=new RestaurantViewHolder(view,this.onRestaurantListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantViewHolder holder,int position){
        holder.bindRestaurant(mRestaurant.get(position));
    }

    @Override
    public int getItemCount(){
        return mRestaurant.size();
    }

    public interface OnRestaurantListener{
        void onRestaurantClick(int position,Restaurant restaurant);
    }

    public void updateRestaurants(ArrayList<Restaurant> restaurants){
        //Log.e("test2",restaurants.size()+"");
        mRestaurant.clear();
        mRestaurant.addAll(restaurants);
        //Log.e("test3",mRestaurant.size()+"");
        this.notifyDataSetChanged();
    }

    private class LoadImage extends AsyncTask<String,Void, Bitmap> {
        ImageView imageView;

        public LoadImage(ImageView img){
            this.imageView=img;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url=strings[0];
            Bitmap bitmap=null;

            try {
                InputStream inputStream=new java.net.URL(url).openStream();
                bitmap= BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
