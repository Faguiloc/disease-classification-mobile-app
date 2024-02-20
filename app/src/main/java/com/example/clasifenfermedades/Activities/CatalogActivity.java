package com.example.clasifenfermedades.Activities;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.clasifenfermedades.Adapter.AdapterDisease;
import com.example.clasifenfermedades.Classes.Diseases;
import com.example.clasifenfermedades.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CatalogActivity extends AppCompatActivity {

    RecyclerView rv;
    AdapterDisease adapterDisease;
    FrameLayout progressBar;
    JSONObject jsonDiseases;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        rv = findViewById(R.id.rvDiseases);
        progressBar = findViewById(R.id.catProgressBar);

        progressBar.setVisibility(View.VISIBLE);

        try {
            GetDiseases();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }

    private void GetDiseases() throws JSONException {
        String url = "http://34.176.120.172:8111/api/getSkinDiseaseAll";



        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    Log.d("jsonTry", String.valueOf(jsonArray));
                    SetRv(jsonArray);

                } catch (JSONException e) {
                    Log.d("jsonCatch", e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null){
                    Log.d("jsonError", error.toString());
                }
                else{
                    Log.d("jsonError", "Se produjo un erro desconocido, variable error null");
                }
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }};
//        Log.d("request", String.valueOf(request));
        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getBaseContext()).add(request);
    }

    private void SetRv(JSONArray jsonArray) throws JSONException {
        ArrayList<Diseases> arrayList = new ArrayList<Diseases>();
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            JSONObject name   = (JSONObject) object.get("name");
            JSONObject desc   = (JSONObject) object.get("description");
            String     img    = object.getString("image");
            String nameString = Codificador(name.getString("es"));
            String descString = Codificador(desc.getString("es"));
            Diseases disease  = new Diseases(nameString, descString, img);
            arrayList.add(disease);
        }
        progressBar.setVisibility(View.INVISIBLE);

        AdapterDisease adapterDisease = new AdapterDisease(arrayList,R.layout.item_catalog);
        rv.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL,false));
        rv.setAdapter(adapterDisease);

    }
    public String Codificador(String cadena) {

        byte[] ptext = cadena.getBytes(ISO_8859_1);
        String resultado = new String(ptext,UTF_8);

        return resultado;
    }

}