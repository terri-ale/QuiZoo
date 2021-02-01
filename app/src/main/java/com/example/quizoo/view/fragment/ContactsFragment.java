package com.example.quizoo.view.fragment;

import android.content.pm.PackageManager;
import android.os.Build;
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

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.quizoo.R;
import com.example.quizoo.model.Repository;
import com.example.quizoo.model.entity.Contact;
import com.example.quizoo.view.adapter.ContactsAdapter;
import com.example.quizoo.viewmodel.ViewModelActivity;

import java.util.ArrayList;


public class ContactsFragment extends Fragment implements Observer<ArrayList<Contact>>{


    private ViewModelActivity viewModel;

    private ContactsAdapter adapter;
    private ArrayList<Contact> contactList = new ArrayList<>();

    private ConstraintLayout mensajeNoContactos;
    private ConstraintLayout constraintWarning;
    private TextView tvWarning;

    public ContactsFragment() { }


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
        mensajeNoContactos = getActivity().findViewById(R.id.mensajeNoContactos);
        mensajeNoContactos.setVisibility(View.GONE);

        constraintWarning = view.findViewById(R.id.constraintWarning);
        tvWarning = view.findViewById(R.id.tvWarning);

        //ConstraintWarning sólo se mostrará como advertencia caso de no haber permisos de contactos.
        constraintWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLoadContacts();
            }
        });



        ImageButton btBackFromContacts = getView().findViewById(R.id.btBackFromContacts);
        btBackFromContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(ContactsFragment.this)
                        .popBackStack();

            }
        });


        RecyclerView recyclerContact = getView().findViewById(R.id.recyclerView);
        adapter = new ContactsAdapter(getContext(), getActivity(), contactList);

        recyclerContact.setAdapter(adapter);
        recyclerContact.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getLiveContacts().observe((AppCompatActivity) getContext(), this);
        attemptLoadContacts();

    }



    private void attemptLoadContacts(){
        if(!viewModel.contactsPermissionIsGranted()){
            setPermissionsWarning(true);
            requestContactsPermission();
        }else {
            setPermissionsWarning(false);
            viewModel.loadContactsWithMail();
        }
    }


    @Override
    public void onChanged(ArrayList<Contact> contacts) {
        if(contacts.size() == 0){
            setNoContactsWarning(true);
        }else{
            setNoContactsWarning(false);
            contactList.clear();
            contactList.addAll(contacts);
            adapter.notifyDataSetChanged();
        }
    }





    private void requestContactsPermission() {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) { return; }
        requestPermissions(Repository.CONTACTS_PERMISSION, Repository.CONTACTS_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int grantedCounter=0;
        switch (requestCode){
            case Repository.CONTACTS_PERMISSION_CODE:
                for(int result : grantResults){
                    if(result== PackageManager.PERMISSION_GRANTED) grantedCounter++;
                }
                break;
        }
        if(grantedCounter==permissions.length){ setPermissionsWarning(false); attemptLoadContacts(); }
    }




    /*
    Los componentes gráficos que se usen para la advertencia, importante que por defecto
    en el XML estén con Visibility GONE
     */


    private void setNoContactsWarning(boolean shouldShowWarning){
        if(shouldShowWarning){
            mensajeNoContactos.setVisibility(View.VISIBLE);
        }else{
            mensajeNoContactos.setVisibility(View.GONE);
        }
    }


    private void setPermissionsWarning(boolean shouldShowWarning){
        if(shouldShowWarning){
            constraintWarning.setVisibility(View.VISIBLE);
            tvWarning.setText(R.string.warning_grant_permission);
        }else{
            constraintWarning.setVisibility(View.GONE);
        }
    }
}