package com.omsworld.familycare.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.databinding.SettingsLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SettingsFragment : BaseFragment<SettingsLayoutBinding>() {

    private val vm: SettingsViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        SettingsLayoutBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        Timber.d("SettingsFragment started")

        binding.OnOff.setOnCheckedChangeListener { _, isChecked ->
            vm.setAlarmMuted(isChecked)
        }

        binding.MyTrackingOnOff.setOnCheckedChangeListener { _, isChecked ->
            vm.setFriendsTracking(isChecked)
        }

        observeState()
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is SettingsUiState.Idle -> Unit
                is SettingsUiState.Loaded -> {
                    binding.OnOff.isChecked = state.alarmMuted
                    binding.MyTrackingOnOff.isChecked = state.friendsTracking
                }
            }
        }
    }
}