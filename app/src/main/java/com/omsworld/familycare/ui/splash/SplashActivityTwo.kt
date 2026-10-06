package com.omsworld.familycare.ui.splash

import android.content.Intent
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import com.omsworld.familycare.R
import com.omsworld.familycare.base.BaseActivity
import com.omsworld.familycare.core.AppUtil
import com.omsworld.familycare.databinding.SplashTwoBinding
import com.omsworld.familycare.ui.auth.SignInUpActivity
import com.omsworld.familycare.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SplashActivityTwo : BaseActivity<SplashTwoBinding>() {

    private val vm: SplashViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater) =
        SplashTwoBinding.inflate(inflater)

    override fun onBindingReady() {
        Timber.d("Splash two started")

        // Animation
        val topAnim = AnimationUtils.loadAnimation(this, R.anim.down_from_top)
        val bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_down)
        binding.RLTop.startAnimation(topAnim)
        binding.RLBottom.startAnimation(bottomAnim)

        // Navigate after animation
        binding.RLBottom.postDelayed({
            navigateNext()
        }, 1800L)
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