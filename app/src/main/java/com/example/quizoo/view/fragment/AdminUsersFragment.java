package com.example.quizoo.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quizoo.R;
import com.example.quizoo.model.entity.User;
import com.example.quizoo.view.adapter.AdminUsersAdapter;
import com.example.quizoo.viewmodel.ViewModelActivity;

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

        viewModel.getLiveUserList().observe(getActivity(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                user.clear();
                user.addAll(users);
                adapter.notifyDataSetChanged();
            }
        });
    }
}