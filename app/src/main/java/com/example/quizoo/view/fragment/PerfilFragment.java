package com.example.quizoo.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.quizoo.R;
import com.example.quizoo.model.Repository;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class PerfilFragment extends Fragment {

    private ViewModelActivity viewModel;

    public PerfilFragment() {
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
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);

        ImageView imgUserProfile = view.findViewById(R.id.imgUserProfile);
        rotarImagen(imgUserProfile);
        imgUserProfile.setImageResource(viewModel.getCurrentUser().getAvatar());

        TextView tvUserProfile = view.findViewById(R.id.tvUserProfile);
        tvUserProfile.setText(viewModel.getCurrentUser().getName());

        TextView tvPuntosProfile = view.findViewById(R.id.tvPuntosProfile);
        tvPuntosProfile.setText(viewModel.getCurrentUser().getNumResponsesCorrect() * Repository.SCORE_MULTIPLIER + getActivity().getString(R.string.string_points));

        clickBotones(view, savedInstanceState);




    }

    private void rotarImagen(View view){
        RotateAnimation animation = new RotateAnimation(0, 180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animation.setDuration(1000);
        animation.setRepeatCount(1);
        animation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(animation);

    }

    private Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // Set the dialog title
        Boolean isChecked;
        builder.setTitle("¿Como desea enviar el correo?").setSingleChoiceItems(R.array.opciones, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 1){
                    dialog.dismiss();
                    NavHostFragment.findNavController(PerfilFragment.this)
                            .navigate(R.id.contactsFragment);
                }else{
                    dialog.dismiss();
                    viewModel.mandarCorreo("", String.valueOf(viewModel.getCurrentUser().getNumResponsesCorrect() * 10));
                }
            }
        });
        builder.show();
        return builder.create();
    }

    private void clickBotones(View view, Bundle savedInstanceState){
        ImageView imgShare = view.findViewById(R.id.imgEnviarCorreo);
        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateDialog(savedInstanceState);
            }
        });

        LottieAnimationView btBackFromProfile = view.findViewById(R.id.btBackFromProfile);
        btBackFromProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(PerfilFragment.this).popBackStack();
            }
        });
    }





}