package com.starters.medion;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import android.widget.Button;
import com.gc.materialdesign.views.ButtonRectangle;
import com.starters.medion.model.User;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by KeerthiTejaNuthi on 11/28/16.
 */

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private EditText etName, etEmail, etPhone, etPassword, etRePassword;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String rePassword;
    private String fcmToken;
    private User user;
    private Button signUpButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        etName = (EditText) findViewById(R.id.Signup_edit_name);
        etEmail = (EditText) findViewById(R.id.Signup_edit_email);
        etPhone = (EditText) findViewById(R.id.Signup_edit_phonenumber);
        etPassword = (EditText) findViewById(R.id.Signup_edit_password);
        etRePassword = (EditText) findViewById(R.id.Signup_edit_reenterpassword);

        signUpButton = (Button) findViewById(R.id.Signup_register);
        Log.d("InsideONCREATE", "Before Connecting");

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("InsideButton", "Before Connecting");
                name = etName.getText().toString();
                email = etEmail.getText().toString();
                phone = etPhone.getText().toString();
                password = etPassword.getText().toString();
                rePassword = etRePassword.getText().toString();

                if (!validate())
                    Toast.makeText(getBaseContext(), "Please fill All Fields or Make sure your Password matches", Toast.LENGTH_LONG).show();
                else
                    new HttpAsyncTask().execute(name, email, phone, password, "https://whispering-everglades-62915.herokuapp.com/api/addUser");
//                    new HttpAsyncTask().execute(name, email, phone, password, "http://149.161.150.186:8080/api/addUser");
            }
        });
    }



    @Override
    public void onClick(View view) {

        Log.d("InsideButton", "Before Connecting");
        name = etName.getText().toString();
        email = etEmail.getText().toString();
        phone = etPhone.getText().toString();
        password = etPassword.getText().toString();
        rePassword = etRePassword.getText().toString();

        switch (view.getId()){
            case R.id.Signup_register:
                if (!validate())
                    Toast.makeText(getBaseContext(), "Please fill All Fields or Make sure your Password matches", Toast.LENGTH_LONG).show();
                new HttpAsyncTask().execute(name, email, phone, password, "http://149.161.149.223:8080/Medion/api/addTheUser");
                break;
        }
    }

    private boolean validate() {
        if(etName.getText().toString().trim().equals(""))
            return false;
        else if(etPhone.getText().toString().trim().equals(""))
            return false;
        else if(etEmail.getText().toString().trim().equals(""))
            return false;
        else if (etPassword.getText().toString().trim().equals(""))
            return false;
        else if(etRePassword.getText().toString().trim().equals(""))
            return false;
        else if (!etPassword.getText().toString().equals(etRePassword.getText().toString()))
            return false;
        else
            return true;
    }

    public static String POST(String stringURL, User user) {
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
            JSONObject userJson = new JSONObject();
            userJson.accumulate("name", user.getName());
            userJson.accumulate("email", user.getEmail());
            userJson.accumulate("phone", user.getPhone());
            userJson.accumulate("password", user.getPassword());
            userJson.accumulate("fcmToken", user.getFcmToken());

            // 4. convert JSONObject to JSON to String and send json content
            out.write(userJson.toString());
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
            user = new User();
            user.setName(args[0]);
            user.setEmail(args[1]);
            user.setPhone(args[2]);
            user.setPassword(args[3]);

            user.setFcmToken(MainActivity.fcmToken);

            System.out.println(args[4]);
            return POST(args[4],user);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "You have signed up!", Toast.LENGTH_LONG).show();
        }
    }
}