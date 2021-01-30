package com.example.quizoo.view.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quizoo.R;
import com.example.quizoo.rest.pojo.DBResponse;
import com.example.quizoo.rest.pojo.Question;
import com.example.quizoo.util.OnDBResponseListener;
import com.example.quizoo.viewmodel.ViewModelActivity;


public class CreateQuestionFragment extends Fragment implements View.OnClickListener, OnDBResponseListener {

    private ViewModelActivity viewModel;

    private ProgressDialog progressDialog;

    private Button btAddEditQuestion;



    public CreateQuestionFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btAddEditQuestion = view.findViewById(R.id.btAddEditQuestion);

        btAddEditQuestion.setOnClickListener(this);
        view.findViewById(R.id.imgBackFromCreateEditQuestion).setOnClickListener(this);

        viewModel.setResponseListener(this);

    }



    private void attemptCreateQuestion(){

        String questionText = ((EditText)getView().findViewById(R.id.etQuestion)).getText().toString();
        String correctAnswer = ((EditText)getView().findViewById(R.id.etCorrectAnswer)).getText().toString();
        String wrongAnswer1 = ((EditText)getView().findViewById(R.id.etWrongAnswer1)).getText().toString();

        if( questionText.isEmpty() || correctAnswer.isEmpty() || wrongAnswer1.isEmpty()){
            //Campos obligatorios
            Toast.makeText(getContext(), R.string.warning_empty_fields, Toast.LENGTH_SHORT).show();
        }else{
            btAddEditQuestion.setOnClickListener(null);
            progressDialog = ProgressDialog.show(getContext(), getContext().getString(R.string.string_adding_question), "", true, false);

            String wrongAnswer2 = ((EditText)getView().findViewById(R.id.etWrongAnswer2)).getText().toString();
            String wrongAnswer3 = ((EditText)getView().findViewById(R.id.etWrongAnswer3)).getText().toString();

            Question question = new Question(viewModel.getCurrentCard().getId(), questionText, correctAnswer, wrongAnswer1);

            if(!wrongAnswer2.isEmpty()){ question.setWrongAnswer2(wrongAnswer2); }
            if(!wrongAnswer3.isEmpty()){ question.setWrongAnswer3(wrongAnswer3); }

            viewModel.addQuestion(question);
        }
    }





    @Override
    public void onSuccess(DBResponse response) {
        progressDialog.dismiss();
        Toast.makeText(getContext(), getContext().getString(R.string.message_question_created_successfully), Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(CreateQuestionFragment.this).popBackStack();
    }

    @Override
    public void onFailed() {
        progressDialog.dismiss();
        btAddEditQuestion.setOnClickListener(this);
        Toast.makeText(getContext(), getContext().getString(R.string.warning_question_not_added), Toast.LENGTH_SHORT).show();
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btAddEditQuestion:
                attemptCreateQuestion();
                break;

            case R.id.imgBackFromCreateEditQuestion:
                NavHostFragment.findNavController(CreateQuestionFragment.this).popBackStack();
                break;
        }
    }

}