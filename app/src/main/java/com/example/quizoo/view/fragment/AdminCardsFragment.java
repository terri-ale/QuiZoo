package com.example.quizoo.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    public AdminCardsFragment() {
        // Required empty public constructor
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


        init();

    }

    private void init() {

        RecyclerView recyclerView = getView().findViewById(R.id.rvCards);
        AdminCardsAdapter adapter = new AdminCardsAdapter(cardList, getActivity(), new OnCardClickListener() {
            @Override
            public void onShowQuestionsClick(Card card) {

            }

            @Override
            public void onEditCardClick(Card card) {

            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getLiveCards().observe(getViewLifecycleOwner(), new Observer<ArrayList<Card>>() {
            @Override
            public void onChanged(ArrayList<Card> cards) {
                if(cards == null){
                    //ERROR - INTERNET
                    Log.v("xyzyx", "NO INTERNTE ");
                }else if(cards.size() == 0){
                    //NO HAY CARTAS
                    Log.v("xyzyx", "NO CARGADAS ");
                }else{
                    Log.v("xyzyx", "CARGADAS "+cards.toString());
                    cardList.clear();
                    cardList.addAll(cards);
                    adapter.notifyDataSetChanged();
                }
            }
        });


        viewModel.loadCards();






    }






}