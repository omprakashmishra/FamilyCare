package com.omsworld.familycare.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.omsworld.familycare.R;
import com.omsworld.familycare.adapter.NewsEventsAdapter;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.omsworld.familycare.api_call.MyServiceListener;
import com.omsworld.familycare.model.NewsEventsModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by rupesh.m on 12/17/2016.
 */

public class NewsEvents_Fr extends Fragment implements View.OnClickListener {
    NewsEventsModel model;
    NewsEventsAdapter adapter;
    ArrayList<NewsEventsModel> list = new ArrayList<>();
    CommonFunctions cmf;
    private View rootView;
    RecyclerView recyclerView;
    private ProgressBar mprogressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.news_events_fr, container, false);
        initilize();
        return rootView;
    }

    private void initilize() {
        cmf = new CommonFunctions(getActivity());
        recyclerView=(RecyclerView)rootView.findViewById(R.id.RV_newsEventsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mprogressBar = (ProgressBar) rootView.findViewById(R.id.mprogressBar);
        newsList();
    }

    private void newsList() {
        if(list.size()>1){
            adapter = new NewsEventsAdapter(getActivity(), list);
            recyclerView.setAdapter(adapter);
            mprogressBar.setVisibility(View.GONE);
            return;
        }
        String AspnetUserID = cmf.myPreference.getString(getActivity(), GlobalConstants.USER_ID);
        new CallWebService("",getActivity(), cmf.urlList.news_list, cmf.user_id(AspnetUserID), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    mprogressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(string);
                    String Status = jsonObject.optString("status");
                    if (Status.equals("1")) {
                        JSONArray jsonArray = jsonObject.optJSONArray("news_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            //----------------------
                            String title = jsonObject1.optString("title");
                            String id = jsonObject1.optString("id");
                            String discription = jsonObject1.optString("discription");
                            String category = jsonObject1.optString("category");
                            String added_date = jsonObject1.optString("added_date");
                            String news_type = jsonObject1.optString("type");
                            String like_count = jsonObject1.optString("like_count");
                            String like = jsonObject1.optString("like");
                            String image = jsonObject1.optString("image");
                            //-----------------
                            model = new NewsEventsModel();
                            model.setAdded_date(added_date);
                            model.setCategory(category);
                            model.setDiscription(discription);
                            model.setId(id);
                            model.setTitle(title);
                            model.setNews_type(news_type);
                            model.setLike_count(like_count);
                            model.setLike(like);
                            model.setImage(image);
                            list.add(model);
                        }
                        //----------
                        adapter = new NewsEventsAdapter(getActivity(), list);
                        recyclerView.setAdapter(adapter);
                       // recyclerView.scrollToPosition(list.size() - 1);

                    } else {
                    }
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
                mprogressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.landlineno:

                break;

        }
    }
}

