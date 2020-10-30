package com.example.restaurantreview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Register_Screen extends AppCompatActivity {

    private EditText et_reg_email;
    private EditText et_reg_password;
    private EditText et_reg_fname;
    private EditText et_reg_lname;
    private EditText et_reg_phone;
    private Button btn_register;
    private TextView error_register;
    private RequestQueue requestQueue;
    private StringRequest objectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__screen);

        et_reg_email=findViewById(R.id.et_reg_email);
        et_reg_password=findViewById(R.id.et_reg_password);
        et_reg_fname=findViewById(R.id.et_reg_fname);
        et_reg_lname=findViewById(R.id.et_reg_lname);
        et_reg_phone=findViewById(R.id.et_reg_phone);
        btn_register=findViewById(R.id.btn_reg_register);
        error_register=findViewById(R.id.tv_reg_error);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    public void register(){
        error_register.setVisibility(View.INVISIBLE);
        if(validateInputs(et_reg_email,et_reg_password,et_reg_phone,et_reg_fname,et_reg_lname)){
            requestQueue= Volley.newRequestQueue(Register_Screen.this);

            objectRequest=new StringRequest(
                    Request.Method.POST,
                    Api.API_REGISTER,
                    new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            error_register.setVisibility(View.INVISIBLE);
                            //Log.e("Register_Response", response);
                            try{
                                JSONObject reg_obj=new JSONObject(response);
                                HashMap<String, Object> reg_status = new Gson().fromJson(reg_obj.toString(), HashMap.class);

                                if(reg_status.get("reg").equals("success")){
                                    //Successfully Register
                                    finish();
                                }
                                else if(reg_status.get("reg").equals("fail")){
                                    //Registration Failed
                                    error_register.setText(reg_status.get("error").toString());
                                    error_register.setVisibility(View.VISIBLE);
                                }
                            }
                            catch(Throwable t){
                                Log.e("JSON Parse Error","JSON malformed.");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Log.e("Register_Error", error.toString());
                            error_register.setText("Unable To Connect. Please\n Check Your Network Connection.");
                            error_register.setVisibility(View.VISIBLE);
                        }
                    }
            )
            {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username",et_reg_email.getText().toString());
                    params.put("password",et_reg_password.getText().toString());
                    params.put("tel_no",et_reg_phone.getText().toString());
                    params.put("f_name",et_reg_fname.getText().toString());
                    params.put("l_name",et_reg_lname.getText().toString());
                    return params;
                }
            };

            requestQueue.add(objectRequest);
        }
    }

    private boolean validateInputs(EditText et_email,EditText et_password,EditText et_phone,EditText et_fname,EditText et_lname){
        boolean valid_email=false,valid_pw=false,valid_phone=false,valid_fname=false,valid_lname=false;
        valid_email=validateEmail(et_email);
        valid_pw=validatePassword(et_password);
        valid_phone=validateTelephone(et_phone);
        valid_fname=validateFName(et_fname);
        valid_lname=validateLName(et_lname);
        return valid_email&&valid_pw&&valid_phone&&valid_fname&&valid_lname;
    }
    private boolean validateEmail(EditText et_email){
        Pattern email_pattern= Patterns.EMAIL_ADDRESS;
        if(et_email.getText().toString().isEmpty()){
            et_email.setError("E-mail Cannot Be Empty.");
            return false;
        }
        else if(!email_pattern.matcher(et_email.getText().toString()).matches()){
            et_email.setError("Invalid E-mail.");
            return false;
        }

        return true;
    }
    private boolean validatePassword(EditText et_password){
        Pattern pw_pattern=Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}");
        if(et_password.getText().toString().isEmpty()){
            et_password.setError("Password Cannot Be Empty.");
            return false;
        }
        else if(!pw_pattern.matcher(et_password.getText().toString()).matches()){
            et_password.setError("Password Requirement:\n1 Uppercase Letter\n1 Lowercase Letter\n1 Digit\nMinimum 8 Character");
            return false;
        }

        return true;
    }
    private boolean validateTelephone(EditText et_phone){
        Pattern phone_pattern=Pattern.compile("^[0-9]{10,10}$");
        if(et_phone.getText().toString().isEmpty()){
            et_phone.setError("Telephone Number Cannot Be Empty.");
            return false;
        }
        else if(!phone_pattern.matcher(et_phone.getText().toString()).matches()){
            et_phone.setError("Invalid Telephone Number.");
            return false;
        }

        return true;
    }
    private boolean validateFName(EditText et_fname){
        Pattern fname_pattern=Pattern.compile("^[a-zA-Z\\s]+$");
        if(et_fname.getText().toString().isEmpty()){
            et_fname.setError("First Name Cannot Be Empty.");
            return false;
        }
        else if(!fname_pattern.matcher(et_fname.getText().toString()).matches()){
            et_fname.setError("First Name Must Only Contain Letters.");
            return false;
        }

        return true;
    }
    private boolean validateLName(EditText et_lname){
        Pattern lname_pattern=Pattern.compile("^[a-zA-Z\\s]+$");
        if(et_lname.getText().toString().isEmpty()){
            et_lname.setError("Last Name Cannot Be Empty.");
            return false;
        }
        else if(!lname_pattern.matcher(et_lname.getText().toString()).matches()){
            et_lname.setError("Last Name Must Only Contain Letters.");
            return false;
        }

        return true;
    }
}
