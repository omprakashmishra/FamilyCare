package com.omsworld.familycare.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.firebase.NotificationHelper
import com.omsworld.familycare.ui.main.MainActivity
import org.json.JSONObject

class ChatService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            val data = intent?.getStringExtra(Constants.NotificationData) ?: ""
            if (data.isNotBlank()) {
                val root = JSONObject(data)
                val payload = root.optJSONObject("data") ?: root
                val type = payload.optString("notification_type")
                val sender = payload.optString("sender_name")
                val text = payload.optString("text")

                if (VisibleFragmentTracker.visibleFragment != "ChatActivity") {
                    NotificationHelper(this).sendNotification(
                        type, sender, text, MainActivity::class.java
                    )
                }
            }
        } catch (_: Exception) { }
        stopSelf()
        return START_STICKY
    }
}

object VisibleFragmentTracker {
    var visibleFragment: String = ""
}