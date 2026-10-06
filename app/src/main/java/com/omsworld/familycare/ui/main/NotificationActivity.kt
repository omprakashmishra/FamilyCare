package com.omsworld.familycare.ui.main

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle

class NotificationActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(intent.getIntExtra(NOTIFICATION_ID, -1))
        finish()
    }

    companion object {
        const val NOTIFICATION_ID = "NOTIFICATION_ID"

        @JvmStatic
        fun getDismissIntent(notificationId: Int, context: Context): PendingIntent {
            val intent = Intent(context, NotificationActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(NOTIFICATION_ID, notificationId)
            }
            return PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}