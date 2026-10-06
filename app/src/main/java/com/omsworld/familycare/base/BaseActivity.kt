package com.omsworld.familycare.base

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.omsworld.familycare.R
import com.omsworld.familycare.core.AppUtil
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.remote.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    /** Lazy, safe access to prefs without Hilt injection. */
    protected val prefs: MySharedPreference
        get() = MySharedPreference.getInstance()

    /** Lazy, safe access to network monitor. */
    protected val networkMonitor: NetworkMonitor by lazy {
        NetworkMonitor(applicationContext)
    }

    protected abstract fun inflateBinding(inflater: LayoutInflater): VB

    protected open fun onBindingReady() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        applyStatusBar()
        super.onCreate(savedInstanceState)
        _binding = inflateBinding(layoutInflater)
        setContentView(binding.root)
        Timber.i("→ Activity: ${this::class.simpleName}")
        onBindingReady()
    }

    private fun applyStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color(R.color.theme_dark)
        }
    }

    protected fun color(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)

    protected fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

    protected fun toast(@StringRes msgRes: Int) =
        Toast.makeText(this, msgRes, Toast.LENGTH_LONG).show()

    protected fun snack(view: View, msg: String) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).apply {
            view.setBackgroundColor(Color.parseColor("#8c1212"))
            show()
        }
    }

    protected fun isNetworkAvailable(): Boolean = networkMonitor.isOnline()

    protected fun deviceId(): String =
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

    protected fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    protected fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    protected fun goNext(next: Class<*>) {
        AppUtil.startActivityWithAnimation(this, Intent(this, next))
    }

    protected fun goNextClearTop(next: Class<*>) {
        startActivity(
            Intent(this, next).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }

    protected fun <T> collectState(flow: Flow<T>, block: suspend (T) -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect { block(it) }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}