package com.example.restaurantreview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder> {
    private ArrayList<Review> mReviews=new ArrayList<>();
    private Context context;

    public class ReviewViewHolder extends RecyclerView.ViewHolder{
        private Context context;
        private TextView tv_review_name;
        private TextView tv_review_review;
        private TextView tv_review_date;
        private RatingBar rb_review_rating;

        public ReviewViewHolder(View itemView){
            super(itemView);
            context=itemView.getContext();
            tv_review_name=itemView.findViewById(R.id.tv_review_name);
            tv_review_review=itemView.findViewById(R.id.tv_review_review);
            tv_review_date=itemView.findViewById(R.id.tv_review_date);
            rb_review_rating=itemView.findViewById(R.id.rb_review_rating);
        }

        public void bindReview(Review review){
            tv_review_name.setText(review.getName());
            tv_review_review.setText(review.getReview());

            DateFormat format= android.text.format.DateFormat.getDateFormat(context);

            tv_review_date.setText(format.format(review.getDate_added()));
            rb_review_rating.setRating(review.getRating());
        }
    }

    public ReviewListAdapter(Context context,ArrayList<Review> reviews){
        this.context=context;
        this.mReviews.addAll(reviews);
    }

    public ReviewListAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item,parent,false);
        ReviewViewHolder viewHolder=new ReviewViewHolder(view);
        return viewHolder;
    }

    public void updateReviews(ArrayList<Review> reviews){
         mReviews.clear();
         mReviews.addAll(reviews);
         this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder,int position){
        holder.bindReview(mReviews.get(position));
    }

    @Override
    public int getItemCount(){
        return mReviews.size();
    }
}
