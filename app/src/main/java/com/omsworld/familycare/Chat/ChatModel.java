package com.omsworld.familycare.Chat;

/**
 * Created by nityanand.p on 8/11/2017.
 */

public class ChatModel {
    private String message_id;
    private String message;
    private String sender_id;
    private String time;
    private String type;
    //--------------------
    private String freind_id;
    private String freind_fullname;
    private String freind_img;


    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getFreind_id() {
        return freind_id;
    }

    public void setFreind_id(String freind_id) {
        this.freind_id = freind_id;
    }

    public String getFreind_fullname() {
        return freind_fullname;
    }

    public void setFreind_fullname(String freind_fullname) {
        this.freind_fullname = freind_fullname;
    }

    public String getFreind_img() {
        return freind_img;
    }

    public void setFreind_img(String freind_img) {
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
