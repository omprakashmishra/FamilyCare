package com.omsworld.familycare.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import coil.load
import com.omsworld.familycare.R
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.databinding.ProfileFrBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class ProfileFragment : BaseFragment<ProfileFrBinding>() {

    private val vm: ProfileViewModel by viewModels()

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@registerForActivityResult
            uploadImage(uri)
        }
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        ProfileFrBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        Timber.d("ProfileFragment started")

        // Load current values
        binding.ETName.setText(prefs.getString(requireContext(), Constants.USER_NAME, "0"))
        binding.ETPhone.setText(prefs.getString(requireContext(), Constants.MOBILE_only, "0"))
        binding.ETMail.setText(prefs.getString(requireContext(), Constants.EMAIL, "0"))
        binding.tvDob.text = prefs.getString(requireContext(), Constants.USER_DOB, "0")
        binding.ETAbout.setText(prefs.getString(requireContext(), Constants.USER_ABOUT, "0"))

        val userImage = prefs.getString(requireContext(), Constants.USER_IMAGE, "0")
        if (userImage.isNotBlank()) {
            binding.IVProfileImg.load(userImage) {
                placeholder(R.drawable.ic_profile)
                error(R.drawable.ic_profile)
            }
        }
        binding.mprogressBar.visibility = View.GONE

        binding.ivUpload.setOnClickListener { pickImage() }
        binding.IVChangePass.setOnClickListener { showChangePasswordDialog() }

        binding.BTUpdate.setOnClickListener {
            vm.updateProfile(
                binding.ETName.text.toString(),
                binding.ETMail.text.toString(),
                binding.tvDob.text.toString(),
                binding.ETAbout.text.toString()
            )
        }

        observeState()
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePicker.launch(intent)
    }

    private fun uploadImage(uri: Uri) {
        try {
            val stream = requireContext().contentResolver.openInputStream(uri) ?: return
            val bitmap = BitmapFactory.decodeStream(stream)
            val base64 = encodeImage(bitmap)
            vm.uploadImage(base64)
        } catch (e: Exception) {
            snack("Image upload failed")
        }
    }

    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
    }

    private fun showChangePasswordDialog() {
        val dialogBinding = com.omsworld.familycare.databinding.ResetPasswordDialogBinding
            .inflate(layoutInflater)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root).create()

        dialogBinding.send.setOnClickListener {
            val old = dialogBinding.edtOldpass.text.toString()
            val new = dialogBinding.edtPass.text.toString()
            val confirm = dialogBinding.edtConPass.text.toString()
            if (old.isBlank() || new.isBlank() || confirm.isBlank()) {
                toast("Fill all fields")
                return@setOnClickListener
            }
            if (new != confirm) {
                toast("Passwords do not match")
                return@setOnClickListener
            }
            vm.changePassword(old, new, confirm)
            dialog.dismiss()
        }
        dialogBinding.cancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is ProfileUiState.Idle -> Unit
                is ProfileUiState.Saving -> {
                    binding.mprogressBar.visibility = View.VISIBLE
                }
                is ProfileUiState.Saved -> {
                    binding.mprogressBar.visibility = View.GONE
                    toast("Updated")
                }
                is ProfileUiState.Error -> {
                    binding.mprogressBar.visibility = View.GONE
                    snack(state.message)
                }
                else -> Unit
            }
        }
    }
}