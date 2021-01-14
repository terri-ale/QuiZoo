package com.example.quizoo.model;


import android.content.Context;

import android.content.SharedPreferences;

import com.example.quizoo.model.dao.UserDao;
import com.example.quizoo.model.entity.User;
import com.example.quizoo.model.room.UserDatabase;


public class Repository {

    private final static String SHARED_PREFERENCE_NAME = "adminData";
    private final static String SHARED_PREFERENCE_KEY = "password";

    private Context context;
    private UserDao userDao;

    private User currentUser;


    public Repository(Context context){
        this.context = context;
        UserDatabase db = UserDatabase.getDb(context);
        userDao = db.getUserDao();
    }


    public boolean checkAdminPassword(String password){
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String value = preferences.getString(SHARED_PREFERENCE_KEY, "");
        return value.equals(password);
    }

    public boolean isSetAdminPassword() {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String password = preferences.getString(SHARED_PREFERENCE_KEY, "");
        return !password.equals("");
    }

    public void setAdminPassword(String password) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(SHARED_PREFERENCE_KEY, password).apply();
    }


    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
