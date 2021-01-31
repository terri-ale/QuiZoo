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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.quizoo.R;
import com.example.quizoo.model.Repository;
import com.example.quizoo.rest.pojo.Card;
import com.example.quizoo.rest.pojo.DBResponse;
import com.example.quizoo.util.OnDBResponseListener;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.textfield.TextInputLayout;

import static android.app.Activity.RESULT_OK;


public class CreateCardsFragment extends Fragment implements View.OnClickListener, OnDBResponseListener{

    private static final int INTENT_IMAGE_CODE = 100;
    private Uri imageUri = null;

    private ViewModelActivity viewModel;

    ProgressDialog progressDialog;

    Button btCreateUpdateCard;


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

        btCreateUpdateCard = getView().findViewById(R.id.btCreateUpdateCard);

        getView().findViewById(R.id.btChoosePicture).setOnClickListener(this);
        btCreateUpdateCard.setOnClickListener(this);
        viewModel.setResponseListener(this);

        ImageButton btAtrasDesdeCrearCarta = view.findViewById(R.id.btAtrasDesdeCrearCarta);
        btAtrasDesdeCrearCarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(CreateCardsFragment.this).popBackStack();
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
            /* Cuando el usuario pulsa el click del boton, se elimina el listener, para evitar errores de pulsacion doble*/
            btCreateUpdateCard.setOnClickListener(null);

            progressDialog = ProgressDialog.show(getContext(), getContext().getString(R.string.string_adding_card), "", true, false);

            Card card = new Card(name, description);

            viewModel.addCard(imageUri, card);
        }
    }


    @Override
    public void onSuccess(DBResponse response) {
        progressDialog.dismiss();
        NavHostFragment.findNavController(CreateCardsFragment.this).popBackStack();
        Toast.makeText(getContext(), getContext().getString(R.string.message_card_added_successfully), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed() {
        progressDialog.dismiss();
        btCreateUpdateCard.setOnClickListener(CreateCardsFragment.this);
        Toast.makeText(getContext(), getContext().getString(R.string.warning_card_not_added), Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btBackFromCreateEditCard:
                NavHostFragment.findNavController(CreateCardsFragment.this).popBackStack();
                break;

            case R.id.btCreateUpdateCard:
                attemptAddCard();
                break;

            case R.id.btChoosePicture:
                chooseImage();
                break;
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
            ImageView imgPreview = getView().findViewById(R.id.imgPreview);
            imgPreview.setImageURI(imageUri);


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