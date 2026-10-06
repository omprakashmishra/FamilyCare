package com.omsworld.familycare.location

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

object GpsAlert {

    private const val TAG = "GpsAlert"
    private const val REQUEST_CHECK_SETTINGS = 0x1

    suspend fun check(context: Context) {
        try {
            val req = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                10_000L
            ).setMinUpdateIntervalMillis(5_000L).build()

            val settings = LocationSettingsRequest.Builder()
                .addLocationRequest(req)
                .setAlwaysShow(true)
                .build()

            val response = LocationServices
                .getSettingsClient(context)
                .checkLocationSettings(settings)
                .await()

            Log.i(TAG, "Location settings OK: ${response.locationSettingsStates}")
        } catch (e: ResolvableApiException) {
            if (context is Activity) {
                try {
                    e.startResolutionForResult(context, REQUEST_CHECK_SETTINGS)
                } catch (ex: IntentSender.SendIntentException) {
                    Log.e(TAG, "Cannot show GPS dialog", ex)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "GPS check failed", e)
        }
    }
}