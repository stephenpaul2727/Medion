package com.starters.medion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.starters.medion.constants.config;

import com.starters.medion.model.GeoCoordinates;
import com.starters.medion.service.TrackGPS;

import com.starters.medion.dbtasks.InsertTask;

import com.starters.medion.utils.NotificationUtils;

import com.gc.materialdesign.views.ButtonRectangle;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static String fcmToken = null;
    private static GeoCoordinates geoCoordinates;
    private TrackGPS trackGPS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addLoginClickListener();
        addSignupClickListener();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    //String message = "Fun Event,02/12/2016,10am,8123456544|5432126879";
                    String[] parts = message.split(",");
                    InsertTask insert = new InsertTask(getApplicationContext());
                    insert.execute(parts);
                    Toast.makeText(getBaseContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    Log.d("FIREBASE sent:", message);
//                    txtMessage.setText(message);
                    geoCoordinates = new GeoCoordinates();
                    trackGPS = new TrackGPS(MainActivity.this);
                    if(trackGPS.canGetLocation()){
                        geoCoordinates.setLatitude(trackGPS.getLatitude());
                        geoCoordinates.setLongitude(trackGPS.getLongitude());
                    }

                }
            }
        };
        displayFirebaseRegId();
    }
    public void addLoginClickListener()
    {
        ButtonRectangle login = (ButtonRectangle) findViewById(R.id.Connectstage_login);
        login.setFocusable(true);
        login.setFocusableInTouchMode(true);
        login.requestFocus();
        login.setOnClickListener(new OnClickListener() {
                                     public void onClick(View v) {
                                         Intent intent = new Intent(getApplicationContext(),Home.class);
                                         startActivity(intent);
                                     }
                                 }

        );
    }

    public void addSignupClickListener()
    {
        ButtonRectangle signup = (ButtonRectangle) findViewById(R.id.ConnectStage_SignUp);
        signup.setFocusable(true);
        signup.setFocusableInTouchMode(true);
        signup.requestFocus();
        signup.setOnClickListener(new OnClickListener(){


            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }

        });
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        fcmToken = regId;
        Log.e(TAG, "Firebase reg id: " + regId);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}