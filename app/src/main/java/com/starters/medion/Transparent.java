package com.starters.medion;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Transparent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent extractIntent = getIntent();
        String mess =extractIntent.getStringExtra("message");
        Intent intent = new Intent("intentKey");
        intent.putExtra("key", mess);
        LocalBroadcastManager.getInstance(this).sendBroadcast(extractIntent);
    }
}
