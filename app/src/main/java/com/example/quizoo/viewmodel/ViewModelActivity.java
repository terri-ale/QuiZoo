package com.example.quizoo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.quizoo.model.Repository;
import com.example.quizoo.model.entity.Contact;
import com.example.quizoo.model.entity.User;

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

    public void setCurrentUser(User currentUser) {
        repository.setCurrentUser(currentUser);
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
}
