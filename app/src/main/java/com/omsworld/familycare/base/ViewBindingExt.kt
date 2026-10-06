package com.omsworld.familycare.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

inline fun <T : ViewBinding> Fragment.viewBinding(
    crossinline bind: (View) -> T
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    bind(requireView())
}

inline fun <T : ViewBinding> Fragment.inflateBinding(
    crossinline factory: (LayoutInflater, ViewGroup?, Boolean) -> T
): T = factory(layoutInflater, null, false)