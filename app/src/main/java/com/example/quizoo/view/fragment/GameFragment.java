package com.example.quizoo.view.fragment;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quizoo.R;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.text.Html.FROM_HTML_MODE_LEGACY;


public class GameFragment extends Fragment {

    private ViewModelActivity viewModel;

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

        FloatingActionButton btHlep = view.findViewById(R.id.btHelp);
        btHlep.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                instruccionesDialog(savedInstanceState);
            }
        });

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