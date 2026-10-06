package com.omsworld.familycare.location

import android.content.Context
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class AddressFromLatLong(private val context: Context) {

    var address: String = ""
        private set
    var city: String = ""
        private set
    var state: String = ""
        private set
    var country: String = ""
        private set

    suspend fun load(latitude: Double, longitude: Double) = withContext(Dispatchers.IO) {
        address = ""; city = ""; state = ""; country = ""
        try {
            @Suppress("DEPRECATION")
            val list = Geocoder(context, Locale.getDefault())
                .getFromLocation(latitude, longitude, 1)
            if (!list.isNullOrEmpty()) {
                val a = list[0]
                val sb = StringBuilder()
                for (i in 0..a.maxAddressLineIndex) {
                    sb.append(a.getAddressLine(i)).append(",")
                }
                address = sb.toString().trimEnd(',')
                city = a.locality ?: ""
                state = a.adminArea ?: ""
                country = a.countryName ?: ""
            }
        } catch (_: Exception) { /* ignore */ }
    }
}