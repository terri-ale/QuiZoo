package com.example.quizoo.model;


import android.content.Context;
import android.content.SharedPreferences;

public class Repository {


    public boolean CompruebaSharedPreferences (SharedPreferences preferences) {
        String password = preferences.getString("password", "N/D");
        if (password.equals("N/D")) {
            return true;
        } else {
            return false;
        }
    }

    public void crearSharedPreferences (SharedPreferences preferences, String password) {
        preferences.edit().putString("password", password).apply();

        //SharedPreferences preferences = getSharedPreferences("datosAdmin", Context.MODE_PRIVATE);
    }

}
