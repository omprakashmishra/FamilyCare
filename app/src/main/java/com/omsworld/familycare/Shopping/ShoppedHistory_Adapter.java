package com.omsworld.familycare.Shopping;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omsworld.familycare.Diary.EditDiary_fr;
import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.DateTime_c;
import com.omsworld.familycare.model.ShoppedHistory_Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omprakash.m on 2/23/2018.
 */

public class ShoppedHistory_Adapter extends RecyclerView.Adapter<ShoppedHistory_Adapter.MyViewHolder> implements View.OnClickListener {

    public ArrayList<ShoppedHistory_Model> arrayList = new ArrayList<>();
    public Context context;
    ShoppedHistory_Model model;
    CommonFunctions cmf;
    private LayoutInflater LIoffer;

    public ShoppedHistory_Adapter(Context context, ArrayList<ShoppedHistory_Model> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        LIoffer = LayoutInflater.from(context);
        cmf = new CommonFunctions(context);
    }


    @Override
    public ShoppedHistory_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LIoffer.inflate(R.layout.shopped_history_item, parent, false);
        // MyViewHolder holder = new MyViewHolder(view);
        return new ShoppedHistory_Adapter.MyViewHolder(view);
    }

    public void setFilter(List<ShoppedHistory_Model> countryModels) {
        arrayList = new ArrayList<>();
        arrayList.addAll(countryModels);
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(ShoppedHistory_Adapter.MyViewHolder holder, int position) {
        model = arrayList.get(position);
       String last_chatTime = DateTime_c.getInstance().changeFormate(arrayList.get(position).getAdded_date());
        holder.TV_Datetime.setText(last_chatTime);
        holder.TV_count.setText(arrayList.get(position).getItem_count());
        holder.TV_addedby.setText(arrayList.get(position).getAdded_by_name());
        holder.TV_item.setText(arrayList.get(position).getItem_name());
        holder.TV_total.setText("Total Price " + arrayList.get(position).getPrice());

        // change time format from server text...
        try {
           /* String dateTime = arrayList.get(position).getAdded_date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = df.parse(dateTime);
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            calendar.setTime(date);
            String Year = String.valueOf(calendar.get(Calendar.YEAR));
            String Day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            String Month = new SimpleDateFormat("MMM").format(calendar.getTime());
            holder.tv_month.setText(Month);
            holder.tv_year.setText(Year);
            holder.tv_day.setText(Day);*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //  holder.updateTime.setText(UpdateDateTime);

       /* model.getSender_id();
        model.getSender_name();
        model.getSender_image();
        model.getMessage();
        model.getMessage_id();
        model.getTime();*/

        // holder.RL_row.setOnClickListener(this);
        // holder.RL_row.setTag(position);
        // holder.IV_deleteDiary.setOnClickListener(this);
        // holder.IV_deleteDiary.setTag(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onClick(View view) {
        Integer position = (Integer) view.getTag();
        switch (view.getId()) {
            case R.id.RL_row:
                Fragment fragment = new EditDiary_fr();
                //cmf.bdl.putString("id", arrayList.get(position).getId());
                //cmf.bdl.putString("note", arrayList.get(position).getNote());
                // cmf.bdl.putString("subject", arrayList.get(position).getSubject());
                cmf.bdl.putString("added_date", arrayList.get(position).getAdded_date());
                fragment.setArguments(cmf.bdl);
                cmf.replaceFragment(context,fragment );
                break;
            case R.id.IV_deleteDiary:
                String USER_ID = cmf.myPreference.getString(context, cmf.gc.USER_ID);
                //delete_diaryPage(USER_ID, arrayList.get(position).getId(), position);
                break;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView TV_Datetime, TV_count, TV_addedby, TV_item, TV_total;

        public MyViewHolder(View itemView) {
            super(itemView);
            TV_Datetime = (TextView) itemView.findViewById(R.id.TV_Datetime);
            TV_count = (TextView) itemView.findViewById(R.id.TV_count);
            TV_addedby = (TextView) itemView.findViewById(R.id.TV_addedby);
            TV_item = (TextView) itemView.findViewById(R.id.TV_item);
            TV_total = (TextView) itemView.findViewById(R.id.TV_total);
        }
    }
}
