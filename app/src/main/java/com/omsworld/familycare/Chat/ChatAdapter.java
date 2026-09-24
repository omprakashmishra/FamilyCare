package com.omsworld.familycare.Chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.DateTime_c;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nityanand.p on 8/11/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> implements View.OnClickListener {

    public ArrayList<ChatModel> arrayList;
    public Context context;
    ChatModel model;
    private LayoutInflater LIoffer;
    private String USER_ID, my_image;
    private String last_chatTime = "";

    public ChatAdapter(Context context, String USER_ID, String my_image, ArrayList<ChatModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        this.USER_ID = USER_ID;
        this.my_image = my_image;
        LIoffer = LayoutInflater.from(context);

    }

  /*  public void setFilter(List<ChatModel> countryModels) {
        arrayList = new ArrayList<>();
        arrayList.addAll(countryModels);
        notifyDataSetChanged();
    }*/

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LIoffer.inflate(R.layout.comments_list_item, parent, false);
        // MyViewHolder holder = new MyViewHolder(view);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        model = arrayList.get(position);
        // change time format from server text...

        last_chatTime = DateTime_c.getInstance().formateDateTime(arrayList.get(position).getTime());
        //*************************************SET DATA************************


        if (USER_ID.equals(arrayList.get(position).getSender_id())) {
            // chat by you...right side layout enable.
            holder.LL_chat_left.setVisibility(View.GONE);
            holder.LL_chat_right.setVisibility(View.VISIBLE);
            holder.TV_rightChat.setText(arrayList.get(position).getMessage());
            // holder.TV_rightChat.setText("static message by me member please change");
            holder.TV_right_datetime.setText(last_chatTime);

            Picasso.with(context).load(my_image).resize(40, 40).placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(holder.IV_right_frinds_img);

        } else {
            // chat by you...right side layout enable.
            holder.LL_chat_left.setVisibility(View.VISIBLE);
            holder.LL_chat_right.setVisibility(View.GONE);
            holder.TV_leftChat.setText(arrayList.get(position).getMessage());
            // holder.TV_leftChat.setText("static message via my family member please change");
            holder.TV_left_datetime.setText(last_chatTime);

            Picasso.with(context).load(arrayList.get(position).getFreind_img()).resize(40, 40).placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(holder.IV_left_frinds_img);
        }

        //-----------------------------------------
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onClick(View view) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView TV_leftChat, TV_left_datetime, TV_rightChat, TV_right_datetime;
        LinearLayout LL_chat_left, LL_chat_right;
        CircleImageView IV_left_frinds_img, IV_right_frinds_img;


        public MyViewHolder(View itemView) {
            super(itemView);
            LL_chat_left = (LinearLayout) itemView.findViewById(R.id.LL_chat_left);
            IV_left_frinds_img = (CircleImageView) itemView.findViewById(R.id.IV_left_frinds_img);
            TV_leftChat = (TextView) itemView.findViewById(R.id.TV_leftChat);
            TV_left_datetime = (TextView) itemView.findViewById(R.id.TV_left_datetime);

            LL_chat_right = (LinearLayout) itemView.findViewById(R.id.LL_chat_right);
            IV_right_frinds_img = (CircleImageView) itemView.findViewById(R.id.IV_right_frinds_img);
            TV_rightChat = (TextView) itemView.findViewById(R.id.TV_rightChat);
            TV_right_datetime = (TextView) itemView.findViewById(R.id.TV_right_datetime);
        }
    }

    //=================================
}
