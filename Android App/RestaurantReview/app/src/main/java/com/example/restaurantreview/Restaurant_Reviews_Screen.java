package com.example.restaurantreview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Restaurant_Reviews_Screen extends AppCompatActivity {
    private String restaurant_id;
    private RequestQueue requestQueue;
    private StringRequest objectRequest;
    private ArrayList<Review> reviews=new ArrayList<>();
    private RecyclerView rv_restaurant_reviews;
    private ImageButton btn_review_back;
    private ReviewListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant__reviews__screen);

        restaurant_id=getIntent().getStringExtra("restaurant_id");

        rv_restaurant_reviews=findViewById(R.id.rv_restaurant_reviews);
        btn_review_back=findViewById(R.id.btn_reviews_back);

        btn_review_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getRestaurantReview(restaurant_id);
    }

    private void getRestaurantReview(final String id){
        requestQueue= Volley.newRequestQueue(Restaurant_Reviews_Screen.this);

        objectRequest=new StringRequest(
                Request.Method.POST,
                Api.API_RESTAURANT_REVIEWS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("Login_Response", response);
                        try{
                            JSONObject featured=new JSONObject(response);

                            if(featured.getString("status").equals("success")){
                                reviews.clear();
                                JSONArray tmp_reviews=featured.getJSONArray("reviews");

                                for(int i=0;i<tmp_reviews.length();i++){
                                    JSONObject review=tmp_reviews.getJSONObject(i);
                                    //Log.e("test",review.toString());

                                    reviews.add(new Review(review.getInt("review_id"),review.getString("name"),review.getString("review"),review.getInt("rating"),review.getString("date_added")));
                                }
                                adapter=new ReviewListAdapter(Restaurant_Reviews_Screen.this,reviews);
                                rv_restaurant_reviews.setHasFixedSize(true);
                                RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(Restaurant_Reviews_Screen.this);
                                rv_restaurant_reviews.setLayoutManager(layoutManager);
                                rv_restaurant_reviews.setAdapter(adapter);
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
                params.put("restaurant_id",id);
                return params;
            }
        };

        requestQueue.add(objectRequest);
    }
}
