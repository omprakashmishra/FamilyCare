package com.omsworld.familycare.Shopping.InWeb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.Controller;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by omprakash.m on 3/26/2018.
 */

public class HeadCategoryAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    JSONArray jsonArray;
    public Context context;
    Bundle bundle;
    Intent intent;
    int layoutId;
    Controller controller;
    String selected_id="";

    public HeadCategoryAdapter(Context context, JSONArray jsonArray, int layoutId, Controller controller) {
        this.jsonArray = jsonArray;
        this.context = context;
        this.layoutId = layoutId;
        this.controller = controller;

        Log.d("---->LENTH--", String.valueOf(jsonArray.length()));
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new StopItemVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder_, final int position) {
        final StopItemVH holder = (StopItemVH) holder_;
        try {
            holder.item_name.setText(getJsonObject(position).optString("name"));

            holder.item_name.setBackground(context.getResources().getDrawable(R.drawable.border));
            holder.item_name.setTextColor(context.getResources().getColor(R.color.black));
            if(selected_id.equals(getJsonObject(position).optString("id")) || selected_id.equals("")){
                selected_id=getJsonObject(position).optString("id");
                holder.item_name.setBackground(context.getResources().getDrawable(R.drawable.dark_blue_bg));
                holder.item_name.setTextColor(context.getResources().getColor(R.color.white));
                controller.callback(getJsonObject(position).optString("id"));
            }
            holder.RL_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    selected_id=getJsonObject(position).optString("id");
                    notifyDataSetChanged();
                }
            });


        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    private JSONObject getJsonObject(int position) {
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonArray.optJSONObject(position);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return jsonObject;
        }
    }

    private void bindClick(View view, int postion) {
        view.setTag(postion);
        view.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {

        return jsonArray.length();
    }

    @Override
    public void onClick(View v) {
        int selectedPo = (int) v.getTag();
        int v_Id = v.getId();
    }

    private class StopItemVH extends RecyclerView.ViewHolder {
        public TextView item_name;
        RelativeLayout RL_row;


        public StopItemVH(View view) {
            super(view);
            item_name = (TextView) view.findViewById(R.id.item_name);
            RL_row = (RelativeLayout) view.findViewById(R.id.RL_row);
        }
    }
}
