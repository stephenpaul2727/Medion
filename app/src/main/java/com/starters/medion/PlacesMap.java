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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import java.util.ArrayList;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
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

import static android.app.Activity.RESULT_OK;

/**
 * Created by stephenpaul on 15/11/16.
 */

public class PlacesMap extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    int PLACE_REQUEST =1;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    TextView mapname;
    TextView mapaddress;
    TextView mapphonenumber;
    Double latitude;
    Double longitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_map);
        ButtonRectangle map_button = (ButtonRectangle)findViewById(R.id.map_places);
        mapname= (TextView)findViewById(R.id.map_name);
        mapaddress= (TextView)findViewById(R.id.map_Address);
        mapphonenumber= (TextView)findViewById(R.id.map_Phonenumber);
        LocationManager lm =(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        final LatLngBounds bounds = new LatLngBounds(new LatLng(latitude,longitude),new LatLng(latitude+0.4,longitude+0.4));

        final GoogleApiClient mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        ArrayList<String> restrictToRestaurants = new ArrayList<>();
        restrictToRestaurants.add(Integer.toString(Place.TYPE_RESTAURANT));
        final PlaceFilter pf;
        pf = new PlaceFilter(false, restrictToRestaurants);
        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( ContextCompat.checkSelfPermission( PlacesMap.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

                    ActivityCompat.requestPermissions( PlacesMap.this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                            MY_PERMISSION_REQUEST_FINE_LOCATION );
                }


                PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, pf);

                result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                    @Override
                    public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                        final CharSequence thirdPartyAttributions = likelyPlaces.getAttributions();
                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            System.out.println("tag"+String.format("Place '%s' has likelihood: %g"+
                                    placeLikelihood.getPlace().getName()+
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
                    startActivityForResult(intent,PLACE_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();

                }

            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

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
                mapname.setText(s);
                mapaddress.setText(t);
                mapphonenumber.setText(u);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
