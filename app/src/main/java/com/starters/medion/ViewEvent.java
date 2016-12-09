package com.starters.medion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ResourceBundle;

/**
 * Created by Ashish on 12/8/2016.
 */
public class ViewEvent extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_event_layout);

        Bundle b = getIntent().getExtras();
        if(b != null){
            String eventId = b.getString("id");
            System.out.println("IDID:" + eventId);
        }

    }
}
