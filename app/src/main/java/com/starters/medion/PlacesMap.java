package com.starters.medion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.Locale;
import android.content.pm.PackageManager;
import android.location.Address;import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.ui.PlacePicker;

import com.gc.materialdesign.views.ButtonRectangle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.starters.medion.model.EventMedian;

import org.json.JSONObject;

/**
 * Created by stephenpaul on 15/11/16.
 */

public class PlacesMap extends AppCompatActivity  {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 9;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    int PLACE_REQUEST = 1;
    TextView placename;
    TextView placeaddress;
    TextView placelatlongs;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    TextView placeratings;
    TextView placepricelevel;
    TextView placephone;
    Double latitude;
    Double longitude;
    String place_id;
    Location mLastLocation;
    LatLngBounds.Builder bounds;
    GoogleApiClient mGoogleApiClient;
    private EventMedian eventMedian;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_map);
        placename = (TextView) findViewById(R.id.place_real_name);
        placeaddress = (TextView) findViewById(R.id.place_real_address);
        placelatlongs = (TextView) findViewById(R.id.place_real_latlongs);
        placeratings = (TextView) findViewById(R.id.place_real_ratings);
        ButtonRectangle map_button = (ButtonRectangle) findViewById(R.id.map_places);
                String s = getIntent().getExtras().getString("latlong");
                String[] latilongi = s.split("/");
                latitude = Double.parseDouble(latilongi[0]);
                longitude = Double.parseDouble(latilongi[1]);
                place_id = latilongi[2];
                String [] placedetais = place_id.split("!");
                System.out.println("name ="+placedetais[0]);
                System.out.println("rating="+placedetais[1]);
                System.out.println("open?"+placedetais[2]);
                placename.setText(placedetais[0].toString());
                placeratings.setText(placedetais[1].toString());
                placelatlongs.setText(latitude+","+longitude);
                placeaddress.setText(placedetais[2].toString());
                System.out.println("place id is: "+place_id+"you don't know");
                System.out.println("latitude="+latitude);
                System.out.println("longitude="+longitude);


        checkpermission();


        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:"+latitude+","+longitude+"?q=" + Uri.encode(placename.getText().toString()));

//                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

    }


    public void checkpermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }


    }


    public static String POST(String stringURL, EventMedian eventMedian) {
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
            connection.setConnectTimeout(50000);
            connection.setReadTimeout(50000);
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());

            // 3. build jsonObject
            JSONObject eventMedianJson = new JSONObject();
            eventMedianJson.accumulate("eventId", eventMedian.getEventID());
            eventMedianJson.accumulate("userFcmToken", eventMedian.getLatitude());
            eventMedianJson.accumulate("acceptance", eventMedian.getLongitude());


            // 4. convert JSONObject to JSON to String and send json content
            out.write(eventMedianJson.toString());
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
            eventMedian = new EventMedian();
            eventMedian.setEventID(110);
            eventMedian.setLatitude(Double.parseDouble(args[1]));
            eventMedian.setLongitude(Double.parseDouble(args[2]));


            return POST(args[3],eventMedian);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "You have signed up!", Toast.LENGTH_LONG).show();
        }
    }

}
