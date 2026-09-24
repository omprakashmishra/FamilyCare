package com.omsworld.familycare.Chat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omsworld.familycare.R;
import com.omsworld.familycare.activity.MainActivity;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.DateTime_c;
import com.omsworld.familycare.api_call.MyServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by omprakash.m on 1/15/2018.
 */

public class Chat_Fr extends Fragment implements View.OnClickListener {

    public static RecyclerView recyclerView;
    public static ChatAdapter chatAdapter;
    static String friend_name, freind_Img, freind_Id, friend_Phone;
    static ArrayList<ChatModel> arraylistTracking = new ArrayList<>();
    boolean API_Call = true;
    TextView sendComment, TV_toolbar_text;
    EditText writeComment;
    String writeCommentes;
    String MyUSER_ID, my_image, my_user_name;
    ImageView IV_side_one;
    RelativeLayout RL_backClick;
    private CommonFunctions cmf;
    private View rootView;
    //================
    private ChatModel chatModel;
    private ProgressBar mprogressBar;


    public static boolean notifyChatData(String notifiyData) {
        boolean value=false;
        try {
            JSONObject jsonObject = new JSONObject(notifiyData);
            JSONObject jsonObject1 = jsonObject.optJSONObject("data");

            if (freind_Id.equals(jsonObject1.optString("sender"))) {
                ChatModel chatModel = new ChatModel();
                chatModel.setFreind_fullname(friend_name);
                chatModel.setFreind_id(freind_Id);
                chatModel.setFreind_img(freind_Img);

                chatModel.setMessage_id(jsonObject1.optString(""));
                chatModel.setMessage(jsonObject1.optString("text"));
                chatModel.setTime(jsonObject1.optString("sending_time"));

                arraylistTracking.add(chatModel);
                recyclerView.scrollToPosition(arraylistTracking.size() - 1);
                chatAdapter.notifyDataSetChanged();
                value= true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            value=false;
        }
        return value;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // Toast.makeText(getContext(),"dddd",Toast.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.comment_activity, container, false);
        cmf = new CommonFunctions(getActivity());
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Initview();
    }


    private void Initview() {
       // Toast.makeText(getContext(),"dddd",Toast.LENGTH_LONG).show();
        MyUSER_ID = cmf.myPreference.getString(getActivity(), cmf.gc.USER_ID);
        my_image = cmf.myPreference.getString(getActivity(), cmf.gc.USER_IMAGE);
        my_user_name = cmf.myPreference.getString(getActivity(), cmf.gc.USER_NAME);
        try {
            cmf.bdl = this.getArguments();
            if (cmf.bdl != null) {
                friend_name = cmf.bdl.getString("friend_Name");
                freind_Img = cmf.bdl.getString("friend_Img");
                freind_Id = cmf.bdl.getString("friend_Id");
                friend_Phone = cmf.bdl.getString("friend_Phone");

            } else {
                cmf.back_fragment();
            }

        } catch (Exception ex) {
        }
        mprogressBar = (ProgressBar) rootView.findViewById(R.id.mprogressBar);
        sendComment = (TextView) rootView.findViewById(R.id.sendComment);
        sendComment.setOnClickListener(this);
        writeComment = (EditText) rootView.findViewById(R.id.writeComment);
        // writeComment.setOnClickListener(this);


        recyclerView = (RecyclerView) rootView.findViewById(R.id.commentsListView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        arraylistTracking.clear();
        chatAdapter = new ChatAdapter(getActivity(), MyUSER_ID, my_image, arraylistTracking);
        recyclerView.setAdapter(chatAdapter);

        mprogressBar.setVisibility(View.VISIBLE);
        // ChantHandler(0);
        ChatListAPICALL();
        initToolBar();
    }

    public void initToolBar() {
        MainActivity.toolbar.setVisibility(View.GONE);
        RL_backClick = (RelativeLayout) rootView.findViewById(R.id.RL_backClick);
        TV_toolbar_text = (TextView) rootView.findViewById(R.id.TV_toolbar_text);
        IV_side_one = (ImageView) rootView.findViewById(R.id.IV_side_one);

        RL_backClick.setOnClickListener(this);
        IV_side_one.setOnClickListener(this);

        TV_toolbar_text.setText(friend_name);
        IV_side_one.setVisibility(View.VISIBLE);

        //  toolbar_image = MainActivity.toolbar_image;
      /*  toolbar_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_phone));
        toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"call you",Toast.LENGTH_LONG).show();
            }
        });*/
    }

    //--------------------------------------------------------------------------------------TimerForUpdateUI....
    public void ChantHandler(final int stop) {
        /*        ChatListAPICALL();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                handler.postDelayed(this, 15000);
                if (stop == 5) {
                    handler.removeCallbacks(this);
                    return;
                }
                try {
                    ChatListAPICALL();
                } catch (Exception ex) {
                }
                //-----------------------
            }
        }, 15000);*/

    }

    private void ChatListAPICALL() {
        if (API_Call)
            API_Call = false;
        new CallWebService("", getActivity(), cmf.urlList.user_chat_details, cmf.user_chat_details(MyUSER_ID, freind_Id), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    API_Call = true;
                    mprogressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(string);
                    String Status = jsonObject.optString("status");
                    // String Message = jsonObject.optString("Message");
                    if (Status.equals("1")) {
                        JSONArray jsonarray = jsonObject.optJSONArray("chat_info");

                        if (jsonarray.length() == arraylistTracking.size()) {
                            return;
                        }

                        arraylistTracking.clear();
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonObject1 = jsonarray.optJSONObject(i);
                            chatModel = new ChatModel();

                            chatModel.setSender_id(jsonObject1.optString("sender_id"));
                            chatModel.setFreind_fullname(friend_name);
                            chatModel.setFreind_id(freind_Id);
                            chatModel.setFreind_img(freind_Img);
                            chatModel.setMessage(jsonObject1.optString("message"));
                            chatModel.setMessage_id(jsonObject1.optString("message_id"));
                            chatModel.setTime(jsonObject1.optString("time"));
                            arraylistTracking.add(chatModel);
                        }
                        if (arraylistTracking.size() > 0) {
                            /*chatAdapter = new ChatAdapter(getActivity(), MyUSER_ID, my_image, arraylistTracking);
                            recyclerView.setAdapter(chatAdapter);*/
                            chatAdapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(arraylistTracking.size() - 1);
                        }
                    } else {
                        //  Toast.makeText(getActivity(), Message, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
                API_Call = true;
                mprogressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendComment:
                if (!TextUtils.isEmpty(writeComment.getText().toString())) {

                    writeCommentes = writeComment.getText().toString();
                    writeComment.setText("");
                    set_chat(writeCommentes);
                    API_Call = true;
                    SendCommentApi(writeCommentes);

                } else {
                    // Toast.makeText(getActivity(), "Please enter a comment.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.RL_backClick:
                MainActivity.toolbar.setVisibility(View.VISIBLE);
                cmf.back_fragment();
                break;
            case R.id.IV_side_one:
                //for call..
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String Phone = friend_Phone;
                //String CallPhone = Phone.replaceAll("\\D+", "");
                intent.setData(Uri.parse("tel:" + Phone));
                try {
                    getActivity().startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                }
                break;
        }
    }


    private void SendCommentApi(final String writeCommentes) {
        if (API_Call)
            API_Call = false;
        new CallWebService("", getActivity(), cmf.urlList.user_chat, cmf.user_chat(MyUSER_ID, freind_Id, writeCommentes), new MyServiceListener() {

            @Override
            public void onSuccess(String string) {
                API_Call = true;
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    String Status = jsonObject.optString("status");
                    //  String Message = jsonObject.optString("Message");
                    if (Status.equals("1")) {

                    } else {
                    }
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
                API_Call = true;
            }
        });
    }

    private void set_chat(final String chattext) {
        if (arraylistTracking.size() == 0) {
            ChatListAPICALL();
            return;
        }
        String currentTime = DateTime_c.getInstance().formateDateTime("currentDateTime");
        chatModel = new ChatModel();
        chatModel.setSender_id(MyUSER_ID);
        chatModel.setFreind_fullname(my_user_name);
        chatModel.setFreind_id(freind_Id);
        chatModel.setFreind_img(freind_Img);
        chatModel.setMessage(chattext);
        chatModel.setMessage_id("");
        chatModel.setTime(currentTime);
        arraylistTracking.add(chatModel);
        chatAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(arraylistTracking.size() - 1);
    }

}
