  package com.example.clasifenfermedades.Fragments.CheckActivity;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

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
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class CheckFragment extends Fragment {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ImageButton btn_tp;
    private ImageButton btn_gallery;
    private ImageView imagePreview;
    FrameLayout progressBar;

    private CheckActivity checkActivity;
    private ResponseFragmentSano responseFragmentSano = new ResponseFragmentSano();
    private ResponseFragmentEnfermo responseFragmentEnfermo = new ResponseFragmentEnfermo();
//    private FragmentManager fragmentManager;


    int SELECT_PICTURE = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkActivity = (CheckActivity) getActivity();
        checkActivity.jsonObject = null;
        checkActivity.bitmapImg = null;
        checkActivity.b64Img = "";
        cameraProviderFuture = ProcessCameraProvider.getInstance(this.getContext());
        ActivityCompat.requestPermissions(this.getActivity(),
                new String[]{Manifest.permission.CAMERA},1);
        previewView = getActivity().findViewById(R.id.previewView);
        btn_tp = getActivity().findViewById(R.id.btn_tp);
        btn_gallery = getActivity().findViewById(R.id.btn_gallery);
        imagePreview = getActivity().findViewById(R.id.imagePreview);
        imagePreview.setVisibility(View.INVISIBLE);
        progressBar   = checkActivity.findViewById(R.id.progressBar);

        Executor executor = new Executor() {
            @Override
            public void execute(Runnable runnable) {
                runnable.run();
            }
        };
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        btn_tp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                imageCapture.takePicture(executor,
                        new ImageCapture.OnImageCapturedCallback() {
                            @Override
                            public void onCaptureSuccess(@NonNull ImageProxy image) {
                                super.onCaptureSuccess(image);
                                String b64Img = convertImageProxyToB64(image);
//                                Log.d("base64",b64Img);
//                                Log.d("base64", String.valueOf(b64Img.length()));
                                image.close();
                                try {
                                    sendImg(b64Img);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                            @Override
                            public void onError(ImageCaptureException error) {
                            }
                        }
                );
            }
        });

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this.getContext()));

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });



    }

    public void sendImg(String b64Img) throws JSONException {
        String url = "http://34.176.120.172:8111/api/predictImageB64";

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("imageB64", b64Img);
        final String requestBody = jsonBody.toString();
        checkActivity.b64Img = b64Img;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressBar.setVisibility(View.INVISIBLE);
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("debug","JsonObject: "+ jsonObject.toString());
                    checkActivity.jsonObject = jsonObject;
                    Log.d("jsonTry", String.valueOf(jsonObject));
                    Boolean skinHasDiseases = (Boolean) checkActivity.jsonObject.get("skinHasDiseases");
                    FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();

                    if(skinHasDiseases){
                        fragmentTransaction.replace(R.id.fragmentContainer, responseFragmentEnfermo,"responseFragmentEnfermo");
                    }
                    else{
                        fragmentTransaction.replace(R.id.fragmentContainer, responseFragmentSano,"responseFragmentSano");
                    }

                    fragmentTransaction.commit();



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

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                         .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        imageCapture =  new ImageCapture.Builder()
                       .setTargetRotation(previewView.getDisplay().getRotation())
                       .build();

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector,imageCapture, preview);
    }

    private String convertImageProxyToB64(ImageProxy image) {
        ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
        byteBuffer.rewind();
        byte[] bytes = new byte[byteBuffer.capacity()];
        byteBuffer.get(bytes);
        byte[] clonedBytes = bytes.clone();
        Bitmap bitmap = BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.length);
        imagePreview.setImageBitmap(bitmap);
        imagePreview.setVisibility(View.VISIBLE);
        previewView.setVisibility(View.INVISIBLE);
        checkActivity.bitmapImg = bitmap;
        String encodedImage = convertBitmapToB64(bitmap);

        return encodedImage;

    }

    private String convertUriToB64(Uri imageUri) throws FileNotFoundException {
        InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        checkActivity.bitmapImg = bitmap;
        String encodedImage = convertBitmapToB64(bitmap);
        return encodedImage;
    }

    private String convertBitmapToB64(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String b64Img = "data:image/jpeg;base64,"+Base64.encodeToString(byteArray,Base64.DEFAULT);
        return b64Img;
    }

    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                progressBar.setVisibility(View.VISIBLE);
                Uri selectedImageUri = data.getData();
                imagePreview.setImageURI(selectedImageUri);
                imagePreview.setVisibility(View.VISIBLE);
                previewView.setVisibility(View.INVISIBLE);
                if (selectedImageUri != null) {
                    try {
                        String imgB64 = convertUriToB64(selectedImageUri);
                        sendImg(imgB64);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    // update the preview image in the layout
//                    IVPreviewImage.setImageURI(selectedImageUri);
                    Log.d("image", String.valueOf(selectedImageUri));
                }
            }
        }
    }
}