package com.omsworld.familycare.ui.contacts

import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.data.model.SearchModel
import com.omsworld.familycare.databinding.SharedContactsFrBinding
import com.omsworld.familycare.databinding.DialogInvitationfriendBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SharedContactsFragment : BaseFragment<SharedContactsFrBinding>() {

    private val vm: SharedContactsViewModel by viewModels()
    private lateinit var adapter: SearchAdapter
    private var allContacts = listOf<SearchModel>()

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
                showAddDialog(name, phone)
            }
        }
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        SharedContactsFrBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        Timber.d("SharedContactsFragment started")

        val userImage = prefs.getString(requireContext(), Constants.USER_IMAGE, "0")
        adapter = SearchAdapter(userImage) { contact -> showEditDialog(contact) }
        binding.RVVendors.layoutManager = LinearLayoutManager(requireContext())
        binding.RVVendors.adapter = adapter

        binding.IVAddContact.setOnClickListener { pickContact() }
        binding.IVShrt.setOnClickListener { /* sort */ }
        binding.IVShareMessage.setOnClickListener {
            val fragment = com.omsworld.familycare.ui.chat.FriendsListFragment()
            (activity as? com.omsworld.familycare.ui.main.MainActivity)
                ?.supportFragmentManager?.beginTransaction()
                ?.replace(com.omsworld.familycare.R.id.fragment_container, fragment)
                ?.addToBackStack(null)?.commit()
        }

        binding.TVSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {
                filter(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        observeState()
    }

    private fun filter(query: String) {
        if (query.isBlank()) {
            adapter.submitList(allContacts)
            return
        }
        val q = query.lowercase()
        adapter.submitList(allContacts.filter { it.name.lowercase().contains(q) })
    }

    private fun pickContact() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI).apply {
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }
        contactPicker.launch(intent)
    }

    private fun showAddDialog(name: String = "", phone: String = "") {
        val db = DialogInvitationfriendBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(db.root).create()

        db.TVName.setText(name)
        db.TVPhone.setText(phone)
        db.ok.text = "Add Contact"

        db.ok.setOnClickListener {
            vm.addContact(
                db.TVName.text.toString(),
                db.TVPhone.text.toString(),
                db.TVEmail.text.toString()
            )
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showEditDialog(contact: SearchModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Edit Contact")
            .setMessage("${contact.name}\n${contact.phone}")
            .setPositiveButton("Delete") { _, _ -> vm.deleteContact(contact.phone) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is SharedContactsUiState.Loading -> Unit
                is SharedContactsUiState.Success -> {
                    allContacts = state.contacts
                    adapter.submitList(allContacts)
                }
                is SharedContactsUiState.Error -> snack(state.message)
            }
        }
    }
}