package com.example.restaurantreview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DishListAdapter extends RecyclerView.Adapter<DishListAdapter.DishViewHolder> {
    private ArrayList<Dish> mDishes=new ArrayList<>();
    private Context context;
    private OnDishListener onDishListener;

    public class DishViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Context context;
        private TextView tv_dish_item_name;
        private TextView tv_dish_item_num_review;
        private ImageView img_dish_item;
        private RatingBar rb_dish_item;
        OnDishListener onDishListener;

        public DishViewHolder(View itemView,OnDishListener onDishListener){
            super(itemView);
            context=itemView.getContext();
            tv_dish_item_name=itemView.findViewById(R.id.tv_dish_item_name);
            tv_dish_item_num_review=itemView.findViewById(R.id.tv_dish_item_num_review);
            img_dish_item=itemView.findViewById(R.id.img_dish_item);
            rb_dish_item=itemView.findViewById(R.id.rb_dish_item);

            this.onDishListener=onDishListener;
            itemView.setOnClickListener(this);
        }

        public void bindDish(Dish dish){
            tv_dish_item_name.setText(dish.getName());
            tv_dish_item_num_review.setText("("+dish.getNum_review()+")");

            LoadImage loadImage=new LoadImage(img_dish_item);
            loadImage.execute(dish.getImage());

            rb_dish_item.setRating(dish.getRating());
        }

        @Override
        public void onClick(View view){
            onDishListener.onDishClick(getAdapterPosition(),mDishes.get(getAdapterPosition()));
        }
    }

    public DishListAdapter(Context context,ArrayList<Dish> dishes,OnDishListener onDishListener){
        this.context=context;
        this.mDishes.addAll(dishes);
        this.onDishListener=onDishListener;
    }

    public DishListAdapter.DishViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.dish_item,parent,false);
        DishViewHolder viewHolder=new DishViewHolder(view,this.onDishListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DishViewHolder holder,int position){
        holder.bindDish(mDishes.get(position));
    }

    @Override
    public int getItemCount(){
        return mDishes.size();
    }

    public interface OnDishListener{
        void onDishClick(int position,Dish dish);
    }

    public void updateDishes(ArrayList<Dish> dishes){
        mDishes.clear();
        mDishes.addAll(dishes);
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
