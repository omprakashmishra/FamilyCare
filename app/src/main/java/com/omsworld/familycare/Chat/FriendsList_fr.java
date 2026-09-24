package com.omsworld.familycare.Chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.MyServiceListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omprakash.m on 1/22/2018.
 */

public class FriendsList_fr extends Fragment implements View.OnClickListener {

    static CommonFunctions cmf;
    ArrayList<String> contactsList;
    String InteractionID;
    TextView sendComment;
    EditText writeComment;
    String writeCommentes;
    ProgressBar mprogressBar;
    String USER_ID;
    EditText TVSearch;
    private View rootView;
    //================
    private PopupWindow popWindow;
    private RecyclerView recyclerView;
    private FriendsList_Adapter chatFriends_Adapter;
    private FriendListModel commentModel;
    private ArrayList<FriendListModel> arraylistTracking = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.chat_list_user_fr, container, false);
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
        writeComment = (EditText) rootView.findViewById(R.id.writeComment);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.RV_Friends);
        TVSearch = (EditText) rootView.findViewById(R.id.TV_search);
        mprogressBar = (ProgressBar) rootView.findViewById(R.id.mprogressBar);


        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        InteractionID = "";
        GetIncidentCommentSummaryAPICALL();

        //----------------
        // search suggestions using the edittext widget
        TVSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (arraylistTracking.isEmpty()) {
                    return;
                }
                final List<FriendListModel> filteredModelList = filter(arraylistTracking, s.toString());

                chatFriends_Adapter.setFilter(filteredModelList);
                chatFriends_Adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private List<FriendListModel> filter(List<FriendListModel> models, String query) {
        query = query.toLowerCase();

        final List<FriendListModel> filteredModelList = new ArrayList<>();
        for (FriendListModel model : models) {
            final String text = model.getFreindFullname().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private void GetIncidentCommentSummaryAPICALL() {
        mprogressBar.setVisibility(View.VISIBLE);
        new CallWebService("",getActivity(), cmf.urlList.user_chat_list, cmf.UserID(USER_ID), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    mprogressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(string);
                    String Status = jsonObject.optString("status");
                    // String Message = jsonObject.optString("Message");
                    if (Status.equals("1")) {

                        arraylistTracking.clear();
                        JSONArray jsonarray = jsonObject.optJSONArray("chat_user_info");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonObject1 = jsonarray.optJSONObject(i);

                            commentModel = new FriendListModel();

                            commentModel.setFreindFullname(jsonObject1.optString("freind_fullname"));
                            commentModel.setFreindId(jsonObject1.optString("freind_id"));
                            commentModel.setFreindImg(jsonObject1.optString("freind_img"));
                            commentModel.setFriend_phone(jsonObject1.optString("friend_phone"));
                            commentModel.setMessage(jsonObject1.optString("message"));
                            commentModel.setMessageId(jsonObject1.optString("message_id"));
                            commentModel.setSenderId(jsonObject1.optString("sender_id"));
                            commentModel.setTime(jsonObject1.optString("time"));
                            commentModel.setType(jsonObject1.optString("type"));


                            arraylistTracking.add(commentModel);
                        }
                        if (arraylistTracking.size() > 0) {

                            chatFriends_Adapter = new FriendsList_Adapter(getActivity(), USER_ID, arraylistTracking);
                            recyclerView.setAdapter(chatFriends_Adapter);
                            chatFriends_Adapter.notifyDataSetChanged();
                        }
                    } else {
                        //  Toast.makeText(getActivity(), Message, Toast.LENGTH_LONG).show();
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
            case R.id.sendComment:

                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cmf.hideKeyboard(rootView);
    }
}
