package com.example.quizoo.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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

import java.util.List;


public class CreateUserFragment extends Fragment implements View.OnClickListener{

    private ViewModelActivity viewModel;
    private int currentImage = R.drawable.icon_anciano;


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
        viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);

        viewModel.getLiveUserList().observe(getActivity(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                Log.v("xyzyx", users.toString());
            }
        });



        ImageView imgUser = view.findViewById(R.id.imgUserAdd);
        TextInputLayout tiUserCreation = view.findViewById(R.id.tiUserCreation);

        view.findViewById(R.id.imgS1).setOnClickListener(this);
        view.findViewById(R.id.imgS2).setOnClickListener(this);
        view.findViewById(R.id.imgS3).setOnClickListener(this);
        view.findViewById(R.id.imgS4).setOnClickListener(this);
        view.findViewById(R.id.imgS5).setOnClickListener(this);
        view.findViewById(R.id.imgS6).setOnClickListener(this);
        view.findViewById(R.id.imgS7).setOnClickListener(this);
        view.findViewById(R.id.imgS8).setOnClickListener(this);

        /*
        ImageView imgS1= view.findViewById(R.id.imgS1);
        imgS1.setOnClickListener(this);
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
         */

        Button btAddUser = view.findViewById(R.id.btAddUser);
        btAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    User user = new User(tiUserCreation.getEditText().getText().toString(), currentImage, 0,0);

                    Log.v("xyzyx", user.toString()) ;
                    viewModel.insert(user);


                }catch (Exception e){
                    Log.v("xyzyx", e.getMessage());
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        ImageView imgUser = getView().findViewById(R.id.imgUserAdd);
        switch (v.getId()){
            case R.id.imgS1:
                currentImage = R.drawable.icon_anciano;
                break;
            case R.id.imgS2:
                currentImage = R.drawable.icon_bruja;
                break;
            case R.id.imgS3:
                currentImage = R.drawable.icon_caballero;
                break;
            case R.id.imgS4:
                currentImage = R.drawable.icon_mago;
                break;
            case R.id.imgS5:
                currentImage = R.drawable.icon_nerd;
                break;
            case R.id.imgS6:
                currentImage = R.drawable.icon_robin;
                break;
            case R.id.imgS7:
                currentImage = R.drawable.icon_vampire;
                break;
            case R.id.imgS8:
                currentImage = R.drawable.icon_viking;
                break;
        }
        imgUser.setImageResource(currentImage);
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