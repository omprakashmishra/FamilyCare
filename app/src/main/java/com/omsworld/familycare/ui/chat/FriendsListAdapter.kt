package com.omsworld.familycare.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.omsworld.familycare.R
import com.omsworld.familycare.core.DateTimeUtil
import com.omsworld.familycare.data.model.FriendListModel
import com.omsworld.familycare.databinding.ChatListUserItemBinding

class FriendsListAdapter(
    private val userId: String,
    private val onItemClick: (FriendListModel) -> Unit
) : ListAdapter<FriendListModel, FriendsListAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ChatListUserItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val b: ChatListUserItemBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: FriendListModel) = with(b) {
            tvTitle.text = item.freind_fullname
            tvShortDis.text = if (item.sender_id == userId) {
                "You: ${item.message}"
            } else {
                item.message
            }
            tvDateRow.text = DateTimeUtil.formatDateTime(item.time)

            IVFrindsImg.load(item.freind_img) {
                placeholder(R.drawable.ic_profile)
                error(R.drawable.ic_profile)
            }
            mprogressBar.visibility = View.GONE

            RLRow.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<FriendListModel>() {
            override fun areItemsTheSame(a: FriendListModel, b: FriendListModel) =
                a.freind_id == b.freind_id
            override fun areContentsTheSame(a: FriendListModel, b: FriendListModel) =
                a == b
        }
    }
}