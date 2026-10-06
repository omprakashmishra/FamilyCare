package com.omsworld.familycare.service

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.data.local.MySharedPreference
import timber.log.Timber

/**
 * Restarts TrackingService when the OS has killed it.
 * Triggered by the "StartKilledService" broadcast action (see AndroidManifest).
 *
 * Guards:
 *  1. User has location permission
 *  2. Safe Zone was previously enabled
 */
class RestartServiceReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_RESTART = "StartKilledService"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_RESTART) {
            Timber.d("RestartServiceReceiver ignored action: ${intent.action}")
            return
        }

        Timber.i("RestartServiceReceiver received — attempting service restart")

        // Check safe zone state
        val prefs = MySharedPreference.getInstance()
        val safeZone = prefs.getString(context, Constants.safeJone)
        if (safeZone != "1") {
            Timber.i("Safe zone disabled — skipping restart")
            return
        }

        // Check location permission
        if (!hasLocationPermission(context)) {
            Timber.w("No location permission — skipping restart")
            return
        }

        // Start service
        try {
            ContextCompat.startForegroundService(
                context,
                Intent(context, TrackingService::class.java)
            )
            Timber.i("TrackingService restarted")
        } catch (e: Exception) {
            Timber.e(e, "Failed to restart TrackingService")
        }
    }

    private fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }
}