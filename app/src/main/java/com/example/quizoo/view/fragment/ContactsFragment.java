package com.example.quizoo.view.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import java.util.ArrayList;


public class ContactsFragment extends Fragment {

    private ArrayList<Contact> contacts = new ArrayList<>();

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


        contacts.clear();

        getContacts(getContext());

        RecyclerView recyclerContact = view.findViewById(R.id.recyclerView);
        Adapter adapter = new Adapter(getContext(), getActivity(),contacts);

        recyclerContact.setAdapter(adapter);
        recyclerContact.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    //----- OBTENER LISTA DE CONTACTOS DE LA AGENDA -----
    public void getContacts(Context context){
        Cursor cursor =  context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                data,
                selectionEmail,
                null,
                order);
        while(cursor.moveToNext()){
            String name = cursor.getString(0);
            String email = cursor.getString(1);

            Contact contact = new Contact(name, email);
            contacts.add(contact);
        }

        //Log.v("xyzyx", nameContact.toString());
        cursor.close();
    }

}