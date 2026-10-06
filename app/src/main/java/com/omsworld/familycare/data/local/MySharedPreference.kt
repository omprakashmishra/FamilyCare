package com.omsworld.familycare.data.local

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thread-safe SharedPreferences wrapper.
 * Injected via Hilt — also usable via getInstance() for legacy code.
 */
@Singleton
class MySharedPreference @Inject constructor() {

    companion object {
        const val APP_PREFERENCE = "FamilyCare"
        const val APP_Firebasetoken = "FamilyCare_Firebasetoken"

        @Volatile
        private var instance: MySharedPreference? = null

        @JvmStatic
        fun getInstance(): MySharedPreference {
            return instance ?: synchronized(this) {
                instance ?: MySharedPreference().also { instance = it }
            }
        }
    }

    private fun prefsFor(context: Context, key: String): SharedPreferences =
        if (key == "Firebasetoken") {
            context.getSharedPreferences(APP_Firebasetoken, Context.MODE_PRIVATE)
        } else {
            context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)
        }

    // ==================== String ====================
    fun setString(context: Context, key: String, value: String) {
        prefsFor(context, key).edit().putString(key, value).apply()
    }

    fun getString(context: Context, key: String, default: String = ""): String =
        prefsFor(context, key).getString(key, default) ?: default

    // ==================== Boolean ====================
    fun setBoolean(context: Context, key: String, value: Boolean) {
        context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)
            .edit().putBoolean(key, value).apply()
    }

    fun getBoolean(context: Context, key: String): Boolean =
        context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)
            .getBoolean(key, false)

    // ==================== Float ====================
    fun setFloat(context: Context, key: String, value: Float) {
        context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)
            .edit().putFloat(key, value).apply()
    }

    fun getFloat(context: Context, key: String): Float =
        context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)
            .getFloat(key, 0f)

    // ==================== Clear ====================
    fun clearSharedPreference(context: Context) {
        context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)
            .edit().clear().apply()
    }
}