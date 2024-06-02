package com.ispc.gymapp.views.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ispc.gymapp.R;


public class PerformanceFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_performance, container, false);

        Button openEmailButton = view.findViewById(R.id.openEmailButton);
        openEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmailApp();
            }
        });
        Button openWhatsAppButton = view.findViewById(R.id.openWhatsAppButton);
        openWhatsAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsApp();
            }
        });

        return view;
    }

    private void openEmailApp() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"soporte@gymapp.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre la aplicación GymApp");

        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    private void openWhatsApp() {
        String phoneNumber = "123456789"; // Reemplaza con el número de teléfono al que quieres enviar el mensaje
        String message = "Hola tengo una consulta sobre la aplicación GymApp"; // Mensaje opcional que quieres enviar

        try {
            Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + Uri.encode(message));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.whatsapp");

            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                redirectToPlayStore();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    private void redirectToPlayStore() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.whatsapp"));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"));
            startActivity(intent);
        }
    }

}
