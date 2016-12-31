package com.starters.medion;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.starters.medion.constants.config;
import com.starters.medion.contract.EventsContract;
import com.starters.medion.dbhelper.EventsDbhelper;
import com.starters.medion.model.UserEvent;
import com.starters.medion.service.TrackGPS;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class DecisionActivity extends AppCompatActivity {

    private static String fcmToken = null;
    private static final int MY_PERMISSIONS_AFL = 9;
    private static final int MY_PERMISSIONS_ACL = 11;
    private String eventId;
    private static final String TAG = DecisionActivity.class.getSimpleName();
    private TrackGPS trackGPS;
    private UserEvent userEvent;
    private String FILENAME ="fcm_details_file";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayFirebaseRegId();
        checkPermission();


    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("key");
            Log.e("mm", message);
            int notifyID=1;
            String newmes = message;
            NotificationManager notify = (NotificationManager) getSystemService(context.NOTIFICATION_SERVICE);
            String[] parts = message.split(",");

            switch (parts[0]) {
                case "EventCreated": {
                    System.out.println("ENTERED EVENT CREATED");

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.app_xxhdpi)
                                    .setContentTitle("Medion")
                                    .setContentText("New Event " + parts[2] + " created");
                    notify.notify(notifyID, mBuilder.build());
                    Toast.makeText(getApplicationContext(), "You have been Added to an Event", Toast.LENGTH_LONG).show();
                    eventId = parts[1];
                    EventsDbhelper mDbHelper = new EventsDbhelper(getApplicationContext());
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    ContentValues content = new ContentValues();
                    content.put(EventsContract.EventsEntry.COLUMN_NAME_EVENTID, parts[1]);
                    content.put(EventsContract.EventsEntry.COLUMN_NAME_EVENTNAME, parts[2]);
                    content.put(EventsContract.EventsEntry.COLUMN_NAME_DATE, parts[3]);
                    content.put(EventsContract.EventsEntry.COLUMN_NAME_TIME, parts[4]);
                    content.put(EventsContract.EventsEntry.COLUMN_NAME_MEMBERS, parts[5]);
                    content.put(EventsContract.EventsEntry.COLUMN_NAME_ADMIN, "MEMBER");
                    content.put(EventsContract.EventsEntry.COLUMN_NAME_LOCATION, "");
                    db.insert(EventsContract.EventsEntry.TABLE_NAME, null, content);
                    eventId = parts[1];

                    trackGPS = new TrackGPS(getApplicationContext(), DecisionActivity.this);
                    if (trackGPS.canGetLocation()) {
//                            geoCoordinates.setLatitude(trackGPS.getLatitude());
//                            geoCoordinates.setLongitude(trackGPS.getLongitude());
                        System.out.println("LOCATION" + trackGPS.getLongitude());
                        Toast.makeText(DecisionActivity.this, "Your lat is: " + trackGPS.getLatitude() + " your long is: " + trackGPS.getLongitude(), Toast.LENGTH_LONG).show();
//                    new MainActivity.HttpAsyncTask().execute(parts[1], fcmToken, String.valueOf(trackGPS.getLatitude()), String.valueOf(trackGPS.getLongitude()),"http://149.161.150.243:8080/api/addUserEvent");

                        SharedPreferences pref = getApplicationContext().getSharedPreferences(config.SHARED_PREF, 0);
                        String reg = pref.getString("regId", null);
                        new HttpAsyncTask().execute(parts[1], reg, String.valueOf(trackGPS.getLatitude()), String.valueOf(trackGPS.getLongitude()), "https://whispering-everglades-62915.herokuapp.com/api/addUserEvent");
                    }
                    break;
                }
                case "MedionCalculated": {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.app_xxhdpi)
                                    .setContentTitle("Medion")
                                    .setContentText(message);
                    notify.notify(notifyID, mBuilder.build());

                    System.out.println("inside medion..!");
                    String latitude = parts[1];
                    String[] resu = parts[2].split("/");
                    String longitude = resu[0];
                    String place_id = resu[1];
                    String filename = config.LOCS;
                    String evid = parts[3];
                    FileOutputStream outputStream;

                    try {
                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(place_id.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    EventsDbhelper eventsDbhelper = new EventsDbhelper(getApplicationContext());
                    SQLiteDatabase db = eventsDbhelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(EventsContract.EventsEntry.COLUMN_NAME_LOCATION, latitude + "," + longitude);
                    db.update(EventsContract.EventsEntry.TABLE_NAME, contentValues, evid, null);
//                db.execSQL("Update " + EventsContract.EventsEntry.TABLE_NAME+" set "+EventsContract.EventsEntry.COLUMN_NAME_LOCATION+"="+parts[1]+","+parts[2]+" where "+EventsContract.EventsEntry.COLUMN_NAME_EVENTID+"="+evid, null);
                    Intent mesintent = new Intent(DecisionActivity.this, PlacesMap.class);
                    mesintent.putExtra("latlong", latitude + "/" + longitude + "/" + place_id);
                    startActivity(mesintent);

                    break;
                }
                case "Event with id": {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.app_xxhdpi)
                                    .setContentTitle("Medion")
                                    .setContentText(message);
                    notify.notify(notifyID, mBuilder.build());
                    EventsDbhelper evehelp = new EventsDbhelper(getApplicationContext());
                    SQLiteDatabase db = evehelp.getWritableDatabase();
                    db.delete(EventsContract.EventsEntry.TABLE_NAME, EventsContract.EventsEntry.COLUMN_NAME_EVENTID + "=?", new String[]{parts[1]});
                    db.close();
                    evehelp.close();
                    break;
                }
                case "Member with Phone": {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.app_xxhdpi)
                                    .setContentTitle("Medion")
                                    .setContentText(message);
                    notify.notify(notifyID, mBuilder.build());
                    Toast.makeText(DecisionActivity.this, message, Toast.LENGTH_LONG).show();
                    EventsDbhelper evehelp = new EventsDbhelper(getApplicationContext());
                    SQLiteDatabase db = evehelp.getWritableDatabase();
                    String phone = parts[1];
                    String eveid = parts[2];
                    ContentValues cval = new ContentValues();
                    Cursor c;
                    c = evehelp.getListContents();
                    c.moveToFirst();
                    while (c.moveToNext()) {
                        if (c.getString(c.getColumnIndex(EventsContract.EventsEntry.COLUMN_NAME_EVENTID)).equals(eveid)) {
                            String mem = c.getString(c.getColumnIndex(EventsContract.EventsEntry.COLUMN_NAME_MEMBERS));
                            String[] splitmem = mem.split(",");
                            StringBuilder s = new StringBuilder();
                            for (String aSplitmem : splitmem) {
                                //noinspection StatementWithEmptyBody
                                if (aSplitmem.equals(phone)) {
                                    //do nothing.
                                } else {
                                    s.append(aSplitmem).append(",");
                                }
                            }
                            //noinspection StatementWithEmptyBody
                            if (s.length() == 0) {//do nothing
                            } else {
                                s.setLength(s.length() - 1);
                            }
                            cval.put(EventsContract.EventsEntry.COLUMN_NAME_MEMBERS, s.toString());
                            db.update(EventsContract.EventsEntry.TABLE_NAME, cval, EventsContract.EventsEntry.COLUMN_NAME_EVENTID + "=?", new String[]{eveid});
                        }
                    }
                    db.close();
                    evehelp.close();
                    break;
                }
                default: {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.app_xxhdpi)
                                    .setContentTitle("Medion")
                                    .setContentText(message);
                    notify.notify(notifyID, mBuilder.build());
                    Toast.makeText(DecisionActivity.this, "Event Finalized", Toast.LENGTH_LONG).show();
                    String[] pawns = newmes.split("!");
                    EventsDbhelper evehelp = new EventsDbhelper(getApplicationContext());
                    SQLiteDatabase db = evehelp.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put(EventsContract.EventsEntry.COLUMN_NAME_MEMBERS, pawns[3]);
                    cv.put(EventsContract.EventsEntry.COLUMN_NAME_LOCATION, pawns[5]);
                    db.update(EventsContract.EventsEntry.TABLE_NAME, cv, EventsContract.EventsEntry.COLUMN_NAME_EVENTID + " = ?", new String[]{pawns[1]});
                    db.close();
                    evehelp.close();
                    break;
                }
            }

        }
    };

    private void checkPermission()
    {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_AFL);
        }
        else
        {
            Intent intent;
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
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_AFL) {
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intent;
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
            else
            {
                Toast.makeText(this,"until you give permissions, this app cannot function properly",Toast.LENGTH_LONG).show();
                checkPermission();
            }
            return;
        }


    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        fcmToken = regId;
        Log.e(TAG, "Firebase reg id: " + regId);
        System.out.println("myfcm token is:"+fcmToken);

    }


    private static String POST(String stringURL, UserEvent userEvent) {
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
            userEvent.setAcceptance();
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
