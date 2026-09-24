package com.omsworld.familycare.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.omsworld.familycare.api_call.MyPlayer;
import com.omsworld.familycare.api_call.MyServiceListener;
import com.omsworld.familycare.api_call.UrlList;

import org.json.JSONObject;


/**
 * Created by rupesh.m on 4/26/2017.
 */

public class Fragment_settings extends Fragment {
    private View rootView;
    private Switch OnOff;
    private Switch MyTrackingOnOff;
    private String IsAlarmMute, IsFriendsTracking;
    private CommonFunctions commonFunctions;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.settings_layout, container, false);
        commonFunctions = new CommonFunctions(getActivity());
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initilize();

        OnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final MyPlayer mp = new MyPlayer();
                if (OnOff.isChecked()) {
                    IsAlarmMute = "1";
                    mp.mp_start(getActivity());
                    AlarmAPICALl();
                } else {
                    IsAlarmMute = "0";
                    mp.mp_stop(getActivity());
                    AlarmAPICALl();
                }
            }
        });
        //-----------------
        MyTrackingOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (MyTrackingOnOff.isChecked()) {
                    IsFriendsTracking = "1";
                    AlarmAPICALl();
                } else {
                    IsFriendsTracking = "0";
                    AlarmAPICALl();
                }
            }
        });
    }

    private void initilize() {
        OnOff = (Switch) rootView.findViewById(R.id.OnOff);
        MyTrackingOnOff = (Switch) rootView.findViewById(R.id.MyTrackingOnOff);

        UpdateStatus();
    }

    private void AlarmAPICALl() {
        String AspnetUserID = commonFunctions.myPreference.getString(getActivity(), GlobalConstants.ASPNETUSERID);
        new CallWebService(getActivity(), UrlList.ALARMONOFFREQUEST, commonFunctions.AlarmOnOff(AspnetUserID, IsAlarmMute, IsFriendsTracking), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                Log.e("------->ALARMAPI-->" + string, "");

                try {
                    JSONObject jsonObject = new JSONObject(string);
                    int Status = jsonObject.optInt("Status");
                    String Message = jsonObject.optString("Message");
                    if (Status == 1) {
                        commonFunctions.myPreference.setString(getActivity(), GlobalConstants.ALARM_MUTE_STATUS, IsAlarmMute);
                        commonFunctions.myPreference.setString(getActivity(), GlobalConstants.ISFRIENDSTRACKING, IsFriendsTracking);

                    } else {
                        Toast.makeText(getActivity(), Message, Toast.LENGTH_SHORT).show();
                    }
                    UpdateStatus();

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }

    //----------------OnOff UPDATE---
    private void UpdateStatus() {
        IsAlarmMute = commonFunctions.myPreference.getString(getActivity(), GlobalConstants.ALARM_MUTE_STATUS);
        IsFriendsTracking = commonFunctions.myPreference.getString(getActivity(), GlobalConstants.ISFRIENDSTRACKING);
        if (IsAlarmMute.equals("1")) {
            OnOff.setChecked(true);
        } else if (IsAlarmMute.equals("0")) {
            OnOff.setChecked(false);
        }
        //------------------------------------
        if (IsFriendsTracking.equals("1")) {
            MyTrackingOnOff.setChecked(true);
        } else if (IsFriendsTracking.equals("0")) {
            MyTrackingOnOff.setChecked(false);
        }
    }

}

