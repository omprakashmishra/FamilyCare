package com.omsworld.familycare.data.remote

import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.data.local.MySharedPreference
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val prefs: MySharedPreference
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val userId = prefs.getString(FamilyCareApp.appContext, Constants.USER_ID, "0")
        val builder = chain.request().newBuilder()
            .header("Accept", "application/json")

        if (userId.isNotBlank()) {
            builder.header("X-User-Id", userId)
        }

        return chain.proceed(builder.build())
    }
}