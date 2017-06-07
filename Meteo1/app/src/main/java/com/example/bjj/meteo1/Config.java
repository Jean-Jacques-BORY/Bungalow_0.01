package com.example.bjj.meteo1;

/**
 * Created by jj123 on 29/03/2017.
 */

public class Config {
    //URL to our login.php file
      public static final String LOGIN_URL = "http://172.30.0.230/login.php";
   // public static final String LOGIN_URL = "http://192.168.1.81/login.php";
    //public static final String LOGIN_URL = "http://192.168.42.240/login.php";

    //Keys for email and password as defined in our $_POST['key'] in login.php
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS = "success";

    //Keys for Sharedpreferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "myloginapp";

    //This would be used to store the email of current logged in user
    public static final String EMAIL_SHARED_PREF = "email";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
}
