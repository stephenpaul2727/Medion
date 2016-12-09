package com.starters.medion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
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
import com.starters.medion.model.UserEvent;
import com.starters.medion.service.TrackGPS;
import com.starters.medion.dbtasks.InsertTask;
import com.starters.medion.utils.NotificationUtils;

import com.gc.materialdesign.views.ButtonRectangle;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static String fcmToken = null;
    private static GeoCoordinates geoCoordinates;
    private TrackGPS trackGPS;
    private UserEvent userEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addLoginClickListener();
        addSignupClickListener();

        /*mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d("Onreceive","inside");
                String key = intent.getStringExtra("key");
                Log.d("message in Main",key);

                // checking for type intent filter
                if (intent.getAction().equals(config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(config.TOPIC_GLOBAL);

                    displayFirebaseRegId();
                    Log.d("Onreceive","inside");

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
                    if(parts[0].equals("EventCreated")) {
                        System.out.println("ENTERED EVENT CREATED");
//                        geoCoordinates = new GeoCoordinates();
                        trackGPS = new TrackGPS(MainActivity.this);
                        if (trackGPS.canGetLocation()) {
//                            geoCoordinates.setLatitude(trackGPS.getLatitude());
//                            geoCoordinates.setLongitude(trackGPS.getLongitude());
                            System.out.println("LOCATION"+trackGPS.getLongitude());
                            new MainActivity.HttpAsyncTask().execute(parts[1], fcmToken, String.valueOf(trackGPS.getLatitude()), String.valueOf(trackGPS.getLongitude()), "https://whispering-everglades-62915.herokuapp.com/api/addUserEvent");
                        }
                    }

                }
            }
        };*/
        displayFirebaseRegId();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("intentKey"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("key");
            Log.e("mm", message);
            Toast.makeText(getApplicationContext(), "You have been Added to an Event", Toast.LENGTH_LONG).show();
            String[] parts = message.split(",");
            if(parts[0].equals("EventCreated")) {
                System.out.println("ENTERED EVENT CREATED");
//                        geoCoordinates = new GeoCoordinates();
                trackGPS = new TrackGPS(MainActivity.this);
                if (trackGPS.canGetLocation()) {
//                            geoCoordinates.setLatitude(trackGPS.getLatitude());
//                            geoCoordinates.setLongitude(trackGPS.getLongitude());
                    System.out.println("LOCATION"+trackGPS.getLongitude());
//                    new MainActivity.HttpAsyncTask().execute(parts[1], fcmToken, String.valueOf(trackGPS.getLatitude()), String.valueOf(trackGPS.getLongitude()),"http://149.161.150.243:8080/api/addUserEvent");
                    new MainActivity.HttpAsyncTask().execute(parts[1], fcmToken, String.valueOf(trackGPS.getLatitude()), String.valueOf(trackGPS.getLongitude()), "https://whispering-everglades-62915.herokuapp.com/api/addUserEvent");
                }
            }

        }
    };

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

    public static String POST(String stringURL, UserEvent userEvent) {
        InputStream inputStream = null;
        String result = "";
        try {

            Log.d("InputStream", "Before Connecting");
            // 1. create URL
            URL url = new URL(stringURL);

            // 2. create connection to given URL
            URLConnection connection = url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());

            // 3. build jsonObject
            JSONObject userEventJson = new JSONObject();
            userEventJson.accumulate("eventId", userEvent.getEventId());
            userEventJson.accumulate("userFcmToken", userEvent.getUserFcmToken());
            userEventJson.accumulate("acceptance", true);
            userEventJson.accumulate("latitude", userEvent.getLatitude());
            userEventJson.accumulate("longitude", userEvent.getLongitude());

            // 4. convert JSONObject to JSON to String and send json content
            out.write(userEventJson.toString());
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while (in.readLine() != null) {
                System.out.println(in);
            }
            System.out.println("\nMedion REST Service Invoked Successfully..");
            in.close();
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... args) {
            userEvent = new UserEvent();
            userEvent.setEventId(Integer.parseInt(args[0]));
            userEvent.setUserFcmToken(args[1]);
            userEvent.setLatitude(args[2]);
            userEvent.setLongitude(args[3]);

            return POST(args[4],userEvent);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "You have signed up!", Toast.LENGTH_LONG).show();
        }
    }

}