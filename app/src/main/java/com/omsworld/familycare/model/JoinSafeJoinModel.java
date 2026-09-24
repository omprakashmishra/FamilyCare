package com.omsworld.familycare.model;

/**
 * Created by ram.sinha on 3/7/2017.
 */

public class JoinSafeJoinModel {

    private String isDeclined;
    private String AspnetUserID;
    private String InvitationID;
    private String IsDeleted;
    private String IsAccepted;
    private String NearAndDearUserID;
    private String NearAndDearName;
    private String NearAndDearMobileNumber;
    private String NearAndDearEmail;
    private String NearAndDearJoinDate;
    private String InviteeStatusID;
    private String UserStatus;
    private String user_id;
    private String user_name;
    private String user_image;
    private String OnlineStatus;
    private String user_mobile;
    private String address;
    private String time;
    private String request_type;

    private String lat;
    private String lng;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }


    public String getFamily_id() {
        return family_id;
    }

    public void setFamily_id(String family_id) {
        this.family_id = family_id;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    private String family_id;
    private String family_name;

    public String getMember_status() {
        return member_status;
    }

    public void setMember_status(String member_status) {
        this.member_status = member_status;
    }

    private String member_status;

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    //--------------------------------
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUserStatus() {
        return UserStatus;
    }

    public void setUserStatus(String userStatus) {
        UserStatus = userStatus;
    }

    public String getOnlineStatus() {
        return OnlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        OnlineStatus = onlineStatus;
    }

    public String getNearAndDearUserID() {
        return NearAndDearUserID;
    }

    public void setNearAndDearUserID(String nearAndDearUserID) {
        NearAndDearUserID = nearAndDearUserID;
    }

    public String getNearAndDearName() {
        return NearAndDearName;
    }

    public void setNearAndDearName(String nearAndDearName) {
        NearAndDearName = nearAndDearName;
    }

    public String getNearAndDearMobileNumber() {
        return NearAndDearMobileNumber;
    }

    public void setNearAndDearMobileNumber(String nearAndDearMobileNumber) {
        NearAndDearMobileNumber = nearAndDearMobileNumber;
    }

    public String getNearAndDearEmail() {
        return NearAndDearEmail;
    }

    public void setNearAndDearEmail(String nearAndDearEmail) {
        NearAndDearEmail = nearAndDearEmail;
    }

    public String getNearAndDearJoinDate() {
        return NearAndDearJoinDate;
    }

    public void setNearAndDearJoinDate(String nearAndDearJoinDate) {
        NearAndDearJoinDate = nearAndDearJoinDate;
    }


    public String getIsDeclined() {
        return isDeclined;
    }

    public void setIsDeclined(String isDeclined) {
        this.isDeclined = isDeclined;
    }

    public String getAspnetUserID() {
        return AspnetUserID;
    }

    public void setAspnetUserID(String AspnetUserID) {
        this.AspnetUserID = AspnetUserID;
    }

    public String getInvitationID() {
        return InvitationID;
    }

    public void setInvitationID(String InvitationID) {
        this.InvitationID = InvitationID;
    }

    public String getIsDeleted() {
        return IsDeleted;
    }

    public void setIsDeleted(String IsDeleted) {
        this.IsDeleted = IsDeleted;
    }

    public String getIsAccepted() {
        return IsAccepted;
    }

    public void setIsAccepted(String IsAccepted) {
        this.IsAccepted = IsAccepted;
    }

    public String getInviteeStatusID() {
        return InviteeStatusID;
    }

    public void setInviteeStatusID(String inviteeStatusID) {
        InviteeStatusID = inviteeStatusID;
    }

}
