package com.omsworld.familycare.ui.family

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.omsworld.familycare.data.model.JoinSafeJoinModel
import com.omsworld.familycare.databinding.SentfriendListBinding

class SentFriendAdapterList(
    private val onRetryClick: (JoinSafeJoinModel) -> Unit,
    private val onCallClick: (JoinSafeJoinModel) -> Unit,
    private val onDeleteClick: (JoinSafeJoinModel) -> Unit
) : ListAdapter<JoinSafeJoinModel, SentFriendAdapterList.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = SentfriendListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val b: SentfriendListBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: JoinSafeJoinModel) = with(b) {
            TVName.text = item.NearAndDearName
            TVPhone.text = item.NearAndDearMobileNumber
            TVUserStatus.text = item.UserStatus

            IVRetry.setOnClickListener { onRetryClick(item) }
            IVPhonebookImage.setOnClickListener { onCallClick(item) }
            IVDelet.setOnClickListener { onDeleteClick(item) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<JoinSafeJoinModel>() {
            override fun areItemsTheSame(a: JoinSafeJoinModel, b: JoinSafeJoinModel) =
                a.InvitationID == b.InvitationID
            override fun areContentsTheSame(a: JoinSafeJoinModel, b: JoinSafeJoinModel) =
                a == b
        }
    }
}