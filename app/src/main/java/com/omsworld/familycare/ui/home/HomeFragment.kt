package com.omsworld.familycare.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.appcompat.widget.Toolbar
import coil.load
import com.omsworld.familycare.R
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.databinding.HomeFrBinding
import com.omsworld.familycare.location.GpsAlert
import com.omsworld.familycare.location.TrackGPS
import com.omsworld.familycare.service.TrackingService
import com.omsworld.familycare.ui.contacts.SharedContactsFragment
import com.omsworld.familycare.ui.diary.DiaryFragment
import com.omsworld.familycare.ui.family.FamilyMembersFragment
import com.omsworld.familycare.ui.main.MainActivity
import com.omsworld.familycare.ui.news.NewsEventsFragment
import com.omsworld.familycare.ui.profile.ProfileFragment
import com.omsworld.familycare.ui.shopping.ShoppingMainActivity
import com.omsworld.familycare.ui.shopping.web.ShoppingSitesListFragment
import com.omsworld.familycare.ui.tracking.MyTrackingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeFrBinding>() {

    private val vm: HomeViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        HomeFrBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        Timber.d("HomeFragment started")

        loadUserProfile()
        setupSafeZoneSwitch()
        setupClickListeners()

        observeState()
    }

    // ==================== User Profile ====================

    private fun loadUserProfile() {
        val userImage = prefs.getString(requireContext(), Constants.USER_IMAGE)
        val name = prefs.getString(requireContext(), Constants.USER_NAME)
        val mobile = prefs.getString(requireContext(), Constants.MOBILE_only)

        binding.TVName.text = name
        binding.TVRoleName.text = mobile
        binding.TVOrg.visibility = View.GONE

        if (userImage.isNotBlank()) {
            binding.CIVProfileDp.load(userImage) {
                placeholder(R.drawable.teacher_fml)
                error(R.drawable.teacher_fml)
            }
        }
        binding.mprogressBar.visibility = View.GONE

        binding.IVGifimg.setImageResource(R.drawable.with_friends)
    }

    // ==================== Safe Zone Switch ====================

    private fun setupSafeZoneSwitch() {
        val safeZoneOn = prefs.getString(requireContext(), Constants.safeJone) == "1"
        binding.SWSafeJone.isChecked = safeZoneOn

        binding.SWSafeJone.setOnCheckedChangeListener { _: CompoundButton, isChecked ->
            if (isChecked) {
                // ⚠️ Only start tracking if we have location permission
                val hasPermission = ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasPermission) {
                    toast("Please enable location permission first")
                    binding.SWSafeJone.isChecked = false
                    return@setOnCheckedChangeListener
                }

                prefs.setString(requireContext(), Constants.safeJone, "1")

                try {
                    ContextCompat.startForegroundService(
                        requireContext(),
                        Intent(requireContext(), TrackingService::class.java)
                    )
                } catch (e: Exception) {
                    Timber.e(e, "Could not start TrackingService")
                }

                checkGpsTracker()
            } else {
                prefs.setString(requireContext(), Constants.safeJone, "0")
                requireContext().stopService(
                    Intent(requireContext(), TrackingService::class.java)
                )
            }
        }
    }

    private fun checkGpsTracker(): Boolean {
        val gps = TrackGPS(requireContext())
        return if (gps.canGetLocation) {
            gps.stop()
            true
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                GpsAlert.check(requireContext())
            }
            false
        }
    }

    // ==================== Click Listeners ====================

    private fun setupClickListeners() {
        binding.CIVProfileDp.setOnClickListener {
            openFragment(ProfileFragment(), "Profile")
        }
        binding.RLAddFamily.setOnClickListener {
            openFragment(FamilyMembersFragment(), "Manage Your Family")
        }
        binding.RLLocation.setOnClickListener {
            openFragment(MyTrackingFragment(), "Family Location")
        }
        binding.RLPost.setOnClickListener {
            openFragment(SharedContactsFragment(), "Phone Book Shared")
        }
        binding.RLDiary.setOnClickListener {
            openFragment(DiaryFragment(), "My Diary")
        }
        binding.RLNewsNotiTtl.setOnClickListener {
            openFragment(NewsEventsFragment(), "Health/Other News")
        }
        binding.RLCurentdate.setOnClickListener {
            toast("Coming soon with your family home DoorBell")
        }
        binding.RLHelp.setOnClickListener {
            openFragment(ShoppingSitesListFragment(), "All In One")
        }
        binding.RLManageProfile.setOnClickListener {
            openFragment(ProfileFragment(), "Profile")
        }
        binding.RLShopping.setOnClickListener {
            startActivity(Intent(requireContext(), ShoppingMainActivity::class.java))
        }
        binding.IVInfo.setOnClickListener {
            toast("When you turn on Safe Zone, your family stays connected with you on the map.")
        }
    }

    private fun openFragment(fragment: Fragment, title: String) {
        (activity as? MainActivity)?.let {
            it.findViewById<Toolbar>(R.id.toolbar12)?.title = title
            it.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    // ==================== State Observer ====================

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is HomeUiState.Idle -> Unit
                is HomeUiState.Loaded -> {
                    binding.TVName.text = state.name
                    binding.TVRoleName.text = state.mobile
                    if (state.image.isNotBlank()) {
                        binding.CIVProfileDp.load(state.image) {
                            placeholder(R.drawable.teacher_fml)
                            error(R.drawable.teacher_fml)
                        }
                    }
                }
                is HomeUiState.Error -> {
                    snack(state.message)
                }
            }
        }
    }
}