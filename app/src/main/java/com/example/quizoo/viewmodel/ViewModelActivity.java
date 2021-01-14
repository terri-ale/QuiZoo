package com.example.quizoo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.example.quizoo.model.Repository;
import com.example.quizoo.model.entity.User;

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
}
