package com.example.quizoo.view.fragment;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
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

import com.example.quizoo.R;
import com.example.quizoo.model.Repository;
import com.example.quizoo.model.entity.Contact;
import com.example.quizoo.view.adapter.ContactsAdapter;
import com.example.quizoo.viewmodel.ViewModelActivity;

import java.util.ArrayList;


public class ContactsFragment extends Fragment {

    private ViewModelActivity viewModel;
    ConstraintLayout mensajeNoContactos;

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
      mensajeNoContactos = getActivity().findViewById(R.id.mensajeNoContactos);
      mensajeNoContactos.setVisibility(View.GONE);

        ImageButton btBackFromContacts = getView().findViewById(R.id.btBackFromContacts);
        btBackFromContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(ContactsFragment.this)
                        .popBackStack();

            }
        });

        if(!viewModel.contactsPermissionIsGranted()){
            setPermissionsWarning(true);
            requestContactsPermission();

        }else{
            init();
        }



    }




    private void init(){
        ArrayList<Contact> contacts = viewModel.getContactsWithMail();

        if(contacts.isEmpty()){
            setNoContactsWarning(true);
        }


        RecyclerView recyclerContact = getView().findViewById(R.id.recyclerView);
        ContactsAdapter adapter = new ContactsAdapter(getContext(), getActivity(),contacts);

        recyclerContact.setAdapter(adapter);
        recyclerContact.setLayoutManager(new LinearLayoutManager(getContext()));


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
        if(grantedCounter==permissions.length){ setPermissionsWarning(false); }
    }




    /*
    Los componentes gráficos que se usen para la advertencia, importante que por defecto
    en el XML estén con Visibility GONE
     */


    private void setNoContactsWarning(boolean shouldShowWarning){



        mensajeNoContactos.setVisibility(View.VISIBLE);
    }


    private void setPermissionsWarning(boolean shouldShowWarning){
        //Mostrar u ocultar los elementos graficos de la advertencia
        //Coger de strings.xml la advertencia de "Para continuar, debes conceder los permisos"
    }





}