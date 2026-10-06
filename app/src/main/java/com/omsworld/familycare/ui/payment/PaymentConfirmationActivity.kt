package com.omsworld.familycare.ui.payment

import android.content.Intent
import android.view.LayoutInflater
import com.omsworld.familycare.base.BaseActivity
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.databinding.PaymentConfermationAcBinding
import com.omsworld.familycare.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentConfirmationActivity : BaseActivity<PaymentConfermationAcBinding>() {

    override fun inflateBinding(inflater: LayoutInflater) =
        PaymentConfermationAcBinding.inflate(inflater)

    override fun onBindingReady() {
        val userName = intent.getStringExtra(Constants.USER_NAME) ?: ""
        val packageName = intent.getStringExtra(Constants.PACKAGE_NAME) ?: ""
        val activationCode = intent.getStringExtra("ActivationCode") ?: ""
        val html = intent.getStringExtra(Constants.PAY_SUB_RESPONSE) ?: ""

        binding.tvUsername.text = userName
        binding.tvPackagename.text = packageName
        binding.tvActivationCode.text = activationCode
        binding.WVMessage.loadDataWithBaseURL("", html, "text/html", "UTF-8", "")

        binding.BtBackToLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            if (prefs.getString(this, Constants.PaymentStatusCode, "0") == "1069") {
                intent.putExtra("paymentProcess", "done")
            }
            startActivity(intent)
            finish()
        }
    }
}