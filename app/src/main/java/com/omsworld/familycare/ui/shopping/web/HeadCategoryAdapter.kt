package com.omsworld.familycare.ui.shopping.web

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.omsworld.familycare.R
import com.omsworld.familycare.data.remote.dto.*
import org.json.JSONArray
import org.json.JSONObject

class HeadCategoryAdapter(
    private val context: android.content.Context,
    private val jsonArray: JSONArray,
    private val layoutId: Int,
    private val controller: (String) -> Unit
) : RecyclerView.Adapter<HeadCategoryAdapter.VH>() {

    private var selectedId: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(context).inflate(layoutId, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val obj = jsonArray.optJSONObject(position) ?: return
        val id = obj.optString("id")
        val name = obj.optString("name")

        holder.itemName.text = name
        holder.itemName.setBackgroundResource(R.drawable.border)
        holder.itemName.setTextColor(context.resources.getColor(R.color.black, null))

        if (selectedId.isEmpty() || selectedId == id) {
            selectedId = id
            holder.itemName.setBackgroundResource(R.drawable.dark_blue_bg)
            holder.itemName.setTextColor(context.resources.getColor(R.color.white, null))
            controller(id)
        }

        holder.rlRow.setOnClickListener {
            selectedId = id
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = jsonArray.length()

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.item_name)
        val rlRow: RelativeLayout = view.findViewById(R.id.RL_row)
    }
}