package com.example.clasifenfermedades.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.clasifenfermedades.Fragments.CheckActivity.CheckFragment;
import com.example.clasifenfermedades.R;

import org.json.JSONObject;

public class CheckActivity extends AppCompatActivity {
    public JSONObject jsonObject;
    public Bitmap bitmapImg;
    public String b64Img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragmentContainer, CheckFragment.class, null)
                    .commit();
        }
    }
}