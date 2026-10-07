package com.omsworld.familycare.ui.family

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.omsworld.familycare.R
import com.omsworld.familycare.data.model.JoinSafeJoinModel
import com.omsworld.familycare.databinding.MyfriendsItemlistviewBinding

class MyFriendAdapterList(
    private val onDeleteClick: (JoinSafeJoinModel) -> Unit,
    private val onCallClick: (JoinSafeJoinModel) -> Unit,
    private val onChatClick: (JoinSafeJoinModel) -> Unit,
    private val onAcceptClick: (JoinSafeJoinModel) -> Unit,
    private val onDeclineClick: (JoinSafeJoinModel) -> Unit,
    private val onProfileClick: (JoinSafeJoinModel) -> Unit
) : ListAdapter<JoinSafeJoinModel, MyFriendAdapterList.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = MyfriendsItemlistviewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val b: MyfriendsItemlistviewBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: JoinSafeJoinModel) = with(b) {
            RLFriends.visibility = View.GONE
            RLAccept.visibility = View.GONE

            when (item.request_type) {
                "freind_request" -> {
                    // Pending join request
                    RLAccept.visibility = View.VISIBLE
                    TVFamilyName.text = "Family: ${item.family_name}"
                    Nameid.text = "Owner: ${item.user_name}"
                    TVPhoneRequest.text = item.user_mobile

                    BTAccept.setOnClickListener { onAcceptClick(item) }
                    BnDeclineR.setOnClickListener { onDeclineClick(item) }
                }
                else -> {
                    // Regular member
                    RLFriends.visibility = View.VISIBLE
                    TVName.text = item.user_name
                    TVLocationStatus.text = item.address
                    TVDatetime.text = item.time
                    TVPhone.text = if (item.member_status == "Owner")
                        "Family Admin ${item.user_mobile}"
                    else item.user_mobile

                    CIVProfileImage.load(item.user_image) {
                        placeholder(R.drawable.user_ic)
                        error(R.drawable.user_ic)
                    }
                    mprogressBar.visibility = View.GONE

                    IVDeclineExit.setOnClickListener { onDeleteClick(item) }
                    IVPhonebookImage.setOnClickListener { onCallClick(item) }
                    RLChatNotif.setOnClickListener { onChatClick(item) }
                    RLFriends.setOnClickListener { onProfileClick(item) }
                }
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<JoinSafeJoinModel>() {
            override fun areItemsTheSame(a: JoinSafeJoinModel, b: JoinSafeJoinModel) =
                a.user_id == b.user_id
            override fun areContentsTheSame(a: JoinSafeJoinModel, b: JoinSafeJoinModel) =
                a == b
        }
    }
}