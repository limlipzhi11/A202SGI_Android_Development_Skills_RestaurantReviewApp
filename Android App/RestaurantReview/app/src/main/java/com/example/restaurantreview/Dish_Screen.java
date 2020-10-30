package com.example.restaurantreview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Dish_Screen extends AppCompatActivity {

    private HashMap<String,Object> dish_data;
    private HashMap<String,Object> user_data;
    private RequestQueue requestQueue;
    private StringRequest objectRequest;
    private ArrayList<Review> reviews=new ArrayList<>();
    private ReviewListAdapter adapter;
    private Intent dish_intent;
    private Boolean is_set_result=false;

    private ImageView img_dish_img;
    private TextView tv_dish_name;
    private TextView tv_dish_num_review;
    private RatingBar rb_dish_rating;
    private Button btn_dish_review;
    private RecyclerView rv_dish_reviews;
    private TextView tv_dish_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish__screen);

        dish_intent=new Intent();
        dish_data=(HashMap<String, Object>)(getIntent().getSerializableExtra("dish_data"));
        user_data=(HashMap<String, Object>)(getIntent().getSerializableExtra("user_data"));

        if(dish_data!=null){
            img_dish_img=findViewById(R.id.img_dish_img);
            tv_dish_name=findViewById(R.id.tv_dish_name);
            tv_dish_num_review=findViewById(R.id.tv_dish_num_review);
            btn_dish_review=findViewById(R.id.btn_dish_review);
            rb_dish_rating=findViewById(R.id.rb_dish_rating);
            rv_dish_reviews=findViewById(R.id.rv_dish_reviews);

            LoadImage loadImage=new LoadImage(img_dish_img);
            loadImage.execute(dish_data.get("image").toString());

            tv_dish_name.setText(dish_data.get("name").toString());
            tv_dish_num_review.setText("("+dish_data.get("num_review").toString()+")");
            tv_dish_empty=findViewById(R.id.tv_dish_empty);

            rb_dish_rating.setRating((int) dish_data.get("rating"));

            btn_dish_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reviewDialog("How Was The Dish?");
                }
            });

            getDishReview(dish_data.get("dish_id").toString(),"INIT");
        }
        else{
            setResult(RESULT_CANCELED,dish_intent);
            finish();
        }
    }

    private void reviewDialog(String msg){
        final Dialog dialog=new Dialog(this,R.style.CustomAlertDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.review_dialog);

        TextView tv_dialog_title=dialog.findViewById(R.id.tv_dialog_title);
        final RatingBar rb_review=dialog.findViewById(R.id.rb_review);
        final EditText et_review=dialog.findViewById(R.id.et_review);

        dialog.setCancelable(true);
        dialog.show();

        tv_dialog_title.setText(msg);

        Button btn_ok=dialog.findViewById(R.id.btn_dialog_confirm);
        ImageButton btn_cancel=dialog.findViewById(R.id.btn_dialog_cancel);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rb_review.getRating()!=0&&!et_review.getText().toString().isEmpty()){
                    giveDishReview(dish_data.get("dish_id").toString(),user_data.get("customer_id").toString(),rb_review,et_review);
                    dialog.dismiss();
                }
                else{
                    et_review.setError("Review Cannot Be Empty!");
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void getRating(final String id){
        requestQueue= Volley.newRequestQueue(Dish_Screen.this);

        objectRequest=new StringRequest(
                Request.Method.POST,
                Api.API_DISH_RATING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("Login_Response", response);
                        try{
                            JSONObject featured=new JSONObject(response);

                            if(featured.getString("status").equals("success")){
                                tv_dish_num_review.setText("("+featured.getInt("num_review")+")");
                                rb_dish_rating.setRating(featured.getInt("rating"));
                            }
                        }
                        catch(Throwable t){
                            Log.e("JSON Parse Error",t.getMessage());
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("featured_error", error.toString());
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("dish_id",id);
                return params;
            }
        };

        requestQueue.add(objectRequest);
    }

    private void getDishReview(final String id,final String status_code){
        requestQueue= Volley.newRequestQueue(Dish_Screen.this);

        objectRequest=new StringRequest(
                Request.Method.POST,
                Api.API_DISH_REVIEWS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("Login_Response", response);
                        try{
                            JSONObject featured=new JSONObject(response);

                            if(featured.getString("status").equals("success")){
                                reviews.clear();
                                JSONArray tmp_reviews=featured.getJSONArray("reviews");

                                tv_dish_empty.setVisibility(View.INVISIBLE);
                                if(featured.get("status").toString().equals("success")){
                                    for(int i=0;i<tmp_reviews.length();i++){
                                        JSONObject review=tmp_reviews.getJSONObject(i);
                                        //Log.e("test",review.toString());

                                        reviews.add(new Review(review.getInt("review_id"),review.getString("name"),review.getString("review"),review.getInt("rating"),review.getString("date_added")));
                                    }
                                }
                                else{
                                    tv_dish_empty.setVisibility(View.VISIBLE);
                                }

                                if(status_code.equals("INIT")){
                                    adapter=new ReviewListAdapter(Dish_Screen.this,reviews);
                                    rv_dish_reviews.setHasFixedSize(true);
                                    RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(Dish_Screen.this);
                                    rv_dish_reviews.setLayoutManager(layoutManager);
                                    rv_dish_reviews.setAdapter(adapter);
                                }
                                else if(status_code.equals("REFRESH")){
                                    adapter.updateReviews(reviews);
                                }

                            }
                            else if(featured.getString("status").equals("fail")){

                            }
                        }
                        catch(Throwable t){
                            Log.e("JSON Parse Error",t.getMessage());
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("featured_error", error.toString());
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("dish_id",id);
                return params;
            }
        };

        requestQueue.add(objectRequest);
    }

    private void giveDishReview(final String dish_id,final String customer_id,final RatingBar rating,final EditText review){
        if(rating.getRating()!=0&&!review.getText().toString().isEmpty()){
            requestQueue= Volley.newRequestQueue(Dish_Screen.this);

            objectRequest=new StringRequest(
                    Request.Method.POST,
                    Api.API_DISH_REVIEW,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Log.e("Login_Response", response);
                            try{
                                JSONObject featured=new JSONObject(response);
                                if(featured.getString("status").equals("fail")){
                                    review.setError("Unable to give review!");
                                }
                                else{
                                    getRating(dish_id);

                                    if(reviews.isEmpty()){
                                        getDishReview(dish_id,"INIT");
                                    }
                                    else{
                                        getDishReview(dish_id,"REFRESH");
                                    }

                                    setResult(RESULT_OK,dish_intent);
                                    is_set_result=true;
                                }
                            }
                            catch(Throwable t){
                                Log.e("JSON Parse Error",t.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("featured_error", error.toString());
                        }
                    }
            )
            {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("dish_id",dish_id);
                    params.put("customer_id",customer_id);
                    params.put("rating",rating.getRating()+"");
                    params.put("review",review.getText().toString());
                    return params;
                }
            };

            requestQueue.add(objectRequest);
        }
        else{
            review.setError("Review cannot be empty!");
        }
    }

    @Override
    public void onBackPressed() {
        if(is_set_result){
            finish();
        }
        else{
            setResult(RESULT_CANCELED,dish_intent);
            finish();
        }
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
