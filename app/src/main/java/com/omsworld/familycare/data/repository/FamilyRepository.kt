package com.omsworld.familycare.data.repository

import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.remote.SupabaseApiService
import com.omsworld.familycare.data.remote.dto.SupabaseFamilyRequestDto
import com.omsworld.familycare.data.remote.dto.SupabaseUserDto
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class FamilyRepository @Inject constructor(
    @Named("supabase") private val supabase: SupabaseApiService
) {

    // ============================================================
    // GET FAMILY MEMBERS (family_group_info.php)
    // ============================================================
    suspend fun getFamilyGroupInfo(userId: String): ApiResult<List<SupabaseUserDto>> =
        safeApiCall {
            val users = supabase.getUserById(idEq = "eq.$userId")
            val me = users.firstOrNull()
                ?: throw IllegalStateException("User not found")

            val familyId = me.familyId
            if (familyId.isNullOrBlank()) return@safeApiCall emptyList()

            supabase.getFamilyMembers(familyEq = "eq.$familyId")
        }

    // ============================================================
    // CREATE FAMILY (create_family_group.php)
    // ============================================================
    suspend fun createFamilyGroup(
        familyId: String,
        userId: String,
        groupName: String
    ): ApiResult<Unit> = safeApiCall {
        val response = supabase.updateUser(
            "eq.$userId",
            mapOf(
                "family_id" to familyId,
                "family_name" to groupName,
                "is_family_admin" to true
            )
        )
        if (!response.isSuccessful) {
            throw IllegalStateException("Create family failed: ${response.code()}")
        }
    }

    // ============================================================
    // ADD MEMBER (add_family_on_group.php)
    // ============================================================
    suspend fun addFamilyMember(
        userId: String,
        familyId: String,
        memberMobile: String
    ): ApiResult<Unit> = safeApiCall {
        // Find the member by mobile
        val members = supabase.getUserByMobile(mobileEq = "eq.$memberMobile")
        val member = members.firstOrNull()
            ?: throw IllegalStateException("Member not found with that mobile")

        // Add them to the family
        val response = supabase.updateUser(
            "eq.${member.aspnetUserId}",
            mapOf("family_id" to familyId)
        )
        if (!response.isSuccessful) {
            throw IllegalStateException("Add member failed: ${response.code()}")
        }
    }

    // ============================================================
    // REMOVE MEMBER (remove_family_group_member.php)
    // ============================================================
    suspend fun removeFamilyMember(
        userId: String,
        familyId: String,
        memberMobile: String
    ): ApiResult<Unit> = safeApiCall {
        val members = supabase.getUserByMobile(mobileEq = "eq.$memberMobile")
        val member = members.firstOrNull()
            ?: throw IllegalStateException("Member not found")

        val response = supabase.updateUser(
            "eq.${member.aspnetUserId}",
            mapOf("family_id" to "")
        )
        if (!response.isSuccessful) {
            throw IllegalStateException("Remove member failed: ${response.code()}")
        }
    }

    // ============================================================
    // LEAVE FAMILY (leave_family_group.php)
    // ============================================================
    suspend fun leaveFamily(
        userId: String,
        familyId: String
    ): ApiResult<Unit> = safeApiCall {
        val response = supabase.updateUser(
            "eq.$userId",
            mapOf("family_id" to "", "family_name" to "", "is_family_admin" to false)
        )
        if (!response.isSuccessful) {
            throw IllegalStateException("Leave family failed: ${response.code()}")
        }
    }

    // ============================================================
    // FAMILY REQUEST ACTION (family_request_action.php)
    // ============================================================
    suspend fun familyRequestAction(
        requestId: Long,
        action: String
    ): ApiResult<Unit> = safeApiCall {
        val updates = when (action) {
            "1" -> mapOf("is_accepted" to "1")
            "2" -> mapOf("is_declined" to "1")
            else -> throw IllegalStateException("Invalid action: $action")
        }

        val response = supabase.updateFamilyRequest(
            idEq = "eq.$requestId",
            updates = updates
        )
        if (!response.isSuccessful) {
            throw IllegalStateException("Request action failed: ${response.code()}")
        }
    }

    suspend fun cancelFamilyRequest(requestId: Long): ApiResult<Unit> = safeApiCall {
        val response = supabase.deleteFamilyRequest(idEq = "eq.$requestId")
        if (!response.isSuccessful) {
            throw IllegalStateException("Cancel request failed: ${response.code()}")
        }
    }

    // ============================================================
    // REQUEST TO JOIN (request_to_join.php)
    // ============================================================
    suspend fun requestToJoin(
        fromPhone: String,
        toPhone: String
    ): ApiResult<Unit> = safeApiCall {
        val request = SupabaseFamilyRequestDto(
            fromPhone = fromPhone,
            toPhone = toPhone
        )
        val response = supabase.insertFamilyRequest(request)
        if (!response.isSuccessful) {
            throw IllegalStateException("Request failed: ${response.code()}")
        }
    }
}