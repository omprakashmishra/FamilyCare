package com.omsworld.familycare.core

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Replaces FieldUtils.java.
 */
object FieldUtils {

    fun isBlank(value: String?): Boolean = value.isNullOrBlank()

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as? ConnectivityManager ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            @Suppress("DEPRECATION")
            val info = cm.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            info.isConnected
        }
    }

    fun getLastKnownLocation(context: Context): Location? {
        val lm = context.getSystemService(Context.LOCATION_SERVICE)
                as? LocationManager ?: return null

        val fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) return null

        val providers = lm.getProviders(true)
        var best: Location? = null
        for (p in providers) {
            val loc = try { lm.getLastKnownLocation(p) } catch (_: SecurityException) { null }
            if (loc == null) continue
            if (best == null || loc.accuracy < best.accuracy) best = loc
        }
        return best
    }

    @Suppress("DEPRECATION")
    fun getDeviceId(context: Context): String = try {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE)
                as? android.telephony.TelephonyManager
        tm?.deviceId ?: "Emulator"
    } catch (_: Exception) {
        "Emulator"
    }
}