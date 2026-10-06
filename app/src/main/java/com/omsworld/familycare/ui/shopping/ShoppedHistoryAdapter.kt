package com.omsworld.familycare.ui.shopping

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.omsworld.familycare.core.DateTimeUtil
import com.omsworld.familycare.data.model.ShoppedHistoryModel
import com.omsworld.familycare.databinding.ShoppedHistoryItemBinding

class ShoppedHistoryAdapter :
    ListAdapter<ShoppedHistoryModel, ShoppedHistoryAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ShoppedHistoryItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val b: ShoppedHistoryItemBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: ShoppedHistoryModel) = with(b) {
            TVDatetime.text = DateTimeUtil.changeFormat(item.added_date)
            TVCount.text = item.item_count
            TVAddedby.text = item.added_by_name
            TVItem.text = item.item_name
            TVTotal.text = "Total Price ${item.price}"
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ShoppedHistoryModel>() {
            override fun areItemsTheSame(a: ShoppedHistoryModel, b: ShoppedHistoryModel) =
                a.added_date == b.added_date && a.added_by_name == b.added_by_name
            override fun areContentsTheSame(a: ShoppedHistoryModel, b: ShoppedHistoryModel) =
                a == b
        }
    }
}