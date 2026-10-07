package com.omsworld.familycare.ui.shopping.web

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.omsworld.familycare.R
import com.omsworld.familycare.data.remote.dto.SupabaseShoppingCategoryDto

class HeadCategoryAdapter(
    private val context: Context,
    private val categories: List<SupabaseShoppingCategoryDto>,
    private val layoutId: Int,
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<HeadCategoryAdapter.VH>() {

    private var selectedId: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(context).inflate(layoutId, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val category = categories[position]
        val id = category.id?.toString() ?: ""
        val name = category.name ?: ""

        holder.itemName.text = name
        holder.itemName.setBackgroundResource(R.drawable.border)
        holder.itemName.setTextColor(context.resources.getColor(R.color.black, null))

        // Auto-select first category on first render
        if (selectedId.isEmpty() || selectedId == id) {
            selectedId = id
            holder.itemName.setBackgroundResource(R.drawable.dark_blue_bg)
            holder.itemName.setTextColor(context.resources.getColor(R.color.white, null))
            onCategoryClick(id)
        }

        holder.rlRow.setOnClickListener {
            val oldSelected = selectedId
            selectedId = id
            notifyDataSetChanged()
            if (oldSelected != id) {
                onCategoryClick(id)
            }
        }
    }

    override fun getItemCount(): Int = categories.size

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.item_name)
        val rlRow: RelativeLayout = view.findViewById(R.id.RL_row)
    }
}