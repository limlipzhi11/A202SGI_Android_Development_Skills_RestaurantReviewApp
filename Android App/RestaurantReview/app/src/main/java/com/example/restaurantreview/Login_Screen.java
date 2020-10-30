package com.example.restaurantreview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login_Screen extends AppCompatActivity {

    private EditText et_email;
    private EditText et_password;
    private Button btn_login;
    private Button btn_register;
    private RequestQueue requestQueue;
    private TextView error_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__screen);

        et_email=findViewById(R.id.et_email);
        et_password=findViewById(R.id.et_password);
        btn_login=findViewById(R.id.btn_login);
        btn_register=findViewById(R.id.btn_register);
        error_msg=findViewById(R.id.login_error);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    public boolean isInputEmpty(){
        return et_email.getText().toString().isEmpty() || et_password.getText().toString().isEmpty();
    }

    public void login(){
        if(!isInputEmpty()){
            requestQueue= Volley.newRequestQueue(Login_Screen.this);

            StringRequest objectRequest=new StringRequest(
                    Request.Method.POST,
                    Api.API_LOGIN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            error_msg.setVisibility(View.INVISIBLE);
                            //Log.e("Login_Response", response);
                            try{
                                JSONObject logged_user=new JSONObject(response);
                                HashMap<String, Object> user_data = new Gson().fromJson(logged_user.toString(), HashMap.class);

                                if(logged_user.getString("auth").equals("success")){
                                    Intent i=new Intent(Login_Screen.this,Restaurant_List_Screen.class);
                                    i.putExtra("user_data",user_data);
                                    startActivity(i);
                                    finish();
                                }
                                else if(logged_user.getString("auth").equals("fail")){
                                    //Authentication Failed
                                    et_email.setError("Incorrect Login Credentials.");
                                    et_password.setError("Incorrect Login Credentials.");
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
                            Log.e("Login_Error", error.toString());
                            error_msg.setText("Please Check Your Network Connection.");
                            error_msg.setVisibility(View.VISIBLE);
                        }
                    }
            )
            {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email",et_email.getText().toString());
                    params.put("password",et_password.getText().toString());
                    return params;
                }
            };

            requestQueue.add(objectRequest);
        }
        else{
            //Credentials Not Filled In
            if(et_email.getText().toString().isEmpty())
                et_email.setError("Email Cannot Be Empty.");
            if(et_password.getText().toString().isEmpty())
                et_password.setError("Password Cannot Be Empty.");
        }
    }

    public void register(){
        Intent i=new Intent(Login_Screen.this,Register_Screen.class);
        startActivity(i);
    }

}
