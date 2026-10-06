package com.omsworld.familycare.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.remote.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    /** Lazy, safe access to prefs without Hilt injection. */
    protected val prefs: MySharedPreference
        get() = MySharedPreference.getInstance()

    /** Lazy, safe access to network monitor. */
    protected val networkMonitor: NetworkMonitor by lazy {
        NetworkMonitor(requireContext().applicationContext)
    }

    protected abstract fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VB

    protected open fun onBindingReady() {}

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateBinding(inflater, container)
        Timber.i("→ Fragment: ${this::class.simpleName}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBindingReady()
    }

    protected fun color(@ColorRes id: Int): Int =
        ContextCompat.getColor(requireContext(), id)

    protected fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()

    protected fun toast(@StringRes msgRes: Int) =
        Toast.makeText(requireContext(), msgRes, Toast.LENGTH_LONG).show()

    protected fun snack(msg: String) {
        _binding?.let { Snackbar.make(it.root, msg, Snackbar.LENGTH_LONG).show() }
    }

    protected fun isNetworkAvailable(): Boolean = networkMonitor.isOnline()

    protected fun <T> collectState(flow: Flow<T>, block: suspend (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect { block(it) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}