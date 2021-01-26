package com.example.quizoo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.quizoo.view.fragment.AdminCardsFragment;
import com.example.quizoo.view.fragment.AdminUsersFragment;
import com.example.quizoo.view.fragment.PerfilAdminFragment;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class NavAdminFragment extends Fragment {

    private ViewModelActivity viewModel;


    public NavAdminFragment() {
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
        return inflater.inflate(R.layout.fragment_nav_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);

        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.Inicio);
        viewModel.setNavigationView(bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new PerfilAdminFragment()).commit();
        }


    }

    public BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.Inicio:
                            selectedFragment = new PerfilAdminFragment();

                            break;
                        case R.id.Usuario:
                            selectedFragment = new AdminUsersFragment();

                            break;
                        case R.id.Carta:
                            selectedFragment = new AdminCardsFragment();
                            break;
                    }
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

}