package com.example.quizoo.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.quizoo.R;
import com.example.quizoo.model.entity.User;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;


public class CreateUserFragment extends Fragment implements View.OnClickListener {

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
        ImageButton btBack = view.findViewById(R.id.btBackFromUserCreation);
        viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);

        viewModel.getLiveUserList().observe(getActivity(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                Log.v("xyzyx", users.toString());
            }
        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(CreateUserFragment.this).popBackStack();
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

        Button btAddUser = view.findViewById(R.id.btAddUser);
        btAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (tiUserCreation.getEditText().getText().toString().contains(" ")) {
                        Snackbar.make(view, getString(R.string.espacio), Snackbar.LENGTH_SHORT)
                                .show();
                    } else {

                        if (tiUserCreation.getEditText().getText().toString().equals("")) {
                            Snackbar.make(view, getString(R.string.vacio), Snackbar.LENGTH_SHORT)
                                    .show();

                        } else {
                            User user = new User(tiUserCreation.getEditText().getText().toString(), currentImage, 0, 0);

                            Log.v("xyzyx", user.toString());
                            viewModel.insert(user);
                            simpleSnackbar(v);
                            NavHostFragment.findNavController(CreateUserFragment.this).popBackStack();
                        }
                    }


                } catch (Exception e) {
                    Log.v("xyzyx", e.getMessage());
                }

            }
        });
    }

    public void simpleSnackbar(View view) {
        Snackbar.make(view, getString(R.string.creado), Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onClick(View v) {
        ImageView imgUser = getView().findViewById(R.id.imgUserAdd);
        switch (v.getId()) {
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