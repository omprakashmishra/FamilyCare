package com.omsworld.familycare.ui.tracking

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.omsworld.familycare.R
import com.omsworld.familycare.core.DateTimeUtil
import com.omsworld.familycare.databinding.LayoutTrackingListItemBinding

class FriendsTrackingAdapter(
    private val onCallClick: (TrackingItem) -> Unit,
    private val onNavigateClick: (TrackingItem) -> Unit
) : ListAdapter<TrackingItem, FriendsTrackingAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = LayoutTrackingListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val b: LayoutTrackingListItemBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: TrackingItem) = with(b) {
            groupmembername.text = item.name
            BTFirstletter.text = item.name
            batterypercentage.text = "${item.battery}%"
            tvAddress.text = item.address
            TVTime.text = DateTimeUtil.formatDateTime(item.lastSeen)

            if (item.onlineStatus == "Offline") {
                online.text = "Offline"
                online.setTextColor(Color.RED)
                LLTrackinglayout.setBackgroundColor(Color.parseColor("#80B6B6B4"))
                IVOnOffLine.setImageResource(R.drawable.offline_ic)
            } else {
                online.text = "Online"
                online.setTextColor(Color.GREEN)
                IVOnOffLine.setImageResource(R.drawable.online_ic)
            }

            if (item.imageUrl.isNotBlank()) {
                IVFrindsImg.load(item.imageUrl) {
                    placeholder(R.drawable.ic_profile)
                    error(R.drawable.ic_profile)
                }
            }
            mprogressBar.visibility = android.view.View.GONE

            track.setOnClickListener { onCallClick(item) }
            IVNavigation.setOnClickListener { onNavigateClick(item) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<TrackingItem>() {
            override fun areItemsTheSame(a: TrackingItem, b: TrackingItem) = a.id == b.id
            override fun areContentsTheSame(a: TrackingItem, b: TrackingItem) = a == b
        }
    }
}