package com.omsworld.familycare.ui.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.databinding.EditDiaryFrBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class EditDiaryFragment : BaseFragment<EditDiaryFrBinding>() {

    private val vm: EditDiaryViewModel by viewModels()
    private var noteId: String = ""

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        EditDiaryFrBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        Timber.d("EditDiaryFragment started")

        arguments?.let {
            noteId = it.getString("id", "")
            binding.title.setText(it.getString("subject", ""))
            binding.body.setText(it.getString("note", ""))
            binding.notelistDate.text = it.getString("added_date", "")
        } ?: run {
            binding.notelistDate.text = SimpleDateFormat(
                "MMM dd,yyyy", Locale.getDefault()
            ).format(Date())
        }

        binding.IVSave.setOnClickListener {
            val subject = binding.title.text.toString().trim()
            val note = binding.body.text.toString().trim()
            if (subject.isBlank() || note.isBlank()) {
                snack("Please fill both title and note")
                return@setOnClickListener
            }
            vm.save(noteId, subject, note)
        }

        binding.IVDelete.setOnClickListener {
            if (noteId.isNotBlank()) vm.delete(noteId)
        }

        observeState()
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is EditDiaryUiState.Idle -> Unit
                is EditDiaryUiState.Saving -> Unit
                is EditDiaryUiState.Saved -> {
                    toast("Saved")
                    parentFragmentManager.popBackStack()
                }
                is EditDiaryUiState.Error -> snack(state.message)
            }
        }
    }
}