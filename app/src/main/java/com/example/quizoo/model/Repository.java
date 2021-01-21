package com.example.quizoo.model;


import android.Manifest;
import android.app.Activity;
import android.content.Context;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.quizoo.MainActivity;
import com.example.quizoo.model.dao.UserDao;
import com.example.quizoo.model.entity.Contact;
import com.example.quizoo.model.entity.User;
import com.example.quizoo.model.room.UserDatabase;
import com.example.quizoo.util.ThreadPool;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;


public class Repository {

    public final static String[] REQUIRED_PERMISSIONS = { Manifest.permission.READ_CONTACTS };
    public final static int PERMISSIONS_CODE = 1;


    private final static String SHARED_PREFERENCE_NAME = "adminData";
    private final static String SHARED_PREFERENCE_KEY = "password";

    private Context context;
    private UserDao userDao;

    private LiveData<List<User>> liveUserList;

    private User currentUser;

    private MutableLiveData<Long> liveUserInsertId = new MutableLiveData<>();

    public Repository(Context context){
        this.context = context;
        UserDatabase db = UserDatabase.getDb(context);
        userDao = db.getUserDao();
        liveUserList = userDao.getAll();
    }

    public LiveData<List<User>> getLiveUserList(){
        return liveUserList;
    }
    public MutableLiveData<Long> getLiveFriendInsertId() {
        return liveUserInsertId;
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


    public void requestContactsPermission(){


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

    public void insert(User user) {
        ThreadPool.threadExecutorPool.execute(new Runnable() {
            @Override
            public void run() {
                userDao.insert(user);
            }
        });
    }

}
