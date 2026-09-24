package com.omsworld.familycare.firebase;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.omsworld.familycare.Chat.ChatService;
import com.omsworld.familycare.R;
import com.omsworld.familycare.activity.MainActivity;
import com.omsworld.familycare.api_call.GlobalConstants;

import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private String key, value;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            Log.d("------->" + TAG, "From: " + remoteMessage.getFrom());
            //Log.d("------->" + TAG, "data: " + remoteMessage.getData().toString());
            // Log.d("------->" + TAG, "Title: " + remoteMessage.getNotification().getTitle());
            // Log.d("------->" + TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
            // sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());

            if(remoteMessage.getData().size()>0) {
                Intent intent = new Intent(this, ChatService.class);

                Bundle bdl = new Bundle();
                bdl.putString(GlobalConstants.NotificationData, remoteMessage.getData().toString());
                intent.putExtras(bdl);
                startService(intent);


            }
        } catch (Exception ex) {
        }

    }

}