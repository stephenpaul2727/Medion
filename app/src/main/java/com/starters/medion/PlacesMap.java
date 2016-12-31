package com.starters.medion;

import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.GoogleApiClient;

import com.gc.materialdesign.views.ButtonRectangle;
import com.google.android.gms.maps.model.LatLngBounds;
import com.starters.medion.model.EventMedian;

import org.json.JSONObject;


public class PlacesMap extends AppCompatActivity  {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 9;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    int PLACE_REQUEST = 1;
    private TextView placename;
    private TextView placeaddress;
    private TextView placelatlongs;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private TextView placeratings;
    TextView placepricelevel;
    TextView placephone;
    private Double latitude;
    private Double longitude;
    private String place_id;
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
        try
        {
            assert s != null;
            String[] latilongi = s.split("/");
                latitude = Double.parseDouble(latilongi[0]);
                longitude = Double.parseDouble(latilongi[1]);
                place_id = latilongi[2];
                String [] placedetais = place_id.split("!");
                placename.setText(placedetais[0]);
                placeratings.setText(placedetais[1]);
                placelatlongs.setText(latitude+","+longitude);
                placeaddress.setText(placedetais[2]);
        }
        catch(Exception e)
        {
            Toast.makeText(this,R.string.splitexception,Toast.LENGTH_LONG).show();
        }


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


    private void checkpermission() {
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


    private static String POST(String stringURL, EventMedian eventMedian) {
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
