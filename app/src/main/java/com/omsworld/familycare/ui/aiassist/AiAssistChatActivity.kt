package com.omsworld.familycare.ui.aiassist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.omsworld.familycare.R
import com.omsworld.familycare.base.BaseActivity
import com.omsworld.familycare.databinding.ActivityAiAssistChatBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class AiAssistChatActivity : BaseActivity<ActivityAiAssistChatBinding>() {

    private val vm: AiAssistChatViewModel by viewModels()
    private lateinit var chatAdapter: AiChatMessageAdapter

    // ============================================================
    // Permission launchers
    // ============================================================

    private val micPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Timber.d("Microphone permission granted")
            vm.onMicPermissionGranted()
        } else {
            Timber.w("Microphone permission denied")
            snack(binding.root, getString(R.string.mic_permission_required))
        }
    }

    private val speakerPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val audioGranted = grants[Manifest.permission.RECORD_AUDIO] == true
        val modifyGranted = grants[Manifest.permission.MODIFY_AUDIO_SETTINGS] == true

        if (audioGranted && modifyGranted) {
            Timber.d("Audio permissions granted")
            vm.onAudioPermissionsGranted()
        } else {
            Timber.w("Audio permissions denied")
            snack(binding.root, getString(R.string.audio_permissions_required))
        }
    }

    // ============================================================
    // Lifecycle
    // ============================================================

    override fun inflateBinding(inflater: LayoutInflater) =
        ActivityAiAssistChatBinding.inflate(inflater)

    override fun onBindingReady() {
        Timber.d("AiAssistChatActivity started")
        setupToolbar()
        setupRecyclerView()
        setupListeners()
        requestPermissions()
        observeState()
    }

    // ============================================================
    // Setup
    // ============================================================

    private fun setupToolbar() {
        binding.toolbar.title = getString(R.string.ai_assist_title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupRecyclerView() {
        chatAdapter = AiChatMessageAdapter()
        binding.rvChatMessages.apply {
            layoutManager = LinearLayoutManager(this@AiAssistChatActivity).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }

    private fun setupListeners() {
        binding.btnSendMessage.setOnClickListener { sendTextMessage() }
        binding.btnStartListening.setOnClickListener { startVoiceInput() }
        binding.btnStopListening.setOnClickListener { stopVoiceInput() }
        binding.btnPlayResponse.setOnClickListener { playAudioResponse() }
    }

    // ============================================================
    // Permission Handling
    // ============================================================

    private fun requestPermissions() {
        val micGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        val audioSettingsGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.MODIFY_AUDIO_SETTINGS
        ) == PackageManager.PERMISSION_GRANTED

        if (!micGranted) {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        if (!audioSettingsGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                speakerPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS
                    )
                )
            }
        }

        if (micGranted && audioSettingsGranted) {
            vm.onPermissionsReady()
        }
    }

    // ============================================================
    // User Actions
    // ============================================================

    private fun sendTextMessage() {
        val message = binding.etMessageInput.text.toString().trim()
        if (message.isBlank()) {
            snack(binding.root, getString(R.string.enter_message))
            return
        }

        vm.sendMessage(message)
        binding.etMessageInput.text.clear()
    }

    private fun startVoiceInput() {
        Timber.d("Starting voice input")
        vm.startListening()
        updateVoiceUiState(isListening = true)
    }

    private fun stopVoiceInput() {
        Timber.d("Stopping voice input")
        vm.stopListening()
        updateVoiceUiState(isListening = false)
    }

    private fun playAudioResponse() {
        Timber.d("Playing audio response")
        vm.playAudioResponse()
    }

    // ============================================================
    // State Observation
    // ============================================================

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.state.collect { state ->
                        when (state) {
                            is AiAssistUiState.Idle -> handleIdleState()
                            is AiAssistUiState.Loading -> handleLoadingState()
                            is AiAssistUiState.ChatUpdated -> handleChatUpdated(state)
                            is AiAssistUiState.Listening -> handleListeningState(state)
                            is AiAssistUiState.Error -> handleErrorState(state)
                        }
                    }
                }

                launch {
                    vm.events.collect { event ->
                        when (event) {
                            is AiAssistEvent.ShowMessage -> toast(event.message)
                            is AiAssistEvent.MessageReceived -> {
                                toast(getString(R.string.message_received))
                            }
                            is AiAssistEvent.VoiceInputError -> {
                                snack(binding.root, event.error)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleIdleState() {
        binding.pbLoading.visibility = View.GONE
        binding.btnSendMessage.isEnabled = true
        binding.btnStartListening.isEnabled = true
    }

    private fun handleLoadingState() {
        binding.pbLoading.visibility = View.VISIBLE
        binding.btnSendMessage.isEnabled = false
    }

    private fun handleChatUpdated(state: AiAssistUiState.ChatUpdated) {
        binding.pbLoading.visibility = View.GONE
        chatAdapter.submitList(state.messages)
        binding.rvChatMessages.scrollToPosition(state.messages.size - 1)
        binding.btnPlayResponse.isEnabled = state.hasAudio
    }

    private fun handleListeningState(state: AiAssistUiState.Listening) {
        binding.tvListeningStatus.text = if (state.isListening) {
            getString(R.string.listening_status)
        } else {
            getString(R.string.processing_status)
        }
        binding.tvListeningStatus.visibility = View.VISIBLE
    }

    private fun handleErrorState(state: AiAssistUiState.Error) {
        binding.pbLoading.visibility = View.GONE
        snack(binding.root, state.message)
        binding.btnSendMessage.isEnabled = true
        binding.btnStartListening.isEnabled = true
    }

    private fun updateVoiceUiState(isListening: Boolean) {
        binding.btnStartListening.isEnabled = !isListening
        binding.btnStopListening.isEnabled = isListening
        binding.tvListeningStatus.visibility = if (isListening) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.stopListening()
    }
}
