package com.omsworld.familycare.ui.family

import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.databinding.FamilyMemberFrBinding
import com.omsworld.familycare.databinding.DialogInvitationfriendBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class FamilyMembersFragment : BaseFragment<FamilyMemberFrBinding>() {

    private val vm: FamilyMembersViewModel by viewModels()
    private lateinit var adapter: MyFriendAdapterList

    private val contactPicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data ?: return@registerForActivityResult
        val uri: Uri = data.data ?: return@registerForActivityResult
        val cursor = requireContext().contentResolver.query(
            uri,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.Contacts.DISPLAY_NAME
            ),
            null, null, null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                val phone = it.getString(0)
                val name = it.getString(1)
                binding.TVPhone.setText(phone)
                binding.TVName.setText(name)
            }
        }
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FamilyMemberFrBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        Timber.d("FamilyMembersFragment started")

        adapter = MyFriendAdapterList(
            onDeleteClick = { member -> confirmRemove(member.user_mobile) },
            onCallClick = { member -> dialNumber(member.user_mobile) },
            onChatClick = { member -> /* open chat */ },
            onAcceptClick = { member ->
                if (member.request_type == "freind_request") {
                    vm.acceptJoinRequest(member)
                } else {
                    vm.loadFamily()
                }
            },
            onDeclineClick = { member ->
                if (member.request_type == "freind_request") {
                    vm.declineJoinRequest(member)
                } else {
                    vm.removeMember(member.user_mobile)
                }
            },
            onProfileClick = { member -> /* show profile dialog */ }
        )
        binding.RVFriendsList.layoutManager = LinearLayoutManager(requireContext())
        binding.RVFriendsList.adapter = adapter

        binding.swipeRefreshLayout.setOnRefreshListener { vm.loadFamily() }
        binding.IVPhonebook.setOnClickListener { pickContact() }
        binding.RLAddFamily.setOnClickListener { showAddDialog() }
        binding.IVEditGroup.setOnClickListener { showEditFamilyDialog() }

        observeState()
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            binding.swipeRefreshLayout.isRefreshing = false
            when (state) {
                is FamilyUiState.Loading -> {
                    binding.mprogressBar.visibility = View.VISIBLE
                }
                is FamilyUiState.Success -> {
                    binding.mprogressBar.visibility = View.GONE

                    // Show family name
                    binding.TVFamilyName.text = state.familyName.ifBlank { "No Family" }

                    // Show/hide bottom buttons based on admin status
                    binding.LLBottom.visibility = if (state.isAdmin) View.VISIBLE else View.GONE
                    binding.IVEditGroup.visibility = if (state.isAdmin) View.VISIBLE else View.GONE

                    // Submit members
                    adapter.submitList(state.members)

                    // If no family exists, prompt user to create one
                    if (state.familyId.isBlank()) {
                        binding.TVFamilyName.text = "Tap to create family →"
                        binding.IVEditGroup.visibility = View.VISIBLE
                    }
                }
                is FamilyUiState.Error -> {
                    binding.mprogressBar.visibility = View.GONE
                    snack(state.message)
                }
                else -> Unit
            }
        }
    }

    private fun pickContact() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI).apply {
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }
        contactPicker.launch(intent)
    }

    private fun showAddDialog() {
        val dialogBinding = DialogInvitationfriendBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.ok.setOnClickListener {
            val name = dialogBinding.TVName.text.toString()
            val phone = dialogBinding.TVPhone.text.toString()
            if (phone.length < 9) {
                toast("Enter a valid mobile number")
                return@setOnClickListener
            }
            vm.addMember(phone, name)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showEditFamilyDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Enter family name"
            setText(prefs.getString(requireContext(), Constants.FAMILY_NAME))
            setPadding(40, 30, 40, 30)
        }

        val currentFamily = prefs.getString(requireContext(), Constants.FAMILY_NAME)
        val title = if (currentFamily.isBlank()) "Create Family" else "Update Family Name"

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isBlank()) {
                    toast("Please enter a family name")
                    return@setPositiveButton
                }
                vm.createOrUpdateFamily(name)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmRemove(mobile: String) {
        AlertDialog.Builder(requireContext())
            .setMessage("Remove this member?")
            .setPositiveButton("Yes") { _, _ -> vm.removeMember(mobile) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun dialNumber(phone: String) {
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
    }
}