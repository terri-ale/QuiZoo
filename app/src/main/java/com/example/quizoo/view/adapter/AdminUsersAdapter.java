package com.example.quizoo.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizoo.R;
import com.example.quizoo.model.entity.Contact;
import com.example.quizoo.model.entity.User;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersAdapter  extends RecyclerView.Adapter<AdminUsersAdapter.ViewHolder> {

    private List<User> userList;
    private Context context;
    private Activity activity;

    public AdminUsersAdapter(List<User> userList, Activity activity) {
        this.userList = userList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AdminUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_create_user, parent, false);
      ViewHolder holder = new ViewHolder(vista);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUsersAdapter.ViewHolder holder, int position) {
        holder.tvUserName.setText(userList.get(position).getName());
        holder.imgUser.setImageResource(userList.get(position).getAvatar());

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController = Navigation.findNavController(v);
                Bundle bundle = new Bundle();
                bundle.putSerializable("userBundle",userList.get(position));
                navController.navigate(R.id.createUserFragment,bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        ImageView imgUser;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserNameItem);
            imgUser = itemView.findViewById(R.id.imgUserItem);
            constraintLayout = itemView.findViewById(R.id.itemCreateUserLayout);
        }
    }
}
