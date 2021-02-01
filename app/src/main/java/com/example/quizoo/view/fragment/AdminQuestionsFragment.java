package com.example.quizoo.view.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.quizoo.R;
import com.example.quizoo.model.Repository;
import com.example.quizoo.rest.pojo.DBResponse;
import com.example.quizoo.rest.pojo.Question;
import com.example.quizoo.util.OnDBResponseListener;
import com.example.quizoo.view.adapter.OnQuestionClickListener;
import com.example.quizoo.view.adapter.QuestionsAdapter;
import com.example.quizoo.viewmodel.ViewModelActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminQuestionsFragment extends Fragment {

    private ViewModelActivity viewModel;

    private QuestionsAdapter adapter;
    private List<Question> questionList = new ArrayList<>();

    private ConstraintLayout constraintWarning;
    private TextView tvWarning;
    private FloatingActionButton btAddQuestion;
    private ProgressDialog progressDialog;


    public AdminQuestionsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);
        viewModel.getLiveQuestions().removeObservers((AppCompatActivity) getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_questions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LottieAnimationView btBackQuestion = getView().findViewById(R.id.backQuestion);
        btBackQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AdminQuestionsFragment.this).popBackStack();
            }
        });

        constraintWarning = view.findViewById(R.id.constraintWarning);
        tvWarning = view.findViewById(R.id.tvWarning);
        btAddQuestion = view.findViewById(R.id.btAddQuestion);
        init();
    }

    private void init() {


        RecyclerView questionsRecycler = getView().findViewById(R.id.questionsRecycler);
        adapter = new QuestionsAdapter(questionList, getActivity(), new OnQuestionClickListener() {
            @Override
            public void onQuestionClick(Question question) {
                viewModel.setCurrentQuestion(question);
                NavHostFragment.findNavController(AdminQuestionsFragment.this)
                        .navigate(R.id.action_adminQuestionsFragment_to_editQuestionFragment);
            }
        });
        attemptLoadQuestions();
        questionsRecycler.setAdapter(adapter);
        questionsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));




        btAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AdminQuestionsFragment.this)
                        .navigate(R.id.action_adminQuestionsFragment_to_createQuestionFragment);

            }
        });

        constraintWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLoadQuestions();
            }
        });
    }





    private void attemptLoadQuestions(){
        progressDialog = ProgressDialog.show(getContext(), getContext().getString(R.string.string_loading), "", true, false);




        viewModel.getLiveQuestions().observe((AppCompatActivity) getContext(), new Observer<ArrayList<Question>>() {
            @Override
            public void onChanged(ArrayList<Question> questions) {
                if(progressDialog == null) return;
                progressDialog.dismiss();

                if(questions == null){
                    // No hay conexion a internet
                    constraintWarning.setVisibility(View.VISIBLE);
                    constraintWarning.setOnClickListener(null);
                    tvWarning.setText(R.string.warning_questions_not_retrieved);
                    btAddQuestion.setVisibility(View.GONE);

                }else if(questions.size() == 0){
                    // Todavia no hay cartas
                    constraintWarning.setOnClickListener(null);
                    constraintWarning.setVisibility(View.VISIBLE);
                    btAddQuestion.setVisibility(View.VISIBLE);
                    tvWarning.setText(R.string.warning_no_questions_yet);


                }else{


                    btAddQuestion.setVisibility(View.VISIBLE);
                    if(questions.size() >= Repository.MAX_QUESTIONS_PER_CARD){ btAddQuestion.setVisibility(View.GONE); }
                    constraintWarning.setVisibility(View.GONE);
                    questionList.clear();
                    questionList.addAll(questions);
                    adapter.notifyDataSetChanged();
                }


            }
        });

        viewModel.loadQuestionsOf(viewModel.getCurrentCard());
    }
}