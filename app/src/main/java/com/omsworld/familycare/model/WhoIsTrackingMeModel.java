package com.omsworld.familycare.model;

/**
 * Created by omprakash.m on 8/10/2017.
 */

public class WhoIsTrackingMeModel {
    String name;

    public String getAspnetUserId() {
        return AspnetUserId;
    }

    public void setAspnetUserId(String aspnetUserId) {
        AspnetUserId = aspnetUserId;
    }

    public String getIstrackMe() {
        return istrackMe;
    }

    public void setIstrackMe(String istrackMe) {
        this.istrackMe = istrackMe;
    }

    String AspnetUserId;
    String istrackMe;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
