package com.example.quizoo.view.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quizoo.R;
import com.example.quizoo.model.Repository;
import com.example.quizoo.viewmodel.ViewModelActivity;

import static android.app.Activity.RESULT_OK;


public class CreateCardsFragment extends Fragment {

    private static final int INTENT_IMAGE_CODE = 100;
    private Uri imageUri = null;

    private ViewModelActivity viewModel;


    public CreateCardsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_cards, container, false);
    }



    //LLAMAR A ESTE MÉTODO DESDE EL BOTÓN DE SELECCIONAR IMAGEN
    private void chooseImage(){
        if(viewModel.storagePermissionIsGranted()) {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, INTENT_IMAGE_CODE);
        }else{
            requestStoragePermission();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == INTENT_IMAGE_CODE){
            imageUri = data.getData();
            //PONER EL IMAGEVIEW DE PREVISUALIZACION CON LA URI



        }
    }

    private void requestStoragePermission() {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) { return; }
        requestPermissions(Repository.STORAGE_PERMISSION, Repository.STORAGE_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int grantedCounter=0;
        switch (requestCode){
            case Repository.STORAGE_PERMISSION_CODE:
                for(int result : grantResults){
                    if(result== PackageManager.PERMISSION_GRANTED) grantedCounter++;
                }
                break;
        }
        if(grantedCounter==permissions.length){ chooseImage(); }
    }


}