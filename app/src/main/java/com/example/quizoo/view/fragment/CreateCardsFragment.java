package com.example.quizoo.view.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.quizoo.R;
import com.example.quizoo.model.Repository;
import com.example.quizoo.rest.pojo.Card;
import com.example.quizoo.rest.pojo.DBResponse;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.textfield.TextInputLayout;

import static android.app.Activity.RESULT_OK;


public class CreateCardsFragment extends Fragment implements Observer<DBResponse> {

    private static final int INTENT_IMAGE_CODE = 100;
    private Uri imageUri = null;

    private ViewModelActivity viewModel;

    ProgressDialog progressDialog;


    public CreateCardsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_cards, container, false);



    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getLiveResponse().observe(getViewLifecycleOwner(), this);
        setUI();
    }

    private void setUI(){
        getView().findViewById(R.id.btChoosePicture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


        getView().findViewById(R.id.btCreateUpdateCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAddCard();
            }
        });
    }



    private void attemptAddCard(){
        TextInputLayout tiName = getView().findViewById(R.id.tiCardName);
        String name = tiName.getEditText().getText().toString();
        TextInputLayout tiDescription = getView().findViewById(R.id.tiCardDescription);
        String description = tiDescription.getEditText().getText().toString();

        if(name.isEmpty() || description.isEmpty()){
            Toast.makeText(getContext(), getContext().getString(R.string.warning_empty_fields), Toast.LENGTH_SHORT).show();
        }else{

            progressDialog = ProgressDialog.show(getContext(), getContext().getString(R.string.string_adding_card), "", true, false);
            //viewModel.getLiveResponse().removeObservers(getActivity());

            /*
            viewModel.getLiveResponse().observe(this, new Observer<DBResponse>() {
                @Override
                public void onChanged(DBResponse dbResponse) {
                    progressDialog.dismiss();
                    if(dbResponse == null || dbResponse.getResult() == false){
                        Toast.makeText(getContext(), getContext().getString(R.string.warning_card_not_added), Toast.LENGTH_SHORT).show();

                    }else if(dbResponse.getResult() == true){
                        NavHostFragment.findNavController(CreateCardsFragment.this).popBackStack();
                        Toast.makeText(getContext(), getContext().getString(R.string.message_card_added_successfully), Toast.LENGTH_SHORT).show();
                    }
                }
            });


             */


            Card card = new Card(name, description);

            viewModel.addCard(imageUri, card);

        }
    }



    @Override
    public void onChanged(DBResponse dbResponse) {
        progressDialog.dismiss();
        if(dbResponse == null || dbResponse.getResult() == false){
            Toast.makeText(getContext(), getContext().getString(R.string.warning_card_not_added), Toast.LENGTH_SHORT).show();

        }else if(dbResponse.getResult() == true){
            NavHostFragment.findNavController(CreateCardsFragment.this).popBackStack();
            Toast.makeText(getContext(), getContext().getString(R.string.message_card_added_successfully), Toast.LENGTH_SHORT).show();
        }
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
            ImageView ivPreview = getView().findViewById(R.id.imgCreateUpdateCard);
            ivPreview.setImageURI(imageUri);


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