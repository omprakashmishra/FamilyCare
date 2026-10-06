package com.omsworld.familycare.ui.shopping

import android.content.Intent
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.tabs.TabLayout
import com.omsworld.familycare.base.BaseActivity
import com.omsworld.familycare.databinding.ShoppingAcBinding
import com.omsworld.familycare.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShoppingMainActivity : BaseActivity<ShoppingAcBinding>() {

    private val tab1 = GroceryListFragment()
    private val tab2 = ShoppedHistoryFragment()

    override fun inflateBinding(inflater: LayoutInflater) =
        ShoppingAcBinding.inflate(inflater)

    override fun onBindingReady() {
        // The shopping_ac.xml is just a FrameLayout (fragment_container).
        // So instead of tabs, we swap fragments here directly.
        showFragment(tab1)
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}