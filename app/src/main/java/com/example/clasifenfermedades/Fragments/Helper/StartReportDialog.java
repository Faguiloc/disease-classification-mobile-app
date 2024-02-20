package com.example.clasifenfermedades.Fragments.Helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.clasifenfermedades.Activities.CheckActivity;
import com.example.clasifenfermedades.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class StartReportDialog extends DialogFragment {
    private CheckActivity checkActivity;
    Spinner spinner;
    EditText etInfo;
    String dPredName,dSugName;
    Float predConf;
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        checkActivity = (CheckActivity) getActivity();
        JSONObject jsonObject = checkActivity.jsonObject;

        LayoutInflater inflater = requireActivity().getLayoutInflater();



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(inflater.inflate(R.layout.dialog_report,null))
                .setPositiveButton("Enviar Reporte", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            sendReport(dPredName,dSugName,predConf,checkActivity.b64Img, String.valueOf(etInfo.getText()));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
//        builder.setTitle(R.string.titleDialogReport)
//                .setSingleChoiceItems()
//                .setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // START THE GAME!
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onStart(){
        super.onStart();
        spinner = getDialog().findViewById(R.id.itemList);
        etInfo  = getDialog().findViewById(R.id.etInfo);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.disArray, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void sendReport(String predictedDisease,String suggestedDisease, Float confidence, String imageB64, String comment) throws JSONException {
        String url = "http://34.176.120.172:8111/api/sendReport";

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("predictedDisease", predictedDisease);
        jsonBody.put("suggestedDisease", suggestedDisease);
        jsonBody.put("predictedConfidence", confidence);
//        jsonBody.put("imageB64", imageB64);
        jsonBody.put("comment", comment);
        final String requestBody = jsonBody.toString();
        Log.d("json",requestBody);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null){
                    Log.d("jsonError", error.toString());
                }
                else{
                    Log.d("jsonError", "Se produjo un error desconocido, variable error null");
                }
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("UTF-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }};
//        Log.d("request", String.valueOf(request));
        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this.getContext()).add(request);
    }
}
