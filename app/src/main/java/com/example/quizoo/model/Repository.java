package com.example.quizoo.model;


import android.Manifest;
import android.content.Context;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.quizoo.model.dao.UserDao;
import com.example.quizoo.model.entity.Contact;
import com.example.quizoo.model.entity.User;
import com.example.quizoo.model.room.UserDatabase;

import java.util.ArrayList;
import java.util.List;


public class Repository {

    public final static String[] REQUIRED_PERMISSIONS = { Manifest.permission.READ_CONTACTS };


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


    public boolean contactsPermissionIsGranted(){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) return true;
        return context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }



    public ArrayList<Contact>getContactsWithMail(){

        ArrayList<Contact> contacts = new ArrayList<>();

        //MutableLiveData<List<Contact>> cont = new MutableLiveData<>();


        //cont.

        String[] data = new String[]{ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Email.ADDRESS};
        String order = ContactsContract.Data.DISPLAY_NAME + " ASC";
        String selectionEmail = ContactsContract.Data.MIMETYPE + "='" +
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE+ "' AND "
                + ContactsContract.CommonDataKinds.Email.ADDRESS + " IS NOT NULL";



        Cursor cursor =  context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                data,
                selectionEmail,
                null,
                order);

        while(cursor.moveToNext()){
            String name = cursor.getString(0);
            String email = cursor.getString(1);

            Contact contact = new Contact(name, email);
            contacts.add(contact);
        }

        //Log.v("xyzyx", nameContact.toString());
        cursor.close();


        return contacts;
    }




    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }



}
