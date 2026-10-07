package com.omsworld.familycare

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.omsworld.familycare.data.local.MySharedPreference
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class FamilyCareApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Crashlytics: only report in release
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG

        // Logging: Timber in debug
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Pre-warm SharedPreferences so first access is instant
        MySharedPreference.getInstance()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        lateinit var instance: FamilyCareApp
            private set

        val appContext: Context get() = instance.applicationContext
    }
}