package com.starters.medion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.starters.medion.constants.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DecisionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        try {
            FileInputStream f =openFileInput("login_details_file");
            intent = new Intent(this, Home.class);
        } catch (FileNotFoundException e) {
            intent = new Intent(this, MainActivity.class);
            e.printStackTrace();
        }
            startActivity(intent);
            finish();
    }
}
