package com.omsworld.familycare.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omsworld.familycare.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by rupesh.m on 12/17/2016.
 */

public class Emergency_Fr extends Fragment {
    @Bind(R.id.Tv_police)
    TextView TvPolice;
    @Bind(R.id.LL_police)
    LinearLayout LLPolice;
    @Bind(R.id.Tv_fire)
    TextView TvFire;
    @Bind(R.id.LL_fire)
    LinearLayout LLFire;
    @Bind(R.id.Tv_ambulace)
    TextView TvAmbulace;
    @Bind(R.id.LL_ambulace)
    LinearLayout LLAmbulace;
    @Bind(R.id.TV_women)
    TextView TVWomen;
    @Bind(R.id.LL_women)
    LinearLayout LLWomen;
    @Bind(R.id.Tv_child)
    TextView TvChild;
    @Bind(R.id.LL_child)
    LinearLayout LLChild;
    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.emergency_no_fr, container, false);
        initilize();
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    private void initilize() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.LL_police, R.id.LL_fire, R.id.LL_ambulace, R.id.LL_women, R.id.LL_child})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.LL_police:
                makeCallNow(TvPolice.getText().toString());
                break;
            case R.id.LL_fire:
                makeCallNow(TvFire.getText().toString());
                break;
            case R.id.LL_ambulace:
                makeCallNow(TvAmbulace.getText().toString());
                break;
            case R.id.LL_women:
                makeCallNow(TVWomen.getText().toString());
                break;
            case R.id.LL_child:
                makeCallNow(TvChild.getText().toString());
                break;
        }
    }

    private void makeCallNow(String phn) {
        final Intent intent = new Intent(Intent.ACTION_DIAL);
        phn.trim();
        //CallPhone = tollfree.replaceAll("\\D+", "");
        intent.setData(Uri.parse("tel:" + phn));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
        }
    }
}

