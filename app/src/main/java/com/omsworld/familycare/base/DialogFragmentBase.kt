package com.omsworld.familycare.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import timber.log.Timber

abstract class DialogFragmentBase<VB : ViewBinding> : DialogFragment() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    protected abstract fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VB

    protected open fun onBindingReady() {}

    protected open fun makeBackgroundTransparent(): Boolean = true

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            if (makeBackgroundTransparent()) {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                requestWindowFeature(Window.FEATURE_NO_TITLE)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateBinding(inflater, container)
        Timber.i("→ Dialog: ${this::class.simpleName}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBindingReady()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}