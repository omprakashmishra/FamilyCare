package com.omsworld.familycare.ui.shopping

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.DateTimeUtil
import com.omsworld.familycare.databinding.ScanFrBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanFragment : BaseFragment<ScanFrBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        ScanFrBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        // Restore last scan result
        val saved = prefs.getString(requireContext(), Constants.LAST_SCAN_RESULT, "0")
        if (saved.contains("=")) {
            val parts = saved.split("=")
            binding.TVDateTime.text = parts.getOrNull(0) ?: ""
            binding.TVBarcodeText.text = parts.getOrNull(1) ?: ""
        }

        // Handle new scan
        if (Constants.scan_result.isNotBlank()) {
            binding.TVBarcodeText.text = Constants.scan_result
            val currentTime = DateTimeUtil.formatDateTime("currentDateTime")
            prefs.setString(
                requireContext(),
                Constants.LAST_SCAN_RESULT,
                "Last Scan: $currentTime=${Constants.scan_result}"
            )
            Constants.scan_result = ""
        }

        binding.IVGoto.setOnClickListener { openUrl() }
        binding.IVShare.setOnClickListener { share() }
        binding.IVBarcode.setOnClickListener {
            startActivity(Intent(requireContext(), ScanActivity::class.java))
        }
        binding.BTAddToGrocery.setOnClickListener {
            toast("Add to grocery — not implemented yet")
        }
    }

    private fun openUrl() {
        val url = binding.TVBarcodeText.text.toString()
        if (url.isNotBlank()) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (_: Exception) {}
        }
    }

    private fun share() {
        val item = binding.TVBarcodeText.text.toString() +
                " \n" + binding.ETExtraComment.text.toString()
        if (binding.TVBarcodeText.text.toString().isBlank()) return
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, item)
            type = "text/plain"
        }
        startActivity(intent)
    }
}