package com.example.quizoo.view.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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

import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
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
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

/*
 GAME FRAGMENT

 En este fragmento se coordina el apartado de la mecánica y funcionamiento de las animaciones, junto a
 la gestión de respuesta de usuario, y calculo de puntos y porcentaje de acierto.
 */



public class GameFragment extends Fragment implements Observer<ArrayList<Card>>{
    MediaPlayer mpAcierto;
    MediaPlayer mpFallo;
    ConstraintLayout card;
    ConstraintLayout pregunta1;
    ConstraintLayout pregunta2;

    private ProgressDialog progressDialog;


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

    public GameFragment() { }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);
        //viewModel.getLiveCards().removeObservers((AppCompatActivity) getContext());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requestExitConfirmation();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        TextView tvScore = view.findViewById(R.id.tvGamePuntos);

        display = getActivity().getWindowManager().getDefaultDisplay();
        tvCount = view.findViewById(R.id.tvCuentaAtras);
        tvInstructions = view.findViewById(R.id.tvInstructionsBeforeGame);


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

        viewModel.getLiveCards().observe((AppCompatActivity) getContext(), this);


        view.findViewById(R.id.btExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestExitConfirmation();
            }
        });




        //SE OBTIENE UN LIVE USER Y SE MUESTRA EN TIEMPO REAL SU PUNTUACION
        viewModel.getLiveUser(viewModel.getCurrentUser().getId()).observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                tvScore.setText(String.valueOf(user.getNumResponsesCorrect() * Repository.SCORE_MULTIPLIER));
            }
        });



        FloatingActionButton btHelp = view.findViewById(R.id.btHelp);
        btHelp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                instruccionesDialog(savedInstanceState);
            }
        });




    }



    private void attemptLoadGame(View v){

        progressDialog = ProgressDialog.show(getActivity(), "Cargando", "Estamos cargando las cartas");


        //Este método carga en MutableLiveData las cartas.
        viewModel.loadCardsForGame();
    }


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
            viewModel.setSessionCards(cards);

            beginScreen.setVisibility(View.GONE);
            gameLoop();

        }
    }

/*
+ Cada partida funcionará con el mismo ciclo:

1. Se tienen dos indices (carta y pregunta) que tendrán la guía de lo que se muestra en cada momento
2. El método animacionCarta, que hace que la carta con la información de la animal aparezca en pantalla, y se vaya después.
        La carga de los datos de carta se hace a través de otro método que verifica índices para mostrar la información que toque en esa carta

        Las cartas estarán barajadas en un ArrayList, y las preguntas tendrán a su vez las opciones barajadas, para que exista menor riesgo
        de que el usuario se aprenda el orden y el juego pierda dificultad.

3. La animación de las preguntas se basa en un juego de indices pares e impares, teniendo dos constraintLayout exactamente iguales: uno para
las preguntas de índice par y otro para los de impar.

Se podía haber hecho perfectamente con uno, pero pierde dinamismo la animación, por tanto se tenderá a comprobar el índice de pregunta para realizar
las acciones sobre uno u otro layout, aunque suponga mayor trabajo de programación.

Las respuestas correctas se van almacenando en el usuario de forma local, generando la puntuación cuando se multiplica el número de respuestas correctas x2

También se ha aprovechado esto para generar un porcentaje de acierto que es mostrado en perfil de usuario.

Cuando el usuario elige una opción de pregunta y pulsa en el botón comprobar, se suma una unidad al valor del indice de pregunta y se vuelve a llamar al
mismo método gameLoop, volviendo a ejecutar todo el proceso anterior.

Se han añadido sonidos usando la clase MediaPlayer para el acierto y el fallo, además de tener una banda sonora en loop continuo.
*
*/




    public void gameLoop(){

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
        btSiguiente.setEnabled(false);
        if(indicePregunta==gameCards.get(indiceCarta).getQuestions().size()) {
            indicePregunta = 0;
            indiceCarta++;
        }
        if(indiceCarta==gameCards.size()-1){
            indiceCarta=0;
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
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        vuelcaDatosPregunta();

                    }
                }, 200);

                animation2.start();

            } else if (indicePregunta % 2 != 0) {
                textoPregunta1.setText(gameCards.get(indiceCarta).getQuestions().get(indicePregunta).getText());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        vuelcaDatosPregunta();


                    }
                }, 200);
                animation1.start();

            }


            // LISTENERS ANIMADORES
            animation1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    rg1.clearCheck();
                    btSiguiente.setBackgroundColor(Color.GRAY);
                    btSiguiente.setEnabled(false);

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
                   rg2.clearCheck();
                    btSiguiente.setBackgroundColor(Color.GRAY);
                    btSiguiente.setEnabled(false);
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
                        mpAcierto = MediaPlayer.create(getActivity(),R.raw.acierto);
                        mpFallo = MediaPlayer.create(getActivity(),R.raw.fallo);

                        if(indicePregunta%2==0 || indicePregunta==0){

                             RadioButton r = rg2.findViewById(checked2);
                            int idx = rg2.indexOfChild(r);
                            RadioButton radio;
                             if(gameCards.get(indiceCarta).getQuestions().get(indicePregunta).checkAnswer(r.getText().toString())){

                                radio = (RadioButton) rg2.getChildAt(idx);
                                 radio.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.respuestacorrecta));
                                 viewModel.sumUserScore(viewModel.getCurrentUser().getId());
                                 mpAcierto.start();


                             }else{

                                  radio = (RadioButton) rg2.getChildAt(idx);
                                 radio.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.respuestafallida));
                                mpFallo.start();

                             }
                            animation4.start();
                            indicePregunta++;
                            viewModel.sumUserResponses(viewModel.getCurrentUser().getId());
                            gameLoop();


                        }else if (indicePregunta%2 != 0){

                            RadioButton r = rg1.findViewById(checked1);
                            int idx = rg1.indexOfChild(r);
                            RadioButton radio;
                            if(gameCards.get(indiceCarta).getQuestions().get(indicePregunta).checkAnswer(r.getText().toString())){

                            radio = (RadioButton) rg1.getChildAt(idx);
                                radio.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.respuestacorrecta));
                                viewModel.sumUserScore(viewModel.getCurrentUser().getId());
                                mpAcierto.start();

                            }else{

                            radio = (RadioButton) rg1.getChildAt(idx);
                                radio.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.respuestafallida));
                                mpFallo.start();

                            }
                            animation3.start();
                            indicePregunta++;
                            viewModel.sumUserResponses(viewModel.getCurrentUser().getId());
                            gameLoop();


                        }

                    btSiguiente.setEnabled(false);
                    btSiguiente.setBackgroundColor(Color.GRAY);

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
        tvDescripcion.setMovementMethod(new ScrollingMovementMethod());
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

        TextView tvNum1 = getActivity().findViewById(R.id.tvNumPregunta1);
        TextView tvNum2 = getActivity().findViewById(R.id.tvNumPregunta2);

        String numPregunta = String.valueOf(indicePregunta+1);
        tvNum1.setText(numPregunta + "/" + gameCards.get(indiceCarta).getQuestions().size());
        tvNum2.setText(numPregunta + "/" + gameCards.get(indiceCarta).getQuestions().size());

        ra1.setVisibility(View.VISIBLE);
        rb1.setVisibility(View.VISIBLE);
        rc1.setVisibility(View.VISIBLE);
        rd1.setVisibility(View.VISIBLE);

        ra2.setVisibility(View.VISIBLE);
        rb2.setVisibility(View.VISIBLE);
        rc2.setVisibility(View.VISIBLE);
        rd2.setVisibility(View.VISIBLE);

        ra1.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.radiobutton));
        rb1.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.radiobutton));
        rc1.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.radiobutton));
        rd1.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.radiobutton));

        ra2.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.radiobutton));
        rb2.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.radiobutton));
        rc2.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.radiobutton));
        rd2.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.radiobutton));

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



    private void requestExitConfirmation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_confirm_exit)
                .setPositiveButton(R.string.string_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.string_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
        builder.create();
        builder.show();
    }


}