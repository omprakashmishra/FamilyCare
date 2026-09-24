package com.omsworld.familycare.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.omsworld.familycare.ClickListener.RecyclerItemClickListener;
import com.omsworld.familycare.ServicesControl.CollegeJobService;
import com.omsworld.familycare.R;
import com.omsworld.familycare.activity.Gps_alert;
import com.omsworld.familycare.adapter.FriendsTrackingAdapter;
import com.omsworld.familycare.adapter.WhoIsTrackingMeAdapter;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.omsworld.familycare.api_call.MyServiceListener;
import com.omsworld.familycare.model.FriendsTrackingModel;
import com.omsworld.familycare.model.WhoIsTrackingMeModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyTrackingFr extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private static Double newlattitude, newlongitude, guardlatitude, guardlongitude;
    private static String newlat, newlong, guardlat, guardlong;
    private static LatLng needy_location, userselectedlocation;
    int mapheight = 370;
    private Marker mymarker;
    private GoogleMap GM_info;

    private CommonFunctions cmf;
    private String newlat2, newlong2, guardlat2, guardlong2;
    private String myCurrentAddress, friendCurrentAddress;
    private String myName, friendName;
    private ImageView IV_IamHere, IV_WhoIsTrackingMe, IV_updown;
    private int isFriendSelected = 0, isFriendZooming = 0, clickedTrackingPossiton;
    private ArrayList<FriendsTrackingModel> arraylistTracking = new ArrayList<>();
    private FriendsTrackingAdapter friendsTrackingAdapter;
    private FriendsTrackingModel friendsTrackingModel;
    //-------------------------------------------
    private View rootView;
    private ScrollView mScrollView;
    private TextView TV_userTracking, TV_accuracy;
    private RecyclerView rv_TrackingFriends;
    private ProgressBar progressBar;
    private RelativeLayout RL_map_zoom_inout;
    private SupportMapFragment mapFragment;
    //-----------
    private String AspnetUserId;

    public static BitmapDescriptor createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);

        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.my_tracking_fr, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cmf = new CommonFunctions(getActivity());
        Initview();
    }

    private void Initview() {

        getActivity().startService(new Intent(getActivity(), CollegeJobService.class));

        mScrollView = (ScrollView) rootView.findViewById(R.id.sv_container);
        ((MapScrollableFragment) (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.Info_Map)).setListener(new MapScrollableFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                mScrollView.requestDisallowInterceptTouchEvent(true);
            }
        });

        mapFragment = ((MapScrollableFragment) (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.Info_Map));
        mapFragment.getMapAsync(this);


        //---- clear service respose
/*        Intent intent = new Intent(getActivity(), CollegeJobService.class);
        intent.putExtra(cmf.gc.fromPage, "MyTrackingFr");
        getActivity().startService(intent);*/
        cmf.myPreference.setString(getActivity(), "ReadResponse", "1");
        AspnetUserId = cmf.myPreference.getString(getActivity(), cmf.gc.ASPNETUSERID);
        //-------------
        TV_accuracy = (TextView) rootView.findViewById(R.id.TV_accuracy);
        // RLInfoMap = (RelativeLayout) rootView.findViewById(R.id.RL_Info_Map);
        progressBar = (ProgressBar) rootView.findViewById(R.id.mprogressBar);
        IV_IamHere = (ImageView) rootView.findViewById(R.id.IV_IamHere);
        IV_WhoIsTrackingMe = (ImageView) rootView.findViewById(R.id.IV_WhoIsTrackingMe);
        IV_updown = (ImageView) rootView.findViewById(R.id.IV_updown);
        RL_map_zoom_inout = (RelativeLayout) rootView.findViewById(R.id.RL_map_zoom_inout);
        myName = cmf.myPreference.getString(getActivity(), cmf.gc.USER_NAME);
        TV_userTracking = (TextView) rootView.findViewById(R.id.TV_userTracking);
        TV_userTracking.setText("Currently Tracking My Location");
        rv_TrackingFriends = (RecyclerView) rootView.findViewById(R.id.rv_TrackingFriends);

        IV_IamHere.setOnClickListener(this);
        IV_WhoIsTrackingMe.setOnClickListener(this);
        IV_updown.setOnClickListener(this);
        RL_map_zoom_inout.setOnClickListener(this);

        rv_TrackingFriends.setLayoutManager(new LinearLayoutManager(getActivity()));

        rv_TrackingFriends.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rv_TrackingFriends, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (arraylistTracking.size() > 0) {

                        clickedTrackingPossiton = position;
                        isFriendSelected = 1;
                        isFriendZooming = 1;

                        String name = arraylistTracking.get(position).getUserName();
                        //set icon on map...
                        String CurrentSLat = arraylistTracking.get(position).getLatitude();
                        String CurrentCSLong = arraylistTracking.get(position).getLongitude();
                        String Address = arraylistTracking.get(position).getAddress();
                        TV_userTracking.setText("Currently Tracking " + name);

                        userselectedlocation = new LatLng(Double.parseDouble(CurrentSLat), Double.parseDouble(CurrentCSLong));

                        ZoomOnMapNG("guard", 17.0f);

                        //  request4TrackOther(arraylistTracking.get(position).getAspnetUserID());
                    }
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));
    }

    private boolean getFromGpsTracker() {
        new Gps_alert(getActivity());
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GM_info = googleMap;
        GM_info.getUiSettings().setMapToolbarEnabled(false);
        getFromGpsTracker();
        try {
            newlat = cmf.myPreference.getString(getActivity(), cmf.gc.JOB_NEW_LATITUDE);
            newlong = cmf.myPreference.getString(getActivity(), cmf.gc.JOB_NEW_LONGITUDE);
            needy_location = new LatLng(Double.valueOf(newlat), Double.valueOf(newlong));
            myMarker(1, needy_location);
        } catch (Exception rx) {
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.IV_IamHere:
                isFriendSelected = 0;
                isFriendZooming = 0;

                ZoomOnMapNG("needy", 17.0f);
                TV_userTracking.setText("Currently Tracking My Location");
                break;
            case R.id.IV_updown:
                mapZoomInOut();
                break;
            case R.id.RL_map_zoom_inout:
                mapZoomInOut();
                break;
            case R.id.IV_WhoIsTrackingMe:
                //  whoIsTrackingMe();
                break;
        }
    }

    //--------------------------------------------------------------------------------------TimerForUpdateUI....
    String ReadResponse = "";
    public void forJobpompleteUpdate(final int stop) {

       // cmf = new CommonFunctions(getActivity());


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                handler.postDelayed(this, 3000);
                if (stop == 5) {
                    handler.removeCallbacks(this);
                    return;
                }

                //Log.d("----------------->fragment FRIENDS TARCKING ", "running...");
                try {
                    //--------------2. WHEN RESPONSE HAS CHANGED..
                    ReadResponse = cmf.myPreference.getString(getActivity(), "ReadResponse");
                    if (ReadResponse.equals("1")) {
                        cmf.myPreference.setString(getActivity(), "ReadResponse", "0");
                        if (cmf.service_response().equals(""))
                            clearAllList();
                        else
                            SOS_request_recyclerview(cmf.service_response());
                    }
                    //--------------1. FOR YOUR LOCATION CHANGE..
                    setMyLatLong();
                    //--------------3. FOR ZOOM WITCH ONE IS SELECTED..
                    setFriendsLatLong();

                } catch (NullPointerException ex) {
                } catch (Exception ex) {
                }
                //-----------------------
            }
        }, 3000);

    }

    private void setMyLatLong() {
        //-----------------when needy has changed location...
        newlat = cmf.myPreference.getString(getActivity(), cmf.gc.JOB_NEW_LATITUDE);
        newlong = cmf.myPreference.getString(getActivity(), cmf.gc.JOB_NEW_LONGITUDE);
        if (!newlat.equals(newlat2) || !newlong.equals(newlong2)) {
            Log.e("---->latlong same", "Inserted");
            // Toast.makeText(getApplicationContext(), "N---Inserted", Toast.LENGTH_SHORT).show();

            newlat2 = newlat;
            newlong2 = newlong;
            if (!newlat.isEmpty()) {
                newlattitude = Double.valueOf(newlat);
                newlongitude = Double.valueOf(newlong);
                needy_location = new LatLng(newlattitude, newlongitude);
                //----------------
                TV_accuracy.setText("Accuracy  " + CollegeJobService.currentAccuracy + " %");
                //------------------
                myCurrentAddress = cmf.myPreference.getString(getActivity(), cmf.gc.JOB_CurrentAddress);
                if (isFriendZooming == 0) {
                    if (mymarker == null)
                        myMarker(1, needy_location);
                    else
                        ZoomOnMapNG("needy", 17.0f);
                }
                //   animateMarker(mymarker, needy_location, false);
                //marker value set
                mymarker.setPosition(needy_location);
                mymarker.setSnippet(myCurrentAddress);
            }
        }
    }

    private void setFriendsLatLong() {
        if (isFriendSelected == 1 && arraylistTracking.size() > 0) {
            guardlat = arraylistTracking.get(clickedTrackingPossiton).getLatitude();
            guardlong = arraylistTracking.get(clickedTrackingPossiton).getLongitude();

            if (!guardlat.equals(guardlat2) || !guardlong.equals(guardlong2)) {
                Log.e("---->G latlong same", "Inserted");
                guardlat2 = guardlat;
                guardlong2 = guardlong;
                if (!newlat.isEmpty()) {
                    guardlatitude = Double.valueOf(guardlat);
                    guardlongitude = Double.valueOf(guardlong);

                    userselectedlocation = new LatLng(guardlatitude, guardlongitude);
                    friendName = arraylistTracking.get(clickedTrackingPossiton).getUserName();
                    friendCurrentAddress = arraylistTracking.get(clickedTrackingPossiton).getAddress();

                    // tv_otherAddress.setText(friendCurrentAddress);

                    //=---------------
                    if (isFriendZooming == 1) {
                        ZoomOnMapNG("guard", 17.0f);
                    }
                    //----------------

                }
            }
        }
    }

    private void myMarker(int zoom, LatLng needy_location) {
        if (GM_info != null) {
            //GM_info.addMarker(new MarkerOptions().position(needy_location).title("Needy Om").snippet("").visible(true)).setIcon(icon);
            if (mymarker != null) {
                mymarker.remove();
            }
            mymarker = GM_info.addMarker(new MarkerOptions().position(needy_location).title(myName).snippet(myCurrentAddress).visible(true));
            mymarker.setIcon(mapicon(R.drawable.needy_marker));
            if (zoom == 1) {
                ZoomOnMapNG("needy", 17.0f);
            }
        }
    }

    private void ZoomOnMapNG(String ZoomFor, float ZoomLevel) {
        try {
            if (ZoomFor.equals("needy")) {
                if (mymarker == null)
                    myMarker(1, needy_location);
                else
                    GM_info.animateCamera(CameraUpdateFactory.newLatLngZoom(needy_location, ZoomLevel));

            } else {
                GM_info.animateCamera(CameraUpdateFactory.newLatLngZoom(userselectedlocation, ZoomLevel));
            }

        } catch (Exception ex) {
        }

    }

    //-----------------------SET FRIENDS DATA
    private void SOS_request_recyclerview(String response) {
        clearAllList();
        try {
            progressBar.setVisibility(View.GONE);
            JSONObject jsonObject = new JSONObject(response);


           /* //---------------------------------------SOME One tracking me or not..
            String WhoIsTrackingMe_count = jsonObject.optString("whoIsTrackingMe");
            //  WhoIsTrackingMe_count="0";
            if (WhoIsTrackingMe_count.equals("0") || WhoIsTrackingMe_count.isEmpty() || WhoIsTrackingMe_count.equals("")) {
                IV_WhoIsTrackingMe.setVisibility(View.GONE);
            } else {
                IV_WhoIsTrackingMe.setVisibility(View.VISIBLE);
            }*/


            //----------------------------------------------------------Me Data
            JSONArray jsonArray = jsonObject.optJSONArray("freind_list");
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                    friendsTrackingModel = new FriendsTrackingModel();
                    friendsTrackingModel.setAspnetUserID(jsonObject1.optString("user_id"));
                    friendsTrackingModel.setUserName(jsonObject1.optString("user_name"));
                    friendsTrackingModel.setMobileNumber(jsonObject1.optString("user_mob"));
                    friendsTrackingModel.setLatitude(jsonObject1.optString("lat"));
                    friendsTrackingModel.setLongitude(jsonObject1.optString("lng"));
                    friendsTrackingModel.setAddress(jsonObject1.optString("address"));
                    friendsTrackingModel.setBattery(jsonObject1.optString("battery"));
                    friendsTrackingModel.setOnlineStatus(jsonObject1.optString("status"));
                    friendsTrackingModel.setLastSeen(jsonObject1.optString("time"));
                    friendsTrackingModel.setUser_img("http://108.170.54.215/App_development/Tracking/profileimage/" + jsonObject1.optString("user_img"));
                    //  friendsTrackingModel.setIsTrackOnOff(jsonObject1.optString("IsTrackOnOff"));

                    arraylistTracking.add(friendsTrackingModel);
                    // create marker...
                    createMarker(new LatLng(Double.parseDouble(jsonObject1.optString("lat")), Double.parseDouble(jsonObject1.optString("lng"))), jsonObject1.optString("user_name"), jsonObject1.optString("address"));
                    /*FriendsMarkerData md = new FriendsMarkerData();
                    md.setTitle(jsonObject1.optString("UserName"));
                    md.setLatLng(new LatLng(Double.parseDouble(jsonObject1.optString("Latitude")), Double.parseDouble(jsonObject1.optString("Longitude"))));
                    md.setSnippet(jsonObject1.optString("Address"));
                    markersArray.add(md);*/
                }
                if (arraylistTracking.size() > 0) {

                    friendsTrackingAdapter = new FriendsTrackingAdapter(getActivity(), arraylistTracking);
                    rv_TrackingFriends.setAdapter(friendsTrackingAdapter);
                    friendsTrackingAdapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected void createMarker(LatLng ltlng, String title, String snippet) {
        final Marker mrkr = GM_info.addMarker(new MarkerOptions().position(ltlng).title(title).snippet(snippet).visible(true));

        final View viewMarker = ((LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.marker_custom, null);
        final TextView tvTitulo = (TextView) viewMarker.findViewById(R.id.teid);
        tvTitulo.setText(title);

         /*  View markerview=getActivity().getLayoutInflater().inflate(R.layout.marker_custom,null);
        TextView tvTitulo = (TextView) markerview.findViewById(R.id.teid);
        tvTitulo.setText("OM");*/

        mrkr.setIcon(createDrawableFromView(getActivity(), viewMarker));
    }

    private void clearAllList() {

        arraylistTracking.clear();
        try {
            if (GM_info != null) {
                GM_info.clear();
            }
            myMarker(0, needy_location);
        } catch (Exception ex) {

        }

        friendsTrackingAdapter = new FriendsTrackingAdapter(getActivity(), arraylistTracking);

        friendsTrackingAdapter.notifyDataSetChanged();

        rv_TrackingFriends.setAdapter(friendsTrackingAdapter);
    }

    private BitmapDescriptor mapicon(int markerImg) {
        final Drawable circle = getResources().getDrawable(markerImg);
        final Canvas canvas = new Canvas();
        final Bitmap bitmap = Bitmap.createBitmap(circle.getIntrinsicWidth(), circle.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        circle.setBounds(5, 5, circle.getIntrinsicWidth(), circle.getIntrinsicHeight());
        circle.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void mapZoomInOut() {
        final ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
        final View mapView = mapFragment.getView();
        final int height = mapView.getMeasuredHeight();
        if (mapheight == 280) {
            mapheight = 0;
            params.height = height - 280;
        } else {
            mapheight = 280;
            params.height = height + mapheight;
        }

        mapFragment.getView().setLayoutParams(params);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            //------intial time last saved latLong.
            forJobpompleteUpdate(0);

            newlat = cmf.myPreference.getString(getActivity(), cmf.gc.JOB_NEW_LATITUDE);
            newlong = cmf.myPreference.getString(getActivity(), cmf.gc.JOB_NEW_LONGITUDE);
            needy_location = new LatLng(Double.valueOf(newlat), Double.valueOf(newlong));
            myMarker(1, needy_location);
        } catch (Exception ex) {
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        forJobpompleteUpdate(5);
        //  frcall_handler.removeCallbacksAndMessages(null);
        if (!cmf.myPreference.getString(getActivity(), GlobalConstants.safeJone).equals("1")) {
            getActivity().stopService(new Intent(getActivity(), CollegeJobService.class));
        }

    }


    //-----------------------------------
    private void whoIsTrackingMe() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.who_is_helping_me_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final ListView LV_helping_me = (ListView) dialog.findViewById(R.id.LV_helping_me);

        //-----------------------------------
        progressBar.setVisibility(View.VISIBLE);
        final ArrayList<WhoIsTrackingMeModel> arraylist = new ArrayList<>();
        final WhoIsTrackingMeAdapter whoIsTrackingMeAdapter = new WhoIsTrackingMeAdapter(getActivity(), arraylist);
        new CallWebService("", getActivity(), cmf.urlList.whoIsTrackingMe, cmf.AspnetUserID(AspnetUserId), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {

                progressBar.setVisibility(View.GONE);
                dialog.show();
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    int Status = jsonObject.optInt("Status");
                    // String Message = jsonObject.optString("Message");
                    if (Status == 1) {

                        JSONArray jsonArray = jsonObject.optJSONArray("Data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            final WhoIsTrackingMeModel tck = new WhoIsTrackingMeModel();
                            final String istrackMe = jsonObject1.optString("istrackMe");
                            if (istrackMe.equals("1")) {
                                String Name = jsonObject1.optString("UserName");
                                String AspnetUserId = jsonObject1.optString("AspnetUserId");
                                tck.setName(Name);
                                tck.setAspnetUserId(AspnetUserId);
                                tck.setIstrackMe(istrackMe);

                                arraylist.add(tck);
                            }


                        }
                        //set and notifiy change
                        LV_helping_me.setAdapter(whoIsTrackingMeAdapter);
                        whoIsTrackingMeAdapter.notifyDataSetChanged();

                    } else {
                        //   Toast.makeText(getActivity(), Message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
            }
        });
        //---------------------------API Call end---


    }


    //----------------------------------CLOSED SCOPE--------------
}
