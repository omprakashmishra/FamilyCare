package com.omsworld.familycare.Diary;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.MyServiceListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by omprakash.m on 1/22/2018.
 */

public class Diary_Fr_Adapter extends RecyclerView.Adapter<Diary_Fr_Adapter.MyViewHolder> implements View.OnClickListener {

    public ArrayList<DiaryModel> arrayList=new ArrayList<>();
    public Context context;
    public int index = -1;
    DiaryModel model;
    String UpdateDateTime;
    CommonFunctions cmf;
    private LayoutInflater LIoffer;

    public Diary_Fr_Adapter(Context context, ArrayList<DiaryModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        LIoffer = LayoutInflater.from(context);
        cmf = new CommonFunctions(context);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LIoffer.inflate(R.layout.diary_item, parent, false);
        // MyViewHolder holder = new MyViewHolder(view);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //  model = arrayList.get(position);

        //holder.TV_rightChat.setText(arrayList.get(position).getMessage());
        holder.tv_title.setText(arrayList.get(position).getSubject());
        holder.tv_short_dis.setText(arrayList.get(position).getNote());
        holder.date_row.setText(arrayList.get(position).getAdded_date());
        holder.tv_title.setText(arrayList.get(position).getSubject());

        // change time format from server text...
        try {
            String dateTime = arrayList.get(position).getAdded_date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = df.parse(dateTime);
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            calendar.setTime(date);
            String Year=String.valueOf(calendar.get(Calendar.YEAR));
            String Day=String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            String Month=new SimpleDateFormat("MMM").format(calendar.getTime());
            holder.tv_month.setText(Month);
            holder.tv_year.setText( Year);
            holder.tv_day.setText(Day);

        } catch (Exception ex){
            ex.printStackTrace();
        }
        //  holder.updateTime.setText(UpdateDateTime);

       /* model.getSender_id();
        model.getSender_name();
        model.getSender_image();
        model.getMessage();
        model.getMessage_id();
        model.getTime();*/

          holder.RL_row.setOnClickListener(this);
          holder.RL_row.setTag(position);
          holder.IV_deleteDiary.setOnClickListener(this);
          holder.IV_deleteDiary.setTag(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onClick(View view) {
        Integer position = (Integer) view.getTag();
        switch (view.getId()){
            case R.id.RL_row:
                Fragment fragment = new EditDiary_fr();
                cmf.bdl.putString("id", arrayList.get(position).getId());
                cmf.bdl.putString("note", arrayList.get(position).getNote());
                cmf.bdl.putString("subject", arrayList.get(position).getSubject());
                cmf.bdl.putString("added_date", arrayList.get(position).getAdded_date());
                fragment.setArguments(cmf.bdl);
                cmf.replaceFragment(context,fragment );
                break;
            case R.id.IV_deleteDiary:
                String USER_ID= cmf.myPreference.getString(context,cmf.gc.USER_ID);
                delete_diaryPage(USER_ID, arrayList.get(position).getId(),position);
                break;
        }

    }

    private void delete_diaryPage(String USER_ID,String id,final int position) {
            new CallWebService(context, cmf.urlList.delete_diary, cmf.delete_diary(USER_ID, id), new MyServiceListener() {
                @Override
                public void onSuccess(String string) {

                    try {
                        JSONObject jsonObject = new JSONObject(string);
                        String Status = jsonObject.optString("status");
                        // String Message = jsonObject.optString("Message");
                        if (Status.equals("1")) {
                            arrayList.remove(position);
                           notifyDataSetChanged();
                        } else {
                            //  Toast.makeText(getActivity(), Message, Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception ex) {
                    }
                }

                @Override
                public void onFailed() {
                }
            });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_month, tv_day, tv_year, tv_title, date_row, tv_short_dis;
        RelativeLayout RL_row;
        ImageView IV_deleteDiary;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_month = (TextView) itemView.findViewById(R.id.tv_month);
            IV_deleteDiary = (ImageView) itemView.findViewById(R.id.IV_deleteDiary);
            tv_day = (TextView) itemView.findViewById(R.id.tv_day);
            tv_year = (TextView) itemView.findViewById(R.id.tv_year);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            date_row = (TextView) itemView.findViewById(R.id.date_row);
            tv_short_dis = (TextView) itemView.findViewById(R.id.tv_short_dis);
            RL_row = (RelativeLayout) itemView.findViewById(R.id.RL_row);
        }
    }
}
