package com.tanginan.www.sikatuna_parish;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

public class CurrentUser{
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    SharedPreferences.Editor editor;


    public CurrentUser(Context context){
        sharedpreferences = context.getSharedPreferences(mypreference, context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    public String getEmail(){
        return getDataFromSharedPreferences("email");
    }

    public String getName(){
        return getDataFromSharedPreferences("name");
    }

    public String getUsername(){
        return getDataFromSharedPreferences("username");
    }

    public String getUserId(){
        return getDataFromSharedPreferences("user_id");
    }

    public String getType(){
        return getDataFromSharedPreferences("type");
    }

    public String getAccessToken(){
        return getDataFromSharedPreferences("access_token");
    }

    public String getUserPhoto(){
        return getDataFromSharedPreferences("photo");
    }

    private String getDataFromSharedPreferences(String dataField){
        if (sharedpreferences.contains(dataField)) {
            return sharedpreferences.getString(dataField, "");
        }else{
            return "";
        }
    }

    public void setDataToSharedPreferences(String dataField, String value){
        editor.putString(dataField, value);
        editor.commit();
    }

}
