package com.example.quizoo.view.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quizoo.R;
import com.example.quizoo.model.entity.Contact;
import com.example.quizoo.view.adapter.Adapter;
import com.example.quizoo.viewmodel.ViewModelActivity;

import java.util.ArrayList;
import java.util.List;


public class ContactsFragment extends Fragment {

    private ViewModelActivity viewModel;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);

        setWarning(!viewModel.contactsPermissionIsGranted());



        ArrayList<Contact> contacts = viewModel.getContactsWithMail();

        RecyclerView recyclerContact = view.findViewById(R.id.recyclerView);
        Adapter adapter = new Adapter(getContext(), getActivity(), contacts);

        recyclerContact.setAdapter(adapter);
        recyclerContact.setLayoutManager(new LinearLayoutManager(getContext()));
    }




    private void setWarning(boolean shouldShowWarning){


    }






}