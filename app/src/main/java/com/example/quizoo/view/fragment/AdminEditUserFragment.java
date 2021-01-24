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
import android.widget.ImageView;

import com.example.quizoo.R;
import com.example.quizoo.model.entity.User;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class AdminEditUserFragment extends Fragment implements View.OnClickListener {

    private ViewModelActivity viewModel;
    private int currentImage = R.drawable.icon_anciano;

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

        viewModel.getLiveUserList().observe(getActivity(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                Log.v("xyzyx", users.toString());
            }
        });

        TextInputLayout tiUserEdit = view.findViewById(R.id.tiUserEdit);
        ImageView imgEditUser = view.findViewById(R.id.imgUserAdd);

        String name = viewModel.getCurrentUser().getName();
        int img = viewModel.getCurrentUser().getAvatar();

        view.findViewById(R.id.imgS1).setOnClickListener(this);
        view.findViewById(R.id.imgS2).setOnClickListener(this);
        view.findViewById(R.id.imgS3).setOnClickListener(this);
        view.findViewById(R.id.imgS4).setOnClickListener(this);
        view.findViewById(R.id.imgS5).setOnClickListener(this);
        view.findViewById(R.id.imgS6).setOnClickListener(this);
        view.findViewById(R.id.imgS7).setOnClickListener(this);
        view.findViewById(R.id.imgS8).setOnClickListener(this);

        tiUserEdit.getEditText().setText(name);
        imgEditUser.setImageResource(img);

        Button btEditUser = view.findViewById(R.id.btEditUser);
        btEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);
                User user = new User(tiUserEdit.getEditText().getText().toString(), currentImage, 0, 0);
                Log.v("xyzyx", user.toString());
                user.setId(viewModel.getCurrentUser().getId());
                Log.v("xyzyx", user.getId() + "");
                viewModel.update(user);

                NavHostFragment.findNavController(AdminEditUserFragment.this)
                        .navigate(R.id.adminUsersFragment);
            }
        });

        ImageView imgBorrarUser = view.findViewById(R.id.imgBorrarUser);
        imgBorrarUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.delete(viewModel.getCurrentUser().getId());
                NavHostFragment.findNavController(AdminEditUserFragment.this)
                        .navigate(R.id.adminUsersFragment);
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