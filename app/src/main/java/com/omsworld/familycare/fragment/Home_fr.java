package com.omsworld.familycare.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.omsworld.familycare.Diary.Diary_fr;
import com.omsworld.familycare.ServicesControl.CollegeJobService;
import com.omsworld.familycare.R;
import com.omsworld.familycare.SharedContacts.Sharedcontacts_fr;
import com.omsworld.familycare.Shopping.InWeb.ShoppingSitesList_Fr;
import com.omsworld.familycare.Shopping.ShoppingMain_Ac;
import com.omsworld.familycare.activity.Gps_alert;
import com.omsworld.familycare.activity.MainActivity;
import com.omsworld.familycare.activity.TrackGPS;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class Home_fr extends Fragment {
    @Bind(R.id.CIV_profileDp)
    CircleImageView CIVProfileDp;
    @Bind(R.id.TV_name)
    TextView TVName;
    @Bind(R.id.TV_roleName)
    TextView TVRoleName;


    @Bind(R.id.TV_org)
    TextView TVOrg;
    CommonFunctions cmf;
    ProgressBar mprogressBar;
    @Bind(R.id.IV_gifimg)
    ImageView IVGifimg;
    @Bind(R.id.SW_safeJone)
    Switch SWSafeJone;


    private View rootView;
    //---------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_fr, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cmf = new CommonFunctions(getActivity());
        Intview();
    }

    private void Intview() {
        mprogressBar = (ProgressBar) rootView.findViewById(R.id.mprogressBar);
        String username = cmf.myPreference.getString(getActivity(), GlobalConstants.USER_NAME);
        String user_mobile = cmf.myPreference.getString(getActivity(), GlobalConstants.MOBILE_only);

        TVName.setText(username);
        TVRoleName.setText(user_mobile);
        // TVOrg.setText(user_image);
        TVOrg.setVisibility(View.GONE);

        setInView();


        //------------------------SW_safeJone


        SWSafeJone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cmf.myPreference.setString(getActivity(), GlobalConstants.safeJone, "1");
                    getActivity().startService(new Intent(getActivity(), CollegeJobService.class));
                    getFromGpsTracker();
                } else {
                    cmf.myPreference.setString(getActivity(), GlobalConstants.safeJone, "0");
                    getActivity().stopService(new Intent(getActivity(), CollegeJobService.class));
                }
            }
        });
    }

    private boolean getFromGpsTracker() {
        final TrackGPS gps = new TrackGPS(getActivity());
        if (gps.canGetLocation()) {
            gps.stopUsingGPS();
            return true;
        } else {
            // gps.showSettingsAlert();
            new Gps_alert(getActivity());
            return false;
        }
    }

    private void setInView() {
        try {
            String user_image = cmf.myPreference.getString(getActivity(), GlobalConstants.USER_IMAGE);
            Picasso.with(getActivity()).load(user_image).placeholder(R.drawable.teacher_fml).error(R.drawable.teacher_fml).into(CIVProfileDp, new Callback() {
                @Override
                public void onSuccess() {
                    mprogressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    mprogressBar.setVisibility(View.GONE);
                }
            });
            Glide.with(this).load(R.drawable.with_friends).into(IVGifimg);
        } catch (Exception ex) {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cmf.myPreference.getString(getActivity(), GlobalConstants.safeJone).equals("1")) {
            SWSafeJone.setChecked(true);
        } else {
            SWSafeJone.setChecked(false);
        }
    }

    @OnClick({R.id.CIV_profileDp, R.id.RL_addFamily, R.id.RL_location, R.id.RL_post, R.id.RL_diary, R.id.RL_newsNoti_Ttl,
            R.id.RL_curentdate, R.id.RL_Help, R.id.RL_manageProfile, R.id.RL_shopping, R.id.IV_info})
    public void onClick(View view) {
        cmf = new CommonFunctions(getActivity());
        switch (view.getId()) {
            case R.id.CIV_profileDp:
                MainActivity.toolbar.setTitle("Profile");
                cmf.replaceFragment(getActivity(), new Profile_Fr());

                break;
            case R.id.RL_addFamily:
               /* Fragment_InvitationFriends fragment = new Fragment_InvitationFriends();
                cmf.bdl.putString(cmf.gc.fromPage, "invitation");
                fragment.setArguments(cmf.bdl);*/
                FamilyMembers_Fr fragment = new FamilyMembers_Fr();
                MainActivity.toolbar.setTitle("Manage Your Family");
                cmf.replaceFragment(getActivity(), fragment);

                break;
            case R.id.RL_location:

                MainActivity.toolbar.setTitle("Family Location");
                cmf.replaceFragment(getActivity(), new MyTrackingFr());
                break;
            case R.id.RL_post:
                MainActivity.toolbar.setTitle("Phone Book Shared");
                cmf.replaceFragment(getActivity(), new Sharedcontacts_fr());
                break;
            case R.id.RL_diary:
                MainActivity.toolbar.setTitle("My Diary");
                cmf.replaceFragment(getActivity(), new Diary_fr());
                // AppUtil.startActivityWithAnimation(getActivity(), new Intent(getActivity(), NoteList.class));
                break;
            case R.id.RL_newsNoti_Ttl:
                MainActivity.toolbar.setTitle("Health/Other  News");
                cmf.replaceFragment(getActivity(), new NewsEvents_Fr());
                break;
            case R.id.RL_curentdate:
                Toast.makeText(getActivity(), "Coming soon with your family home DoorBell", Toast.LENGTH_LONG).show();
                break;
            case R.id.RL_Help:
                MainActivity.toolbar.setTitle("All In One");
                // cmf.replaceFragment(getActivity(),new Fragment_contactus(),"Fragment_contactus");
                cmf.replaceFragment(getActivity(), new ShoppingSitesList_Fr());
                break;
            case R.id.RL_manageProfile:
                MainActivity.toolbar.setTitle("Profile");
                cmf.replaceFragment(getActivity(), new Profile_Fr());
                break;
            case R.id.RL_shopping:
                MainActivity.toolbar.setTitle("Shop");
                startActivity(new Intent(getActivity(), ShoppingMain_Ac.class));
                //  cmf.replaceFragment(new ShoppingSitesList_Fr());
                break;
            case R.id.IV_info:
                Toast.makeText(getActivity(), "When You Turn On Safe Jone Then Your Family Always Connected With You In Map.", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
