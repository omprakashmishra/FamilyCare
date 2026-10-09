package com.omsworld.familycare.ui.auth

import android.content.Intent
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.omsworld.familycare.R
import com.omsworld.familycare.base.BaseActivity
import com.omsworld.familycare.core.AppUtil
import com.omsworld.familycare.core.FieldUtils
import com.omsworld.familycare.databinding.ActivitySignUpBinding
import com.omsworld.familycare.ui.main.MainActivity
import com.omsworld.familycare.ui.settings.TermAndConditionsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpActivity : BaseActivity<ActivitySignUpBinding>() {

    private val vm: SignUpViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater) =
        ActivitySignUpBinding.inflate(inflater)

    override fun onBindingReady() {
        binding.edtUsername.requestFocus()
        binding.CKInvitee.setOnClickListener {
            binding.LLInvitationCode.visibility =
                if (binding.CKInvitee.isChecked) View.VISIBLE else View.GONE
        }

        binding.subscriptionCheckbox.setOnClickListener {
            val on = binding.subscriptionCheckbox.isChecked
            binding.btnSubscribe.isEnabled = on
            binding.btnSubscribe.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    if (on) R.color.theme_color else R.color.RedFadeColor
                )
            )
        }

        binding.tvTermsconditions.setOnClickListener {
            startActivity(Intent(this, TermAndConditionsActivity::class.java))
        }

        binding.btnSubscribe.setOnClickListener { register() }

        observeState()
    }

    private fun register() {
        val username = binding.edtUsername.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val code = binding.SPCountrycode.selectedCountryCode.toString().trim()
        val mobile = binding.etMobile.text.toString().trim()
        val password = binding.etPin.text.toString()
        val confirm = binding.etConfirmPin.text.toString()
        val invite = binding.eTOne.text.toString().trim()

        if (!binding.subscriptionCheckbox.isChecked) {
            snack(binding.root, "Please accept the terms to continue.")
            return
        }
        if (FieldUtils.isBlank(username)) {
            binding.edtUsername.error = "Enter username"; return
        }
        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.error = "Enter valid email"; return
        }
        if (mobile.length !in 10..11) {
            binding.etMobile.error = "Enter valid mobile"; return
        }
        if (password.length < 2) {
            binding.etPin.error = "Min 2 characters"; return
        }
        if (password != confirm) {
            binding.etConfirmPin.error = "Passwords do not match"; return
        }

        vm.register(username, email, code+mobile, password, invite)
    }

    private fun observeState() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            vm.state.collect { state ->
                when (state) {
                    is SignUpUiState.Idle -> Unit
                    is SignUpUiState.Loading -> {
                        binding.btnSubscribe.isEnabled = false
                    }
                    is SignUpUiState.Success -> {
                        val i = Intent(this@SignUpActivity, MainActivity::class.java)
                        i.putExtra("mobile", state.mobile)
                        AppUtil.startActivityWithAnimation(this@SignUpActivity, i)
                        finish()
                    }
                    is SignUpUiState.Error -> {
                        binding.btnSubscribe.isEnabled = true
                        snack(binding.root, state.msg)
                    }
                    else -> Unit
                }
            }
        }
    }
}