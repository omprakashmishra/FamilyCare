package com.omsworld.familycare.ui.diary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.omsworld.familycare.data.model.DiaryModel
import com.omsworld.familycare.databinding.DiaryItemBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class DiaryAdapter(
    private val onItemClick: (DiaryModel) -> Unit,
    private val onDeleteClick: (DiaryModel) -> Unit
) : ListAdapter<DiaryModel, DiaryAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = DiaryItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val b: DiaryItemBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: DiaryModel) = with(b) {
            tvTitle.text = item.subject
            tvShortDis.text = item.note
            dateRow.text = item.added_date

            try {
                val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = df.parse(item.added_date)
                if (date != null) {
                    val cal = Calendar.getInstance(TimeZone.getDefault())
                    cal.time = date
                    tvMonth.text = SimpleDateFormat("MMM", Locale.getDefault()).format(cal.time)
                    tvDay.text = cal.get(Calendar.DAY_OF_MONTH).toString()
                    tvYear.text = cal.get(Calendar.YEAR).toString()
                }
            } catch (_: Exception) {}

            RLRow.setOnClickListener { onItemClick(item) }
            IVDeleteDiary.setOnClickListener { onDeleteClick(item) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<DiaryModel>() {
            override fun areItemsTheSame(a: DiaryModel, b: DiaryModel) = a.id == b.id
            override fun areContentsTheSame(a: DiaryModel, b: DiaryModel) = a == b
        }
    }
}