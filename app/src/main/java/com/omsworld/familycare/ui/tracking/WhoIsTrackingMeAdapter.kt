package com.omsworld.familycare.ui.tracking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.omsworld.familycare.R
import com.omsworld.familycare.data.model.WhoIsTrackingMeModel

class WhoIsTrackingMeAdapter(
    private val context: Context,
    private val arrayList: ArrayList<WhoIsTrackingMeModel>
) : BaseAdapter(), View.OnClickListener {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = arrayList.size
    override fun getItem(position: Int): Any = arrayList[position]
    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder
        if (view == null) {
            view = inflater.inflate(R.layout.who_is_helping_item, viewGroup, false)
            holder = ViewHolder()
            holder.TV_Name = view.findViewById(R.id.groupmembername)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }
        holder.TV_Name?.text = arrayList[position].name
        return view
    }

    override fun onClick(v: View) {}

    class ViewHolder {
        var TV_Name: TextView? = null
    }
}