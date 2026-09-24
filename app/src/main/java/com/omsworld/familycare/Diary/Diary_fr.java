package com.omsworld.familycare.Diary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.MyServiceListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by omprakash.m on 1/22/2018.
 */

public class Diary_fr extends Fragment implements View.OnClickListener {

    static CommonFunctions cmf;
    String USER_ID;
    RecyclerView RV_diary;
    Diary_Fr_Adapter diary_Fr_Adapter;
    private View rootView;
    private DiaryModel diaryModel;
    private static ArrayList<DiaryModel> diaryList = new ArrayList<>();
    private ProgressBar progressBar;
    TextView tv_head;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.diary_fr, container, false);
        cmf = new CommonFunctions(getActivity());
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Initview();
    }

    private void Initview() {
        USER_ID = cmf.myPreference.getString(getActivity(), cmf.gc.USER_ID);
        progressBar = (ProgressBar) rootView.findViewById(R.id.mprogressBar);
        RV_diary = (RecyclerView) rootView.findViewById(R.id.RV_diary);
        tv_head = (TextView) rootView.findViewById(R.id.tv_head);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RV_diary.setLayoutManager(layoutManager);

        ImageView addnote = (ImageView) rootView.findViewById(R.id.IV_add_new);
        addnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNote();
            }
        });

        GetDiaryListAPICALL();
    }

    private void createNote() {
        cmf.replaceFragment(getActivity(),new EditDiary_fr() );
    }

    private void GetDiaryListAPICALL() {
        //--------------------------------------without api data load
        diary_Fr_Adapter = new Diary_Fr_Adapter(getActivity(), diaryList);
        RV_diary.setAdapter(diary_Fr_Adapter);
        diary_Fr_Adapter.notifyDataSetChanged();

        //--------------------------------------

        new CallWebService("", getActivity(), cmf.urlList.show_diary, cmf.UserID(USER_ID), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    String Status = jsonObject.optString("status");
                    diaryList.clear();
                    // String Message = jsonObject.optString("Message");
                    if (Status.equals("1")) {
                        JSONArray jsonarray = jsonObject.optJSONArray("my_diary");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonObject1 = jsonarray.optJSONObject(i);
                            diaryModel = new DiaryModel();

                            diaryModel.setAdded_date(jsonObject1.optString("added_date"));
                            diaryModel.setId(jsonObject1.optString("id"));
                            diaryModel.setNote(jsonObject1.optString("note"));
                            diaryModel.setSubject(jsonObject1.optString("subject"));
                            diaryList.add(diaryModel);
                        }
                    }
                    diary_Fr_Adapter = new Diary_Fr_Adapter(getActivity(), diaryList);
                    RV_diary.setAdapter(diary_Fr_Adapter);
                    diary_Fr_Adapter.notifyDataSetChanged();
                    //  Toast.makeText(getActivity(), Message, Toast.LENGTH_LONG).show();

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}
