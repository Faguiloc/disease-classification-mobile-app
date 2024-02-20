package com.example.clasifenfermedades.Fragments.CheckActivity;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clasifenfermedades.Activities.CheckActivity;
import com.example.clasifenfermedades.Adapter.AdapterDisease;
import com.example.clasifenfermedades.Classes.Diseases;
import com.example.clasifenfermedades.Fragments.Helper.StartReportDialog;
import com.example.clasifenfermedades.Helper.DownloadImageTask;
import com.example.clasifenfermedades.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ResponseFragmentEnfermo extends Fragment {

    CheckActivity checkActivity;
    TextView responseTitle, responseBody, responseLink, lblMoreInfo, responseConfidence;
    ImageView responseImg, userImg;
    Button btn_atras,btn_reporte;
    RecyclerView rv_otras;
    LinearLayout moreDiseases;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_response_enfermo, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkActivity      = (CheckActivity) getActivity();
        responseTitle      = checkActivity.findViewById(R.id.responseTitle);
        responseBody       = checkActivity.findViewById(R.id.responseBody);
        lblMoreInfo        = checkActivity.findViewById(R.id.lblMoreInfo);
        responseLink       = checkActivity.findViewById(R.id.responseLink);
        responseImg        = checkActivity.findViewById(R.id.responseImg);
        responseConfidence = checkActivity.findViewById(R.id.responseConfidence);
        userImg            = checkActivity.findViewById(R.id.userImg);
        rv_otras           = checkActivity.findViewById(R.id.rv_otras);
        btn_atras          = checkActivity.findViewById(R.id.btn_atras);
        btn_reporte        = checkActivity.findViewById(R.id.btn_reporte);
        moreDiseases       = checkActivity.findViewById(R.id.moreDiseases);



        btn_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        btn_reporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newDialog = new StartReportDialog();
                newDialog.show(getActivity().getSupportFragmentManager(),"report");
            }
        });



        if(checkActivity.jsonObject != null){
            JSONObject jsonObject = checkActivity.jsonObject;
            Log.d("json", String.valueOf(jsonObject));
            try {
                    JSONObject topPredictionInformation = (JSONObject) jsonObject.get("topPredictionInformation");
                    JSONArray sortedPredictions = (JSONArray) jsonObject.get("sortedPredictions");
                    JSONObject name = (JSONObject) topPredictionInformation.get("name");
                    String nameES = (String) name.get("es");

                    JSONObject desc = (JSONObject) topPredictionInformation.get("description");
                    String descES = (String) desc.get("es");


                    String link = (String) topPredictionInformation.get("link");
                    String image = (String) topPredictionInformation.get("image");
                    double confidence = Math.round(sortedPredictions.getJSONArray(0).getDouble(1)*10)/10;
                    String title = nameES;

                    ArrayList<Diseases> arrayList = new ArrayList<Diseases>();
                    for(int i=1;i<4;i++) {
                        JSONArray object = sortedPredictions.getJSONArray(i);
                        String nameDis = object.getJSONObject(0).getString("es");
//                        double conf    =  Math.round(object.getDouble(1)*10)/10;
                        double conf = object.getDouble(1);
                        String confDis = conf + " %";
                        String     img = object.getString(2);
                            Diseases disease  = new Diseases(nameDis, confDis, img);
                            arrayList.add(disease);
                    }
                    if(arrayList.size()>0){
                        AdapterDisease adapterDisease = new AdapterDisease(arrayList,R.layout.item_enfermedad);
                        rv_otras.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
                        rv_otras.setAdapter(adapterDisease);
                    }
                    else{
                        moreDiseases.setVisibility(View.GONE);
                    }

                    descES = Codificador(descES);
                    title = Codificador(title);
                    responseTitle.setText(title);
                    responseBody.setText(descES);
                    responseLink.setText(link);
                    responseConfidence.setText(" (" + confidence + getResources().getString(R.string.confidence) +")");

                    new DownloadImageTask((ImageView) getActivity().findViewById(R.id.responseImg))
                            .execute(image);
                    userImg.setImageBitmap(checkActivity.bitmapImg);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
        else{
            Toast.makeText(view.getContext(),"Se produjo un error en la lectura de la API, por favor intentelo de nuevo",Toast.LENGTH_SHORT).show();
            return;
        }
    }
    public String Codificador(String cadena) {

        byte[] ptext = cadena.getBytes(ISO_8859_1);
        String resultado = new String(ptext,UTF_8);

        return resultado;
    }

}