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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.quizoo.R;
import com.example.quizoo.model.entity.User;
import com.example.quizoo.view.adapter.AdminUsersAdapter;
import com.example.quizoo.view.adapter.ChooseUserAdapter;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class ChooseUserFragment extends Fragment {

    private ViewModelActivity viewModel;
    private List<User> user = new ArrayList<>();

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
        TextView tvNoUser = view.findViewById(R.id.tvNoUsers);

        viewModel=new ViewModelProvider(getActivity()).get(ViewModelActivity.class);

       ArrayList<User> user = new ArrayList<>();
       User a = new User("Sergio",R.drawable.icon_nerd,0,0);
        User b = new User("Pablo",R.drawable.icon_bruja,0,0);
        User c = new User("Lucas",R.drawable.icon_viking,0,0);
        User d = new User("Paco",R.drawable.icon_robin,0,0);

       user.add(a);
       user.add(b);
        user.add(c);
        user.add(d);


        RecyclerView chooseUser = view.findViewById(R.id.recyclerChoose);
        ChooseUserAdapter adapter = new ChooseUserAdapter(user, getActivity());

        chooseUser.setAdapter(adapter);
        chooseUser.setLayoutManager(new GridLayoutManager(getContext(),2));

        viewModel.getLiveUserList().observe(getActivity(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                user.clear();
                user.addAll(users);
                adapter.notifyDataSetChanged();

                if(user.size()==0){
                    tvNoUser.setVisibility(View.VISIBLE);
                }else{
                    tvNoUser.setVisibility(View.INVISIBLE);
                }

            }
        });


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