package com.omsworld.familycare.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.omsworld.familycare.R
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.repository.TrackingRepository
import com.omsworld.familycare.location.AddressFromLatLong
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : Service() {

    companion object {
        private const val NOTIF_ID = 1001
        private const val CHANNEL_ID = "tracking_channel"
        private const val CHANNEL_NAME = "Location Tracking"
        private const val INTERVAL_MS = 35_000L
        private const val MIN_DISTANCE_M = 30f
        var currentAccuracy: String = "0.0"
    }

    @Inject lateinit var prefs: MySharedPreference
    @Inject lateinit var repo: TrackingRepository

    private lateinit var fused: FusedLocationProviderClient
    private lateinit var addressUtil: AddressFromLatLong

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var updateJob: Job? = null

    private var lastLat = 0.0
    private var lastLng = 0.0
    private var currentAddress: String = ""
    private var batteryLevel = "40"
    private var firstLocation = true

    override fun onCreate() {
        super.onCreate()

        // MUST call startForeground() within 5s of startForegroundService().
        startForegroundCompat()

        if (!hasLocationPermission()) {
            Timber.w("TrackingService: no location permission — stopping cleanly")
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return
        }

        fused = LocationServices.getFusedLocationProviderClient(this)
        addressUtil = AddressFromLatLong(this)
        observeLocation()
        pushLoop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!hasLocationPermission()) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    private fun startForegroundCompat() {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }

        val notif = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Family Care")
            .setContentText("Starting…")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val type = if (hasLocationPermission())
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                else
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE

                startForeground(NOTIF_ID, notif, type)
            } else {
                startForeground(NOTIF_ID, notif)
            }
            Timber.d("TrackingService started foreground OK")
        } catch (e: SecurityException) {
            Timber.e(e, "SecurityException in startForeground — stopping")
            try { stopForeground(STOP_FOREGROUND_REMOVE) } catch (_: Exception) {}
            stopSelf()
        } catch (e: Exception) {
            Timber.e(e, "Exception in startForeground — stopping")
            try { stopForeground(STOP_FOREGROUND_REMOVE) } catch (_: Exception) {}
            stopSelf()
        }
    }

    private fun observeLocation() {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            INTERVAL_MS
        ).setMinUpdateDistanceMeters(MIN_DISTANCE_M).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { onLoc(it) }
            }
        }

        try {
            fused.requestLocationUpdates(request, callback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            Timber.e(e, "Missing location permission")
            stopSelf()
        }
    }

    private fun onLoc(loc: Location) {
        currentAccuracy = loc.accuracy.toString()
        if (firstLocation) {
            if (loc.accuracy < 100) {
                firstLocation = false
                lastLat = loc.latitude
                lastLng = loc.longitude
                saveLocation()
            }
        } else if (loc.accuracy <= 30) {
            lastLat = loc.latitude
            lastLng = loc.longitude
            saveLocation()
        }
    }

    private fun saveLocation() {
        scope.launch {
            try {
                addressUtil.load(lastLat, lastLng)
                currentAddress = addressUtil.address
                val ctx = applicationContext
                prefs.setString(ctx, Constants.JOB_NEW_LATITUDE, lastLat.toString())
                prefs.setString(ctx, Constants.JOB_NEW_LONGITUDE, lastLng.toString())
                prefs.setString(ctx, Constants.JOB_CurrentAddress, currentAddress)
            } catch (e: Exception) {
                Timber.e(e, "saveLocation failed")
            }
        }
    }

    private fun pushLoop() {
        updateJob = scope.launch {
            while (isActive) {
                delay(INTERVAL_MS)
                val ctx = applicationContext
                val userId = prefs.getString(ctx, Constants.USER_ID)
                val familyId = prefs.getString(ctx, Constants.FAMILY_ID)
                if (userId.isBlank()) continue

                try {
                    repo.pushLocation(
                        userId = userId,
                        familyId = familyId,
                        battery = batteryLevel,
                        lat = lastLat.toString(),
                        lng = lastLng.toString(),
                        address = currentAddress
                    )
                } catch (e: Exception) {
                    Timber.e(e, "Push failed")
                }
            }
        }
    }

    override fun onDestroy() {
        updateJob?.cancel()
        scope.cancel()
        super.onDestroy()
    }
}