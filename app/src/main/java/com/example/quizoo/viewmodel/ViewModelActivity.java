package com.example.quizoo.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quizoo.model.Repository;
import com.example.quizoo.model.entity.Contact;
import com.example.quizoo.model.entity.User;
import com.example.quizoo.rest.pojo.Card;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewModelActivity extends androidx.lifecycle.AndroidViewModel {

    private Repository repository;



    public ViewModelActivity(@NonNull Application application) {
        super(application);
        repository=new Repository(application);
    }


    public boolean isSetAdminPassword() {
        return repository.isSetAdminPassword();
    }

    public boolean checkAdminPassword(String password) {
        return repository.checkAdminPassword(password);
    }

    public void setAdminPassword(String password) {
        repository.setAdminPassword(password);
    }

    public User getCurrentUser() {
        return repository.getCurrentUser();
    }

    public void setCurrentUser(User user) {
        repository.setCurrentUser(user);
    }


    public boolean contactsPermissionIsGranted() {
        return repository.contactsPermissionIsGranted();
    }

    public ArrayList<Contact> getContactsWithMail() {
        return repository.getContactsWithMail();
    }

    public LiveData<List<User>> getLiveUserList(){
        return repository.getLiveUserList();
    }

    public void insert(User user){
        repository.insert(user);
    }

    public void update(User user) {
        repository.update(user);
    }

    public void delete(long id){
        repository.delete(id);
    }

    public void mandarCorreo(String correo, String puntuacion) {
        repository.mandarCorreo(correo, puntuacion);
    }


    public MutableLiveData<Long> getLiveFriendInsertId() {
        return repository.getLiveFriendInsertId();
    }

    public boolean storagePermissionIsGranted() {
        return repository.storagePermissionIsGranted();
    }

    public File getFileFromUri(Uri uri) {
        return repository.getFileFromUri(uri);
    }


    public MutableLiveData<ArrayList<Card>> getLiveCards() {
        return repository.getLiveCards();
    }

    public void loadCards() {
        repository.loadCards();
    }
}
