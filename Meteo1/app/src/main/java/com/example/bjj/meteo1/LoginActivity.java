package com.example.bjj.meteo1;

/**
 * Created by jj123 on 29/03/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //Defining views
    private EditText editTextEmail;
    private EditText editTextPassword;
    private AppCompatButton buttonLogin;

    //boolean variable to check user is logged in or not
    //initially it is false
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonLogin = (AppCompatButton) findViewById(R.id.buttonLogin);

        //Adding click listener
        buttonLogin.setOnClickListener(this);
    }

    private void login(){
        //Getting values from edit texts
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        if(response.equalsIgnoreCase(Config.LOGIN_SUCCESS)){

                            //Starting profile activity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }else{
                            //If the server response is not success
                            //Displaying an error message on toast
                            Toast.makeText(LoginActivity.this, "Identifiant ou mot de passe incorrecte", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put(Config.KEY_EMAIL, email);
                params.put(Config.KEY_PASSWORD, password);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        //Calling the login function
        login();
    }
}
