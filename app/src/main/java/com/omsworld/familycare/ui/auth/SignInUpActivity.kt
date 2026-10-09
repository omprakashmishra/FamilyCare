package com.omsworld.familycare.ui.auth

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.omsworld.familycare.R
import com.omsworld.familycare.base.BaseActivity
import com.omsworld.familycare.base.UiEvent
import com.omsworld.familycare.core.AppUtil
import com.omsworld.familycare.databinding.ActivitySigninBinding
import com.omsworld.familycare.databinding.ForgetpasswordDialogBinding
import com.omsworld.familycare.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInUpActivity : BaseActivity<ActivitySigninBinding>() {

    private val vm: SignInUpViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater) =
        ActivitySigninBinding.inflate(inflater)

    override fun onBindingReady() {
        if (vm.isAlreadyLoggedIn()) {
            goNextClearTop(MainActivity::class.java)
            return
        }

        hideKeyboard()

        binding.tvForgotPassword.setOnClickListener { showForgotDialog() }

        binding.tvSignUp.setOnClickListener {
            AppUtil.startActivityWithAnimation(
                this,
                Intent(this, SignUpActivity::class.java)
            )
        }

        binding.btSignIn.setOnClickListener { onOkClick() }

        binding.tvTrydemo.setOnClickListener {
            vm.login("919999999999", "1234")
        }

        observeState()
    }

    private fun onOkClick() {
        hideKeyboard()
        if (!isNetworkAvailable()) {
            snack(binding.root, "Internet connection failed!")
            return
        }
        val mobile = binding.etMobile.text.toString().trim()
        val pin = binding.etPassword.text.toString().trim()
        val code =binding.ccp.selectedCountryCode.toString()
        if (mobile.isBlank() ||pin.isBlank()) {
            snack(binding.root, "Enter valid mobile and pin!")
            return
        }
        vm.login(code+mobile, pin)
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    vm.state.collect { state ->
                        when (state) {
                            is SignInUiState.Idle -> Unit
                            is SignInUiState.Loading -> {
                                binding.mprogressBar.visibility = View.VISIBLE
                            }
                            is SignInUiState.Success -> {
                                binding.mprogressBar.visibility = View.GONE
                                AppUtil.startActivityWithAnimation(
                                    this@SignInUpActivity,
                                    Intent(this@SignInUpActivity, MainActivity::class.java)
                                )
                                finish()
                            }
                            is SignInUiState.Error -> {
                                binding.mprogressBar.visibility = View.GONE
                                snack(binding.root, state.msg)
                            }
                            is SignInUiState.NeedsOtp -> {
                                binding.mprogressBar.visibility = View.GONE
                                val i = Intent(this@SignInUpActivity, VerifyPinActivity::class.java)
                                i.putExtra("mobile", state.mobile)
                                AppUtil.startActivityWithAnimation(this@SignInUpActivity, i)
                                finish()
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    vm.events.collect { event ->
                        when (event) {
                            is UiEvent.ShowMessage -> toast(event.message)
                            is UiEvent.ShowError -> snack(binding.root, event.message)
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun showForgotDialog() {
        val dialog = Dialog(this)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val b = ForgetpasswordDialogBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(b.root)

        b.edtEmail.hint = "Please enter your mobile No."

        b.lay11.setOnClickListener { dialog.dismiss() }
        b.pencil.setOnClickListener { dialog.dismiss() }
        b.cancel.setOnClickListener { dialog.dismiss() }

        b.send.setOnClickListener {
            val mobile = b.edtEmail.text.toString().trim()
            if (TextUtils.isEmpty(mobile)) {
                b.edtEmail.error = "Please enter an mobile No."
                return@setOnClickListener
            }
            dialog.dismiss()
            vm.forgotPassword(mobile)
        }

        dialog.show()
    }
}
