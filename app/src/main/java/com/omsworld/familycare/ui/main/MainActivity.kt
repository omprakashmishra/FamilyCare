package com.omsworld.familycare.ui.main

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.omsworld.familycare.R
import com.omsworld.familycare.base.BaseActivity
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.databinding.ActivityMainBinding
import com.omsworld.familycare.databinding.NotificationAlertBinding
import com.omsworld.familycare.service.TrackingService
import com.omsworld.familycare.ui.auth.SignInUpActivity
import com.omsworld.familycare.ui.chat.FriendsListFragment
import com.omsworld.familycare.ui.contacts.SharedContactsFragment
import com.omsworld.familycare.ui.diary.DiaryFragment
import com.omsworld.familycare.ui.family.FamilyMembersFragment
import com.omsworld.familycare.ui.home.HomeFragment
import com.omsworld.familycare.ui.profile.ProfileFragment
import com.omsworld.familycare.ui.settings.AboutUsFragment
import com.omsworld.familycare.ui.settings.ContactUsFragment
import com.omsworld.familycare.ui.settings.SettingsFragment
import com.omsworld.familycare.ui.settings.TermAndConditionsActivity
import com.omsworld.familycare.ui.shopping.ScanFragment
import com.omsworld.familycare.ui.shopping.ShoppingMainActivity
import com.omsworld.familycare.ui.tracking.MyTrackingFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val vm: MainViewModel by viewModels()
    private var doubleBackToExitPressedOnce = false

    // ============================================================
    // Permission launchers (must be fields, not locals)
    // ============================================================

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val fine = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fine || coarse) {
            Timber.d("Location permission granted")

            // On Android 10+ also ask for background location
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val bgGranted = ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (!bgGranted) {
                    backgroundLocationLauncher.launch(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    return@registerForActivityResult
                }
            }
            startTrackingIfSafeZone()
        } else {
            toast("Location permission is required for tracking")
        }
    }

    private val backgroundLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Timber.d("Background location granted: $granted")
        startTrackingIfSafeZone()
    }

    // ============================================================
    // Lifecycle
    // ============================================================

    override fun inflateBinding(inflater: LayoutInflater) =
        ActivityMainBinding.inflate(inflater)

    override fun onBindingReady() {
        Timber.d("MainActivity started")

        setupToolbar()
        setupDrawer()
        setupNavigationClicks()
        setupBottomTabs()

        // Request location permissions on first entry
        ensureLocationPermissions()

        // Initial fragment
        if (intent.extras?.getString("from") == "otp") {
            replaceFragment(FamilyMembersFragment())
        } else {
            replaceFragment(HomeFragment())
        }
    }

    // ============================================================
    // Toolbar
    // ============================================================

    private fun setupToolbar() {
        setSupportActionBar(binding.appBarMain.toolbar12)
        binding.appBarMain.toolbar12.setTitleTextColor(color(R.color.white))
        binding.appBarMain.toolbar12.title = "Family Care"

        binding.appBarMain.IVNotification.setOnClickListener {
            showNotificationAlert()
        }
    }

    // ============================================================
    // Drawer
    // ============================================================

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.appBarMain.toolbar12,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    // ============================================================
    // Navigation Drawer Items
    // ============================================================

    private fun setupNavigationClicks() {
        binding.leftDrawer.LLHome.setOnClickListener { onNavClick(R.id.LL_home) }
        binding.leftDrawer.LLTracking.setOnClickListener { onNavClick(R.id.LL_tracking) }
        binding.leftDrawer.LLInvitation.setOnClickListener { onNavClick(R.id.LL_invitation) }
        binding.leftDrawer.LLSharedcontact.setOnClickListener { onNavClick(R.id.LL_sharedcontact) }
        binding.leftDrawer.LLNotes.setOnClickListener { onNavClick(R.id.LL_notes) }
        binding.leftDrawer.LLProfile.setOnClickListener { onNavClick(R.id.LL_profile) }
        binding.leftDrawer.LLAboutus.setOnClickListener { onNavClick(R.id.LL_aboutus) }
        binding.leftDrawer.LLContactus.setOnClickListener { onNavClick(R.id.LL_contactus) }
        binding.leftDrawer.LLSettings.setOnClickListener { onNavClick(R.id.LL_Settings) }
        binding.leftDrawer.LLTermsCondition.setOnClickListener { onNavClick(R.id.LL_TermsCondition) }
        binding.leftDrawer.LLLogout.setOnClickListener { onNavClick(R.id.LL_logout) }
    }

    private fun onNavClick(id: Int) {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        when (id) {
            R.id.LL_home -> {
                supportFragmentManager.popBackStack(
                    null, FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                replaceFragment(HomeFragment())
                binding.appBarMain.toolbar12.title = "Home"
            }
            R.id.LL_tracking -> {
                replaceFragment(MyTrackingFragment())
                binding.appBarMain.toolbar12.title = "Family Location"
            }
            R.id.LL_invitation -> {
                replaceFragment(FamilyMembersFragment())
                binding.appBarMain.toolbar12.title = "Manage Your Family"
            }
            R.id.LL_sharedcontact -> {
                replaceFragment(SharedContactsFragment())
                binding.appBarMain.toolbar12.title = "Phone Book Post"
            }
            R.id.LL_notes -> {
                replaceFragment(DiaryFragment())
                binding.appBarMain.toolbar12.title = "My Diary"
            }
            R.id.LL_profile -> {
                replaceFragment(ProfileFragment())
                binding.appBarMain.toolbar12.title = "Profile"
            }
            R.id.LL_aboutus -> {
                replaceFragment(AboutUsFragment())
                binding.appBarMain.toolbar12.title = "About Us"
            }
            R.id.LL_contactus -> {
                replaceFragment(ContactUsFragment())
                binding.appBarMain.toolbar12.title = "Help"
            }
            R.id.LL_Settings -> {
                replaceFragment(SettingsFragment())
                binding.appBarMain.toolbar12.title = "Settings"
            }
            R.id.LL_TermsCondition -> {
                startActivity(Intent(this, TermAndConditionsActivity::class.java))
            }
            R.id.LL_logout -> logout()
        }
    }

    // ============================================================
    // Bottom Tabs
    // ============================================================

    private fun setupBottomTabs() {
        binding.appBarMain.bottomDrawer.tab1.setOnClickListener { onTabClick(1) }
        binding.appBarMain.bottomDrawer.tab2.setOnClickListener { onTabClick(2) }
        binding.appBarMain.bottomDrawer.tab3.setOnClickListener { onTabClick(3) }
        binding.appBarMain.bottomDrawer.tab4.setOnClickListener { onTabClick(4) }
        binding.appBarMain.bottomDrawer.tab5.setOnClickListener { onTabClick(5) }
    }

    private fun onTabClick(index: Int) {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        when (index) {
            1 -> {
                setTabSelected(1)
                supportFragmentManager.popBackStack(
                    null, FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                replaceFragment(HomeFragment())
                binding.appBarMain.toolbar12.title = "Family Care"
            }
            2 -> {
                setTabSelected(2)
                replaceFragment(SharedContactsFragment())
                binding.appBarMain.toolbar12.title = "Phone Book Post"
            }
            3 -> {
                setTabSelected(3)
                replaceFragment(FriendsListFragment())
                binding.appBarMain.toolbar12.title = "Family Post Message"
            }
            4 -> {
                setTabSelected(4)
                replaceFragment(ScanFragment())
                binding.appBarMain.toolbar12.title = "Scan"
            }
            5 -> {
                setTabSelected(5)
                startActivity(Intent(this, ShoppingMainActivity::class.java))
            }
        }
    }

    private fun setTabSelected(index: Int) {
        val b = binding.appBarMain.bottomDrawer
        val black = color(R.color.black)
        val accent = color(R.color.theme_color)
        val tabColor = color(R.color.tab_color)
        val white = color(R.color.white)

        // Reset all
        b.petTab.setColorFilter(black)
        b.searchTab.setColorFilter(black)
        b.likeTab.setColorFilter(black)
        b.calenderTab.setColorFilter(black)
        b.profileTab.setColorFilter(black)
        b.TVHome.setTextColor(black)
        b.TVSearch.setTextColor(black)
        b.TVFav.setTextColor(black)
        b.TVbh.setTextColor(black)
        b.TVaccount.setTextColor(black)
        b.tab1.setBackgroundColor(tabColor)
        b.tab2.setBackgroundColor(tabColor)
        b.tab3.setBackgroundColor(tabColor)
        b.tab4.setBackgroundColor(tabColor)
        b.tab5.setBackgroundColor(tabColor)

        when (index) {
            1 -> {
                b.petTab.setColorFilter(accent)
                b.TVHome.setTextColor(accent)
                b.tab1.setBackgroundColor(white)
            }
            2 -> {
                b.searchTab.setColorFilter(accent)
                b.TVSearch.setTextColor(accent)
                b.tab2.setBackgroundColor(white)
            }
            3 -> {
                b.likeTab.setColorFilter(accent)
                b.TVFav.setTextColor(accent)
                b.tab3.setBackgroundColor(white)
            }
            4 -> {
                b.calenderTab.setColorFilter(accent)
                b.TVbh.setTextColor(accent)
            }
            5 -> {
                b.profileTab.setColorFilter(accent)
                b.TVaccount.setTextColor(accent)
                b.tab5.setBackgroundColor(white)
            }
        }
    }

    // ============================================================
    // Fragment Helper
    // ============================================================

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    // ============================================================
    // Notification Alert Popup
    // ============================================================

    private fun showNotificationAlert() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        val dialogBinding = NotificationAlertBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(true)

        val topMarginPx = (35 * resources.displayMetrics.density).toInt()

        dialog.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.TOP or Gravity.END)

            val params = attributes
            params.y = topMarginPx  // offset from top of the screen
            attributes = params
        }

        dialog.show()
    }

    // ============================================================
    // Logout
    // ============================================================

    private fun logout() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        vm.logout()
        val intent = Intent(this, SignInUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    // ============================================================
    // Permission flow
    // ============================================================

    private fun ensureLocationPermissions() {
        val fine = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fine && !coarse) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val bg = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if (!bg) {
                backgroundLocationLauncher.launch(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
                return
            }
        }

        // If we got here, we have all permissions
        startTrackingIfSafeZone()
    }

    private fun startTrackingIfSafeZone() {
        val safeZone = prefs.getString(this, Constants.safeJone)
        if (safeZone != "1") return

        val hasPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            Timber.w("Cannot start tracking — no location permission")
            return
        }

        try {
            ContextCompat.startForegroundService(
                this,
                Intent(this, TrackingService::class.java)
            )
        } catch (e: Exception) {
            Timber.e(e, "Could not start TrackingService")
        }
    }

    // ============================================================
    // Back press — double-tap to exit
    // ============================================================

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        try {
            binding.appBarMain.toolbar12.visibility = View.VISIBLE
            binding.appBarMain.toolbar12.title = "Family Care"

            val count = supportFragmentManager.backStackEntryCount
            if (count == 1) {
                if (doubleBackToExitPressedOnce) {
                    moveTaskToBack(true)
                    return
                }
                doubleBackToExitPressedOnce = true
                Snackbar.make(
                    binding.leftDrawer.LLHome,
                    "Please click BACK again to exit",
                    Snackbar.LENGTH_SHORT
                ).setBackgroundTint(color(R.color.theme_dark)).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            } else {
                supportFragmentManager.popBackStackImmediate()
            }
            hideKeyboard()
        } catch (ex: Exception) {
            Timber.e(ex, "onBackPressed error")
        }
    }
}