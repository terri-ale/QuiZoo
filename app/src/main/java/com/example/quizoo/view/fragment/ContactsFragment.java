package com.example.quizoo.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.quizoo.R;
import com.example.quizoo.model.entity.Contact;
import com.example.quizoo.view.adapter.ContactsAdapter;
import com.example.quizoo.viewmodel.ViewModelActivity;

import java.util.ArrayList;


public class ContactsFragment extends Fragment {


    private ViewModelActivity viewModel;

    String[] data = new String[]{ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Email.ADDRESS};
    String order = ContactsContract.Data.DISPLAY_NAME + " ASC";
    String selectionEmail = ContactsContract.Data.MIMETYPE + "='" +
            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE+ "' AND "
            + ContactsContract.CommonDataKinds.Email.ADDRESS + " IS NOT NULL";


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(ViewModelActivity.class);
        setWarning(!viewModel.contactsPermissionIsGranted());

        ArrayList<Contact> contacts = viewModel.getContactsWithMail();


        RecyclerView recyclerContact = view.findViewById(R.id.recyclerView);
        ContactsAdapter adapter = new ContactsAdapter(getContext(), getActivity(),contacts);

        recyclerContact.setAdapter(adapter);
        recyclerContact.setLayoutManager(new LinearLayoutManager(getContext()));

        ImageButton btBackFromContacts = view.findViewById(R.id.btBackFromContacts);
        btBackFromContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(ContactsFragment.this)
                        .navigate(R.id.perfilFragment);
            }
        });
    }



    private void setWarning(boolean shouldShowWarning){
        //Mostrar u ocultar los elementos graficos de la advertencia
    }





}