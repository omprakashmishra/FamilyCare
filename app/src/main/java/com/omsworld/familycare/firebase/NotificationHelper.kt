package com.omsworld.familycare.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.omsworld.familycare.R

class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "family_care_default"
        private const val CHANNEL_NAME = "Family Care"
        private var notifId = 100
    }

    fun sendNotification(
        notificationType: String,
        title: String,
        body: String,
        landingClass: Class<*>
    ) {
        try {
            ensureChannel()

            val intent = Intent(context, landingClass).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("notification_type", notificationType)
            }

            val pending = PendingIntent.getActivity(
                context,
                notifId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val sound = RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_NOTIFICATION
            )

            val notif = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(sound)
                .setVibrate(longArrayOf(1000, 1000))
                .setContentIntent(pending)
                .build()

            val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            mgr.notify(notifId++, notif)
        } catch (_: Exception) { }
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            if (mgr.getNotificationChannel(CHANNEL_ID) == null) {
                mgr.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                )
            }
        }
    }
}