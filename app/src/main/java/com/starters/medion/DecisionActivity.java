package com.starters.medion;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.starters.medion.constants.config;
import com.starters.medion.contract.EventsContract;
import com.starters.medion.dbhelper.EventsDbhelper;
import com.starters.medion.dbtasks.InsertTask;
import com.starters.medion.model.UserEvent;
import com.starters.medion.service.TrackGPS;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class DecisionActivity extends AppCompatActivity {

    public static String fcmToken = null;
    public String eventId;
    private static final String TAG = DecisionActivity.class.getSimpleName();
    private TrackGPS trackGPS;
    private UserEvent userEvent;
    private String FILENAME ="fcm_details_file";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        displayFirebaseRegId();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("intentKey"));

        try {
            FileInputStream f =openFileInput("login_details_file");
            intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        } catch (FileNotFoundException e) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            e.printStackTrace();
        }

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("key");
            Log.e("mm", message);
            int notifyID=1;
            NotificationManager notify = (NotificationManager) getSystemService(context.NOTIFICATION_SERVICE);
            String[] parts = message.split(",");

            if(parts[0].equals("EventCreated")) {
                System.out.println("ENTERED EVENT CREATED");

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.appimage)
                                .setContentTitle("Medion")
                                .setContentText("New Event "+parts[2]+" created");
                notify.notify(notifyID,mBuilder.build());
                Toast.makeText(getApplicationContext(), "You have been Added to an Event", Toast.LENGTH_LONG).show();
                eventId = parts[1];
                InsertTask insert = new InsertTask(getApplicationContext());
                insert.execute("",parts[1],parts[2],parts[3],parts[4],parts[5],"MEMBER","");
                eventId = parts[1];

                trackGPS = new TrackGPS(DecisionActivity.this);
                if (trackGPS.canGetLocation()) {
//                            geoCoordinates.setLatitude(trackGPS.getLatitude());
//                            geoCoordinates.setLongitude(trackGPS.getLongitude());
                    System.out.println("LOCATION"+trackGPS.getLongitude());
                    Toast.makeText(DecisionActivity.this,"Your lat is: "+trackGPS.getLatitude()+" your long is: "+trackGPS.getLongitude(),Toast.LENGTH_LONG).show();
//                    new MainActivity.HttpAsyncTask().execute(parts[1], fcmToken, String.valueOf(trackGPS.getLatitude()), String.valueOf(trackGPS.getLongitude()),"http://149.161.150.243:8080/api/addUserEvent");
                    new HttpAsyncTask().execute(parts[1], fcmToken, String.valueOf(trackGPS.getLatitude()), String.valueOf(trackGPS.getLongitude()), "https://whispering-everglades-62915.herokuapp.com/api/addUserEvent");
                }
            }else if(parts[0].equals("MedionCalculated")){

                System.out.println("inside medion..!");
                String latitude = parts[1];
                String [] resu = parts[2].split("/");
                String longitude = resu[0];
                String place_id = resu[1];
                String evid = parts[3];
                EventsDbhelper eventsDbhelper = new EventsDbhelper(getApplicationContext());
                SQLiteDatabase db = eventsDbhelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(EventsContract.EventsEntry.COLUMN_NAME_LOCATION,latitude+","+longitude);
                db.update(EventsContract.EventsEntry.TABLE_NAME,contentValues,evid,null);
//                db.execSQL("Update " + EventsContract.EventsEntry.TABLE_NAME+" set "+EventsContract.EventsEntry.COLUMN_NAME_LOCATION+"="+parts[1]+","+parts[2]+" where "+EventsContract.EventsEntry.COLUMN_NAME_EVENTID+"="+evid, null);
                Intent mesintent=new Intent(DecisionActivity.this,PlacesMap.class);
                mesintent.putExtra("latlong",latitude+"/"+longitude+"/"+place_id);
                startActivity(mesintent);

            }else if(parts[0].equals("FinalPlace")){
                String latitude = parts[1];
                String longitude = parts[2];
                Intent msgfinal = new Intent(DecisionActivity.this,Home.class);
                msgfinal.putExtra("ll",latitude+"/"+longitude);
                startActivity(msgfinal);
            }

        }
    };

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        fcmToken = regId;
        config.ownerfcm = regId;
        Log.e(TAG, "Firebase reg id: " + regId);
        System.out.println("myfcm token is:"+fcmToken);

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
            userEvent.setAcceptance(true);
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
