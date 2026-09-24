package com.omsworld.familycare.model;

/**
 * Created by rupesh.m on 1/4/2017.
 */

public class Guard_list_model {
    private String Name;
    private String MobileNO;
    private String CurrentStatus;
    private String DateTime;
    private String GuardLongitude;
    private String GuardLatitude;

    private String TrackingUserId;

    public String getTrackingUserId() {
        return TrackingUserId;
    }

    public void setTrackingUserId(String trackingUserId) {
        TrackingUserId = trackingUserId;
    }


    public String getGuardLongitude() {
        return GuardLongitude;
    }

    public void setGuardLongitude(String guardLongitude) {
        GuardLongitude = guardLongitude;
    }

    public String getGuardLatitude() {
        return GuardLatitude;
    }

    public void setGuardLatitude(String guardLatitude) {
        GuardLatitude = guardLatitude;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobileNO() {
        return MobileNO;
    }

    public void setMobileNO(String mobileNO) {
        MobileNO = mobileNO;
    }

    public String getCurrentStatus() {
        return CurrentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        CurrentStatus = currentStatus;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }
}
