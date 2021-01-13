package com.example.quizoo.model;


import android.content.Context;

import android.content.SharedPreferences;


public class Repository {

    private Context context;

    public boolean checkSharedPreferences () {
        SharedPreferences preferences = context.getSharedPreferences("datosAdmin", Context.MODE_PRIVATE);;
        String password = preferences.getString("password", "");
        if (password.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public void createSharedPreferences (String password) {
        SharedPreferences preferences = context.getSharedPreferences("datosAdmin", Context.MODE_PRIVATE);;
        preferences.edit().putString("password", password).apply();
    }

}
