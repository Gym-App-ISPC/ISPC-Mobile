package com.ispc.gymapp.views.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ispc.gymapp.R;
import com.ispc.gymapp.model.User;
import com.ispc.gymapp.views.activities.ActivityFavoritos;
import com.ispc.gymapp.views.activities.LoginActivity;
import com.ispc.gymapp.views.activities.SplashActivity;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText pesoEditText;
    private EditText alturaEditText;
    private TextView imcTextView,logout, deleteAccount;
    private User user;
    private FirebaseAuth mAuth;

    public ProfileFragment(User user) {
        this.user = user;
    }


    public static ProfileFragment newInstance(String param1, String param2,User user) {
        ProfileFragment fragment = new ProfileFragment(user);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        pesoEditText = view.findViewById(R.id.peso);
        pesoEditText.setEnabled(false);
        alturaEditText = view.findViewById(R.id.altura);
        alturaEditText.setEnabled(false);
        imcTextView = view.findViewById(R.id.textImc);
        logout = view.findViewById(R.id.textView21);
        deleteAccount = view.findViewById(R.id.deleteAccount);
        mAuth = FirebaseAuth.getInstance();

        logoutOperation();
        delete(user);

        Double userWeight = user.getWeight();
        Integer userHeight = user.getHeight();
        pesoEditText.setText(userWeight.toString());
        alturaEditText.setText(userHeight.toString());
        String IMC =  imcCalculator(userWeight,userHeight);
        imcTextView.setText(IMC);

        AppCompatImageView favoritos = view.findViewById(R.id.favoritos);
        favoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityFavoritos.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void logoutOperation() {
        logout.setOnClickListener(view -> {
            mAuth.signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        });
    }

    private void delete(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        deleteAccount.setOnClickListener(view1 -> db.collection("users").whereEqualTo("mail", user.getMail())
                .get().addOnSuccessListener(task ->{
                    for (DocumentSnapshot documentSnapshot : task.getDocuments()) {
                        Toast toast = Toast.makeText(getContext(), "Cuenta eliminada", Toast.LENGTH_LONG);
                        toast.show();
                        db.collection("users")
                                .document(documentSnapshot.getId()).delete()
                                .addOnSuccessListener(unused -> {
                                    Intent intent = new Intent(getContext(), SplashActivity.class);
                                    startActivity(intent);
                                });
                    }
                }));
    }

    private String imcCalculator(double weight, int height) {

            if(weight<=0 && height<=0) {
                return "";
            }
            double imcCalculator= weight / (height * height);
            if (imcCalculator<18.5) {
                return "BAJO PESO";
            } else if (imcCalculator>=18.5 && imcCalculator<=24.9) {
                return "NORMAL";
            } else if (imcCalculator>=25 && imcCalculator<=29.9) {
                return "SOBREPESO";
            } else {
                return "OBESIDAD";
            }
    }


    }
