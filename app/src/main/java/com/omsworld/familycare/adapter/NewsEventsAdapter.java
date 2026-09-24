package com.omsworld.familycare.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.DateTime_c;
import com.omsworld.familycare.api_call.MyServiceListener;
import com.omsworld.familycare.fragment.NewsDetail_Fr;
import com.omsworld.familycare.model.NewsEventsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by omprakash.m on 1/22/2018.
 */

public class NewsEventsAdapter extends RecyclerView.Adapter<NewsEventsAdapter.MyViewHolder> implements View.OnClickListener {

    public ArrayList<NewsEventsModel> arrayList = new ArrayList<>();
    public Context context;
    public int index = -1;
    NewsEventsModel model;
    String UpdateDateTime;
    CommonFunctions cmf;
    private LayoutInflater LIoffer;
    String userId;


    public NewsEventsAdapter(Context context, ArrayList<NewsEventsModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        LIoffer = LayoutInflater.from(context);
        cmf = new CommonFunctions(context);
        userId=cmf.myPreference.getString(context,cmf.gc.USER_ID);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LIoffer.inflate(R.layout.news_events_item, parent, false);
        // MyViewHolder holder = new MyViewHolder(view);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //  model = arrayList.get(position);


        holder.TV_newsHeadLine.setText(arrayList.get(position).getTitle());
        holder.TV_news.setText(arrayList.get(position).getDiscription());

        if(arrayList.get(position).getDiscription().length()>150){
            holder.RL_layout_top3.setVisibility(View.VISIBLE);
        }


        holder.IV_newsimage.setVisibility(View.GONE);
        if(arrayList.get(position).getNews_type().equals("image")){
            Picasso.with(context).load(arrayList.get(position).getImage()).placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(holder.IV_newsimage);
            holder.IV_newsimage.setVisibility(View.VISIBLE);
        }








        //---------------------------------------
        String last_chatTime = DateTime_c.getInstance().changeFormate(arrayList.get(position).getAdded_date());
        holder.tvPostedTime.setText(last_chatTime);
        holder.TV_likecount.setText(arrayList.get(position).getLike_count() + " Likes");

        holder.likeimage.setImageDrawable(holder.likeimage.getResources().getDrawable(R.drawable.icon_like));
        if (arrayList.get(position).getLike().equals("1")) {
            holder.likeimage.setImageDrawable(holder.likeimage.getResources().getDrawable(R.drawable.liked_ic));
        }
        //----------------------------------------





        holder.LL_layoutlikes.setOnClickListener(this);
        holder.LL_layoutlikes.setTag(position);
        holder.LL_layoutshare.setOnClickListener(this);
        holder.LL_layoutshare.setTag(position);
        holder.RL_layout_top3.setOnClickListener(this);
        holder.RL_layout_top3.setTag(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onClick(View view) {
        Integer position = (Integer) view.getTag();
        switch (view.getId()) {
            case R.id.LL_layoutshare:
                /*Fragment fragment = new EditDiary_fr();
                cmf.bdl.putString("id", arrayList.get(position).getId());
                //cmf.bdl.putString("note", arrayList.get(position).getNote());
                // cmf.bdl.putString("subject", arrayList.get(position).getSubject());
                cmf.bdl.putString("added_date", arrayList.get(position).getAdded_date());
                fragment.setArguments(cmf.bdl);
                cmf.replaceFragment(fragment);*/
                break;
                case R.id.RL_layout_top3:
                    Fragment fragment = new NewsDetail_Fr();
                    NewsEventsModel newsEventsModel=arrayList.get(position);
                    cmf.bdl.putSerializable("newsEventsModel", newsEventsModel);
                    fragment.setArguments(cmf.bdl);
                    cmf.replaceFragment(context,fragment );
                break;
            case R.id.LL_layoutlikes:
                if (arrayList.get(position).getLike().equals("0")) {
                    model = arrayList.get(position);
                    int likecount=Integer.parseInt(model.getLike_count())+1;
                    model.setLike_count(String.valueOf(likecount));
                    model.setLike("1");
                    arrayList.set(position, model);
                    notifyDataSetChanged();

                    likeDislike(1,arrayList.get(position).getId());
                } else {
                    model = arrayList.get(position);
                    int likecount=Integer.parseInt(model.getLike_count())-1;
                    model.setLike_count(String.valueOf(likecount));
                    model.setLike("0");
                    arrayList.set(position, model);
                    notifyDataSetChanged();
                    likeDislike(0,arrayList.get(position).getId());
                }
                //API call notifychange..
                break;

        }

    }

    private void likeDislike(int i,String newsId) {
        String url;
        if (i == 1) {
            url = cmf.urlList.like_news;
        } else {
            url = cmf.urlList.unlike_news;
        }
        new CallWebService("NoProgress", context, url, cmf.likeUnlike(userId,newsId), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {

            }

            @Override
            public void onFailed() {

            }
        });
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView TV_newsHeadLine, tvPostedTime, TV_news, TV_likecount;
        ImageView IV_newsimage, ivPlayPause, likeimage;
        VideoView VV_newsVideo;
        RelativeLayout RL_fullVideo,RL_layout_top3;
        LinearLayout LL_layoutlikes, LL_layoutshare;
        ProgressBar progress;

        public MyViewHolder(View itemView) {
            super(itemView);
            TV_newsHeadLine = (TextView) itemView.findViewById(R.id.TV_newsHeadLine);
            tvPostedTime = (TextView) itemView.findViewById(R.id.tvPostedTime);
            TV_news = (TextView) itemView.findViewById(R.id.TV_news);
            TV_likecount = (TextView) itemView.findViewById(R.id.TV_likecount);
            IV_newsimage = (ImageView) itemView.findViewById(R.id.IV_newsimage);
            ivPlayPause = (ImageView) itemView.findViewById(R.id.ivPlayPause);
            likeimage = (ImageView) itemView.findViewById(R.id.likeimage);
            VV_newsVideo = (VideoView) itemView.findViewById(R.id.VV_newsVideo);
            RL_fullVideo = (RelativeLayout) itemView.findViewById(R.id.RL_fullVideo);
            RL_layout_top3 = (RelativeLayout) itemView.findViewById(R.id.RL_layout_top3);
            LL_layoutlikes = (LinearLayout) itemView.findViewById(R.id.LL_layoutlikes);
            LL_layoutshare = (LinearLayout) itemView.findViewById(R.id.LL_layoutshare);
            progress = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }
}
