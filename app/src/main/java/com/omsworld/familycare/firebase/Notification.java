package com.omsworld.familycare.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.omsworld.familycare.R;

/**
 * Created by om's on 3/17/2018.
 */

public class Notification {
    public Context mcontext;
    int notificationID = 1;

    public Notification(Context context) {
        this.mcontext = context;
    }

    public void sendNotification(String notification_type, String title, String messageBody, Class<?> landing) {
        try {
            Intent intent = new Intent(mcontext, landing);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       /* PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);*/
            PendingIntent pendingIntent = PendingIntent.getActivity(mcontext, notificationID, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            // notificationBuilder.setSound(uri);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mcontext)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(uri)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);
            //notificationManager.notify(0, notificationBuilder.build());
            notificationManager.notify(notificationID, notificationBuilder.build());
        } catch (Exception ex) {

        }
    }
}
