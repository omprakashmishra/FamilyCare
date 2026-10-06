package com.omsworld.familycare.ui.settings

import android.app.ProgressDialog
import android.view.LayoutInflater
import android.webkit.WebViewClient
import com.omsworld.familycare.base.BaseActivity
import com.omsworld.familycare.core.UrlList
import com.omsworld.familycare.databinding.TermAndConditionsAcBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermAndConditionsActivity : BaseActivity<TermAndConditionsAcBinding>() {

    override fun inflateBinding(inflater: LayoutInflater) =
        TermAndConditionsAcBinding.inflate(inflater)

    override fun onBindingReady() {
        if (!isNetworkAvailable()) {
            snack(binding.root, "Please connect to internet")
            finish()
            return
        }

        val progress = ProgressDialog(this).apply {
            setCancelable(false)
            setTitle("Loading...")
            show()
        }

        binding.WVTermcondition.settings.javaScriptEnabled = true
        binding.WVTermcondition.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                progress.dismiss()
            }
        }
        binding.WVTermcondition.loadUrl(UrlList.term_condition)

        binding.IVBackpress.setOnClickListener { finish() }
    }
}