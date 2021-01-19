package com.example.quizoo.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quizoo.R;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ChooseUserFragment extends Fragment {

    private ViewModelActivity viewModel;

    public ChooseUserFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_user, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton btIrALogin = view.findViewById(R.id.btIrALogin);
        final NavController navController = Navigation.findNavController(view);

        viewModel=new ViewModelProvider(getActivity()).get(ViewModelActivity.class);


        btIrALogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.adminLoginFragment);
            }
        });
        if(!viewModel.isSetAdminPassword()){
            NavHostFragment.findNavController(ChooseUserFragment.this)
                    .navigate(R.id.action_chooseUserFragment_to_adminLoginFragment);
        }


    }
}