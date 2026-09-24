package com.omsworld.familycare.model;

/**
 * Created by Rupesh.m on 7/15/2017.
 */

public class FriendsTrackingModel {
    private String AspnetUserID;
    private String UserName;
    private String MobileNumber;
    private String Latitude;
    private String Longitude;
    private String Address;
    private String Battery;
    private String LastSeen;
    private String OnlineStatus;
    private String IsTrackOnOff;

    private String user_img;

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }


    public String getAspnetUserID() {
        return AspnetUserID;
    }

    public void setAspnetUserID(String aspnetUserID) {
        AspnetUserID = aspnetUserID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getBattery() {
        return Battery;
    }

    public void setBattery(String battery) {
        Battery = battery;
    }

    public String getLastSeen() {
        return LastSeen;
    }

    public void setLastSeen(String lastSeen) {
        LastSeen = lastSeen;
    }

    public String getOnlineStatus() {
        return OnlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        OnlineStatus = onlineStatus;
    }

    public String getIsTrackOnOff() {
        return IsTrackOnOff;
    }

    public void setIsTrackOnOff(String isTrackOnOff) {
        IsTrackOnOff = isTrackOnOff;
    }
}
