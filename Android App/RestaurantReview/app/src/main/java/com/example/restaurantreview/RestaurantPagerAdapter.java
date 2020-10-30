package com.example.restaurantreview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

//import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class RestaurantPagerAdapter extends PagerAdapter{

    private ArrayList<Restaurant> mRestaurant_list=new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public RestaurantPagerAdapter(ArrayList<Restaurant> restaurants,Context context,OnItemClickListener onItemClickListener){
        this.mRestaurant_list.addAll(restaurants);
        this.context=context;
        this.onItemClickListener=onItemClickListener;
    }

    @Override
    public int getCount() {
        return mRestaurant_list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        String tags="";
        layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.featured_item,container,false);

        ImageView img_featured=view.findViewById(R.id.img_featured);
        TextView tv_featured_name=view.findViewById(R.id.tv_featured_name);
        TextView tv_featured_tag=view.findViewById(R.id.tv_featured_tag);
        RatingBar rb_featured_rating=view.findViewById(R.id.rb_featured_rating);
        TextView tv_featured_num_review=view.findViewById(R.id.tv_featured_num_review);
        CardView cv_featured=view.findViewById(R.id.cv_featured);

        //Picasso.with(context).load(mRestaurant_list.get(position).getThumbnail()).into(img_featured);
        LoadImage loadImage=new LoadImage(img_featured);
        loadImage.execute(mRestaurant_list.get(position).getThumbnail());

        tv_featured_name.setText(mRestaurant_list.get(position).getName());

        for(int i=0;i<mRestaurant_list.get(position).getTags().size();i++){
            if(i!=0)
                tags+=", "+mRestaurant_list.get(position).getTags().get(i);
            else
                tags+=mRestaurant_list.get(position).getTags().get(i);
        }

        tv_featured_tag.setText(tags);
        rb_featured_rating.setRating(mRestaurant_list.get(position).getRating());
        String num_review="("+mRestaurant_list.get(position).getNum_review()+")";
        tv_featured_num_review.setText(num_review);

        cv_featured.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(position,mRestaurant_list.get(position));
            }
        });
        container.addView(view,0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public void updateFeatured(ArrayList<Restaurant> restaurants){
        //Log.e("test2",restaurants.size()+"");
        mRestaurant_list.clear();
        mRestaurant_list.addAll(restaurants);
        //Log.e("test3",mRestaurant_list.size()+"");
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public interface OnItemClickListener{
        void onItemClick(int position,Restaurant restaurant);
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
