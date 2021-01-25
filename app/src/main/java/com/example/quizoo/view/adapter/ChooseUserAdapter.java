package com.example.quizoo.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizoo.R;
import com.example.quizoo.model.entity.User;
import com.example.quizoo.view.fragment.AdminEditUserFragment;
import com.example.quizoo.viewmodel.ViewModelActivity;

import java.util.List;

public class ChooseUserAdapter  extends RecyclerView.Adapter<ChooseUserAdapter.ViewHolder>  {

    private List<User> userList;
    private OnUserClickListener listener;


    public ChooseUserAdapter(List<User> userList, Activity activity, OnUserClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ChooseUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_choose, parent, false);
        ViewHolder holder = new ViewHolder(vista);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvUserName.setText(userList.get(position).getName());
        holder.imgUser.setImageResource(userList.get(position).getAvatar());

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(userList.get(position));
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
            tvUserName = itemView.findViewById(R.id.tvRecyclerUserName);
            imgUser = itemView.findViewById(R.id.imgGridUser);
            constraintLayout = itemView.findViewById(R.id.constraintgriditem);
        }
    }
}
