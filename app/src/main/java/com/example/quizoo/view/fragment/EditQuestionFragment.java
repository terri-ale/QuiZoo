package com.example.quizoo.view.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.quizoo.R;
import com.example.quizoo.rest.pojo.DBResponse;
import com.example.quizoo.util.OnDBResponseListener;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.textfield.TextInputLayout;


public class EditQuestionFragment extends Fragment implements OnDBResponseListener, View.OnClickListener {

    private ProgressDialog progressDialog;


    private ViewModelActivity viewModel;

    private Button btAddEditQuestion;
    private EditText etQuestion;
    private EditText etCorrectAnswer;
    private EditText etWrongAnswer1;
    private EditText etWrongAnswer2;
    private EditText etWrongAnswer3;

    private ImageView imgDeleteQuestion;





    public EditQuestionFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);
        viewModel.getLiveResponse().removeObservers((AppCompatActivity) getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        etQuestion = view.findViewById(R.id.etQuestion);
        etCorrectAnswer = view.findViewById(R.id.etCorrectAnswer);
        etWrongAnswer1 = view.findViewById(R.id.etWrongAnswer1);
        etWrongAnswer2 = view.findViewById(R.id.etWrongAnswer2);
        etWrongAnswer3 = view.findViewById(R.id.etWrongAnswer3);


        btAddEditQuestion = view.findViewById(R.id.btAddEditQuestion);
        imgDeleteQuestion = view.findViewById(R.id.imgDeleteQuestion);

        btAddEditQuestion.setOnClickListener(this);
        imgDeleteQuestion.setOnClickListener(this);
        view.findViewById(R.id.imgBackFromCreateEditQuestion).setOnClickListener(this);

        setUI();
        viewModel.setResponseListener(this);

    }

    private void setUI() {

        etQuestion.setText(viewModel.getCurrentQuestion().getText());
        etCorrectAnswer.setText(viewModel.getCurrentQuestion().getRightAnswer());
        etWrongAnswer1.setText(viewModel.getCurrentQuestion().getWrongAnswer1());
        etWrongAnswer2.setText(viewModel.getCurrentQuestion().getWrongAnswer2());
        etWrongAnswer3.setText(viewModel.getCurrentQuestion().getWrongAnswer3());
    }


    private void attemptUpdateQuestion(){

        String question = etQuestion.getText().toString();
        String correctAnswer = etCorrectAnswer.getText().toString();
        String wrongAnswer1 = etWrongAnswer1.getText().toString();

        if( question.isEmpty() || correctAnswer.isEmpty() || wrongAnswer1.isEmpty()){

            Toast.makeText(getContext(), R.string.warning_empty_fields, Toast.LENGTH_SHORT).show();
        }else{
            btAddEditQuestion.setOnClickListener(null);
            progressDialog = ProgressDialog.show(getContext(), getContext().getString(R.string.string_updating_question), "", true, false);

            String wrongAnswer2 = etWrongAnswer2.getText().toString();
            String wrongAnswer3 = etWrongAnswer3.getText().toString();

            viewModel.getCurrentQuestion().setText(question);
            viewModel.getCurrentQuestion().setRightAnswer(correctAnswer);
            viewModel.getCurrentQuestion().setWrongAnswer1(wrongAnswer1);

            if(!wrongAnswer2.isEmpty()){ viewModel.getCurrentQuestion().setWrongAnswer2(wrongAnswer2); }
            if(!wrongAnswer3.isEmpty()){ viewModel.getCurrentQuestion().setWrongAnswer3(wrongAnswer3); }

            viewModel.updateQuestion(viewModel.getCurrentQuestion());
        }
    }


    private void attemptDeleteQuestion() {
        imgDeleteQuestion.setOnClickListener(null);
        progressDialog = ProgressDialog.show(getContext(), getContext().getString(R.string.string_deleting_quesiton), "", true, false);
        viewModel.deleteQuestion(viewModel.getCurrentQuestion());
    }


    @Override
    public void onSuccess(DBResponse response) {
        progressDialog.dismiss();
        Toast.makeText(getContext(), getContext().getString(R.string.message_changes_successfully_applied), Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(EditQuestionFragment.this).popBackStack();
    }

    @Override
    public void onFailed() {
        progressDialog.dismiss();
        Toast.makeText(getContext(), getContext().getString(R.string.warning_changes_not_applied), Toast.LENGTH_SHORT).show();
        btAddEditQuestion.setOnClickListener(this);
        imgDeleteQuestion.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btAddEditQuestion:
                attemptUpdateQuestion();
                break;

            case R.id.imgDeleteQuestion:
                attemptDeleteQuestion();
                break;

            case R.id.imgBackFromCreateEditQuestion:
                NavHostFragment.findNavController(EditQuestionFragment.this).popBackStack();
                break;
        }
    }


}