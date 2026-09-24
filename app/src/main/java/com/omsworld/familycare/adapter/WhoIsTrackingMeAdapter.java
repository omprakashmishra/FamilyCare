package com.omsworld.familycare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omsworld.familycare.R;
import com.omsworld.familycare.model.WhoIsTrackingMeModel;

import java.util.ArrayList;


/**
 * Created by omprakash.m on 8/10/2017.
 */

public class WhoIsTrackingMeAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<WhoIsTrackingMeModel> arrayList;


    public WhoIsTrackingMeAdapter(final Context context, ArrayList<WhoIsTrackingMeModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.who_is_helping_item, viewGroup, false);
            holder = new ViewHolder();
            holder.TV_Name = (TextView) view.findViewById(R.id.groupmembername);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        WhoIsTrackingMeModel guard_list_model = arrayList.get(position);
        holder.TV_Name.setText(arrayList.get(position).getName());

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    public static class ViewHolder {
        TextView TV_Name, TV_Phone, TV_datetime, TV_status;
    }

}
