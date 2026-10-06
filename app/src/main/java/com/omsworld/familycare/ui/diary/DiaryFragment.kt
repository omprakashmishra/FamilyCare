package com.omsworld.familycare.ui.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.data.model.DiaryModel
import com.omsworld.familycare.databinding.DiaryFrBinding
import com.omsworld.familycare.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DiaryFragment : BaseFragment<DiaryFrBinding>() {

    private val vm: DiaryViewModel by viewModels()
    private lateinit var adapter: DiaryAdapter

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        DiaryFrBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        Timber.d("DiaryFragment started")

        adapter = DiaryAdapter(
            onItemClick = { diary -> openEdit(diary) },
            onDeleteClick = { diary -> confirmDelete(diary) }
        )
        binding.RVDiary.layoutManager = LinearLayoutManager(requireContext())
        binding.RVDiary.adapter = adapter

        binding.IVAddNew.setOnClickListener { openNew() }

        observeState()
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is DiaryUiState.Loading -> binding.mprogressBar.visibility = View.VISIBLE
                is DiaryUiState.Success -> {
                    binding.mprogressBar.visibility = View.GONE
                    adapter.submitList(state.entries)
                }
                is DiaryUiState.Error -> {
                    binding.mprogressBar.visibility = View.GONE
                    snack(state.message)
                }
            }
        }
    }

    private fun openNew() {
        val fragment = EditDiaryFragment()
        (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
            ?.replace(com.omsworld.familycare.R.id.fragment_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun openEdit(diary: DiaryModel) {
        val bundle = Bundle().apply {
            putString("id", diary.id)
            putString("subject", diary.subject)
            putString("note", diary.note)
            putString("added_date", diary.added_date)
        }
        val fragment = EditDiaryFragment().apply { arguments = bundle }
        (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
            ?.replace(com.omsworld.familycare.R.id.fragment_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun confirmDelete(diary: DiaryModel) {
        AlertDialog.Builder(requireContext())
            .setMessage("Delete this note?")
            .setPositiveButton("Yes") { _, _ -> vm.deleteDiary(diary.id) }
            .setNegativeButton("No", null)
            .show()
    }
}