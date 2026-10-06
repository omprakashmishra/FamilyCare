package com.omsworld.familycare.data.repository

import com.omsworld.familycare.core.UrlList
import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.remote.ApiService
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilyRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun getFamilyGroupInfo(userId: String): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.family_group_info,
            mapOf("user_id" to userId, "family_id" to "")
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun createFamilyGroup(
        familyId: String,
        userId: String,
        groupName: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.create_family_group,
            mapOf(
                "family_id" to familyId,
                "user_id" to userId,
                "group_name" to groupName
            )
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun addFamilyMember(
        userId: String,
        familyId: String,
        memberMobile: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.add_family_on_group,
            mapOf(
                "user_id" to userId,
                "family_id" to familyId,
                "member_mob" to memberMobile
            )
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun removeFamilyMember(
        userId: String,
        familyId: String,
        memberMobile: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.remove_family_group_member,
            mapOf(
                "user_id" to userId,
                "family_id" to familyId,
                "member_mob" to memberMobile
            )
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun leaveFamily(
        userId: String,
        familyId: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.leave_family_group,
            mapOf("user_id" to userId, "family_id" to familyId)
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun familyRequestAction(
        userId: String,
        familyId: String,
        action: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.family_request_action,
            mapOf(
                "user_id" to userId,
                "family_id" to familyId,
                "action" to action
            )
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun requestToJoin(
        fromPhone: String,
        toPhone: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.request_to_join,
            mapOf("from_phone" to fromPhone, "to_phone" to toPhone)
        ).body() ?: ""
        JSONObject(raw)
    }
}