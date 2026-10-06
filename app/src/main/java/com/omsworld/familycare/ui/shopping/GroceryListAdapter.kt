package com.omsworld.familycare.ui.shopping

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.omsworld.familycare.R
import com.omsworld.familycare.data.model.AddedItemModel
import com.omsworld.familycare.databinding.GroceryListItemBinding

class GroceryListAdapter(
    private val userId: String,
    private val onDeleteClick: (AddedItemModel) -> Unit,
    private val onCheckedChange: () -> Unit
) : ListAdapter<AddedItemModel, GroceryListAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = GroceryListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val b: GroceryListItemBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: AddedItemModel) = with(b) {
            TVAddedBy.text = item.added_by_name
            TVItemName.text = item.note

            if (item.isItemShopped == "1") {
                RLRow.setBackgroundResource(R.color.transparent)
                CBIsShopped.isChecked = true
                CBIsShopped.isClickable = false
                TVItemName.paintFlags =
                    TVItemName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                CBIsShopped.setOnCheckedChangeListener { _, checked ->
                    if (checked) {
                        GroceryListFragment.ShopedItem.add(item.id)
                        IVItemDelete.visibility = View.GONE
                    } else {
                        IVItemDelete.visibility = View.VISIBLE
                        GroceryListFragment.ShopedItem.remove(item.id)
                    }
                    onCheckedChange()
                }
            }

            IVItemDelete.setOnClickListener { onDeleteClick(item) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<AddedItemModel>() {
            override fun areItemsTheSame(a: AddedItemModel, b: AddedItemModel) = a.id == b.id
            override fun areContentsTheSame(a: AddedItemModel, b: AddedItemModel) = a == b
        }
    }
}