package com.starters.medion.model;

/**
 * Created by KeerthiTejaNuthi on 12/8/16.
 */

public class UserEvent {

    private int eventId;
    private String userFcmToken;
    private boolean acceptance;
    private String latitude;
    private String longitude;

    public int getEventId() {
        return eventId;
    }
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    public String getUserFcmToken() {
        return userFcmToken;
    }
    public void setUserFcmToken(String userFcmToken) {
        this.userFcmToken = userFcmToken;
    }
    public boolean isAcceptance() {
        return acceptance;
    }
    public void setAcceptance(boolean acceptance) {
        this.acceptance = acceptance;
    }
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
