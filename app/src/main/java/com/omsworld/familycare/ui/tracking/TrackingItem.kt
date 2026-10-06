package com.omsworld.familycare.ui.tracking

data class TrackingItem(
    val id: String = "",
    val name: String = "",
    val mobile: String = "",
    val imageUrl: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val address: String = "",
    val battery: String = "0",
    val onlineStatus: String = "Offline",
    val lastSeen: String = ""
)