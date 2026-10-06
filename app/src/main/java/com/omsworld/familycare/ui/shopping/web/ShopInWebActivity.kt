package com.omsworld.familycare.ui.shopping.web

import android.graphics.Bitmap
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.omsworld.familycare.R
import com.omsworld.familycare.base.BaseActivity
import com.omsworld.familycare.databinding.ShopInWebAcBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopInWebActivity : BaseActivity<ShopInWebAcBinding>() {

    override fun inflateBinding(inflater: LayoutInflater) =
        ShopInWebAcBinding.inflate(inflater)

    override fun onBindingReady() {
        val siteName = intent.getStringExtra("siteName") ?: ""
        val siteUrl = intent.getStringExtra("siteUrl") ?: ""

        binding.toolbarBack.TVToolbarText.visibility = View.VISIBLE
        binding.toolbarBack.TVToolbarText.text =
            siteName.ifBlank { getString(R.string.app_name) }

        val settings: WebSettings = binding.webview01.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true

        binding.webview01.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                binding.progressBar1.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                binding.progressBar1.visibility = View.GONE
            }
        }

        if (siteUrl.isNotBlank()) {
            binding.webview01.loadUrl(siteUrl)
        }

        binding.toolbarBack.RLBackClick.setOnClickListener { finish() }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.webview01.canGoBack()) {
            binding.webview01.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}