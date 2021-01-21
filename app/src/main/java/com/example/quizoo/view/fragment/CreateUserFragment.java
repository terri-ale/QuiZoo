package com.example.quizoo.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.quizoo.R;
import com.example.quizoo.model.entity.User;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.textfield.TextInputLayout;


public class CreateUserFragment extends Fragment {

    private ViewModelActivity viewModel;


    public CreateUserFragment() {
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
        return inflater.inflate(R.layout.fragment_create_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imgUser = view.findViewById(R.id.imgUserAdd);
        TextInputLayout tiUserCreation = view.findViewById(R.id.tiUserCreation);

        ImageView imgS1= view.findViewById(R.id.imgS1);
        imgS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUser.setImageResource(R.drawable.icon_anciano);
            }
        });
        ImageView imgS2= view.findViewById(R.id.imgS2);
        imgS2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUser.setImageResource(R.drawable.icon_bruja);
            }
        });
        ImageView imgS3= view.findViewById(R.id.imgS3);
        imgS3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUser.setImageResource(R.drawable.icon_caballero);
            }
        });
        ImageView imgS4= view.findViewById(R.id.imgS4);
        imgS4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUser.setImageResource(R.drawable.icon_mago);
            }
        });
        ImageView imgS5= view.findViewById(R.id.imgS5);
        imgS5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUser.setImageResource(R.drawable.icon_nerd);
            }
        });
        ImageView imgS6= view.findViewById(R.id.imgS6);
        imgS6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUser.setImageResource(R.drawable.icon_robin);
            }
        });
        ImageView imgS7= view.findViewById(R.id.imgS7);
        imgS7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUser.setImageResource(R.drawable.icon_vampire);
            }
        });
        ImageView imgS8= view.findViewById(R.id.imgS8);
        imgS8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgUser.setImageResource(R.drawable.icon_viking);
            }
        });

        Button btAddUser = view.findViewById(R.id.btAddUser);
        btAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    User user = new User(tiUserCreation.getEditText().getText().toString(), imgUser.getId(), 0,0);

                    Log.v("xyzyx", user.toString()) ;
                    viewModel.insert(user);

                }catch (Exception e){
                    Log.v("xyzyx", e.getMessage());
                }

            }
        });
    }
/*
    public void onClickImg(View view){
        ImageView imgUser = view.findViewById(R.id.imgUserAdd);
        ImageView imgS4= view.findViewById(R.id.imgS4);
        switch ( view.getId() ){
            case R.id.imgS4:
                imgUser.setImageResource(R.drawable.icon_anciano);
                break;
        }
    }
*/

}