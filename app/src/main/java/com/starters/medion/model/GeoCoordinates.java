package com.starters.medion.model;

/**
 * Created by KeerthiTejaNuthi on 12/2/16.
 */

public class GeoCoordinates {
    private double latitude;
    private double longitude;

    public GeoCoordinates(){

    }

    public GeoCoordinates(double latitute, double longitude){
        this.latitude = latitute;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
