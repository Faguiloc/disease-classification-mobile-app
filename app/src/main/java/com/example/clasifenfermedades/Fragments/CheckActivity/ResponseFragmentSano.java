package com.example.clasifenfermedades.Fragments.CheckActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.clasifenfermedades.Activities.CatalogActivity;
import com.example.clasifenfermedades.Activities.CheckActivity;
import com.example.clasifenfermedades.Activities.MainActivity;
import com.example.clasifenfermedades.Fragments.Helper.StartReportDialog;
import com.example.clasifenfermedades.Helper.DownloadImageTask;
import com.example.clasifenfermedades.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseFragmentSano extends Fragment {

    CheckActivity checkActivity;
    TextView responseTitle, responseBody,responseLink,lblMoreInfo;
    ImageView responseImg, userImg;
    Button btn_atras, btn_catalog;
    ImageButton btn_reporte;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_response_sano, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkActivity = (CheckActivity) getActivity();
        userImg   = checkActivity.findViewById(R.id.userImg);

        btn_atras = checkActivity.findViewById(R.id.btn_response_check);
        btn_catalog = checkActivity.findViewById(R.id.btn_response_cat);
//        btn_reporte = checkActivity.findViewById(R.id.btn_reporte);

        btn_catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                Intent intent = new Intent(getContext(), CatalogActivity.class);
                startActivity(intent);
            }
        });

        btn_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                Intent intent = new Intent(getContext(), CheckActivity.class);
                startActivity(intent);
            }
        });

//        btn_reporte.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogFragment newDialog = new StartReportDialog();
//                newDialog.show(getActivity().getSupportFragmentManager(),"report");
//            }
//        });

        if(checkActivity.jsonObject != null){
            JSONObject jsonObject = checkActivity.jsonObject;
            userImg.setImageBitmap(checkActivity.bitmapImg);
        }
        else{
            Toast.makeText(view.getContext(),"Se produjo un error en la lectura de la API, por favor intentelo de nuevo",Toast.LENGTH_SHORT).show();
            return;
        }
    }

}