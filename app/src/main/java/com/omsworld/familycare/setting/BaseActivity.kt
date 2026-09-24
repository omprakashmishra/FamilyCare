package com.omsworld.familycare.setting

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.omsworld.familycare.R
import com.omsworld.familycare.api_call.CallWebService
import com.omsworld.familycare.api_call.CommonFunctions
import com.omsworld.familycare.api_call.MyServiceListener
import com.omsworld.familycare.api_call.UrlList

abstract class BaseActivity : AppCompatActivity(), MyServiceListener {

    companion object {
        private const val TAG = "FieldUtils"

        lateinit var cdr: ConnectionDetector
        lateinit var commonFunctions: CommonFunctions
        lateinit var myservice: CallWebService
        lateinit var urlList: UrlList

        @JvmStatic
        fun jsonData(jsonData: String) {
            println(jsonData)
        }

        @JvmStatic
        fun tst(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }

        @JvmStatic
        fun log_(head: String, msg: String) {
            Log.d(head, "---->$msg")
        }
    }
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            )

            window.statusBarColor = getColorCompat(R.color.theme_dark)
        }

        super.onCreate(savedInstanceState)

        cdr = ConnectionDetector(this)
        commonFunctions = CommonFunctions(this)

        setTAG(this.toString())
        getAllBundle(intent.extras)
    }

    private fun getColorCompat(colorResId: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getColor(colorResId)
        } else {
            @Suppress("DEPRECATION")
            resources.getColor(colorResId)
        }
    }

    fun setTAG(tagValue: String) {
        Log.i("------***CLASS***-> ", tagValue)
    }

    fun getAllBundle(bundle: Bundle?) {
        try {
            bundle?.keySet()?.forEach { key ->
                Log.d(
                    "-------->>>>    ",
                    "$key==${bundle.getString(key)}"
                )
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error reading bundle", ex)
        }
    }

    fun isInternetAvailable(): Boolean {
        return cdr.isConnectingToInternet()
    }

    fun gotoNextActivityClearTop(
        currentActivity: Activity,
        nextActivity: Class<out Activity>
    ) {
        val intent = Intent(currentActivity, nextActivity).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        currentActivity.startActivity(intent)
        currentActivity.finish()
    }

    fun tstSnake(view: View, msg: String) {
        try {
            val snackbar = Snackbar.make(
                view,
                msg,
                Snackbar.LENGTH_LONG
            )

            snackbar.view.setBackgroundColor(
                Color.parseColor("#8c1212")
            )

            snackbar.show()
        } catch (ex: Exception) {
            Toast.makeText(
                this,
                msg,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun deviceId(): String {
        return Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    override fun onSuccess(string: String) {
        // Override in child activity if required
    }

    override fun onFailed() {
        // Override in child activity if required
    }


    override fun onBackPressed() {
        // Intentionally disabled.
        // Call super.onBackPressed() if normal back navigation is required.
    }
}