package com.starters.medion;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.LayoutInflater;
import android.view.View;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.ui.PlacePicker;

import com.gc.materialdesign.views.ButtonRectangle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.starters.medion.model.EventMedian;
import com.starters.medion.model.UserEvent;

import org.json.JSONObject;

import static android.R.attr.data;
import static android.app.Activity.RESULT_OK;
import static com.starters.medion.MainActivity.fcmToken;

/**
 * Created by stephenpaul on 15/11/16.
 */

public class PlacesMap extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 9;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    int PLACE_REQUEST = 1;
    TextView mapname;
    TextView mapaddress;
    TextView mapphonenumber;
    TextView maplat;
    TextView maplong;
    Double latitude = 39.768826;
    Double longitude = -86.168968;
    Location mLastLocation;
    LatLngBounds.Builder bounds;
    GoogleApiClient mGoogleApiClient;
    private EventMedian eventMedian;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_map);
        ButtonRectangle map_button = (ButtonRectangle) findViewById(R.id.map_places);
//        if(getIntent().getExtras().getString("latlong")!= null) {
//        String s = getIntent().getExtras().getString("latlong");
//        String[] latilongi = s.split("/");
//        latitude = Double.parseDouble(latilongi[0]);
//        longitude = Double.parseDouble(latilongi[1]);
//        }
        mapname = (TextView) findViewById(R.id.map_name);
        mapaddress = (TextView) findViewById(R.id.map_Address);
        mapphonenumber = (TextView) findViewById(R.id.map_Phonenumber);
        maplat = (TextView) findViewById(R.id.map_lat);
        maplong = (TextView) findViewById(R.id.map_long);

        checkpermission();

        final LatLngBounds bounds = new LatLngBounds(new LatLng(latitude-0.008983,longitude-0.015060),new LatLng(latitude+0.008983,longitude+0.015060));

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();

        ArrayList<String> restrictToRestaurants = new ArrayList<>();
        restrictToRestaurants.add(Integer.toString(Place.TYPE_RESTAURANT));
        final PlaceFilter pf;
        pf = new PlaceFilter(false, restrictToRestaurants);



        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(PlacesMap.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(PlacesMap.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }


                PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, pf);

                result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                    @Override
                    public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                        final CharSequence thirdPartyAttributions = likelyPlaces.getAttributions();
                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            System.out.println("tag" + String.format("Place '%s' has likelihood: %g" +
                                    placeLikelihood.getPlace().getName() +
                                    placeLikelihood.getLikelihood()));


                        }
                        likelyPlaces.release();
                    }
                });
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                builder.setLatLngBounds(bounds);
                Intent intent;
                try {
                    intent = builder.build(PlacesMap.this);
                    startActivityForResult(intent, PLACE_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();

                }


            }
        });

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            latitude=mLastLocation.getLatitude();

            longitude = mLastLocation.getLongitude();
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);



                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PLACE_REQUEST)
        {
            if(resultCode==RESULT_OK)
            {
                Place place=PlacePicker.getPlace(this,data);
                String s = (String) place.getName();
                String t = (String) place.getAddress();
                String u = (String) place.getPhoneNumber();
                LatLng latLng= place.getLatLng();
                //THese are the required place details which the user has picked.
                mapname.setText(s);
                mapaddress.setText(t);
                mapphonenumber.setText(u);
                maplat.setText(Double.toString(latLng.latitude));
                maplong.setText(Double.toString(latLng.longitude));
                new PlacesMap.HttpAsyncTask().execute("AA",Double.toString(latLng.latitude),Double.toString(latLng.longitude), "https://whispering-everglades-62915.herokuapp.com/api/sendMedian");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
