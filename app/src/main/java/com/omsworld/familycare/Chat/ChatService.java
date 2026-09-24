package com.omsworld.familycare.Chat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.omsworld.familycare.activity.MainActivity;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.omsworld.familycare.firebase.Notification;

import org.json.JSONObject;

/**
 * Created by omprakash.m on 3/15/2018.
 */

public class ChatService extends Service {
    String notificationDate;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        try {
            notificationDate = intent.getStringExtra(GlobalConstants.NotificationData);
            if (!notificationDate.equals("")) {
                Log.d("------->", "data: " + notificationDate);

                JSONObject jsonObject = new JSONObject(notificationDate);
                JSONObject jsonObject1 = jsonObject.optJSONObject("data");

                final String notification_type = jsonObject1.optString("notification_type");
                final String sender_name = jsonObject1.optString("sender_name");
                final String message = jsonObject1.optString("text");

                //-----------------------------------------MANAGE NOTIFICATION AS WELL LANDING PAGE.
                if (CommonFunctions.VisibleFragmentNm.equals("Chat_Fr")) {
                    if (!Chat_Fr.notifyChatData(notificationDate)) {
                        new Notification(this).sendNotification(notification_type,sender_name, message, MainActivity.class);
                    }
                }else {
                    new Notification(this).sendNotification(notification_type,sender_name, message, MainActivity.class);
                }


                //-----------------------------------------
                notificationDate = "";
            }
            stopSelf();
        } catch (Exception ex) {
        }
        return START_STICKY;
    }

  /*  @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        try {
            notificationDate = intent.getStringExtra(GlobalConstants.NotificationData);
            if (!notificationDate.equals("")) {
                Log.d("------->", "data: " +notificationDate);
                Chat_Fr.chat_fr.notifyChatData(notificationDate);
            }
            stopSelf();
        } catch (Exception ex) {
        }
        return START_STICKY;
    }*/
}
