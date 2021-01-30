package com.example.quizoo.view.fragment;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.quizoo.R;
import com.example.quizoo.model.Repository;
import com.example.quizoo.rest.pojo.DBResponse;
import com.example.quizoo.util.OnDBResponseListener;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.textfield.TextInputLayout;

import static android.app.Activity.RESULT_OK;


public class EditCardFragment extends Fragment implements View.OnClickListener, OnDBResponseListener {

    private static final int INTENT_IMAGE_CODE = 100;

    private ViewModelActivity viewModel;

    private Context context;

    private Uri imageUri = null;

    private TextInputLayout tiCardName;
    private TextInputLayout tiCardDescription;


    private Button btCreateUpdateCard;
    private ImageView btDeleteCard;




    private ProgressDialog progressDialog;



    public EditCardFragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //progressDialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_card, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);

        context = getContext();

        tiCardName = view.findViewById(R.id.tiCardName);
        tiCardDescription = view.findViewById(R.id.tiCardDescription);
        btCreateUpdateCard = view.findViewById(R.id.btCreateUpdateCard);
        btDeleteCard = view.findViewById(R.id.imgDeleteCard);


        setUI();
    }


    private void setUI(){
        tiCardName.getEditText().setText(viewModel.getCurrentCard().getName());
        tiCardDescription.getEditText().setText(viewModel.getCurrentCard().getDescription());

        btCreateUpdateCard.setOnClickListener(this);
        btDeleteCard.setOnClickListener(this);

        getView().findViewById(R.id.btBackFromCreateEditCard).setOnClickListener(this);

        viewModel.setResponseListener(this);

        getView().findViewById(R.id.btChoosePicture).setOnClickListener(this);

    }



    private void attemptEditCard(){
        //As soon as the user presses the button, the ClickListener is removed so it can't be pressed
        //twice, avoiding errors or duplicates at the DB.
        btCreateUpdateCard.setOnClickListener(null);

        String name = tiCardName.getEditText().getText().toString();
        String description = tiCardDescription.getEditText().getText().toString();


        if(name.isEmpty() || description.isEmpty()){
            Toast.makeText(getContext(), getContext().getString(R.string.warning_empty_fields), Toast.LENGTH_SHORT).show();
        }else{
            progressDialog = ProgressDialog.show(getContext(), getContext().getString(R.string.string_updating_card), "", true, false);
            viewModel.getCurrentCard().setName(name);
            viewModel.getCurrentCard().setDescription(description);


            viewModel.updateCard(imageUri, viewModel.getCurrentCard());
        }
    }



    private void attemptDeleteCard() {
        btDeleteCard.setOnClickListener(null);

        progressDialog = ProgressDialog.show(getContext(), getContext().getString(R.string.string_deleting_card), "", true, false);

        viewModel.deleteCard(viewModel.getCurrentCard());
    }



    @Override
    public void onSuccess(DBResponse response) {
        progressDialog.dismiss();
        Toast.makeText(getContext(), getContext().getString(R.string.message_changes_successfully_applied), Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(EditCardFragment.this).popBackStack();
    }

    @Override
    public void onFailed() {
        progressDialog.dismiss();
        btCreateUpdateCard.setOnClickListener(EditCardFragment.this);
        Toast.makeText(getContext(), getContext().getString(R.string.warning_changes_not_applied), Toast.LENGTH_SHORT).show();
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btBackFromCreateEditCard:
                NavHostFragment.findNavController(EditCardFragment.this).popBackStack();
                break;

            case R.id.btCreateUpdateCard:
                attemptEditCard();
                break;

            case R.id.imgDeleteCard:
                attemptDeleteCard();
                break;

            case R.id.btChoosePicture:
                chooseImage();
                break;
        }
    }




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
            ImageView ivPreview = getView().findViewById(R.id.imgPreview);
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