package com.example.quizoo.view.fragment;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quizoo.R;
import com.example.quizoo.model.Repository;
import com.example.quizoo.model.entity.User;
import com.example.quizoo.rest.pojo.Card;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

import static android.text.Html.FROM_HTML_MODE_LEGACY;


public class GameFragment extends Fragment {
    ConstraintLayout card ;
    private ViewModelActivity viewModel;

    private ArrayList<Card> gameCards;

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

        TextView tvScore = view.findViewById(R.id.tvGamePuntos);
        ImageView ivProfile = view.findViewById(R.id.imgIrAPerfil);
        ivProfile.setImageResource(viewModel.getCurrentUser().getAvatar());
         card = view.findViewById(R.id.CardLayout);
         card.setVisibility(View.INVISIBLE);



        ConstraintLayout beginScreen = view.findViewById(R.id.constraintStart);

        beginScreen.setVisibility(View.VISIBLE);

        beginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginScreen.setVisibility(View.GONE);
                animacionCarta(view);
            }
        });




        //SE OBTIENE UN LIVE USER Y SE MUESTRA EN TIEMPO REAL SU PUNTUACION
        viewModel.getLiveUser(viewModel.getCurrentUser().getId()).observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                tvScore.setText(String.valueOf(user.getNumResponsesCorrect() * Repository.SCORE_MULTIPLIER));
            }
        });

        //CUANDO EL USUARIO ACIERTE UNA PREGUNTA, SE SUMA EN LA BASE DE DATOS CON:
        //viewModel.sumUserScore(viewModel.getCurrentUser().getId());



        //ESTE MÉTODO SE DEBE LLAMAR DESDE EL BOTÓN DE JUGAR.
     //   attemptLoadGame();





        FloatingActionButton btHelp = view.findViewById(R.id.btHelp);
        btHelp.setOnClickListener(new View.OnClickListener() {
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
                        .navigate(R.id.perfilFragment);
            }
        });












    }



    private void attemptLoadGame(){

        ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Cargando", "Estamos cargando las cartas");

        viewModel.getLiveCards().observe(getActivity(), new Observer<ArrayList<Card>>() {
            @Override
            public void onChanged(ArrayList<Card> cards) {
                progressDialog.dismiss();

                if(cards == null){
                    //MOSTRAR ERROR DE QUE NO SE HAN PODIDO RECUPERAR LAS CARTAS (CONEXIÓN A INTERNET)
                }else if(cards.size() == 0){
                    //MOSTRAR ERROR DE QUE NO HAY CARTAS AÑADIDAS TODAVÍA
                }else{
                    gameCards = cards;
                    Log.v("xyzyx CARGADAS", cards.toString());
                    gameLoop();
                }


            }
        });


        //Este método carga en MutableLiveData las cartas.
        viewModel.loadCardsForGame();

    }




    private void gameLoop(){

        Log.v("xyzyx", "ANIMANDO");
       // animacionCarta(getView());

        Log.v("xyzyx", "ANIMANDO 2");


        for (int i = 0; i < gameCards.size(); i++) {




            //for (int j = 0; j < gameCards.get(i).getQuestions().size(); j++) {
            //
            //}
        }
    }






    @RequiresApi(api = Build.VERSION_CODES.N)
    public Dialog instruccionesDialog(Bundle savedInstanceState) {

        String text = getString(R.string.instructions_game);
        Spanned styledText = Html.fromHtml(text, FROM_HTML_MODE_LEGACY);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(styledText);
        builder.show();
        return builder.create();
    }


    public void animacionCarta(View v){

         card = v.findViewById(R.id.CardLayout);
        TextView tvCount = v.findViewById(R.id.tvCuentaAtras);
        card.setY(2000f);
        card.setVisibility(View.VISIBLE);


        float width;
        float height;

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {  // > API 12
            Point size = new Point();
            display.getSize(size);

            width = size.x;
            height = size.y;
        } else {
            width   = display.getWidth();
            height  = display.getHeight();
        }
        card.setX(width/7);
        card.setY(height);
        ObjectAnimator animation = ObjectAnimator.ofFloat(card, "translationY", height/30f);
        animation.setDuration(2000);
        animation.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
              //Contador 10 secs (+1 por delay)
                tvCount.setVisibility(View.VISIBLE);
                CountDownTimer countDownTimer = new CountDownTimer(11000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        tvCount.setText(String.format(Locale.getDefault(), "%d", millisUntilFinished / 1000L));

                    }

                    public void onFinish() {
                        tvCount.setVisibility(View.GONE);
                        ObjectAnimator animation2 = ObjectAnimator.ofFloat(card, "translationX", -width);
                        animation2.setDuration(2000);
                        animation2.start();

                    }
                }.start();


            }
        }, 2000);

    }

    public void initGame(View v){





    }




}