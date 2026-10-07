package com.omsworld.familycare.data.remote

import com.omsworld.familycare.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .header("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("Prefer", "return=representation")
            .build()

        return chain.proceed(request)
    }
}