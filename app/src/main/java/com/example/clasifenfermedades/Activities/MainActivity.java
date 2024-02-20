package com.example.clasifenfermedades.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.example.clasifenfermedades.R;

// TODO: 05-05-2023 Top de enfermedades reconocidas
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_menu_check = findViewById(R.id.btn_menu_check);

        btn_menu_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CheckActivity.class);
                startActivity(intent);
            }
        });

        Button btn_menu_cat = findViewById(R.id.btn_menu_cat);

        btn_menu_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CatalogActivity.class);
                startActivity(intent);
            }
        });
        Button btn_menu_about = findViewById(R.id.btn_menu_about);

        btn_menu_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent);
            }
        });
    }
}