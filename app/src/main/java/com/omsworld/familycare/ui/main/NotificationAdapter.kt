package com.omsworld.familycare.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.omsworld.familycare.databinding.ChatListUserItemBinding

/**
 * Simple notification list adapter.
 * Replace ChatListUserItemBinding with your notification item layout
 * when you have one.
 */
class NotificationAdapter(
    private val onItemClick: (NotificationItem) -> Unit
) : ListAdapter<NotificationItem, NotificationAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ChatListUserItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val b: ChatListUserItemBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: NotificationItem) = with(b) {
            tvTitle.text = item.title
            tvShortDis.text = item.message
            tvDateRow.text = item.time
            RLRow.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<NotificationItem>() {
            override fun areItemsTheSame(a: NotificationItem, b: NotificationItem) =
                a.id == b.id
            override fun areContentsTheSame(a: NotificationItem, b: NotificationItem) =
                a == b
        }
    }
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: String = "default"
)