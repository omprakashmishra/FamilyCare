package com.omsworld.familycare.ServicesControl;

/**
 * Created by omprakash.m on 1/19/2017.
 */

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.MyPlayer;
import com.omsworld.familycare.api_call.MyServiceListener;

import org.json.JSONObject;


public class CollegeJobService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static String currentAccuracy = "0.0";
    static boolean ApIcall = true;
    private static int battryCheckInMinut = 0;
    final Handler ServiceHandler = new Handler();
    public double Final_Lat = 0, Final_Long = 0;
    public int firstTimelocation = 0;
    public String currentAdress;
    public double newlattitude = 0, newlongitude = 0, newlattitude2, newlongitude2;
    public float DistanceForLT_LG = 0;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    Integer Count = 0;
    String newaccuracy, newprovider;
    private String AspnetUserId,family_id;
    private int stopService = 0;
    private String btry = "40";
    private CommonFunctions cmf;
    private Runnable rnable;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        //minimum time lat long change via fused..
        mLocationRequest.setInterval(5500);
        // mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void onCreate() {
        super.onCreate();
        Initservice();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void Initservice() {
        Log.e("---------------********************>", "Service Started");
        cmf = new CommonFunctions(this);
        if (!isGooglePlayServicesAvailable()) {
            // Toast.makeText(getApplicationContext(), "Google play service not available please turn on", Toast.LENGTH_SHORT).show();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        //--------------------------------------------->API Hit interval with lat long...
        AspnetUserId = cmf.myPreference.getString(CollegeJobService.this, cmf.gc.USER_ID);
        family_id = cmf.myPreference.getString(CollegeJobService.this, cmf.gc.FAMILY_ID);
        stopService = 0;

        initializeTimerTask();

        cmf.myPreference.setString(CollegeJobService.this, "ReadResponse", "0");
        cmf.myPreference.setString(CollegeJobService.this, cmf.gc.RESPOSE_CLG_JB_SERVICE, "RESPOSE_CLG_JB_SERVICE");
        APICall();
    }


    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            //  Log.d("------>", " isGooglePlayServicesAvailable....true. ");
            return true;
        } else {
            //  Log.d("------>", " isGooglePlayServicesAvailable....false. ");
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //  Log.d("------>", " onConnectionFailed..... ");
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Log.d("------>", " onConnectionSuspended..... ");
    }

    protected void startLocationUpdates() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Log.d("------>", " missing permissions and then overriding..............: ");
                return;
            }
            final PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (Exception ex) {
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        newlattitude = mCurrentLocation.getLatitude();
        newlongitude = mCurrentLocation.getLongitude();

        //  Log.d("------onLocationChanged---IntraId>", InteractionID);
        // Log.d("------onLocationChanged--->", "latitude" + newlattitude + "\n" + "longitude" + newlongitude + "\n" + "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" + "Provider: " + newprovider);

        if (firstTimelocation != 1) {
            firstTimelocation++;
            if (mCurrentLocation.getAccuracy() < 20) {
                Final_Lat = newlattitude;
                Final_Long = newlongitude;
            } else if (mCurrentLocation.getAccuracy() < 30) {
                Final_Lat = newlattitude;
                Final_Long = newlongitude;
            } else if (mCurrentLocation.getAccuracy() < 40) {
                Final_Lat = newlattitude;
                Final_Long = newlongitude;
            } else if (mCurrentLocation.getAccuracy() < 50) {
                Final_Lat = newlattitude;
                Final_Long = newlongitude;
            } else if (mCurrentLocation.getAccuracy() < 60) {
                Final_Lat = newlattitude;
                Final_Long = newlongitude;
            } else if (mCurrentLocation.getAccuracy() < 70) {
                Final_Lat = newlattitude;
                Final_Long = newlongitude;
            } else if (mCurrentLocation.getAccuracy() < 80) {
                Final_Lat = newlattitude;
                Final_Long = newlongitude;
            } else if (mCurrentLocation.getAccuracy() < 90) {
                Final_Lat = newlattitude;
                Final_Long = newlongitude;
            } else if (mCurrentLocation.getAccuracy() < 100) {
                Final_Lat = newlattitude;
                Final_Long = newlongitude;
            } else {
                firstTimelocation = 0;
            }
            //----------------------SAVE LOCATION ------IF ANY ABOVE CONDITION BECOME TRUE..
            if (firstTimelocation == 1) {
                cmf.myPreference.setString(CollegeJobService.this, cmf.gc.JOB_NEW_LATITUDE, Double.toString(Final_Lat));
                cmf.myPreference.setString(CollegeJobService.this, cmf.gc.JOB_NEW_LONGITUDE, Double.toString(Final_Long));
                currentAccuracy = String.valueOf(mCurrentLocation.getAccuracy());
            }

        } else if (mCurrentLocation.getAccuracy() <= 30) {
            if (Count == 1 || newlattitude2 == 0) {
                Count = 0;

                if (DiffrenceCheckInMeter(newlattitude2, newlongitude2, newlattitude, newlongitude)) {

                    newlattitude2 = newlattitude;
                    newlongitude2 = newlongitude;
                    //-----------------------------------------------------------
                    newaccuracy = String.valueOf(mCurrentLocation.getAccuracy());
                    newprovider = String.valueOf(mCurrentLocation.getProvider());
                    //----------------------
                    currentAccuracy = String.valueOf(mCurrentLocation.getAccuracy());
                    //----------------------

                    //Log.d("-----onLocationChanged***after all condition>", "latitude" + newlattitude + "\n" + "longitude" + newlongitude + "\n" + "Accuracy: " + newaccuracy + "\n" + "Provider: " + newprovider);
                    Final_Lat = newlattitude;
                    Final_Long = newlongitude;

                    cmf.myPreference.setString(CollegeJobService.this, cmf.gc.JOB_NEW_LATITUDE, Double.toString(Final_Lat));
                    cmf.myPreference.setString(CollegeJobService.this, cmf.gc.JOB_NEW_LONGITUDE, Double.toString(Final_Long));

                    // cmf.myPreference.setString(CollegeJobService.this, cmf.gc.JOB_NEW_ACCURACY, newaccuracy);
                    // cmf.myPreference.setString(CollegeJobService.this, cmf.gc.JOB_NEW_PROVIDER, newprovider);
                    //---------
                    cmf.addressfrmltlg.SaveLocationData(newlattitude, newlongitude);//pasted
                    currentAdress = cmf.addressfrmltlg.getAddress();
                    cmf.myPreference.setString(CollegeJobService.this, cmf.gc.JOB_CurrentAddress, currentAdress);
                }
            }
            Count++;
        }
    }

    public boolean DiffrenceCheckInMeter(final Double lat1, final Double lon1, final Double lat2, final Double lon2) {
        float distanceInMeters = 0;
        try {
            final Location loc1 = new Location("");
            loc1.setLatitude(lat1);
            loc1.setLongitude(lon1);

            final Location loc2 = new Location("");
            loc2.setLatitude(lat2);
            loc2.setLongitude(lon2);

            distanceInMeters = loc1.distanceTo(loc2);
        } catch (Exception ex) {

        }

        if (distanceInMeters >= 30 || lat1 == 0 || lat2 == 0) {
            if (lat1 != 0) {
                // calculateDistance(distanceInMeters);
            }
            DistanceForLT_LG = distanceInMeters;
            return true;
        } else {
            return false;
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("---------------********************>", "Service Stopped");
        mGoogleApiClient.disconnect();
        firstTimelocation = 0;
        ServiceHandler.removeCallbacks(rnable);
        stopService = 1;
        cmf.myPreference.setString(CollegeJobService.this, "ReadResponse", "0");
        cmf.myPreference.setString(CollegeJobService.this, cmf.gc.RESPOSE_CLG_JB_SERVICE, "RESPOSE_CLG_JB_SERVICE");

       /* if (stopService == 1) {
            Log.e("---------------********************>", "Service Stopped");
            mGoogleApiClient.disconnect();
            firstTimelocation = 0;
            super.onDestroy();
        } else {
            final Intent in = new Intent();
            in.setAction("StartkilledService");
            sendBroadcast(in);
            Log.e("------------->", "Service Killed");
        }*/
    }

   /* @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        cmf = new CommonFunctions(CollegeJobService.this);
        super.onStartCommand(intent, flags, startId);
        stopService = 0;
        try {
            if (intent.getStringExtra(cmf.gc.fromPage).equals("stopService")) {
                stopService = 1;
                stopSelf();
            }
        } catch (Exception ex) {
        }

        return START_STICKY;
    }*/

    //=============================================================================================
    public void initializeTimerTask() {
        ApIcall = true;
        ServiceHandler.postDelayed(rnable = new Runnable() {

            public void run() {
                ServiceHandler.postDelayed(this, 35000);
                try {
                    // Log.e("........", battryCheckInMinut + "--" + ApIcall);
                    /*String sss=cmf.myPreference.getString(CollegeJobService.this,cmf.gc.NotificationData);
                    cmf.myPreference.setString(CollegeJobService.this,cmf.gc.NotificationData,"");
                    Toast.makeText(getApplicationContext(),sss,Toast.LENGTH_SHORT).show();*/
                    if (!ApIcall) {
                        Log.e("------->", "..Api call false..");
                        return;

                    } else if (!cmf.cdr.isConnectingToInternet()) {
                        Log.e("------->", "*******NETWORK FAILED *********");
                        return;

                    } else if (!AspnetUserId.equals("")) {
                        Log.e("------->", "CollegeJobService  RUN--UserID---" + AspnetUserId);
                        APICall();
                    }
                } catch (Exception ex) {
                }
            }
        }, 35000);
    }

    private void APICall() {
        try {
            ApIcall = false;
            // address check null pointer
            try {
                if (currentAdress.equals("null") || currentAdress == null) {
                    currentAdress = "";
                }
            } catch (NullPointerException ex) {
                currentAdress = "";
            }

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    //-----------------------for battery check...
                    battryCheckInMinut++;
                    if (battryCheckInMinut == 1 || battryCheckInMinut == 5) {
                        battryCheckInMinut = 2;
                        batteryLevel();
                        cmf.cdr.freeMemory();
                    }

                    //--------------------
                    String LL, LG;
                    LL = String.valueOf(Final_Lat);
                    LG = String.valueOf(Final_Long);
                    if (Final_Lat == 0 || Final_Long == 0) {
                        // lat long not found and user also off background service location
                        if (!cmf.myPreference.getString(CollegeJobService.this, cmf.gc.safeJone).equals("1")) {
                            LL = "1";
                            LG = "1";
                            btry = "1";
                            currentAdress = "1";
                            currentAccuracy="Your family can't see you.";
                        } else {
                            LL = cmf.myPreference.getString(CollegeJobService.this, cmf.gc.JOB_NEW_LATITUDE);
                            LG = cmf.myPreference.getString(CollegeJobService.this, cmf.gc.JOB_NEW_LONGITUDE);
                        }
                    }


                    new CallWebForService("service", CollegeJobService.this, cmf.urlList.insert_position, cmf.insert_position(AspnetUserId,family_id, btry, LL, LG, currentAdress), new MyServiceListener() {
                        @Override
                        public void onSuccess(String string) {
                            ApIcall = true;
                            // Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
                            final Task tsk = new Task();
                            tsk.execute(string);
                        }

                        @Override
                        public void onFailed() {
                            ApIcall = true;
                            Log.e("---xxxxxx->", "CollegeJobService---->onFailed()");
                        }
                    });
                }
            });
        } catch (Exception ex) {
            Log.e("-------->", ex.toString());
            ApIcall = true;
        }

    }

    //--------------
    private void batteryLevel() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        context.unregisterReceiver(this);
                        int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                        int level = -1;
                        if (rawlevel >= 0 && scale > 0) {
                            level = (rawlevel * 100) / scale;
                        }
                        //batteryLevel.setText("Battery Level Remaining: " + level + "%");
                        btry = String.valueOf(level);
                        // cmf.myPreference.setString(CollegeJobService.this, cmf.gc.BataryLevel, String.valueOf(level));
                    }
                };
                IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                registerReceiver(batteryLevelReceiver, batteryLevelFilter);
            }
        });
    }

  /*  public void freeMemory() {
        //System.runFinalization();
        //Runtime.getRuntime().gc();
        //System.gc();
      *//*  StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build());*//*
    }*/

    //------------------------------------------

    class Task extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String returnValue = "";
            try {
                //------------------
                final JSONObject root = new JSONObject(params[0]);
                String status = root.optString("status");

                if (status.equals("1")) {
                    // get guard Lat Long

                    if (!cmf.myPreference.getString(CollegeJobService.this, cmf.gc.RESPOSE_CLG_JB_SERVICE).equals(params[0])) {

                        //NOTE:  check and set ReadResponse=0 from map showing fragment
                        cmf.myPreference.setString(CollegeJobService.this, "ReadResponse", "1");

                        // final String isAlarmRinging = root.optString("isAlarmRinging");
                        //  cmf.myPreference.setString(CollegeJobService.this, cmf.gc.FriendRequestCount, root.optString("FriendRequestCount"));
                        cmf.myPreference.setString(CollegeJobService.this, cmf.gc.RESPOSE_CLG_JB_SERVICE, params[0]);
                        // cmf.myPreference.setString(CollegeJobService.this, cmf.gc.ISFRIENDSTRACKING, root.optString("IsFriendsTracking"));
                        //------------------------media player
                        //  cmf.myPreference.setString(CollegeJobService.this, cmf.gc.ALARM_MUTE_STATUS, root.optString("IsAlarmMute"));

                        // returnValue = isAlarmRinging;
                        returnValue = "0";
                    }
                }
            } catch (Exception ex) {
            }
            //------------------------------------------------**************************************
            return returnValue;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("1")) {
                //for alarm--
                final MyPlayer mp = new MyPlayer();
                mp.mp_start(CollegeJobService.this);
            }
        }
    }

//-----------------------------------CLOSED SCOPE---
}