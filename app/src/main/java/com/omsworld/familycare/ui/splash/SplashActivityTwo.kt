package com.omsworld.familycare.ui.splash

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
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

        // Wait for the layout to be measured before starting pivot-based animations
        binding.splashRoot.post {
            startAnimations()
        }

        binding.splashRoot.postDelayed({
            navigateNext()
        }, 2600L)
    }

    private fun startAnimations() {
        startCenteredPulse(binding.splashOrb, scaleMax = 1.25f, duration = 900)
        startCenteredPulse(binding.splashGlow, scaleMax = 1.15f, duration = 900)

        startRotation(binding.splashRing1, 6000L, clockwise = true)
        startRotation(binding.splashRing2, 9000L, clockwise = false)
        startRotation(binding.splashRing3, 7000L, clockwise = true)

        startCenteredPulse(binding.splashLogo, scaleMax = 1.15f, duration = 700)

        val titleAnim = AnimationUtils.loadAnimation(this, R.anim.splash_text_slide_up)
        binding.splashTitle.startAnimation(titleAnim)

        val taglineAnim = AnimationUtils.loadAnimation(this, R.anim.splash_text_slide_up).apply {
            startOffset = 200L
        }
        binding.splashTagline.startAnimation(taglineAnim)

        val p1 = AnimationUtils.loadAnimation(this, R.anim.splash_particle_float)
        binding.particle1.startAnimation(p1)

        val p2 = AnimationUtils.loadAnimation(this, R.anim.splash_particle_float).apply {
            startOffset = 800L
        }
        binding.particle2.startAnimation(p2)

        val p3 = AnimationUtils.loadAnimation(this, R.anim.splash_particle_float).apply {
            startOffset = 1600L
        }
        binding.particle3.startAnimation(p3)

        startCenteredPulse(binding.dot1, 1.4f, 500, 0L)
        startCenteredPulse(binding.dot2, 1.4f, 500, 150L)
        startCenteredPulse(binding.dot3, 1.4f, 500, 300L)
    }

    private fun startCenteredPulse(
        view: View,
        scaleMax: Float = 1.2f,
        duration: Long = 800,
        delay: Long = 0L
    ) {
        view.pivotX = view.width / 2f
        view.pivotY = view.height / 2f

        ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat("scaleX", 1f, scaleMax),
            PropertyValuesHolder.ofFloat("scaleY", 1f, scaleMax),
            PropertyValuesHolder.ofFloat("alpha", 1f, 0.85f)
        ).apply {
            this.duration = duration
            this.startDelay = delay
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun startRotation(view: View, duration: Long, clockwise: Boolean) {
        view.pivotX = view.width / 2f
        view.pivotY = view.height / 2f

        val to = if (clockwise) 360f else -360f

        ObjectAnimator.ofFloat(view, "rotation", 0f, to).apply {
            this.duration = duration
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
    }

    private fun navigateNext() {
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.splash_fade_out)
        binding.splashRoot.startAnimation(fadeOut)

        binding.splashRoot.postDelayed({
            val next = if (vm.isLoggedIn()) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, SignInUpActivity::class.java)
            }
            AppUtil.startActivityWithAnimation(this, next)
            finish()
        }, 500L)
    }
}