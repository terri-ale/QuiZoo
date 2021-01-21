package com.example.quizoo.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quizoo.R;
import com.example.quizoo.model.entity.User;
import com.example.quizoo.view.adapter.AdminUsersAdapter;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class AdminUsersFragment extends Fragment {

    private ViewModelActivity viewModel;
    private List<User> user = new ArrayList<>();

    public AdminUsersFragment() {
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
        return inflater.inflate(R.layout.fragment_admin_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);

        RecyclerView adminUser = view.findViewById(R.id.recyclerCreateUser);
        AdminUsersAdapter adapter = new AdminUsersAdapter(user, getActivity());

        adminUser.setAdapter(adapter);
        adminUser.setLayoutManager(new LinearLayoutManager(getContext()));
        ConstraintLayout noUsers = view.findViewById(R.id.noUsersAdvise);

        viewModel.getLiveUserList().observe(getActivity(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                user.clear();
                user.addAll(users);
                adapter.notifyDataSetChanged();

                if(user.size()==0){
                    noUsers.setVisibility(View.VISIBLE);
                }else{
                    noUsers.setVisibility(View.INVISIBLE);
                }

            }
        });



        FloatingActionButton ftbAddUser = view.findViewById(R.id.ftbAddUser);
        ftbAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AdminUsersFragment.this)
                        .navigate(R.id.createUserFragment);
            }
        });
    }
}