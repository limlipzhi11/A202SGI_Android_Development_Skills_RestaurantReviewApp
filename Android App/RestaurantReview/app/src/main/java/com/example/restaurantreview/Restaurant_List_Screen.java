package com.example.restaurantreview;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Restaurant_List_Screen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,RestaurantListAdapter.OnRestaurantListener,RestaurantPagerAdapter.OnItemClickListener{

    private ViewPager vp_featured;
    private RestaurantPagerAdapter featured_adapter;
    private ArrayList<Restaurant> featured_list=new ArrayList<>();
    private RequestQueue requestQueue;
    private StringRequest objectRequest;
    private HashMap<String,Object> user_data;
    private ArrayList<Restaurant> mRestaurants=new ArrayList<>();
    private RestaurantListAdapter restaurantListAdapter;
    private RecyclerView rv_restaurants;

    private TextView tv_header_name;
    private TextView tv_header_email;

    private final int REFRESH_CODE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant__list__screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(getResources().getColor(R.color.default_orange));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        user_data=(HashMap<String, Object>) (getIntent().getSerializableExtra("user_data"));
        View headerView=navigationView.getHeaderView(0);
        tv_header_email=headerView.findViewById(R.id.tv_header_email);
        tv_header_name=headerView.findViewById(R.id.tv_header_name);
        tv_header_name.setText(user_data.get("name").toString());
        tv_header_email.setText(user_data.get("email").toString());

        vp_featured=findViewById(R.id.vp_featured);
        rv_restaurants=findViewById(R.id.rv_restaurants);
        getFeatured("INIT");
        getRestaurants("INIT");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.restaurant__list__screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Intent i=new Intent(Restaurant_List_Screen.this,Login_Screen.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.btn_nav_logout) {
            Intent i=new Intent(Restaurant_List_Screen.this,Login_Screen.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getFeatured(final String status_code){
        requestQueue= Volley.newRequestQueue(Restaurant_List_Screen.this);

        objectRequest=new StringRequest(
                Request.Method.POST,
                Api.API_FEATURED,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("Login_Response", response);
                        try{
                            JSONObject featured=new JSONObject(response);

                            if(featured.getString("status").equals("success")){
                                JSONArray restaurants=featured.getJSONArray("restaurants");
                                featured_list.clear();
                                for(int i=0;i<restaurants.length();i++){
                                    JSONObject restaurant=restaurants.getJSONObject(i);
                                    //Log.e("test",restaurant.toString());
                                    String tmp_tag[]=restaurant.getString("tags").split(",");
                                    ArrayList<String> tags=new ArrayList<>();
                                    for(int j=0;j<tmp_tag.length;j++){
                                        if(j!=0)
                                            tags.add(tmp_tag[j]);
                                    }
                                    String tmp_services[]=restaurant.getString("services").split(",");
                                    ArrayList<String> services=new ArrayList<>();
                                    for(int j=0;j<tmp_services.length;j++){
                                        if(j!=0)
                                            services.add(tmp_services[j]);
                                    }
                                    //Log.e("test",restaurant.toString());
                                    featured_list.add(new Restaurant(restaurant.getInt("restaurant_id"),restaurant.getString("name"),restaurant.getString("website"),restaurant.getString("tel_no"),restaurant.getString("address"),restaurant.getString("description"),tags,services,restaurant.getString("thumbnail"),restaurant.getInt("num_review"),restaurant.getInt("rating")));
                                }

                                if(status_code.equals("INIT")){
                                    featured_adapter=new RestaurantPagerAdapter(featured_list,Restaurant_List_Screen.this,Restaurant_List_Screen.this);
                                    vp_featured.setAdapter(featured_adapter);
                                }
                                else if(status_code.equals("REFRESH")){
                                    //Log.e("test",featured_list.size()+"");
                                    featured_adapter.updateFeatured(featured_list);
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
                return params;
            }
        };

        requestQueue.add(objectRequest);
    }

    public void getRestaurants(final String status_code){
        requestQueue= Volley.newRequestQueue(Restaurant_List_Screen.this);

        objectRequest=new StringRequest(
                Request.Method.POST,
                Api.API_RESTAURANTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("Login_Response", response);
                        try{
                            JSONObject featured=new JSONObject(response);

                            if(featured.getString("status").equals("success")){
                                JSONArray restaurants=featured.getJSONArray("restaurants");
                                mRestaurants.clear();
                                for(int i=0;i<restaurants.length();i++){
                                    JSONObject restaurant=restaurants.getJSONObject(i);
                                    //Log.e("test",restaurant.toString());
                                    String tmp_tag[]=restaurant.getString("tags").split(",");
                                    ArrayList<String> tags=new ArrayList<>();
                                    for(int j=0;j<tmp_tag.length;j++){
                                        if(j!=0)
                                            tags.add(tmp_tag[j]);
                                    }
                                    String tmp_services[]=restaurant.getString("services").split(",");
                                    ArrayList<String> services=new ArrayList<>();
                                    for(int j=0;j<tmp_services.length;j++){
                                        if(j!=0)
                                            services.add(tmp_services[j]);
                                    }
                                    //Log.e("test",restaurant.toString());
                                    mRestaurants.add(new Restaurant(restaurant.getInt("restaurant_id"),restaurant.getString("name"),restaurant.getString("website"),restaurant.getString("tel_no"),restaurant.getString("address"),restaurant.getString("description"),tags,services,restaurant.getString("thumbnail"),restaurant.getInt("num_review"),restaurant.getInt("rating")));
                                }

                                if(status_code.equals("INIT")){
                                    restaurantListAdapter=new RestaurantListAdapter(Restaurant_List_Screen.this,mRestaurants,Restaurant_List_Screen.this);
                                    rv_restaurants.setHasFixedSize(true);
                                    RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(Restaurant_List_Screen.this);
                                    rv_restaurants.setLayoutManager(layoutManager);
                                    rv_restaurants.setAdapter(restaurantListAdapter);
                                }
                                else if(status_code.equals("REFRESH")){
                                    //Log.e("test",mRestaurants.size()+"");
                                    restaurantListAdapter.updateRestaurants(mRestaurants);
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
                return params;
            }
        };

        requestQueue.add(objectRequest);
    }

    @Override
    public void onRestaurantClick(int position, Restaurant restaurant) {
        Intent i=new Intent(Restaurant_List_Screen.this,Restaurant_Screen.class);
        HashMap<String,Object> restaurant_data=new HashMap<>();
        restaurant_data.put("name",restaurant.getName());
        restaurant_data.put("restaurant_id",restaurant.getRestaurant_id());
        restaurant_data.put("website",restaurant.getWebsite());
        restaurant_data.put("tel_no",restaurant.getTel_no());
        restaurant_data.put("address",restaurant.getAddress());
        restaurant_data.put("description",restaurant.getDescription());
        restaurant_data.put("tags",restaurant.getTags());
        restaurant_data.put("services",restaurant.getServices());
        restaurant_data.put("thumbnail",restaurant.getThumbnail());
        restaurant_data.put("num_review",restaurant.getNum_review());
        restaurant_data.put("rating",restaurant.getRating());
        i.putExtra("restaurant_data",restaurant_data);
        i.putExtra("user_data",user_data);
        startActivityForResult(i,REFRESH_CODE);
    }

    @Override
    public void onItemClick(int position,Restaurant restaurant) {
        Intent i=new Intent(Restaurant_List_Screen.this,Restaurant_Screen.class);
        HashMap<String,Object> restaurant_data=new HashMap<>();
        restaurant_data.put("name",restaurant.getName());
        restaurant_data.put("restaurant_id",restaurant.getRestaurant_id());
        restaurant_data.put("website",restaurant.getWebsite());
        restaurant_data.put("tel_no",restaurant.getTel_no());
        restaurant_data.put("address",restaurant.getAddress());
        restaurant_data.put("description",restaurant.getDescription());
        restaurant_data.put("tags",restaurant.getTags());
        restaurant_data.put("services",restaurant.getServices());
        restaurant_data.put("thumbnail",restaurant.getThumbnail());
        restaurant_data.put("num_review",restaurant.getNum_review());
        restaurant_data.put("rating",restaurant.getRating());
        i.putExtra("restaurant_data",restaurant_data);
        i.putExtra("user_data",user_data);
        startActivityForResult(i,REFRESH_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==REFRESH_CODE)
        {
            if(resultCode==RESULT_OK){
                getFeatured("REFRESH");
                getRestaurants("REFRESH");
            }
        }
    }
}
