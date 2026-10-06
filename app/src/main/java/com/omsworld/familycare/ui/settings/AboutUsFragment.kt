package com.omsworld.familycare.ui.settings

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.databinding.FragmentAboutusBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutUsFragment : BaseFragment<FragmentAboutusBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAboutusBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        val html = """
            <html><body style="text-align:justify; font-size:10px">
            <p>Optimization Software Technologies (Osoftec) provides process improvement-focused business intelligence analytics and enterprise-wide operations planning solutions...</p>
            </body></html>
        """.trimIndent()
        binding.WebView.loadData(html, "text/html; charset=UTF-8", "utf-8")

        binding.landlineno.setOnClickListener { dial(binding.landlineno.text.toString()) }
        binding.indiano.setOnClickListener { dial(binding.indiano.text.toString()) }

        binding.serviceemail.setOnClickListener {
            sendEmail("info@osoftec.com")
        }
        binding.email.setOnClickListener {
            sendEmail("support@osoftec.com")
        }
    }

    private fun dial(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone.trim()}"))
        try { startActivity(intent) } catch (_: Exception) {}
    }

    private fun sendEmail(address: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
            putExtra(Intent.EXTRA_SUBJECT, "")
            putExtra(Intent.EXTRA_TEXT, "")
        }
        try { startActivity(Intent.createChooser(intent, " ")) } catch (_: Exception) {}
    }
}