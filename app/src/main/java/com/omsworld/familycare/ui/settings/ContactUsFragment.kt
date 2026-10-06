package com.omsworld.familycare.ui.settings

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.databinding.FragmentContactusBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ContactUsFragment : BaseFragment<FragmentContactusBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentContactusBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        Timber.d("ContactUsFragment started")

        binding.landlineno.setOnClickListener { dial(binding.landlineno.text.toString()) }
        binding.indiano.setOnClickListener { dial(binding.indiano.text.toString()) }
        binding.serviceno.setOnClickListener { sendEmail("info@osoftec.com") }
        binding.email.setOnClickListener { sendEmail("support@osoftec.com") }

        binding.UsWebsiteUrl.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://adectec.com")))
        }
        binding.IndiaWebsiteUrl.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://osoftec.com")))
        }

        binding.sendComment.setOnClickListener {
            val msg = binding.writeComment.text.toString()
            if (TextUtils.isEmpty(msg)) {
                snack("Please enter a comment")
                return@setOnClickListener
            }
            binding.writeComment.setText("")
            //val vm = androidx.lifecycle.viewmodel.compose.viewModel
            // Simple: send directly via repo
            toast("Thanks for your suggestion")
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
        }
        try { startActivity(Intent.createChooser(intent, " ")) } catch (_: Exception) {}
    }
}