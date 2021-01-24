package com.example.quizoo.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.quizoo.R;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class AdminLoginFragment extends Fragment {

    private ViewModelActivity viewModel;

    private TextInputLayout tiPassAdmin;
   private  LottieAnimationView backhand;

    public AdminLoginFragment() {
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
        return inflater.inflate(R.layout.fragment_admin_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backhand = getView().findViewById(R.id.btBackFromAdMinLogin);
        final NavController navController = Navigation.findNavController(view);

        backhand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.chooseUserFragment);
            }
        });

        viewModel=new ViewModelProvider(getActivity()).get(ViewModelActivity.class);

        tiPassAdmin = view.findViewById(R.id.tiPassAdmin);

        if(!viewModel.isSetAdminPassword()) { setUIForFirstRun(); }


        Button btConfirmAdmin = view.findViewById(R.id.btConfirmarAdmin);
        btConfirmAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }


    private void setUIForFirstRun(){

        backhand.setVisibility(View.INVISIBLE);
        TextView tvExplanation = getView().findViewById(R.id.tvExplicacion);
        String explanation = getContext().getString(R.string.crea_una_contrase_a_para_entrar_en_el_modo_administrador);
        tvExplanation.setText(explanation);

        Button button = getView().findViewById(R.id.btConfirmarAdmin);
        String textEnter = getContext().getString(R.string.confirmar_password);
        button.setText(textEnter);
    }

    private void attemptLogin() {
        String insertedPassword = tiPassAdmin.getEditText().getText().toString();
        String error = null;

        if(insertedPassword.isEmpty()) {
            error = getContext().getString(R.string.warning_empty_field);
        }else if(viewModel.isSetAdminPassword() && !viewModel.checkAdminPassword(insertedPassword)){
            error = getContext().getString(R.string.warning_incorrect_password);
        }

        if(error != null){
            tiPassAdmin.setError(error);
        }else{
            if(!viewModel.isSetAdminPassword()){
                viewModel.setAdminPassword(insertedPassword);
            }
            NavHostFragment.findNavController(AdminLoginFragment.this)
                    .navigate(R.id.perfilAdminFragment);
        }


    }
}