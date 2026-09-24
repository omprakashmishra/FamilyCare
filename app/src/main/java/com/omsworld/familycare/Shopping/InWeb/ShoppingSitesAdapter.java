package com.omsworld.familycare.Shopping.InWeb;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.omsworld.familycare.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ShoppingSitesAdapter extends ArrayAdapter<ShoppingModel> {
    Context context;
    int layoutResourceId;
    ArrayList<ShoppingModel> data = new ArrayList<ShoppingModel>();

    public ShoppingSitesAdapter(Context context, int layoutResourceId,
                                ArrayList<ShoppingModel> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.item_text);
            holder.imageItem = (CircleImageView) row.findViewById(R.id.item_image);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        ShoppingModel item = data.get(position);
        holder.txtTitle.setText(item.getTitle());
        try {
            Picasso.with(context).load(item.getImage()).placeholder(R.drawable.shopping_ic).resize(120,120)
                    .error(R.drawable.shopping_ic)
                    .into(holder.imageItem);
        } catch (Exception ex) {
        }
        return row;

    }

    static class RecordHolder {
        TextView txtTitle;
        CircleImageView imageItem;

    }
}
