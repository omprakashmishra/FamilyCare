package com.omsworld.familycare.Chat;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.DateTime_c;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nityanand.p on 8/11/2017.
 */

public class FriendsList_Adapter extends RecyclerView.Adapter<FriendsList_Adapter.MyViewHolder> implements View.OnClickListener {

    public ArrayList<FriendListModel> arrayList;
    public Context context;
    public int index = -1;
    FriendListModel model;
    CommonFunctions cmf;
    private LayoutInflater LIoffer;
    private String USER_ID;
    final String messageby="You :";
   final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public FriendsList_Adapter(Context context, String USER_ID, ArrayList<FriendListModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        this.USER_ID = USER_ID;
        LIoffer = LayoutInflater.from(context);
        cmf = new CommonFunctions(context);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LIoffer.inflate(R.layout.chat_list_user_item, parent, false);
        // MyViewHolder holder = new MyViewHolder(view);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        model = arrayList.get(position);

        holder.tv_title.setText(arrayList.get(position).getFreindFullname());


        if (arrayList.get(position).getSenderId().equals(USER_ID)) {
            holder.tv_short_dis.setText(messageby + arrayList.get(position).getMessage());
        }else {
            holder.tv_short_dis.setText(arrayList.get(position).getMessage());
        }

        holder.tv_date_row.setText(DateTime_c.getInstance().formateDateTime(arrayList.get(position).getTime()));

        try {
            Picasso.with(context).load(arrayList.get(position).getFreindImg()).resize(40, 40).placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(holder.IV_frinds_img, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.mprogressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.mprogressBar.setVisibility(View.GONE);
                        }
                    });
        } catch (Exception ex) {
            holder.mprogressBar.setVisibility(View.GONE);
        }
       /* model.getSender_id();
        model.getSender_name();
        model.getSender_image();
        model.getMessage();
        model.getMessage_id();
        model.getTime();*/

        holder.RL_row.setOnClickListener(this);
        holder.RL_row.setTag(position);
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
                Fragment fragment = new Chat_Fr();
                cmf.bdl.putString("friend_Name", arrayList.get(position).getFreindFullname());
                cmf.bdl.putString("friend_Img", arrayList.get(position).getFreindImg());
                cmf.bdl.putString("friend_Id", arrayList.get(position).getFreindId());
                cmf.bdl.putString("friend_Phone", arrayList.get(position).getFriend_phone());
                fragment.setArguments(cmf.bdl);
                cmf.replaceFragment(context,fragment );
                break;

        }
    }

    public void setFilter(List<FriendListModel> countryModels) {
        arrayList = new ArrayList<>();
        arrayList.addAll(countryModels);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_title, tv_short_dis, tv_date_row;
        RelativeLayout RL_row;
        CircleImageView IV_frinds_img;
        private ProgressBar mprogressBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            RL_row = (RelativeLayout) itemView.findViewById(R.id.RL_row);
            IV_frinds_img = (CircleImageView) itemView.findViewById(R.id.IV_frinds_img);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_short_dis = (TextView) itemView.findViewById(R.id.tv_short_dis);
            tv_date_row = (TextView) itemView.findViewById(R.id.tv_date_row);
            mprogressBar = (ProgressBar) itemView.findViewById(R.id.mprogressBar);
        }
    }
    //=================================
}
