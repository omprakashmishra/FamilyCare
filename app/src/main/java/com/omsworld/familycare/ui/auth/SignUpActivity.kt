package com.omsworld.familycare.ui.auth
import android.content.Intent
import android.util.Patterns
import android.view.LayoutInflater
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
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import timber.log.Timber

@AndroidEntryPoint
class SignUpActivity : BaseActivity<ActivitySignUpBinding>() {

    private val vm: SignUpViewModel by viewModels()
    private lateinit var credentialManager: CredentialManager

    override fun inflateBinding(inflater: LayoutInflater) =
        ActivitySignUpBinding.inflate(inflater)

    override fun onBindingReady() {
        credentialManager = CredentialManager.create(this)

        binding.edtUsername.requestFocus()
//        binding.CKInvitee.setOnClickListener {
//            binding.LLInvitationCode.visibility =
//                if (binding.CKInvitee.isChecked) View.VISIBLE else View.GONE
//        }

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
        binding.btnGoogleSignIn.setOnClickListener { launchGoogleSignIn() }

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

    private fun launchGoogleSignIn() {
        val webClientId = getString(R.string.default_web_client_id)
        if (webClientId.isBlank()) {
            snack(binding.root, "Google Sign-In is not configured.")
            return
        }

        // Key: use GetGoogleIdOption to request an ID Token
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)   // false = show all accounts, suitable for new users
            .setAutoSelectEnabled(false)
            .setServerClientId(webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        binding.btnGoogleSignIn.isEnabled = false

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = this@SignUpActivity,
                    request = request
                )
                handleGoogleCredential(result.credential)
            } catch (e: GetCredentialCancellationException) {
                Timber.i("Google sign-in cancelled by user")
            } catch (e: GetCredentialException) {
                Timber.e(e, "Google sign-in failed")
                snack(binding.root, "Google sign-in failed. Try again.")
            } finally {
                binding.btnGoogleSignIn.isEnabled = true
            }
        }
    }
    private fun handleGoogleCredential(credential: androidx.credentials.Credential) {
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                val google = GoogleIdTokenCredential.createFrom(credential.data)
                // Pass the ID Token to the ViewModel (your existing method)
                vm.signInWithGoogle(
                    idToken = google.idToken,
                    email = google.id,
                    name = google.displayName.orEmpty()
                )
            } catch (e: GoogleIdTokenParsingException) {
                Timber.e(e, "Failed to parse Google credential")
                snack(binding.root, "Google sign-in failed")
            }
        } else {
            Timber.w("Unexpected credential type: ${credential.type}")
        }
    }
    //--------
}