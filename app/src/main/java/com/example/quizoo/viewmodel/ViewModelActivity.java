package com.example.quizoo.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quizoo.model.Repository;
import com.example.quizoo.model.entity.Contact;
import com.example.quizoo.model.entity.User;
import com.example.quizoo.rest.pojo.Card;
import com.example.quizoo.rest.pojo.DBResponse;
import com.example.quizoo.util.OnDBResponseListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewModelActivity extends androidx.lifecycle.AndroidViewModel {

    private Repository repository;

    private MutableLiveData<Fragment> currentFragment = new MutableLiveData<>();



    public ViewModelActivity(@NonNull Application application) {
        super(application);
        repository=new Repository(application);
    }

    public MutableLiveData<Fragment> getCurrentFragment(){ return currentFragment; }

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


    public LiveData<User> getLiveUser(long id) {
        return repository.getLiveUser(id);
    }

    public void sumUserScore(long id) {
        repository.sumUserScore(id);
    }

    public boolean contactsPermissionIsGranted() {
        return repository.contactsPermissionIsGranted();
    }

    public ArrayList<Contact> getContactsWithMail() {
        return repository.getContactsWithMail();
    }

    public LiveData<DBResponse> getLiveResponse() {
        return repository.getLiveResponse();
    }


    public void setResponseListener(OnDBResponseListener listener) {
        repository.setResponseListener(listener);
    }

    public void addCard(Uri imageUri, Card card) {
        repository.addCard(imageUri, card);
    }


    public void updateCard(Uri imageUri, Card card) {
        repository.updateCard(imageUri, card);
    }

    public void deleteCard(Card card) {
        repository.deleteCard(card);
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


    public void getQuestionsOf(Card card) {
        repository.getQuestionsOf(card);
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

    public void loadCardsForGame() {
        repository.loadCardsForGame();
    }

    public void loadCards() {
        repository.loadCards();
    }


    public Card getCurrentCard() {
        return repository.getCurrentCard();
    }

    public void setCurrentCard(Card currentCard) {
        repository.setCurrentCard(currentCard);
    }
}
