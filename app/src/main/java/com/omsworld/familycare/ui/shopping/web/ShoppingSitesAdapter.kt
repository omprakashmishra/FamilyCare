package com.omsworld.familycare.ui.shopping.web

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import coil.load
import com.omsworld.familycare.R
import com.omsworld.familycare.data.model.ShoppingModel
import de.hdodenhof.circleimageview.CircleImageView

class ShoppingSitesAdapter(
    private val context: Context,
    private val layoutResourceId: Int,
    private val data: ArrayList<ShoppingModel>
) : ArrayAdapter<ShoppingModel>(context, layoutResourceId, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        val holder: RecordHolder

        if (row == null) {
            val inflater = (context as Activity).layoutInflater
            row = inflater.inflate(layoutResourceId, parent, false)
            holder = RecordHolder()
            holder.txtTitle = row.findViewById(R.id.item_text)
            holder.imageItem = row.findViewById(R.id.item_image)
            row.tag = holder
        } else {
            holder = row.tag as RecordHolder
        }

        val item = data[position]
        holder.txtTitle?.text = item.title
        holder.imageItem?.load(item.image) {
            placeholder(R.drawable.shopping_ic)
            error(R.drawable.shopping_ic)
        }
        return row
    }

    class RecordHolder {
        var txtTitle: TextView? = null
        var imageItem: CircleImageView? = null
    }
}