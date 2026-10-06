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
 * Restarts TrackingService after device reboot, but ONLY if:
 *  1. User has location permission
 *  2. Safe Zone was previously enabled (Constants.safeJone == "1")
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            "android.intent.action.QUICKBOOT_POWERON_HTC" -> {
                Timber.i("BootReceiver received: ${intent.action}")
                handleBoot(context)
            }
            else -> {
                Timber.d("BootReceiver ignored action: ${intent.action}")
            }
        }
    }

    private fun handleBoot(context: Context) {
        // Check safe zone state
        val prefs = MySharedPreference.getInstance()
        val safeZone = prefs.getString(context, Constants.safeJone)
        if (safeZone != "1") {
            Timber.i("BootReceiver: safe zone disabled — skipping tracking restart")
            return
        }

        // Check location permission
        if (!hasLocationPermission(context)) {
            Timber.w("BootReceiver: no location permission — skipping tracking restart")
            return
        }

        // Start service
        try {
            ContextCompat.startForegroundService(
                context,
                Intent(context, TrackingService::class.java)
            )
            Timber.i("BootReceiver: TrackingService started")
        } catch (e: Exception) {
            Timber.e(e, "BootReceiver: failed to start TrackingService")
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