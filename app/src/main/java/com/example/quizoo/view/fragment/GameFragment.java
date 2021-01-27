package com.example.quizoo.view.fragment;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.quizoo.R;
import com.example.quizoo.rest.pojo.Card;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static android.text.Html.FROM_HTML_MODE_LEGACY;


public class GameFragment extends Fragment {

    private ViewModelActivity viewModel;

    private ArrayList<Card> cards;

    public GameFragment() {
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
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);

        cards = viewModel.getLiveCards().getValue();


        if(cards == null || cards.size() == 0){
            Toast.makeText(getActivity(), "NO HAY", Toast.LENGTH_SHORT).show();
        }


        FloatingActionButton btHlep = view.findViewById(R.id.btHelp);
        btHlep.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                instruccionesDialog(savedInstanceState);
            }
        });

        ImageView imgIrAPerfil = view.findViewById(R.id.imgIrAPerfil);
        imgIrAPerfil.setImageResource(viewModel.getCurrentUser().getAvatar());
        imgIrAPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(GameFragment.this)
                        .navigate(R.id.action_gameFragment_to_perfilFragment);
            }
        });










        //Este método carga en MutableLiveData las cartas.
        //viewModel.loadCardsForGame();







    }





    @RequiresApi(api = Build.VERSION_CODES.N)
    public Dialog instruccionesDialog(Bundle savedInstanceState) {

        String text = getString(R.string.instrucciones);
        Spanned styledText = Html.fromHtml(text, FROM_HTML_MODE_LEGACY);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(styledText);
        builder.show();
        return builder.create();
    }


}