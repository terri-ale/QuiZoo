package com.example.quizoo.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizoo.R;
import com.example.quizoo.rest.pojo.Card;
import com.example.quizoo.viewmodel.ViewModelActivity;

import java.util.List;

public class AdminCardsAdapter extends RecyclerView.Adapter<AdminCardsAdapter.ViewHolder>{

    private List<Card> cardList;
    private Activity activity;
    private OnCardClickListener listener;


    public AdminCardsAdapter(List<Card> cardList, Activity activity, OnCardClickListener listener){
        this.cardList = cardList;
        this.activity = activity;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_cards, parent, false);
        ViewHolder holder = new ViewHolder(vista);
        //viewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(ViewModelActivity.class);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
                holder.tvAnimalName.setText(cardList.get(position).getName());

        try {
            Glide.with(activity)
                    .load(cardList.get(position).getPictureUrl())
                    .placeholder(R.drawable.glidepordefecto)
                    .into(holder.imgAnimal);
        }catch(Exception e){
            Toast.makeText(activity,"No se ha podido conectar con el servidor, prueba más tarde",Toast.LENGTH_SHORT).show();
        }

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEditCardClick(cardList.get(position));
            }
        });

        holder.btPreguntas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onShowQuestionsClick(cardList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList != null ? cardList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvAnimalName;
        ImageView imgAnimal;
        ImageButton btPreguntas;
        ConstraintLayout constraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAnimalName = itemView.findViewById(R.id.tvAnimalRecycler);
            imgAnimal = itemView.findViewById(R.id.imgAnimalCardRecycler);
            btPreguntas = itemView.findViewById(R.id.btVerPreguntas);
            constraintLayout = itemView.findViewById(R.id.recyclerCards);


        }
    }
}
