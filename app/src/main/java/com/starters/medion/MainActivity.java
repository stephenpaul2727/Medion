package com.starters.medion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;
import com.starters.medion.model.GeoCoordinates;
import com.starters.medion.model.UserEvent;
import com.starters.medion.service.TrackGPS;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static String fcmToken = null;
    private static GeoCoordinates geoCoordinates;
    private TrackGPS trackGPS;
    private UserEvent userEvent;
    private EditText userName;
    private EditText password;
    private String username;
    private String pass;
    private String res;
    private String eventId;
    private android.widget.CheckBox cb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addLoginClickListener();
        addSignupClickListener();
        userName = (EditText) findViewById(R.id.ConnectStage_Username);
        password = (EditText) findViewById(R.id.ConnectStage_Password);

//        displayFirebaseRegId();
//        LocalBroadcastManager.getInstance(this).registerReceiver(
//                mMessageReceiver, new IntentFilter("intentKey"));
    }

//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // Get extra data included in the Intent
//            String message = intent.getStringExtra("key");
//            Log.e("mm", message);
//            int notifyID=1;
//            NotificationManager notify = (NotificationManager) getSystemService(context.NOTIFICATION_SERVICE);
//            String[] parts = message.split(",");
//
//            if(parts[0].equals("EventCreated")) {
//                System.out.println("ENTERED EVENT CREATED");
//
//                NotificationCompat.Builder mBuilder =
//                        new NotificationCompat.Builder(getApplicationContext())
//                                .setSmallIcon(R.drawable.appimage)
//                                .setContentTitle("Medion")
//                                .setContentText("New Event "+parts[2]+" created");
//                notify.notify(notifyID,mBuilder.build());
//                Toast.makeText(getApplicationContext(), "You have been Added to an Event", Toast.LENGTH_LONG).show();
//                eventId = parts[1];
//                InsertTask insert = new InsertTask(getApplicationContext());
//                insert.execute("",parts[1],parts[2],parts[3],parts[4],parts[5],"MEMBER","");
//                eventId = parts[1];
//
//                trackGPS = new TrackGPS(MainActivity.this);
//                if (trackGPS.canGetLocation()) {
////                            geoCoordinates.setLatitude(trackGPS.getLatitude());
////                            geoCoordinates.setLongitude(trackGPS.getLongitude());
//                    System.out.println("LOCATION"+trackGPS.getLongitude());
//                    Toast.makeText(MainActivity.this,"Your lat is: "+trackGPS.getLatitude()+" your long is: "+trackGPS.getLongitude(),Toast.LENGTH_LONG).show();
////                    new MainActivity.HttpAsyncTask().execute(parts[1], fcmToken, String.valueOf(trackGPS.getLatitude()), String.valueOf(trackGPS.getLongitude()),"http://149.161.150.243:8080/api/addUserEvent");
//                    new MainActivity.HttpAsyncTask().execute(parts[1], fcmToken, String.valueOf(trackGPS.getLatitude()), String.valueOf(trackGPS.getLongitude()), "https://whispering-everglades-62915.herokuapp.com/api/addUserEvent");
//                }
//            }else if(parts[0].equals("MedionCalculated")){
//
//                System.out.println("inside medion..!");
//                String latitude = parts[1];
//                String [] resu = parts[2].split("/");
//                String longitude = resu[0];
//                String place_id = resu[1];
//                String evid = parts[3];
//                EventsDbhelper eventsDbhelper = new EventsDbhelper(getApplicationContext());
//                SQLiteDatabase db = eventsDbhelper.getWritableDatabase();
//                ContentValues contentValues = new ContentValues();
//                contentValues.put(EventsContract.EventsEntry.COLUMN_NAME_LOCATION,latitude+","+longitude);
//                db.update(EventsContract.EventsEntry.TABLE_NAME,contentValues,evid,null);
////                db.execSQL("Update " + EventsContract.EventsEntry.TABLE_NAME+" set "+EventsContract.EventsEntry.COLUMN_NAME_LOCATION+"="+parts[1]+","+parts[2]+" where "+EventsContract.EventsEntry.COLUMN_NAME_EVENTID+"="+evid, null);
//                Intent mesintent=new Intent(MainActivity.this,PlacesMap.class);
//                mesintent.putExtra("latlong",latitude+"/"+longitude+"/"+place_id);
//                startActivity(mesintent);
//
//            }else if(parts[0].equals("FinalPlace")){
//                String latitude = parts[1];
//                String longitude = parts[2];
//                Intent msgfinal = new Intent(MainActivity.this,Home.class);
//                msgfinal.putExtra("ll",latitude+"/"+longitude);
//                startActivity(msgfinal);
//            }
//
//        }
//    };

    private void addLoginClickListener()
    {
        Button login = (Button) findViewById(R.id.Connectstage_login);
        login.setFocusable(true);
        login.setFocusableInTouchMode(true);
        login.requestFocus();
        login.setOnClickListener(new OnClickListener() {
                                     public void onClick(View v) {
                                         if(userName.getText().toString().isEmpty() || password.getText().toString().isEmpty())
                                         {
                                             Toast.makeText(getApplicationContext(),"Invalid username or password sequences",Toast.LENGTH_LONG).show();
                                             return;
                                         }
                                         else {
                                             username = userName.getText().toString();
                                             pass = password.getText().toString();
                                             new LoginAsyncTask().execute(username, pass);
//                                         Intent intent = new Intent(getApplicationContext(),Home.class);
//                                         startActivity(intent);
                                         }
                                     }
                                 }

        );
    }

    private void addSignupClickListener()
    {
        Button signup = (Button) findViewById(R.id.ConnectStage_SignUp);
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

//    private void displayFirebaseRegId() {
//        SharedPreferences pref = getApplicationContext().getSharedPreferences(config.SHARED_PREF, 0);
//        String regId = pref.getString("regId", null);
//        fcmToken = regId;
//        Log.e(TAG, "Firebase reg id: " + regId);
//
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // register GCM registration complete receiver
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(config.REGISTRATION_COMPLETE));
//
//        // register new push message receiver
//        // by doing this, the activity will be notified each time a new message arrives
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(config.PUSH_NOTIFICATION));
//
//        // clear the notification area when the app is opened
//        NotificationUtils.clearNotifications(getApplicationContext());
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
//    }

    private class LoginAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
//            Toast.makeText(MainActivity.this,"Successfully sent login details to server",Toast.LENGTH_LONG).show();
            if(res.equals("Valid User"))
            {
                    String FILENAME = "login_details_file";
                    String string = username;

                    try {
                        FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                        fos.write(string.getBytes());
                        fos.close();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                Toast.makeText(MainActivity.this,"Successfully logged in",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),Home.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(MainActivity.this,"Invalid Login Details",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

                res = GET("https://whispering-everglades-62915.herokuapp.com/api/login?name="+params[0]+"&pass="+params[1]);

            return res ;
        }


    }
    private static String GET(String stringURL) {
        String result = "";
        try {

            Log.d("InputStream", "Before Connecting");
            // 1. create URL
            URL url = new URL(stringURL);

            // 2. create connection to given URL
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
//            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            System.out.println(connection.getResponseMessage());
//            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
//
//            // 3. build jsonObject
//           JSONObject json = new JSONObject();
//            json.put("username",myuser);
//            json.put("password",mypass);
//
//            // 4. convert JSONObject to JSON to String and send json content
//            out.write(json.toString());
//            out.flush();
//            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                result = inputLine;
            in.close();
//            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//
//            while (in.readLine() != null) {
//                System.out.println(in);
//            }
            System.out.println("\nsuccessfully sent login details.");
//            in.close();
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }
}