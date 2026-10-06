package com.omsworld.familycare.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FamilyCareMessagingService : FirebaseMessagingService() {

    @Inject lateinit var prefs: MySharedPreference

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("FCM token refreshed: %s", token)
        prefs.setString(this, Constants.Firebasetoken, token)
        // Optionally: POST token to /push_registration
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        if (data.isEmpty()) return

        Timber.d("FCM data: %s", data)

        val type = data["notification_type"] ?: "default"
        val sender = data["sender_name"] ?: ""
        val text = data["text"] ?: ""

        NotificationHelper(this).sendNotification(
            notificationType = type,
            title = sender.ifBlank { "Family Care" },
            body = text,
            landingClass = MainActivity::class.java
        )
    }
}