package com.omsworld.familycare.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.omsworld.familycare.R
import com.omsworld.familycare.core.DateTimeUtil
import com.omsworld.familycare.data.model.NewsEventsModel
import com.omsworld.familycare.databinding.NewsEventsItemBinding

class NewsEventsAdapter(
    private val onLikeClick: (NewsEventsModel) -> Unit,
    private val onItemClick: (NewsEventsModel) -> Unit
) : ListAdapter<NewsEventsModel, NewsEventsAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = NewsEventsItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val b: NewsEventsItemBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: NewsEventsModel) = with(b) {
            TVNewsHeadLine.text = item.title
            TVNews.text = item.discription
            tvPostedTime.text = DateTimeUtil.changeFormat(item.added_date)
            TVLikecount.text = "${item.like_count} Likes"

            RLLayoutTop3.visibility = if (item.discription.length > 150) View.VISIBLE else View.GONE
            IVNewsimage.visibility = View.GONE

            if (item.news_type == "image" && item.image.isNotBlank()) {
                IVNewsimage.load(item.image) {
                    placeholder(R.drawable.placeholder_image)
                    error(R.drawable.placeholder_image)
                }
                IVNewsimage.visibility = View.VISIBLE
            }

            if (item.like == "1") {
                likeimage.setImageResource(R.drawable.liked_ic)
            } else {
                likeimage.setImageResource(R.drawable.icon_like)
            }

            LLLayoutlikes.setOnClickListener { onLikeClick(item) }
            RLLayoutTop3.setOnClickListener { onItemClick(item) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<NewsEventsModel>() {
            override fun areItemsTheSame(a: NewsEventsModel, b: NewsEventsModel) = a.id == b.id
            override fun areContentsTheSame(a: NewsEventsModel, b: NewsEventsModel) = a == b
        }
    }
}