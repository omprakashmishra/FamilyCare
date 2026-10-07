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
import com.omsworld.familycare.data.model.ChatModel
import com.omsworld.familycare.databinding.CommentsListItemBinding
import timber.log.Timber

class ChatAdapter(
    private val myUserId: String,
    private val myImage: String
) : ListAdapter<ChatModel, ChatAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = CommentsListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val b: CommentsListItemBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: ChatModel) = with(b) {
            val time = DateTimeUtil.formatDateTime(item.time)

            Timber.d("ChatAdapter.bind: myUserId=$myUserId sender=${item.sender_id} myImage='$myImage' freindImg='${item.freind_img}'")

            if (myUserId == item.sender_id) {
                // ─── Sent (right) ───
                LLChatLeft.visibility = View.GONE
                LLChatRight.visibility = View.VISIBLE
                TVRightChat.text = item.message
                TVRightDatetime.text = time
            } else {
                // ─── Received (left) ───
                LLChatLeft.visibility = View.VISIBLE
                LLChatRight.visibility = View.GONE
                TVLeftChat.text = item.message
                TVLeftDatetime.text = time

                if (item.freind_img.isNotBlank()) {
                    IVLeftFrindsImg.load(item.freind_img) {
                        placeholder(R.drawable.ic_profile)
                        error(R.drawable.ic_profile)
                    }
                } else {
                    IVLeftFrindsImg.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ChatModel>() {
            override fun areItemsTheSame(a: ChatModel, b: ChatModel) =
                a.message_id == b.message_id && a.message == b.message
            override fun areContentsTheSame(a: ChatModel, b: ChatModel) = a == b
        }
    }
}