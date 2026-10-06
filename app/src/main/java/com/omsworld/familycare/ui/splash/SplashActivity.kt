package com.omsworld.familycare.ui.splash

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import com.omsworld.familycare.R
import com.omsworld.familycare.base.BaseActivity
import com.omsworld.familycare.core.AppUtil
import com.omsworld.familycare.databinding.SplashBinding
import com.omsworld.familycare.ui.auth.SignInUpActivity
import com.omsworld.familycare.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SplashActivity : BaseActivity<SplashBinding>() {

    private val vm: SplashViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater) =
        SplashBinding.inflate(inflater)

    override fun onBindingReady() {
        Timber.d("Splash started")

        // Bounce animation on logo
        val bounce = AnimationUtils.loadAnimation(this, R.anim.bounce)
        binding.IvSplash.startAnimation(bounce)

        // Show progress bar
        binding.LLPrgressBarLayout.visibility = View.VISIBLE

        // Navigate after delay
        binding.LLPrgressBarLayout.postDelayed({
            navigateNext()
        }, 1500L)
    }

    private fun navigateNext() {
        val next = if (vm.isLoggedIn()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, SignInUpActivity::class.java)
        }
        AppUtil.startActivityWithAnimation(this, next)
        finish()
    }
}