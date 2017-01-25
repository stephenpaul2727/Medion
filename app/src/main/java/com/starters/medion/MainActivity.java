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

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
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
    private EditText userName;
    private EditText password;
    private String username;
    private String pass;
    private String res;
    private ProgressBarCircularIndeterminate progbarmain;
    private android.widget.CheckBox cb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addLoginClickListener();
        addSignupClickListener();
        userName = (EditText) findViewById(R.id.ConnectStage_Username);
        password = (EditText) findViewById(R.id.ConnectStage_Password);
        progbarmain =(ProgressBarCircularIndeterminate)findViewById(R.id.progressBarMain);

    }

    //adding event listener to login button
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
                                             Toast.makeText(MainActivity.this,"Loggin in. Please wait!",Toast.LENGTH_SHORT).show();
                                             progbarmain.setVisibility(View.VISIBLE);
                                             //starting asynctask to post request to server
                                             new LoginAsyncTask().execute(username, pass);
                                         }
                                     }
                                 }

        );
    }

    //adding functionality for sign up button
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

    //Login async task to send the username and password to the server
    private class LoginAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            if(res.equals("Valid User"))
            {
                    String FILENAME = "login_details_file";
                    String string = username;

                    try {
                        //creating and writing username to the file login_details_file in private mode.
                        FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                        fos.write(string.getBytes());
                        fos.close();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                Toast.makeText(MainActivity.this,"Successfully logged in",Toast.LENGTH_SHORT).show();
                //progress bar set to invisible.
                progbarmain.setVisibility(View.INVISIBLE);
                //if successfully logged in , control redirects to homepage.
                Intent intent = new Intent(getApplicationContext(),Home.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(MainActivity.this,"Invalid Login Details",Toast.LENGTH_LONG).show();
                progbarmain.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... params) {

                //passing username and password as get request to verify they exist in the server db.
                res = GET("https://whispering-everglades-62915.herokuapp.com/api/login?name="+params[0]+"&pass="+params[1]);

            return res ;
        }


    }
    //function to send get request to the server
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
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                result = inputLine;
            in.close();
            System.out.println("\nsuccessfully sent login details.");
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }
}