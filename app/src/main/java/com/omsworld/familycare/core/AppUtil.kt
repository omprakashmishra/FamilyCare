package com.omsworld.familycare.core

import android.app.Activity
import android.content.Intent
import com.omsworld.familycare.R

/**
 * Activity transition helpers. Replaces AppUtil.java.
 */
object AppUtil {

    fun startActivityWithAnimation(activity: Activity, intent: Intent) {
        try {
            activity.startActivity(intent)
            activity.overridePendingTransition(
                R.anim.slide_in_right, R.anim.slide_out_left
            )
        } catch (_: Exception) { }
    }

    fun finishActivityWithAnimation(activity: Activity) {
        try {
            activity.finish()
            activity.overridePendingTransition(
                R.anim.slide_in_left, R.anim.slide_out_right
            )
        } catch (_: Exception) { }
    }

    fun finishActivityWithAnimationRight(activity: Activity) {
        try {
            activity.finish()
            activity.overridePendingTransition(
                R.anim.slide_in_right, R.anim.slide_out_left
            )
        } catch (_: Exception) { }
    }

    fun startActivityForResultWithAnimation(
        activity: Activity,
        intent: Intent,
        requestCode: Int
    ) {
        try {
            activity.startActivityForResult(intent, requestCode)
            activity.overridePendingTransition(
                R.anim.slide_in_right, R.anim.slide_out_left
            )
        } catch (_: Exception) { }
    }
}