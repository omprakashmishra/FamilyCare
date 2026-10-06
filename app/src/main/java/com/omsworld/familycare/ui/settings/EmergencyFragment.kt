package com.omsworld.familycare.ui.settings

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.databinding.EmergencyNoFrBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmergencyFragment : BaseFragment<EmergencyNoFrBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        EmergencyNoFrBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        binding.LLPolice.setOnClickListener { call(binding.TvPolice.text.toString()) }
        binding.LLFire.setOnClickListener { call(binding.TvFire.text.toString()) }
        binding.LLAmbulace.setOnClickListener { call(binding.TvAmbulace.text.toString()) }
        binding.LLWomen.setOnClickListener { call(binding.TVWomen.text.toString()) }
        binding.LLChild.setOnClickListener { call(binding.TvChild.text.toString()) }
    }

    private fun call(number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${number.trim()}"))
        try { startActivity(intent) } catch (_: Exception) {}
    }
}