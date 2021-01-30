package com.example.quizoo.model;


import android.Manifest;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Query;

import com.example.quizoo.model.dao.UserDao;
import com.example.quizoo.model.entity.Contact;
import com.example.quizoo.model.entity.User;
import com.example.quizoo.model.room.UserDatabase;
import com.example.quizoo.rest.client.CardClient;
import com.example.quizoo.rest.client.QuestionClient;
import com.example.quizoo.rest.pojo.Card;
import com.example.quizoo.rest.pojo.DBResponse;
import com.example.quizoo.util.ThreadPool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Repository {

    private Context context;
    private UserDao userDao;

    private final static String REST_URL="https://informatica.ieszaidinvergeles.org:9039/PSP/QuiZoo/public/api/";
    private Retrofit retrofit;
    private CardClient cardClient;
    private QuestionClient questionClient;

    public final static String[] CONTACTS_PERMISSION = { Manifest.permission.READ_CONTACTS };
    public final static String[] STORAGE_PERMISSION = { Manifest.permission.READ_EXTERNAL_STORAGE };
    public final static int CONTACTS_PERMISSION_CODE = 1;
    public final static int STORAGE_PERMISSION_CODE = 2;

    private final static String SHARED_PREFERENCE_NAME = "adminData";
    private final static String SHARED_PREFERENCE_KEY = "password";

    private LiveData<List<User>> liveUserList;
    private MutableLiveData<ArrayList<Card>> liveCards;

    private MutableLiveData<DBResponse> liveResponse;


    public final static int SCORE_MULTIPLIER = 10;

    private User currentUser;

    private MutableLiveData<Long> liveUserInsertId = new MutableLiveData<>();

    public Repository(Context context){
        this.context = context;
        UserDatabase db = UserDatabase.getDb(context);
        userDao = db.getUserDao();
        liveUserList = userDao.getAll();

        liveCards = new MutableLiveData<>();
        liveResponse = new MutableLiveData<>();
        retrofit = new Retrofit.Builder()
                .baseUrl(REST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        cardClient = retrofit.create(CardClient.class);
        questionClient = retrofit.create(QuestionClient.class);
    }

    public LiveData<List<User>> getLiveUserList(){
        return liveUserList;
    }
    public MutableLiveData<Long> getLiveFriendInsertId() {
        return liveUserInsertId;
    }

    public MutableLiveData<ArrayList<Card>> getLiveCards() {
        return liveCards;
    }

    public MutableLiveData<DBResponse> getLiveResponse() { return liveResponse; }



    public LiveData<User> getLiveUser(long id) {
        return userDao.getLiveUser(id);
    }


    public void sumUserScore(long id) {
        ThreadPool.threadExecutorPool.execute(new Runnable() {
            @Override
            public void run() {
                userDao.sumUserScore(id);
            }
        });

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
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) { return true; }
        return context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean storagePermissionIsGranted(){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) { return true; }
        return context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
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
    public void update(User user) {
        ThreadPool.threadExecutorPool.execute(new Runnable() {
            @Override
            public void run() {
                userDao.update(user);
            }
        });
    }

    public void delete(long id){
        ThreadPool.threadExecutorPool.execute(new Runnable() {
            @Override
            public void run() {
                userDao.delete(id);
            }
        });
    }


    public File getFileFromUri(Uri uri){
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return new File(picturePath);
    }

    public void mandarCorreo(String correo, String puntuacion){

        String[] TO = {correo};

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Puntuacion QuiZoo");
        emailIntent.putExtra(Intent.EXTRA_TEXT, puntuacion);

        String title = "Mandar este email con...";

        if(emailIntent.resolveActivity(context.getPackageManager()) != null){
            context.startActivity(Intent.createChooser(emailIntent, title).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }



    public void loadCardsForGame(){
        Call<ArrayList<Card>> call = cardClient.getAllCardsWithQuestions();
        call.enqueue(new Callback<ArrayList<Card>>() {
            @Override
            public void onResponse(Call<ArrayList<Card>> call, Response<ArrayList<Card>> response) {
                liveCards.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Card>> call, Throwable t) {
                liveCards.setValue(null);
            }
        });

    }


    public void loadCards(){
        Call<ArrayList<Card>> call = cardClient.getAllCards();
        call.enqueue(new Callback<ArrayList<Card>>() {
            @Override
            public void onResponse(Call<ArrayList<Card>> call, Response<ArrayList<Card>> response) {
                liveCards.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Card>> call, Throwable t) {
                liveCards.setValue(null);
            }
        });
    }


    public String saveImage(Uri imageUri){
        return "";
    }






    private void addCard(Card card){
        Call<DBResponse> request = cardClient.addCard(card);

        request.enqueue(new Callback<DBResponse>() {
            @Override
            public void onResponse(Call<DBResponse> call, Response<DBResponse> response) {
                liveResponse.setValue(response.body());

            }

            @Override
            public void onFailure(Call<DBResponse> call, Throwable t) {
                liveResponse.setValue(null);
            }
        });
    }



    public void addCard(Uri imageUri, Card card){
        if(imageUri == null){
            addCard(card);
        }else {
            if (storagePermissionIsGranted()) {
                File imageFile = getFileFromUri(imageUri);
                RequestBody requestFile =
                        RequestBody.create(
                                MediaType.parse(context.getContentResolver().getType(imageUri)),
                                imageFile
                        );

                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

                Call<DBResponse> request = cardClient.saveImage(body);

                request.enqueue(new Callback<DBResponse>() {
                    @Override
                    public void onResponse(Call<DBResponse> call, Response<DBResponse> response) {
                        if (response.body() != null && response.body().getResult() == true) {
                            card.setPictureUrl(response.body().getUrl());
                        }
                        addCard(card);
                    }

                    @Override
                    public void onFailure(Call<DBResponse> call, Throwable t) {
                        //Si ha fallado subir la imagen al servidor, se sube sin imagen
                        addCard(card);
                    }

                });
            }
        }

    }
}
