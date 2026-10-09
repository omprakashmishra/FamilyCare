package com.omsworld.familycare.ui.shopping.web

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.omsworld.familycare.R
import com.omsworld.familycare.data.model.ShoppingModel
import com.omsworld.familycare.databinding.ShoppingSitesRowBinding

class ShoppingSitesAdapter(
    private val items: MutableList<ShoppingModel> = mutableListOf(),
    private val onItemClick: (ShoppingModel) -> Unit
) : RecyclerView.Adapter<ShoppingSitesAdapter.SiteViewHolder>() {

    fun submitList(newItems: List<ShoppingModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiteViewHolder {
        val binding = ShoppingSitesRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SiteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SiteViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class SiteViewHolder(
        private val binding: ShoppingSitesRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ShoppingModel) {
            binding.itemText.text = item.title
            binding.itemImage.load(item.image) {
                placeholder(R.drawable.shopping_ic)
                error(R.drawable.shopping_ic)
            }
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }
}