package com.omsworld.familycare.Chat;

/**
 * Created by nityanand.p on 8/11/2017.
 */

public class FriendListModel {

    private String freind_id;
    private String freind_fullname;
    private String freind_img;
    private String message_id;
    private String message;
    private String sender_id;
    private String time;
    private String type;

    public String getFriend_phone() {
        return friend_phone;
    }

    public void setFriend_phone(String friend_phone) {
        this.friend_phone = friend_phone;
    }

    private String friend_phone;

    public String getFreindId() {
        return this.freind_id;
    }

    public void setFreindId(String freind_id) {
        this.freind_id = freind_id;
    }

    public String getFreindFullname() {
        return this.freind_fullname;
    }

    public void setFreindFullname(String freind_fullname) {
        this.freind_fullname = freind_fullname;
    }

    public String getFreindImg() {
        return this.freind_img;
    }

    public void setFreindImg(String freind_img) {
        this.freind_img = freind_img;
    }

    public String getMessageId() {
        return this.message_id;
    }

    public void setMessageId(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return this.sender_id;
    }

    public void setSenderId(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
