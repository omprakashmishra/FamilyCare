package com.omsworld.familycare.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.DateTime_c;
import com.omsworld.familycare.model.NewsEventsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by rupesh.m on 12/17/2016.
 */

public class NewsDetail_Fr extends Fragment implements View.OnClickListener {

    CommonFunctions cmf;
    private View rootView;
    NewsEventsModel newsEventsModel=new NewsEventsModel();
    //-----------------------------------
    public TextView TV_newsHeadLine, tvPostedTime, TV_news, TV_likecount;
    ImageView IV_newsimage, ivPlayPause, likeimage;
    VideoView VV_newsVideo;
    RelativeLayout RL_fullVideo,RL_layout_top3;
    LinearLayout LL_layoutlikes, LL_layoutshare;
    ProgressBar progress;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.news_detail_fr, container, false);
        initilize();
        return rootView;
    }

    private void initilize() {
        cmf = new CommonFunctions(getActivity());
        TV_newsHeadLine = (TextView) rootView.findViewById(R.id.TV_newsHeadLine);
        tvPostedTime = (TextView) rootView.findViewById(R.id.tvPostedTime);
        TV_news = (TextView) rootView.findViewById(R.id.TV_news);
        TV_news.setMaxLines(10000);
        TV_likecount = (TextView) rootView.findViewById(R.id.TV_likecount);
        IV_newsimage = (ImageView) rootView.findViewById(R.id.IV_newsimage);
        ivPlayPause = (ImageView) rootView.findViewById(R.id.ivPlayPause);
        likeimage = (ImageView) rootView.findViewById(R.id.likeimage);
        VV_newsVideo = (VideoView) rootView.findViewById(R.id.VV_newsVideo);
        RL_fullVideo = (RelativeLayout) rootView.findViewById(R.id.RL_fullVideo);
        RL_layout_top3 = (RelativeLayout) rootView.findViewById(R.id.RL_layout_top3);
        LL_layoutlikes = (LinearLayout) rootView.findViewById(R.id.LL_layoutlikes);
        LL_layoutlikes.setVisibility(View.GONE);
        LL_layoutshare = (LinearLayout) rootView.findViewById(R.id.LL_layoutshare);
        LL_layoutshare.setVisibility(View.GONE);
        progress = (ProgressBar) rootView.findViewById(R.id.progress);
        try {
            cmf.bdl = this.getArguments();
            if (cmf.bdl != null) {

                // pageType = cmf.bdl.getString(cmf.gc.fromPage);
                newsEventsModel= (NewsEventsModel) cmf.bdl.getSerializable("newsEventsModel");
                Log.e("---newsEventsModel---", "" + newsEventsModel.getDiscription());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //------------------------setup------------
        TV_newsHeadLine.setText(newsEventsModel.getTitle());
        TV_news.setText(newsEventsModel.getDiscription());



        IV_newsimage.setVisibility(View.GONE);
        if(newsEventsModel.getNews_type().equals("image")){
            Picasso.with(getActivity()).load(newsEventsModel.getImage()).placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(IV_newsimage);
            IV_newsimage.setVisibility(View.VISIBLE);
        }

        //---------------------------------------
        String last_chatTime = DateTime_c.getInstance().changeFormate(newsEventsModel.getAdded_date());
        tvPostedTime.setText(last_chatTime);
        TV_likecount.setText(newsEventsModel.getLike_count() + " Likes");

        likeimage.setImageDrawable(likeimage.getResources().getDrawable(R.drawable.icon_like));
        if (newsEventsModel.getLike().equals("1")) {
            likeimage.setImageDrawable(likeimage.getResources().getDrawable(R.drawable.liked_ic));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.landlineno:

                break;

        }
    }
}

