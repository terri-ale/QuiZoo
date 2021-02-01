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
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizoo.R;
import com.example.quizoo.rest.pojo.Card;
import com.example.quizoo.view.adapter.AdminCardsAdapter;
import com.example.quizoo.view.adapter.ContactsAdapter;
import com.example.quizoo.view.adapter.OnCardClickListener;
import com.example.quizoo.viewmodel.ViewModelActivity;

import java.util.ArrayList;
import java.util.List;


public class AdminCardsFragment extends Fragment {

    private ViewModelActivity viewModel;
    private List<Card> cardList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private AdminCardsAdapter adapter;

    private ConstraintLayout constraintWarning;
    private TextView tvWarning;


    public AdminCardsFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_cards, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imgUsers = view.findViewById(R.id.imgUserN);
        ImageView imgHome = view.findViewById(R.id.imgHomeN);

        imgUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AdminCardsFragment.this)
                        .navigate(R.id.action_adminCardsFragment_to_adminUsersFragment);
            }
        });

        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AdminCardsFragment.this)
                        .navigate(R.id.action_adminCardsFragment_to_perfilAdminFragment);
            }
        });

        constraintWarning = view.findViewById(R.id.constraintWarning);
        tvWarning = view.findViewById(R.id.tvWarning);


        constraintWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("xyzyx", "REINTENTAR");
                attemptLoadCards();
            }
        });

        init();

    }



    private void setUI(){

    }

    private void init() {

        RecyclerView recyclerView = getView().findViewById(R.id.rvCards);
        adapter = new AdminCardsAdapter(cardList, getActivity(), new OnCardClickListener() {
            @Override
            public void onShowQuestionsClick(Card card) {
                viewModel.setCurrentCard(card);
                NavHostFragment.findNavController(AdminCardsFragment.this)
                        .navigate(R.id.action_adminCardsFragment_to_adminQuestionsFragment);
            }

            @Override
            public void onEditCardClick(Card card) {
                viewModel.setCurrentCard(card);
                NavHostFragment.findNavController(AdminCardsFragment.this)
                        .navigate(R.id.action_adminCardsFragment_to_editCardFragment);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        attemptLoadCards();

        getView().findViewById(R.id.fabAddCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AdminCardsFragment.this).navigate(R.id.action_adminCardsFragment_to_createCardsFragment);
            }
        });

    }




    private void attemptLoadCards(){
        progressDialog = ProgressDialog.show(getContext(), getContext().getString(R.string.string_loading), "", false);

        viewModel.getLiveCards().observe((AppCompatActivity)getContext(), new Observer<ArrayList<Card>>() {
            @Override
            public void onChanged(ArrayList<Card> cards) {
                if(progressDialog == null) return; //Danke Carmelo
                progressDialog.dismiss();

                if(cards == null){
                    //ERROR - INTERNET
                    constraintWarning.setVisibility(View.VISIBLE);
                    tvWarning.setText(getContext().getString(R.string.warning_cards_not_retrieved));

                }else if(cards.size() == 0){
                    //NO HAY CARTAS
                    constraintWarning.setVisibility(View.VISIBLE);
                    tvWarning.setText(getContext().getString(R.string.warning_no_cards_yet));

                }else{

                    constraintWarning.setVisibility(View.GONE);
                    cardList.clear();
                    cardList.addAll(cards);
                    adapter.notifyDataSetChanged();
                }


            }
        });


        viewModel.loadCards();
    }


}