package com.omsworld.familycare.ui.auth

import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.omsworld.familycare.base.BaseActivity
import com.omsworld.familycare.core.AppUtil
import com.omsworld.familycare.databinding.ActivityVerifyOtpBinding
import com.omsworld.familycare.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerifyPinActivity : BaseActivity<ActivityVerifyOtpBinding>() {

    private val vm: VerifyPinViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater) =
        ActivityVerifyOtpBinding.inflate(inflater)

    override fun onBindingReady() {
        val mobile = intent.getStringExtra("mobile") ?: ""
        binding.headerTextRegister.text = mobile

        binding.btnContinue.setOnClickListener {
            val otp = binding.txtOTP.text.toString().trim()
            if (otp.isEmpty()) {
                snack(binding.root, "Enter OTP")
                return@setOnClickListener
            }
            vm.verify(mobile, otp)
        }

        binding.IVBackpress.setOnClickListener {
            AppUtil.finishActivityWithAnimationRight(this)
        }

        binding.TVResendotp.setOnClickListener { vm.resend(mobile) }

        observeState()
    }

    private fun observeState() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            vm.state.collect { state ->
                when (state) {
                    is VerifyUiState.Idle -> Unit
                    is VerifyUiState.Loading -> binding.btnContinue.isEnabled = false
                    is VerifyUiState.Success -> {
                        AppUtil.startActivityWithAnimation(
                            this@VerifyPinActivity,
                            Intent(this@VerifyPinActivity, MainActivity::class.java)
                        )
                        finish()
                    }
                    is VerifyUiState.Error -> {
                        binding.btnContinue.isEnabled = true
                        snack(binding.root, state.msg)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtil.finishActivityWithAnimationRight(this)
    }
}