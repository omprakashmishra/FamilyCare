package com.omsworld.familycare.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omsworld.familycare.R;
import com.omsworld.familycare.adapter.MyFriendAdapterList;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.omsworld.familycare.api_call.MyServiceListener;
import com.omsworld.familycare.model.JoinSafeJoinModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by omprakash.m on 2/26/2018.
 */

public class FamilyMembers_Fr extends Fragment {
    @Bind(R.id.mprogressBar)
    ProgressBar mprogressBar;
    @Bind(R.id.IV_phonebook)
    ImageView IVPhonebook;
    @Bind(R.id.TV_Name)
    EditText TVName;
    @Bind(R.id.TV_Phone)
    EditText TVPhone;
    @Bind(R.id.RL_addFamily)
    RelativeLayout RLAddFamily;
    String Phone, Name, Email;
    @Bind(R.id.RV_friendsList)
    RecyclerView RVFriendsList;
    List<JoinSafeJoinModel> joinSafeJoinModels1 = new ArrayList<>();
    MyFriendAdapterList myFriendAdapterList;
    @Bind(R.id.RL_phoneNM)
    RelativeLayout RLPhoneNM;
    String family_id, family_name, AspnetUserID, IsFamilyAdmin;
    int request = 0;
    @Bind(R.id.TV_FamilyName)
    TextView TVFamilyName;
    @Bind(R.id.IV_editGroup)
    ImageView IVEditGroup;
    @Bind(R.id.LL_bottom)
    LinearLayout LLBottom;
    private CommonFunctions cmf;
    private View rootView;
    // private SentFriendAdapterList sentFriendAdapterList;
    private JoinSafeJoinModel joinSafeJoinModel1;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.family_member_fr, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cmf = new CommonFunctions(getActivity());
        InitView();
    }

    private void InitView() {
        AspnetUserID = cmf.myPreference.getString(getActivity(), GlobalConstants.USER_ID); //"151041";
      //  validatefamilyInfo();

        //----------------------------
         mSwipeRefreshLayout=(SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                MyFriendsApiCall();
            }
        });
        MyFriendsApiCall();
    }

    private void validatefamilyInfo() {
/*        family_id = cmf.myPreference.getString(getActivity(), cmf.gc.FAMILY_ID);
        family_name = cmf.myPreference.getString(getActivity(), cmf.gc.FAMILY_NAME);
        IsFamilyAdmin = cmf.myPreference.getString(getActivity(), cmf.gc.IsFamilyAdmin);*/
        if (family_id.length() == 0 || family_name.length() == 0) {
            LLBottom.setVisibility(View.GONE);
            //  updateFamilyName("Create Family Name");
            Toast.makeText(getActivity(),"Please create your family and join family member.",Toast.LENGTH_LONG).show();
        } else {
            TVFamilyName.setText(family_name);
            // MyFriendsApiCall();
            if (IsFamilyAdmin.equals("1")) {
                LLBottom.setVisibility(View.VISIBLE);
                IVEditGroup.setVisibility(View.VISIBLE);
            }else {
                LLBottom.setVisibility(View.GONE);
                IVEditGroup.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.IV_phonebook, R.id.RL_addFamily, R.id.IV_editGroup})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.IV_editGroup:
                // TVFamilyName.setText(family_name);
                if (family_id.length() == 0 || family_name.length() == 0) {
                    updateFamilyName("Create Family Name", "Create New Family");
                } else {
                    //only admin can update family name.
                    if (IsFamilyAdmin.equals("1")) {
                        updateFamilyName(family_name, "Update Your Family");
                    } else {
                        Toast.makeText(getContext(), "Please contact to family owner for update family name or other control.", Toast.LENGTH_LONG).show();
                    }
                }
                break;

            case R.id.IV_phonebook:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 1);
                break;
            case R.id.RL_addFamily:

                if (family_id.length() == 0 || family_name.length() == 0) {
                    //createFamilyName();
                } else {

                    Name = TVName.getText().toString();
                    Phone = TVPhone.getText().toString();
                    Email = TVName.getText().toString();

                    if (Phone.length() < 9) {
                        Toast.makeText(getActivity(), "Please enter a valid mobile number.", Toast.LENGTH_SHORT).show();
                    } else {
                        DialogInvitationApiCall(Name, Phone, "", Email);
                    }
                }
                cmf.hideKeyboard(TVName);

                break;
        }
    }

    //-----------------------------------------------------------------forget password
    private void updateFamilyName(String familynm, String headtext) {

        dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.forgetpassword_dialog);
        final ImageView pencil = (ImageView) dialog.findViewById(R.id.pencil);
        final Button lay11 = (Button) dialog.findViewById(R.id.lay11);
        final TextView tv_headText = (TextView) dialog.findViewById(R.id.tv_headText);
        tv_headText.setText(headtext);

        final Button send = (Button) dialog.findViewById(R.id.send);
        final Button cancel = (Button) dialog.findViewById(R.id.cancel);
        final EditText ETemail = (EditText) dialog.findViewById(R.id.edt_email);
        ETemail.setHint(familynm);
        lay11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        pencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = ETemail.getText().toString();
                if (email.length() == 0) {
                    ETemail.setError("Please enter an family name.");
                    return;
                } else {
                    cmf.hideKeyboard(v);
                    if (cmf.cdr.isConnectingToInternet()) {
                        createFamilyName(ETemail.getText().toString());

                    } else {
                        Toast.makeText(getActivity(), "Internet connection failed !", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        dialog.show();
    }

    private void createFamilyName(String FamilyName) {
        if (FamilyName.length() == 0) {
            Toast.makeText(getActivity(), "Please Set Your Valuable Family Name.", Toast.LENGTH_SHORT).show();
            return;
        }
        mprogressBar.setVisibility(View.VISIBLE);

        new CallWebService("", getActivity(), cmf.urlList.create_family_group, cmf.create_family_group(family_id, AspnetUserID, FamilyName), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    request = 0;
                    mprogressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(string);
                    String Status = jsonObject.optString("status");
                    String Message = jsonObject.optString("message");
                    if (Status.equals("1")) {
                        dialog.dismiss();
                        /*family_id = jsonObject.getString("family_id");
                        family_name = jsonObject.getString("family_name");
                        cmf.myPreference.setString(getActivity(), GlobalConstants.FAMILY_ID, family_id);
                        cmf.myPreference.setString(getActivity(), GlobalConstants.FAMILY_NAME, family_name);
                        cmf.myPreference.setString(getActivity(), GlobalConstants.IsFamilyAdmin, "1");
                        IsFamilyAdmin="1";
                        TVFamilyName.setText(family_name);
                        if (IsFamilyAdmin.equals("1")) {
                            LLBottom.setVisibility(View.VISIBLE);
                            cmf.myPreference.setString(getActivity(), cmf.gc.IsFamilyAdmin, "1");
                            IsFamilyAdmin = "1";
                        }*/
                        MyFriendsApiCall();
                    }
                    Toast.makeText(getActivity(), Message, Toast.LENGTH_SHORT).show();

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
                request = 0;
                mprogressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                Cursor c = null;
                try {
                    c = getActivity().managedQuery(uri, null, null, null, null);
                    if (c != null && c.moveToFirst()) {
                        final int namedisplay = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                        String name = c.getString(namedisplay);
                        //------------------------------
                        final int numberIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        String phoneNumber = c.getString(numberIndex);

                        //  c.close();
                        setname_Number(name, phoneNumber);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void DialogInvitationApiCall(String Name, String Phone, String CountryCode, String
            Email) {
        String to_ph = Phone.replaceAll("\\s", "");
        String Names = Name;
        // new CallWebService(getActivity(), cmf.urlList.request_to_join, cmf.request_to_join(from_ph, to_ph), new MyServiceListener() {
        new CallWebService(getActivity(), cmf.urlList.add_family_on_group,
                cmf.add_family_on_group(AspnetUserID, family_id, to_ph), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {

                try {
                    JSONObject jsonObject = new JSONObject(string);
                    String Status = jsonObject.optString("success");
                    String Message = jsonObject.optString("message");
                    if (Status.equals("1")) {
                        // canceljob.dismiss();
                        Toast.makeText(getActivity(), Message, Toast.LENGTH_SHORT).show();
                        TVName.setText("");
                         TVPhone.setText("");
                          TVName.setText("");
                        // SentFriendApiCall();
                    } else {
                        Toast.makeText(getActivity(), Message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
            }
        });
    }

    public void setname_Number(String name, String mobilenumber) {
        // remove white space..
        String phn = mobilenumber.replaceAll("\\s+", "");

        TVPhone.setText("" + phoneValidate(phn));
        TVName.setText(name);
    }

    private String phoneValidate(final String phn) {

        String pn = "";
        try {
            if (phn.contains("+91")) {
                pn = phn.replace("+91", "");
            } else if (phn.contains("+1")) {
                pn = phn.replace("+1", "");
            } else if (phn.contains("(")) {
                pn = phn.replaceAll("[()]", "");
                pn = pn.replaceAll("-", "");
            } else {
                pn = phn;
            }
        } catch (Exception ex) {
            pn = phn;
        }

        return pn;
    }


    private void MyFriendsApiCall() {
        mprogressBar.setVisibility(View.VISIBLE);
        joinSafeJoinModels1 = new ArrayList<>();
        joinSafeJoinModels1.clear();
        RVFriendsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        myFriendAdapterList = new MyFriendAdapterList(joinSafeJoinModels1, getActivity());
        new CallWebService("", getActivity(), cmf.urlList.family_group_info,
                cmf.family_group_info(AspnetUserID,""), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    mSwipeRefreshLayout.setRefreshing(false);
                    request = 0;
                    mprogressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(string);
                    String Status = jsonObject.optString("success");
                    if (Status.equals("1")) {
                        family_id = jsonObject.optString("family_id");
                        family_name = jsonObject.optString("family_name");
                        IsFamilyAdmin = jsonObject.optString("isFamilyAdmin");

                         cmf.myPreference.setString(getActivity(), cmf.gc.FAMILY_ID,family_id);
                        cmf.myPreference.setString(getActivity(), cmf.gc.FAMILY_NAME,family_name);
                         cmf.myPreference.setString(getActivity(), cmf.gc.IsFamilyAdmin,IsFamilyAdmin);
                        validatefamilyInfo();

                        JSONArray jsonArrayRequest = jsonObject.optJSONArray("request_list");
                        JSONArray jsonArray = jsonObject.optJSONArray("freind_list");
                        //------------------------------------------------
                        //means no owner but have request.
                        if (jsonArray.length() == 0) {
                            for (int j = 0; j < jsonArrayRequest.length(); j++) {
                                JSONObject jsonObjectRequest = jsonArrayRequest.optJSONObject(j);
                                String user_id = jsonObjectRequest.optString("user_id");
                                String user_name = jsonObjectRequest.optString("user_name");
                                String user_mobile = jsonObjectRequest.optString("user_mobile");
                                String user_image = jsonObjectRequest.optString("user_img");
                                String online_status = jsonObjectRequest.optString("status");
                                String address = jsonObjectRequest.optString("address");
                                String time = jsonObjectRequest.optString("time");
                                String request_type = "freind_request";
                                String member_status = jsonObjectRequest.optString("member_status");
                                String lat = jsonObjectRequest.optString("lat");
                                String lng = jsonObjectRequest.optString("lng");


                                joinSafeJoinModel1 = new JoinSafeJoinModel();

                                joinSafeJoinModel1.setUser_id(user_id);
                                joinSafeJoinModel1.setUser_name(user_name);
                                joinSafeJoinModel1.setUser_image(user_image);
                                joinSafeJoinModel1.setUser_mobile(user_mobile);
                                joinSafeJoinModel1.setOnlineStatus(online_status);
                                joinSafeJoinModel1.setAddress(address);
                                joinSafeJoinModel1.setTime(time);
                                joinSafeJoinModel1.setRequest_type(request_type);
                                joinSafeJoinModel1.setMember_status(member_status);
                                joinSafeJoinModel1.setLat(lat);
                                joinSafeJoinModel1.setLng(lng);
                                joinSafeJoinModel1.setFamily_id(jsonObjectRequest.optString("family_id"));
                                joinSafeJoinModel1.setFamily_name(jsonObjectRequest.optString("family_name"));

                                joinSafeJoinModels1.add(joinSafeJoinModel1);
                            }
                        }
                        //------------------------------------------------------------

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            //----------------------
                            //means no owner existing..with other family request...
                            String user_id = jsonObject1.optString("user_id");
                            String user_name = jsonObject1.optString("user_name");
                            String user_mobile = jsonObject1.optString("user_mob");
                            String user_image = jsonObject1.optString("user_img");
                            String online_status = jsonObject1.optString("status");
                            String address = jsonObject1.optString("address");
                            String time = jsonObject1.optString("time");
                            String request_type = jsonObject1.optString("type");
                            String member_status = jsonObject1.optString("member_status");


                            joinSafeJoinModel1 = new JoinSafeJoinModel();

                            joinSafeJoinModel1.setUser_id(user_id);
                            joinSafeJoinModel1.setUser_name(user_name);
                            joinSafeJoinModel1.setUser_image(user_image);
                            joinSafeJoinModel1.setUser_mobile(user_mobile);
                            joinSafeJoinModel1.setOnlineStatus(online_status);
                            joinSafeJoinModel1.setAddress(address);
                            joinSafeJoinModel1.setTime(time);
                            joinSafeJoinModel1.setRequest_type(request_type);
                            joinSafeJoinModel1.setMember_status(member_status);
                            joinSafeJoinModel1.setFamily_id(family_id);
                            joinSafeJoinModel1.setFamily_name(family_name);

                            joinSafeJoinModels1.add(joinSafeJoinModel1);

                            //------------------------------------------------------------------
                            if (i == 0) {
                                //below owner will show request...
                                for (int j = 0; j < jsonArrayRequest.length(); j++) {
                                    JSONObject jsonObjectRequest = jsonArrayRequest.optJSONObject(j);
                                    user_id = jsonObjectRequest.optString("user_id");
                                    user_name = jsonObjectRequest.optString("user_name");
                                    user_mobile = jsonObjectRequest.optString("user_mobile");
                                    user_image = jsonObjectRequest.optString("user_img");
                                    online_status = jsonObjectRequest.optString("status");
                                    address = jsonObjectRequest.optString("address");
                                    time = jsonObjectRequest.optString("time");
                                    request_type = "freind_request";
                                    member_status = jsonObjectRequest.optString("member_status");


                                    joinSafeJoinModel1 = new JoinSafeJoinModel();

                                    joinSafeJoinModel1.setUser_id(user_id);
                                    joinSafeJoinModel1.setUser_name(user_name);
                                    joinSafeJoinModel1.setUser_image(user_image);
                                    joinSafeJoinModel1.setUser_mobile(user_mobile);
                                    joinSafeJoinModel1.setOnlineStatus(online_status);
                                    joinSafeJoinModel1.setAddress(address);
                                    joinSafeJoinModel1.setTime(time);
                                    joinSafeJoinModel1.setRequest_type(request_type);
                                    joinSafeJoinModel1.setMember_status(member_status);
                                    joinSafeJoinModel1.setFamily_id(jsonObjectRequest.optString("family_id"));
                                    joinSafeJoinModel1.setFamily_name(jsonObjectRequest.optString("family_name"));

                                    joinSafeJoinModels1.add(joinSafeJoinModel1);
                                }
                            }
                            //---------------------------------------------------------------------

                        }
                        //----------
                        myFriendAdapterList = new MyFriendAdapterList(joinSafeJoinModels1, getActivity());
                        RVFriendsList.setAdapter(myFriendAdapterList);
                        myFriendAdapterList.notifyDataSetChanged();
                    } else {
                        //  allNotificationLinearLayout.setVisibility(View.VISIBLE);
                        RVFriendsList.setVisibility(View.GONE);
                    }
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
                mSwipeRefreshLayout.setRefreshing(false);
                request = 0;
                mprogressBar.setVisibility(View.GONE);
            }
        });

    }
}
