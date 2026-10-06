package com.omsworld.familycare.data.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng

data class FriendsMarkerData(
    var latLng: LatLng? = null,
    var title: String = "",
    var Snippet: String = "",
    var position: Int = 0,
    var bitmap: Bitmap? = null
)