package com.example.quizoo.view.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.SensorAdditionalInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.quizoo.R;
import com.example.quizoo.model.Repository;
import com.example.quizoo.model.entity.User;
import com.example.quizoo.rest.pojo.Card;
import com.example.quizoo.rest.pojo.Question;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.text.Html.FROM_HTML_MODE_LEGACY;


public class GameFragment extends Fragment {
    ConstraintLayout card;
    ConstraintLayout pregunta1;
    ConstraintLayout pregunta2;

    int checked1;
    int checked2;

    int indiceCarta=0;
    int indicePregunta=0;

    CountDownTimer countDownTimer;
    private ViewModelActivity viewModel;

    ConstraintLayout beginScreen;
    TextView tvInstructions;

    private ArrayList<Card> gameCards;

    TextView tvCount;
    Display display;

    public GameFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);
        viewModel.getLiveCards().removeObservers((AppCompatActivity) getContext());


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


        TextView tvScore = view.findViewById(R.id.tvGamePuntos);

        display = getActivity().getWindowManager().getDefaultDisplay();
        tvCount = view.findViewById(R.id.tvCuentaAtras);
        tvInstructions = view.findViewById(R.id.tvInstructionsBeforeGame);
        ImageView ivProfile = view.findViewById(R.id.imgIrAPerfil);
        ivProfile.setImageResource(viewModel.getCurrentUser().getAvatar());
         card = view.findViewById(R.id.CardLayout);
         card.setVisibility(View.GONE);

         pregunta1 = view.findViewById(R.id.questionLayout1);
         pregunta1.setVisibility(View.GONE);
         pregunta2 = view.findViewById(R.id.questionLayout2);
         pregunta2.setVisibility(View.GONE);

        float width;
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        pregunta1.setX(width);
        pregunta2.setX(width);


        beginScreen = view.findViewById(R.id.constraintStart);

        beginScreen.setVisibility(View.VISIBLE);

        beginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indiceCarta=0;
                indicePregunta=0;
                attemptLoadGame(v);
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
                countDownTimer.cancel();
                NavHostFragment.findNavController(GameFragment.this)
                        .navigate(R.id.perfilFragment);
            }
        });

    }



    private void attemptLoadGame(View v){

        ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Cargando", "Estamos cargando las cartas");

        viewModel.getLiveCards().observe((AppCompatActivity) getContext(), new Observer<ArrayList<Card>>() {
            @Override
            public void onChanged(ArrayList<Card> cards) {
                progressDialog.dismiss();

                if(cards == null){
                    //MOSTRAR ERROR DE QUE NO SE HAN PODIDO RECUPERAR LAS CARTAS (CONEXIÓN A INTERNET)
                    tvInstructions.setText(getContext().getString(R.string.warning_cards_not_retrieved));
                }else if(cards.size() == 0){
                    //MOSTRAR ERROR DE QUE NO HAY CARTAS AÑADIDAS TODAVÍA
                    tvInstructions.setText(getContext().getString(R.string.warning_cards_not_retrieved));
                }else{
                    gameCards = cards;
                    beginScreen.setVisibility(View.GONE);

                    pruebaJuego();
                }

                viewModel.getLiveCards().removeObservers((AppCompatActivity) getContext());

            }

        });



        //Este método carga en MutableLiveData las cartas.
        viewModel.loadCardsForGame();

    }






    public void pruebaJuego(){



        float width;
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        final Handler handler = new Handler();
        ObjectAnimator animation1 = ObjectAnimator.ofFloat(pregunta1, "translationX", (width/2f)-(width/2f));
        animation1.setDuration(2000);

        ObjectAnimator animation2 = ObjectAnimator.ofFloat(pregunta2, "translationX", (width/2f)-(width/2f));
        animation2.setDuration(2000);

        ObjectAnimator animation3 = ObjectAnimator.ofFloat(pregunta1, "translationX", -width);
        animation2.setDuration(4000);

        ObjectAnimator animation4 = ObjectAnimator.ofFloat(pregunta2, "translationX", -width);
        animation2.setDuration(4000);


        pregunta1.setVisibility(View.VISIBLE);
        pregunta2.setVisibility(View.VISIBLE);


        RadioGroup rg1 =getActivity().findViewById(R.id.radiogroup1);
        RadioGroup rg2 =getActivity().findViewById(R.id.radiogroup2);

        RadioButton ra1 = getActivity().findViewById(R.id.respuestaA1);
        RadioButton ra2 = getActivity().findViewById(R.id.respuestaA2);
        RadioButton rb1 = getActivity().findViewById(R.id.respuestaB1);
        RadioButton rb2 = getActivity().findViewById(R.id.respuestaB2);
        RadioButton rc1 = getActivity().findViewById(R.id.respuestaC1);
        RadioButton rc2 = getActivity().findViewById(R.id.respuestaC2);
        RadioButton rd1 = getActivity().findViewById(R.id.respuestaD1);
        RadioButton rd2 = getActivity().findViewById(R.id.respuestaD2);

        TextView textoPregunta1 = getActivity().findViewById(R.id.tvPregunta1);
        TextView textoPregunta2 = getActivity().findViewById(R.id.tvPregunta2);

        Button btSiguiente = getActivity().findViewById(R.id.btSiguienteP);
        btSiguiente.setBackgroundColor(Color.GRAY);

        if(indicePregunta==gameCards.get(indiceCarta).getQuestions().size()) {
            indicePregunta = 0;
            indiceCarta++;
        }

              if (indicePregunta == 0) {

                animacionCarta();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btSiguiente.setEnabled(false);

                        textoPregunta2.setText(gameCards.get(indiceCarta).getQuestions().get(0).getText());
                        vuelcaDatosPregunta();
                        animation2.start();


                    }
                }, 14000);
            }

            if (indicePregunta % 2 == 0 && indicePregunta!=0) {
                textoPregunta2.setText(gameCards.get(indiceCarta).getQuestions().get(indicePregunta).getText());
                vuelcaDatosPregunta();
                animation2.start();

            } else if (indicePregunta % 2 != 0) {
                textoPregunta1.setText(gameCards.get(indiceCarta).getQuestions().get(indicePregunta).getText());
                vuelcaDatosPregunta();
                animation1.start();

            }


            // LISTENERS ANIMADORES

            animation1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    btSiguiente.setEnabled(true);

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            animation2.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            animation3.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    pregunta1.setX(width);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            animation4.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    pregunta2.setX(width);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });



            //LISTENER BOTON

            btSiguiente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        btSiguiente.setEnabled(false);
                        btSiguiente.setBackgroundColor(Color.GRAY);

                        if(indicePregunta%2==0 || indicePregunta==0){

                             RadioButton r = rg2.findViewById(checked2);
                            int idx = rg2.indexOfChild(r);

                             if(gameCards.get(indiceCarta).getQuestions().get(indicePregunta).checkAnswer(r.getText().toString())){

                                 RadioButton radio = (RadioButton) rg2.getChildAt(idx);
                                 radio.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.respuestacorrecta));
                                 viewModel.sumUserScore(viewModel.getCurrentUser().getId());


                             }else{

                                 RadioButton radio = (RadioButton) rg2.getChildAt(idx);
                                 radio.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.respuestafallida));


                             }
                            animation4.start();
                            indicePregunta++;
                            pruebaJuego();


                        }else if (indicePregunta%2 != 0){

                            RadioButton r = rg1.findViewById(checked1);
                            int idx = rg1.indexOfChild(r);

                            if(gameCards.get(indiceCarta).getQuestions().get(indicePregunta).checkAnswer(r.getText().toString())){

                                RadioButton radio = (RadioButton) rg1.getChildAt(idx);
                                radio.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.respuestacorrecta));
                                viewModel.sumUserScore(viewModel.getCurrentUser().getId());


                            }else{

                                RadioButton radio = (RadioButton) rg1.getChildAt(idx);
                                radio.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.respuestafallida));


                            }
                            animation3.start();

                            indicePregunta++;
                            pruebaJuego();


                        }

                }
            });

            //LISTENERS RADIOS
            rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    btSiguiente.setBackgroundColor(Color.WHITE);
                    btSiguiente.setEnabled(true);
                    checked1 = rg1.getCheckedRadioButtonId();


                    if(ra1.getId()==checkedId){
                                ra1.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.respuestamarcada));
                    }else{
                        ra1.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.radiobutton));


                    }

                    if(rb1.getId()==checkedId){
                        rb1.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.respuestamarcada));
                    }else{
                        rb1.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.radiobutton));

                    }

                    if(rc1.getId()==checkedId){
                        rc1.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.respuestamarcada));
                    }else{
                        rc1.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.radiobutton));

                    }

                    if(rd1.getId()==checkedId){
                        rd1.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.respuestamarcada));
                    }else{
                        rd1.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.radiobutton));

                    }




                    Log.v("idradio", String.valueOf(checked1));
                }
            });

            rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    btSiguiente.setEnabled(true);
                    checked2= rg2.getCheckedRadioButtonId();
                    btSiguiente.setBackgroundColor(Color.WHITE);


                    if(ra2.getId()==checkedId){
                        ra2.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.respuestamarcada));
                    }else{
                        ra2.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.radiobutton));


                    }

                    if(rb2.getId()==checkedId){
                        rb2.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.respuestamarcada));
                    }else{
                        rb2.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.radiobutton));

                    }

                    if(rc2.getId()==checkedId){
                        rc2.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.respuestamarcada));
                    }else{
                        rc2.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.radiobutton));

                    }

                    if(rd2.getId()==checkedId){
                        rd2.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.respuestamarcada));
                    }else{
                        rd2.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.radiobutton));

                    }
                }
            });




















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


    public void animacionCarta(){
        vuelcaDatosCarta();
        Button btSiguiente = getActivity().findViewById(R.id.btSiguienteP);
        btSiguiente.setVisibility(View.GONE);

        card.setVisibility(View.VISIBLE);

        float width;
        float height;

            Point size = new Point();
            display.getSize(size);

            width = size.x;
            height = size.y;

        card.setX((width/2f)-(width/2f));

        card.setY(height);
        float halfCard = (height/2f);

        ObjectAnimator animation = ObjectAnimator.ofFloat(card, "translationY", (height/2f)-halfCard);
        animation.setDuration(2000);
        animation.start();



        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
              //Contador 10 secs (+1 por delay)
                tvCount.setVisibility(View.VISIBLE);
                 countDownTimer = new CountDownTimer(11000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        tvCount.setText(String.format(Locale.getDefault(), "%d", millisUntilFinished / 1000L));


                    }

                    public void onFinish() {
                        btSiguiente.setVisibility(View.VISIBLE);
                        tvCount.setVisibility(View.GONE);
                        ObjectAnimator animation2 = ObjectAnimator.ofFloat(card, "translationX", -width);
                        animation2.setDuration(2000);
                        animation2.start();
                    }
                }.start();


            }
        }, 2000);

    }


    public void vuelcaDatosCarta(){

        TextView tvAnimal = getActivity().findViewById(R.id.tvAnimal);
        TextView tvDescripcion = getActivity().findViewById(R.id.tvDescripcionCard);
        ImageView imgAnimal = getActivity().findViewById(R.id.imgAnimalCard);

        tvAnimal.setText(gameCards.get(indiceCarta).getName());
        tvDescripcion.setText(gameCards.get(indiceCarta).getDescription());
        Glide.with(getActivity()).load(gameCards.get(indiceCarta).getPictureUrl()).into(imgAnimal);

    }

    public void vuelcaDatosPregunta(){

            List<String> opciones;
        opciones =  gameCards.get(indiceCarta).getQuestions().get(indicePregunta).getShuffledAnswers();
        Log.v("opciones",opciones.toString());
        RadioButton ra1 = getActivity().findViewById(R.id.respuestaA1);
        RadioButton ra2 = getActivity().findViewById(R.id.respuestaA2);
        RadioButton rb1 = getActivity().findViewById(R.id.respuestaB1);
        RadioButton rb2 = getActivity().findViewById(R.id.respuestaB2);
        RadioButton rc1 = getActivity().findViewById(R.id.respuestaC1);
        RadioButton rc2 = getActivity().findViewById(R.id.respuestaC2);
        RadioButton rd1 = getActivity().findViewById(R.id.respuestaD1);
        RadioButton rd2 = getActivity().findViewById(R.id.respuestaD2);

        ra1.setVisibility(View.VISIBLE);
        rb1.setVisibility(View.VISIBLE);
        rc1.setVisibility(View.VISIBLE);
        rd1.setVisibility(View.VISIBLE);

        ra2.setVisibility(View.VISIBLE);
        rb2.setVisibility(View.VISIBLE);
        rc2.setVisibility(View.VISIBLE);
        rd2.setVisibility(View.VISIBLE);

        if(indicePregunta == 0 || indicePregunta%2==0){

            if(opciones.size()==2){

                ra2.setText(opciones.get(0));
                rb2.setText(opciones.get(1));
                rc2.setVisibility(View.GONE);
                rd2.setVisibility(View.GONE);


            }else if(opciones.size()==3){
                ra2.setText(opciones.get(0));
                rb2.setText(opciones.get(1));
                rc2.setText(opciones.get(2));
                rd2.setVisibility(View.GONE);


            }else if(opciones.size()==4){

                ra2.setText(opciones.get(0));
                rb2.setText(opciones.get(1));
                rc2.setText(opciones.get(2));
                rd2.setText(opciones.get(3));

            }


        }else{

            if(opciones.size()==2){

                ra1.setText(opciones.get(0));
                rb1.setText(opciones.get(1));
                rc1.setVisibility(View.GONE);
                rd1.setVisibility(View.GONE);


            }else if(opciones.size()==3){
                ra1.setText(opciones.get(0));
                rb1.setText(opciones.get(1));
                rc1.setText(opciones.get(2));
                rd1.setVisibility(View.GONE);


            }else if(opciones.size()==4){

                ra1.setText(opciones.get(0));
                rb1.setText(opciones.get(1));
                rc1.setText(opciones.get(2));
                rd1.setText(opciones.get(3));

            }

        }





    }



}