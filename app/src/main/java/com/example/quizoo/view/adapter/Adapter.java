package com.example.quizoo.view.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizoo.R;
import com.example.quizoo.model.entity.Contact;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private ArrayList<Contact> contact;
    private Context context;
    private Activity activity;

    public Adapter(Context context, Activity activity, ArrayList<Contact> contact) {
        this.contact = contact;
        this.context = context;
        this.activity = activity;
    }



    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
        ViewHolder holder = new ViewHolder(vista);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        holder.tvNameContact.setText(contact.get(position).getName().toString());
        holder.tvEmailContact.setText(contact.get(position).getEmail().toString());

        holder.constraintLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return contact.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameContact;
        TextView tvEmailContact;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameContact = itemView.findViewById(R.id.tvContactName);
            tvEmailContact = itemView.findViewById(R.id.tvContactEmail);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }
}
