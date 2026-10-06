package com.omsworld.familycare.ui.contacts

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.omsworld.familycare.R
import com.omsworld.familycare.data.model.SearchModel
import com.omsworld.familycare.databinding.SearchItemBinding

class SearchAdapter(
    private val userImage: String,
    private val onEditClick: (SearchModel) -> Unit
) : ListAdapter<SearchModel, SearchAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = SearchItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val b: SearchItemBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: SearchModel) = with(b) {
            TVCatText.text = item.name
            TVComment.text = item.comment
            TVAddedDate.text = item.added_date
            TVPhone.text = item.phone
            TVAddedBy.text = item.added_by

            if (item.added_by == "me") {
                IVEdit.visibility = View.VISIBLE
                if (userImage.isNotBlank()) {
                    IVCatImg.load(userImage) {
                        placeholder(R.mipmap.ic_launcher)
                        error(R.mipmap.ic_launcher)
                    }
                }
            } else {
                IVEdit.visibility = View.GONE
            }

            IVMessage.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "for you my friend\n${item.name}\n${item.phone}"
                    )
                }
                b.root.context.startActivity(intent)
            }

            IVCall.setOnClickListener {
                b.root.context.startActivity(
                    Intent(Intent.ACTION_DIAL, Uri.parse("tel:${item.phone}"))
                )
            }

            IVEdit.setOnClickListener { onEditClick(item) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<SearchModel>() {
            override fun areItemsTheSame(a: SearchModel, b: SearchModel) = a.id == b.id
            override fun areContentsTheSame(a: SearchModel, b: SearchModel) = a == b
        }
    }
}