package com.example.quizoo.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.quizoo.R;
import com.example.quizoo.model.entity.User;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class AdminEditUserFragment extends Fragment implements View.OnClickListener {

    private ViewModelActivity viewModel;
    private int currentImage;

    public AdminEditUserFragment() {
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
        return inflater.inflate(R.layout.fragment_admin_edit_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);

        //DEPURACION - BORRAR
        viewModel.getLiveUserList().observe(getActivity(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {

            }
        });

        TextInputLayout tiUserEdit = view.findViewById(R.id.tiUserEdit);
        ImageView imgEditUser = view.findViewById(R.id.imgUserAdd);

        String name = viewModel.getCurrentUser().getName();
        currentImage = viewModel.getCurrentUser().getAvatar();

        view.findViewById(R.id.imgS1).setOnClickListener(this);
        view.findViewById(R.id.imgS2).setOnClickListener(this);
        view.findViewById(R.id.imgS3).setOnClickListener(this);
        view.findViewById(R.id.imgS4).setOnClickListener(this);
        view.findViewById(R.id.imgS5).setOnClickListener(this);
        view.findViewById(R.id.imgS6).setOnClickListener(this);
        view.findViewById(R.id.imgS7).setOnClickListener(this);
        view.findViewById(R.id.imgS8).setOnClickListener(this);

        tiUserEdit.getEditText().setText(name);
        imgEditUser.setImageResource(currentImage);

        Button btEditUser = view.findViewById(R.id.btEditUser);
        btEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);

                User user = viewModel.getCurrentUser();
                user.setName(tiUserEdit.getEditText().getText().toString());
                user.setAvatar(currentImage);
                viewModel.update(user);

                NavHostFragment.findNavController(AdminEditUserFragment.this).popBackStack();
            }
        });

        ImageView imgBorrarUser = view.findViewById(R.id.imgBorrarUser);
        imgBorrarUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.delete(viewModel.getCurrentUser().getId());
                NavHostFragment.findNavController(AdminEditUserFragment.this).popBackStack();
            }
        });

       ImageButton imgAtras = view.findViewById(R.id.btBackFromUserCreation);
       imgAtras.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               NavHostFragment.findNavController(AdminEditUserFragment.this).popBackStack();
           }
       });


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
}