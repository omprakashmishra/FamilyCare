package com.omsworld.familycare.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.DateTime_c;
import com.omsworld.familycare.model.FriendsTrackingModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Rupesh.m on 7/15/2017.
 */

public class FriendsTrackingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    public static Context context;
    static CommonFunctions cmf;
    public List<FriendsTrackingModel> mDataset;

    public FriendsTrackingAdapter(Context context, List<FriendsTrackingModel> mDataset) {
        this.mDataset = mDataset;
        this.context = context;
        cmf = new CommonFunctions(context);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ContactViewHolder) {
            final ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
            contactViewHolder.groupmembername.setText(mDataset.get(position).getUserName());
            contactViewHolder.BT_firstletter.setText(mDataset.get(position).getUserName());
            contactViewHolder.batterypercentage.setText(mDataset.get(position).getBattery() + "%");
            contactViewHolder.tv_address.setText(mDataset.get(position).getAddress());

            String last_chatTime = DateTime_c.getInstance().formateDateTime(mDataset.get(position).getLastSeen());
            contactViewHolder.TV_time.setText(last_chatTime);

            if (mDataset.get(position).getOnlineStatus().equals("Offline")) {
                contactViewHolder.online.setText("Offline");
                contactViewHolder.online.setTextColor(Color.RED);

                contactViewHolder.LL_trackinglayout.setBackgroundColor(Color.parseColor("#80B6B6B4"));
                contactViewHolder.IV_on_off_line.setImageResource(R.drawable.offline_ic);
            } else {
                contactViewHolder.online.setText("Online");
                contactViewHolder.online.setTextColor(Color.GREEN);

                contactViewHolder.IV_on_off_line.setImageResource(R.drawable.online_ic);
            }

            try {
                Picasso.with(context).load(mDataset.get(position).getUser_img()).resize(40, 40).placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(contactViewHolder.IV_frinds_img, new Callback() {
                            @Override
                            public void onSuccess() {
                                contactViewHolder.mprogressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                contactViewHolder.mprogressBar.setVisibility(View.GONE);
                            }
                        });
            } catch (Exception ex) {
                contactViewHolder.mprogressBar.setVisibility(View.GONE);
            }


            contactViewHolder.track.setOnClickListener(this);
            contactViewHolder.track.setTag(position);
            contactViewHolder.IV_navigation.setOnClickListener(this);
            contactViewHolder.IV_navigation.setTag(position);
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_tracking_list_item, viewGroup, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onClick(View view) {
        Integer position;
        switch (view.getId()) {
            case R.id.track:
                position = (Integer) view.getTag();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String Phone = mDataset.get(position).getMobileNumber();
                //String CallPhone = Phone.replaceAll("\\D+", "");
                intent.setData(Uri.parse("tel:" + Phone));
                context.startActivity(intent);
                break;
            case R.id.IV_navigation:
                position = (Integer) view.getTag();
                googleMapNavigation(mDataset.get(position).getLatitude(), mDataset.get(position).getLongitude());
                break;
        }
    }

    private void googleMapNavigation(String SneedyLat, String SneedyLong) {
        String Snewlattitude = cmf.myPreference.getString(context, cmf.gc.JOB_NEW_LATITUDE);
        String Snewlongitude = cmf.myPreference.getString(context, cmf.gc.JOB_NEW_LONGITUDE);
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + Snewlattitude + "," + Snewlongitude + "&daddr=" + SneedyLat + "," + SneedyLong));
        //   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView groupmembername, online, TV_locn, batterypercentage, tv_address, TV_time;
        private Button BT_firstletter, track;

        private ImageView IV_on_off_line, IV_navigation;
        private LinearLayout LL_trackinglayout;
        private CircleImageView IV_frinds_img;
        private ProgressBar mprogressBar;

        public ContactViewHolder(View v) {
            super(v);
            groupmembername = (TextView) v.findViewById(R.id.groupmembername);
            IV_frinds_img = (CircleImageView) v.findViewById(R.id.IV_frinds_img);
            mprogressBar = (ProgressBar) v.findViewById(R.id.mprogressBar);
            online = (TextView) v.findViewById(R.id.online);
            tv_address = (TextView) v.findViewById(R.id.tv_address);
           // TV_locn = (TextView) v.findViewById(R.id.TV_locn);
            TV_time = (TextView) v.findViewById(R.id.TV_time);
            batterypercentage = (TextView) v.findViewById(R.id.batterypercentage);
            BT_firstletter = (Button) v.findViewById(R.id.BT_firstletter);
            track = (Button) v.findViewById(R.id.track);
            IV_on_off_line = (ImageView) v.findViewById(R.id.IV_on_off_line);
            IV_navigation = (ImageView) v.findViewById(R.id.IV_navigation);
            LL_trackinglayout = (LinearLayout) v.findViewById(R.id.LL_trackinglayout);
        }
    }
}
