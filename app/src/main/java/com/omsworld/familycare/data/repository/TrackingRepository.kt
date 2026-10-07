package com.omsworld.familycare.data.repository

import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.remote.SupabaseApiService
import com.omsworld.familycare.data.remote.dto.SupabaseDeviceDto
import com.omsworld.familycare.data.remote.dto.SupabaseUserDto
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TrackingRepository @Inject constructor(
    @Named("supabase") private val supabase: SupabaseApiService
) {

    // ============================================================
    // PUSH LOCATION
    // Replaces: POST insert_position.php
    // Params: user_id, family_id, Battery, lat, lng, address
    // ============================================================
    suspend fun pushLocation(
        userId: String,
        familyId: String,
        battery: String,
        lat: String,
        lng: String,
        address: String
    ): ApiResult<Unit> = safeApiCall {
        val existing = supabase.getDeviceForUser(userEq = "eq.$userId")

        val now = nowIso()

        if (existing.isEmpty()) {
            // ─── First time: insert a new row ───
            val device = SupabaseDeviceDto(
                userId = userId,
                familyId = familyId.ifBlank { null },
                battery = battery,
                lat = lat,
                lng = lng,
                address = address,
                onlineStatus = "Online",
                lastSeen = now
            )
            val response = supabase.insertDevice(device)
            if (!response.isSuccessful) {
                throw IllegalStateException("Insert device failed: ${response.code()}")
            }
            Timber.d("Device inserted for $userId")
        } else {
            // ─── Subsequent: update the existing row ───
            val updates = mutableMapOf<String, Any>(
                "battery" to battery,
                "lat" to lat,
                "lng" to lng,
                "address" to address,
                "online_status" to "Online",
                "last_seen" to now
            )
            if (familyId.isNotBlank()) updates["family_id"] = familyId

            val response = supabase.updateDevice("eq.$userId", updates)
            if (!response.isSuccessful) {
                throw IllegalStateException("Update device failed: ${response.code()}")
            }
            Timber.d("Device updated for $userId")
        }
    }

    // ============================================================
    // FETCH FRIENDS / FAMILY MEMBERS WITH LOCATIONS
    // Replaces: POST family_group_info.php  (returns freind_list with lat/lng)
    // ============================================================
    suspend fun fetchFriends(userId: String): ApiResult<List<FamilyMemberWithLocation>> =
        safeApiCall {
            // 1. Find my family
            val me = supabase.getUserById(idEq = "eq.$userId").firstOrNull()
                ?: throw IllegalStateException("User not found")

            val familyId = me.familyId
            if (familyId.isNullOrBlank()) {
                return@safeApiCall emptyList()
            }

            // 2. Get all family members
            val members = supabase.getFamilyMembers(familyEq = "eq.$familyId")

            // 3. Get all family devices (locations)
            val devices = supabase.getFamilyDevices(familyEq = "eq.$familyId")
            val deviceByUser = devices.associateBy { it.userId }

            // 4. Merge member info + location
            members.mapNotNull { member ->
                val memberId = member.aspnetUserId ?: return@mapNotNull null
                if (memberId == userId) return@mapNotNull null // skip self

                val device = deviceByUser[memberId]
                FamilyMemberWithLocation(
                    userId = memberId,
                    userName = member.userName ?: "",
                    mobile = member.mobileNo ?: "",
                    imageUrl = member.userImg ?: "",
                    lat = device?.lat ?: "0",
                    lng = device?.lng ?: "0",
                    address = device?.address ?: "",
                    battery = device?.battery ?: "0",
                    onlineStatus = device?.onlineStatus ?: "Offline",
                    lastSeen = device?.lastSeen ?: ""
                )
            }
        }

    // ============================================================
    // WHO IS TRACKING ME
    // Replaces: POST whoIsTrackingMe (v1)
    // ============================================================
    suspend fun whoIsTrackingMe(userId: String): ApiResult<List<WhoTracksMe>> =
        safeApiCall {
            val me = supabase.getUserById(idEq = "eq.$userId").firstOrNull()
                ?: throw IllegalStateException("User not found")

            val familyId = me.familyId
            if (familyId.isNullOrBlank()) {
                return@safeApiCall emptyList()
            }

            // All family members are tracking me
            supabase.getFamilyMembers(familyEq = "eq.$familyId")
                .filter { it.aspnetUserId != userId }
                .map {
                    WhoTracksMe(
                        aspnetUserId = it.aspnetUserId ?: "",
                        userName = it.userName ?: ""
                    )
                }
        }

    // ============================================================
    // MARK USER OFFLINE (called when service stops)
    // ============================================================
    suspend fun markOffline(userId: String): ApiResult<Unit> = safeApiCall {
        val response = supabase.updateDevice(
            "eq.$userId",
            mapOf(
                "online_status" to "Offline",
                "last_seen" to nowIso()
            )
        )
        if (!response.isSuccessful) {
            throw IllegalStateException("Mark offline failed: ${response.code()}")
        }
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private fun nowIso(): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
            .format(Date())
}

// ============================================================
// LOCAL DATA CLASSES (returned by repository)
// ============================================================

data class FamilyMemberWithLocation(
    val userId: String,
    val userName: String,
    val mobile: String,
    val imageUrl: String,
    val lat: String,
    val lng: String,
    val address: String,
    val battery: String,
    val onlineStatus: String,
    val lastSeen: String
)

data class WhoTracksMe(
    val aspnetUserId: String,
    val userName: String
)