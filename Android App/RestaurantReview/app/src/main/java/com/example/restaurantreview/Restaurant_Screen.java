package com.example.restaurantreview;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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

public class Restaurant_Screen extends AppCompatActivity implements DishListAdapter.OnDishListener{

    private RequestQueue requestQueue;
    private StringRequest objectRequest;
    private HashMap<String,Object> restaurant_data;
    private HashMap<String,Object> user_data;
    private TableLayout tl_rs_services;
    private TextView tv_rs_name;
    private TextView tv_rs_tags;
    private TextView tv_rs_num_review;
    private TextView tv_rs_phone;
    private TextView tv_rs_address;
    private TextView tv_rs_website;
    private TextView tv_rs_description;
    private RatingBar rb_rs_rating;
    private ImageView img_rs_banner;
    private ArrayList<Dish> mDishes;
    private RecyclerView rv_rs_dishes;
    private DishListAdapter adapter;
    private Button btn_rs_review;
    private Button btn_rs_view_review;
    private Intent restaurant_intent;
    private Boolean is_set_result=false;
    private int DISH_REQUEST_CODE=200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant__screen);

        restaurant_data=(HashMap<String, Object>) (getIntent().getSerializableExtra("restaurant_data"));
        user_data=(HashMap<String, Object>)(getIntent().getSerializableExtra("user_data"));
        if(restaurant_data!=null&&!restaurant_data.isEmpty()){
            tl_rs_services=findViewById(R.id.tl_rs_services);
            tv_rs_name=findViewById(R.id.tv_rs_title);
            tv_rs_tags=findViewById(R.id.tv_rs_tag);
            tv_rs_num_review=findViewById(R.id.tv_rs_num_review);
            tv_rs_phone=findViewById(R.id.tv_rs_telno);
            tv_rs_address=findViewById(R.id.tv_rs_address);
            tv_rs_website=findViewById(R.id.tv_rs_website);
            tv_rs_description=findViewById(R.id.tv_rs_description);
            rb_rs_rating=findViewById(R.id.rb_rs_rating);
            img_rs_banner=findViewById(R.id.img_rs_banner);
            rv_rs_dishes=findViewById(R.id.rv_rs_dishes);
            btn_rs_review=findViewById(R.id.btn_rs_review);
            btn_rs_view_review=findViewById(R.id.btn_rs_view_review);
            restaurant_intent=new Intent();

            mDishes=new ArrayList<>();

            ArrayList<String> services=new ArrayList<>((ArrayList<String>)restaurant_data.get("services"));
            buildServiceTable(services);
            initializeDetails();
            getDishes(restaurant_data.get("restaurant_id").toString(),"INIT");

            btn_rs_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reviewDialog("Rate This Restaurant!");
                }
            });

            btn_rs_view_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   Intent i=new Intent(Restaurant_Screen.this,Restaurant_Reviews_Screen.class);
                   i.putExtra("restaurant_id",restaurant_data.get("restaurant_id").toString());
                   startActivity(i);
                }
            });
        }
        else{
            finish();
        }
    }

    private void buildServiceTable(ArrayList<String> services){
        int j=0;
        for(int i=0;i<3&&j<services.size();i++){
            TableRow row=new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            for(;j<services.size();j++){
                TextView tv=new TextView(this);
                tv.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                tv.setGravity(Gravity.LEFT);
                tv.setTextSize(16);
                tv.setText(services.get(j));
                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_services,0,0,0);
                tv.setCompoundDrawablePadding(15);
                tv.setPadding(0,0,50,0);
                row.addView(tv);
            }
            tl_rs_services.addView(row);
        }
    }

    private void initializeDetails(){
        tv_rs_name.setText(restaurant_data.get("name").toString());
        ArrayList<String> tags=new ArrayList<>((ArrayList<String>) restaurant_data.get("tags"));
        String tag="";
        for(int i=0;i<tags.size();i++){
            if(i!=0)
                tag+=", "+tags.get(i);
            else
                tag+=tags.get(i);
        }
        tv_rs_tags.setText(tag);
        tv_rs_num_review.setText("("+restaurant_data.get("num_review").toString()+")");
        if(Integer.parseInt(restaurant_data.get("num_review").toString())==0)
            btn_rs_view_review.setEnabled(false);
        else
            btn_rs_view_review.setEnabled(true);
        rb_rs_rating.setRating((int) restaurant_data.get("rating"));
        tv_rs_phone.setText(restaurant_data.get("tel_no").toString());
        tv_rs_address.setText(restaurant_data.get("address").toString());
        tv_rs_website.setText(restaurant_data.get("website").toString());
        tv_rs_description.setText(restaurant_data.get("description").toString());

        LoadImage loadImage=new LoadImage(img_rs_banner);
        loadImage.execute(restaurant_data.get("thumbnail").toString());

        tv_rs_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:"+tv_rs_phone.getText().toString()));
                if(i.resolveActivity(getPackageManager())!=null){
                    startActivity(i);
                }
            }
        });

        tv_rs_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://"+tv_rs_website.getText().toString()));
                startActivity(i);
            }
        });

        tv_rs_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_VIEW,Uri.parse("geo:0,0?q="+tv_rs_address.getText().toString()));
                i.setPackage("com.google.android.apps.maps");
                startActivity(i);
            }
        });
    }

    private void getDishes(final String id,final String status_code){
        requestQueue= Volley.newRequestQueue(Restaurant_Screen.this);

        objectRequest=new StringRequest(
                Request.Method.POST,
                Api.API_DISHES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("Login_Response", response);
                        try{
                            JSONObject featured=new JSONObject(response);

                            if(featured.getString("status").equals("success")){
                                mDishes.clear();
                                JSONArray tmp_dishes=featured.getJSONArray("dishes");

                                for(int i=0;i<tmp_dishes.length();i++){
                                    JSONObject dish=tmp_dishes.getJSONObject(i);
                                    //Log.e("test",restaurant.toString());

                                    mDishes.add(new Dish(dish.getInt("dish_id"),dish.getInt("restaurant_id"),dish.getString("name"),dish.getString("image"),dish.getInt("rating"),dish.getInt("num_review")));
                                }

                                if(status_code.equals("INIT")){
                                    adapter=new DishListAdapter(Restaurant_Screen.this,mDishes,Restaurant_Screen.this);
                                    rv_rs_dishes.setHasFixedSize(true);
                                    RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(Restaurant_Screen.this,LinearLayoutManager.HORIZONTAL,false);
                                    rv_rs_dishes.setLayoutManager(layoutManager);
                                    rv_rs_dishes.setAdapter(adapter);
                                }
                                else if(status_code.equals("REFRESH")){
                                    adapter.updateDishes(mDishes);
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
                params.put("restaurant_id",id);
                return params;
            }
        };

        requestQueue.add(objectRequest);
    }

    private void getRestaurantRating(final String id){
        requestQueue= Volley.newRequestQueue(Restaurant_Screen.this);

        objectRequest=new StringRequest(
                Request.Method.POST,
                Api.API_RESTAURANT_RATING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("Login_Response", response);
                        try{
                            JSONObject featured=new JSONObject(response);

                            rb_rs_rating.setRating(featured.getInt("rating"));
                            tv_rs_num_review.setText("("+featured.getInt("num_review")+")");

                            if(featured.getInt("num_review")==0)
                                btn_rs_view_review.setEnabled(false);
                            else
                                btn_rs_view_review.setEnabled(true);
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

    private void giveRestaurantReview(final String restaurant_id, final String customer_id,final RatingBar rating,final EditText review){
        if(rating.getRating()!=0&&!review.getText().toString().isEmpty()){
            requestQueue= Volley.newRequestQueue(Restaurant_Screen.this);

            objectRequest=new StringRequest(
                    Request.Method.POST,
                    Api.API_RESTAURANT_REVIEW,
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
                                    getRestaurantRating(restaurant_data.get("restaurant_id").toString());
                                    setResult(RESULT_OK,restaurant_intent);
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
                    params.put("restaurant_id",restaurant_id);
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
                    giveRestaurantReview(restaurant_data.get("restaurant_id").toString(),user_data.get("customer_id").toString(),rb_review,et_review);
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

    @Override
    public void onDishClick(int position, Dish dish) {
        Intent i=new Intent(this,Dish_Screen.class);
        HashMap<String,Object> dish_pkg=new HashMap<>();
        dish_pkg.put("dish_id",dish.getDish_id());
        dish_pkg.put("name",dish.getName());
        dish_pkg.put("image",dish.getImage());
        dish_pkg.put("rating",dish.getRating());
        dish_pkg.put("num_review",dish.getNum_review());
        i.putExtra("dish_data",dish_pkg);
        i.putExtra("user_data",user_data);
        startActivityForResult(i,DISH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==DISH_REQUEST_CODE){
            if(resultCode==RESULT_OK){
                getDishes(restaurant_data.get("restaurant_id").toString(),"REFRESH");
            }
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

    @Override
    public void onBackPressed() {
        if(is_set_result){
            finish();
        }
        else{
            setResult(RESULT_CANCELED,restaurant_intent);
            finish();
        }
    }
}
