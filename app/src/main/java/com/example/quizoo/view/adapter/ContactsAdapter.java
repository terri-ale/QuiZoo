package com.example.quizoo.view.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizoo.R;
import com.example.quizoo.model.entity.Contact;
import com.example.quizoo.viewmodel.ViewModelActivity;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private ArrayList<Contact> contact;
    private Context context;
    private Activity activity;

    private ViewModelActivity viewModel;

    public ContactsAdapter(Context context, Activity activity, ArrayList<Contact> contact) {
        this.contact = contact;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
        ViewHolder holder = new ViewHolder(vista);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(ViewModelActivity.class);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder holder, int position) {
        holder.tvNameContact.setText(contact.get(position).getName().toString());
        holder.tvEmailContact.setText(contact.get(position).getEmail().toString());

        holder.constraintLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                viewModel.mandarCorreo(holder.tvEmailContact.getText().toString(), "Hola, estoy jugando a un nuevo juego llamado QuiZoo," +
                        " en el cual aprenderás todo sobre animales. Acabo de realizar uno de mis records de puntuación. Descarga y juega QuiZoo con tus amigos." +
                        "Mi puntuación es: " + String.valueOf(viewModel.getCurrentUser().getNumResponsesCorrect() * 10) );
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
