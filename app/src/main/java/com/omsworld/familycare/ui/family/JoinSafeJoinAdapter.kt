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
import com.omsworld.familycare.databinding.JoinsafezoneitemeListviewBinding

class JoinSafeJoinAdapter(
    private val onAcceptClick: (JoinSafeJoinModel) -> Unit,
    private val onDeclineClick: (JoinSafeJoinModel) -> Unit
) : ListAdapter<JoinSafeJoinModel, JoinSafeJoinAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = JoinsafezoneitemeListviewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val b: JoinsafezoneitemeListviewBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: JoinSafeJoinModel) = with(b) {
            tvName.text = item.user_name
            tvPhone.text = item.user_mobile

            IVUserDp.load(item.user_image) {
                placeholder(R.drawable.invitation_ic)
                error(R.drawable.about_us_ic)
            }

            okButton.setOnClickListener { onAcceptClick(item) }
            BnDeclineR.setOnClickListener { onDeclineClick(item) }
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