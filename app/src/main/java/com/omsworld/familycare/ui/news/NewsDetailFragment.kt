package com.omsworld.familycare.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.omsworld.familycare.R
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.core.DateTimeUtil
import com.omsworld.familycare.data.model.NewsEventsModel
import com.omsworld.familycare.databinding.NewsDetailFrBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsDetailFragment : BaseFragment<NewsDetailFrBinding>() {

    private var model: NewsEventsModel = NewsEventsModel()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        NewsDetailFrBinding.inflate(inflater, container, false)

    @Suppress("DEPRECATION")
    override fun onBindingReady() {
        arguments?.let {
            model = it.getSerializable("newsEventsModel") as? NewsEventsModel
                ?: NewsEventsModel()
        }

        // Bind through the included layout's ID
        val item = binding.newsEventsItem

        item.TVNewsHeadLine.text = model.title
        item.TVNews.text = model.discription
        item.tvPostedTime.text = DateTimeUtil.changeFormat(model.added_date)
        item.TVLikecount.text = "${model.like_count} Likes"

        item.IVNewsimage.visibility = View.GONE
        if (model.news_type == "image" && model.image.isNotBlank()) {
            item.IVNewsimage.load(model.image) {
                placeholder(R.drawable.placeholder_image)
                error(R.drawable.placeholder_image)
            }
            item.IVNewsimage.visibility = View.VISIBLE
        }

        item.likeimage.setImageResource(
            if (model.like == "1") R.drawable.liked_ic else R.drawable.icon_like
        )
    }
}