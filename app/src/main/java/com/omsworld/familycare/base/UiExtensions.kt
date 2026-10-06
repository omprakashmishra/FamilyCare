package com.omsworld.familycare.base

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar

// ==================== View ====================
fun View.visible() { visibility = View.VISIBLE }
fun View.gone() { visibility = View.GONE }
fun View.invisible() { visibility = View.INVISIBLE }

fun View.toggleVisibility() {
    visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
}

fun View.snack(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun View.snackShort(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

// ==================== Context ====================
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.toastShort(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.hideKeyboard(view: View) {
    getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(view.windowToken, 0)
}

// ==================== Fragment ====================
fun Fragment.toast(message: String) {
    context?.let { Toast.makeText(it, message, Toast.LENGTH_LONG).show() }
}

fun FragmentActivity.replace(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = true
) {
    supportFragmentManager.beginTransaction().apply {
        replace(containerId, fragment)
        if (addToBackStack) addToBackStack(null)
        commit()
    }
}