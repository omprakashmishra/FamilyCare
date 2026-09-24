package com.omsworld.familycare.activity;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omsworld.familycare.Chat.FriendsList_fr;
import com.omsworld.familycare.Diary.Diary_fr;
import com.omsworld.familycare.R;
import com.omsworld.familycare.SharedContacts.Sharedcontacts_fr;
import com.omsworld.familycare.Shopping.ShoppingMain_Ac;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.omsworld.familycare.fragment.FamilyMembers_Fr;
import com.omsworld.familycare.fragment.Fragment_aboutus;
import com.omsworld.familycare.fragment.Fragment_contactus;
import com.omsworld.familycare.fragment.Fragment_settings;
import com.omsworld.familycare.fragment.Home_fr;

import com.omsworld.familycare.fragment.MyTrackingFr;
import com.omsworld.familycare.fragment.Profile_Fr;
import com.omsworld.familycare.fragment.Scan_fr;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    public static Resources resources;

    public static Toolbar toolbar;
    public static ImageView IV_notification;
    public static FragmentManager manager;
    private static LinearLayout LL_home, LL_tracking, LL_invitation, LL_profile, LL_sharedcontact, LL_notes,
            LL_request, LL_contacts, LL_aboutus, LL_contactus, LL_logout, LL_Settings, LL_TermsCondition;
    public final int LOCATION_REQUEST = 100;
    CircleImageView CIV_iv;
    boolean doubleBackToExitPressedOnce = false;
    private DrawerLayout drawer;
    private TextView name, mobileno, tv_UnreadCount;
    private NavigationView navigationView;
    private String username, user_mobile, user_image;
    private String Devicetoken = "";
    private TextView TV_firstletter, TV_onlinestatus;
    private String InviteeStatusID = "", COUNTYCD;
    private CommonFunctions cmf;
    ProgressBar mprogressBar;

    //------------------------
    public static LinearLayout bottomDrawer;
    LinearLayout tab_1, tab_2, tab_3, tab_4, tab_5;
    TextView TVHome, TVSearch, TVFav, TVbh, TVaccount;
    ImageView homet, searchTab, likeTab, calenderTab, profileTab;
    //---------------------------------

    public static void LeftMenueOpenForPiad() {
        LL_home.setVisibility(View.VISIBLE);

        LL_invitation.setVisibility(View.VISIBLE);
        LL_request.setVisibility(View.VISIBLE);
        LL_contacts.setVisibility(View.VISIBLE);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.theme_dark));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resources = getResources();
        Initialization();
    }

    private void Initialization() {
        cmf = new CommonFunctions(MainActivity.this);
        toolbar = (Toolbar) findViewById(R.id.toolbar12);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(resources.getColor(R.color.white));
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                cmf.hideKeyboard(drawer);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        IV_notification = (ImageView) findViewById(R.id.IV_notification);
        IV_notification.setOnClickListener(this);

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        manager = getSupportFragmentManager();


        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("from").equals("otp")) {
                // fragment = new MyTrackingFr();
                cmf.replaceFragment(MainActivity.this, new FamilyMembers_Fr());
            }
        } else {
            // fragment = new MyTrackingFr();
                     cmf.replaceFragment(MainActivity.this, new Home_fr());
        }


        idSetUp();
        // setInView();
    }


    private void idSetUp() {
        username = cmf.myPreference.getString(this, GlobalConstants.USER_NAME);
        user_mobile = cmf.myPreference.getString(this, GlobalConstants.MOBILE_only);
        user_image = cmf.myPreference.getString(this, GlobalConstants.USER_IMAGE);

        mobileno = (TextView) findViewById(R.id.mobileno);
        CIV_iv = (CircleImageView) findViewById(R.id.CIV_profile_image);
        name = (TextView) findViewById(R.id.name);

        name.setText(username);
        mobileno.setText(user_mobile);

        LL_home = (LinearLayout) findViewById(R.id.LL_home);
        LL_tracking = (LinearLayout) findViewById(R.id.LL_tracking);
        LL_invitation = (LinearLayout) findViewById(R.id.LL_invitation);
        LL_aboutus = (LinearLayout) findViewById(R.id.LL_aboutus);
        LL_contactus = (LinearLayout) findViewById(R.id.LL_contactus);
        LL_logout = (LinearLayout) findViewById(R.id.LL_logout);
        tv_UnreadCount = (TextView) findViewById(R.id.tv_UnreadCount);
        TV_firstletter = (TextView) findViewById(R.id.TV_firstletter);
        TV_onlinestatus = (TextView) findViewById(R.id.TV_onlinestatus);
        LL_Settings = (LinearLayout) findViewById(R.id.LL_Settings);
        LL_TermsCondition = (LinearLayout) findViewById(R.id.LL_TermsCondition);
        LL_profile = (LinearLayout) findViewById(R.id.LL_profile);
        LL_sharedcontact = (LinearLayout) findViewById(R.id.LL_sharedcontact);
        LL_notes = (LinearLayout) findViewById(R.id.LL_notes);
        mprogressBar = (ProgressBar) findViewById(R.id.mprogressBar);


        LL_home.setOnClickListener(this);
        LL_tracking.setOnClickListener(this);
        LL_invitation.setOnClickListener(this);
        LL_Settings.setOnClickListener(this);
        LL_TermsCondition.setOnClickListener(this);
        LL_aboutus.setOnClickListener(this);
        LL_contactus.setOnClickListener(this);
        LL_logout.setOnClickListener(this);
        LL_profile.setOnClickListener(this);
        LL_sharedcontact.setOnClickListener(this);
        LL_notes.setOnClickListener(this);

        //--------------------
        bottomDrawer = (LinearLayout) findViewById(R.id.bottomDrawer);
        tab_1 = (LinearLayout) findViewById(R.id.tab_1);
        tab_2 = (LinearLayout) findViewById(R.id.tab_2);
        tab_3 = (LinearLayout) findViewById(R.id.tab_3);
        tab_4 = (LinearLayout) findViewById(R.id.tab_4);
        tab_5 = (LinearLayout) findViewById(R.id.tab_5);

        // Find Tab Label.......................................
        TVHome = (TextView) findViewById(R.id.TVHome);
        TVSearch = (TextView) findViewById(R.id.TVSearch);
        TVFav = (TextView) findViewById(R.id.TVFav);
        TVbh = (TextView) findViewById(R.id.TVbh);
        TVaccount = (TextView) findViewById(R.id.TVaccount);
        homet = (ImageView) findViewById(R.id.petTab);
        searchTab = (ImageView) findViewById(R.id.searchTab);
        likeTab = (ImageView) findViewById(R.id.likeTab);
        calenderTab = (ImageView) findViewById(R.id.calenderTab);
        profileTab = (ImageView) findViewById(R.id.profileTab);

        //  tabBackground(1);
        reValidate();
    }

    private void setInView() {
        try {

            Picasso.with(this).load(user_image).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).resize(90, 90).into(CIV_iv, new Callback() {
                @Override
                public void onSuccess() {
                    mprogressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    mprogressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception ex) {
        }
    }

    /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }*/

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
        }
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.LL_home:
                manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                drawer.closeDrawer(GravityCompat.START);
                // layout_bottom.setVisibility(View.VISIBLE);
                toolbar.setTitle("Home");
                cmf.replaceFragment(MainActivity.this, new Home_fr());
                break;
            case R.id.LL_tracking:
                drawer.closeDrawer(GravityCompat.START);
                // layout_bottom.setVisibility(View.VISIBLE);

                cmf.replaceFragment(MainActivity.this, new MyTrackingFr());
                toolbar.setTitle("Family Location");
                break;

            case R.id.LL_invitation:
                drawer.closeDrawer(GravityCompat.START);


                cmf.replaceFragment(MainActivity.this, new FamilyMembers_Fr());

                toolbar.setTitle("Manage Your Family");

                break;


            case R.id.LL_aboutus:
                drawer.closeDrawer(GravityCompat.START);
                // layout_bottom.setVisibility(View.VISIBLE);
                cmf.replaceFragment(MainActivity.this, new Fragment_aboutus());
                toolbar.setTitle("About Us");

                break;

            case R.id.LL_sharedcontact:
                drawer.closeDrawer(GravityCompat.START);
                // layout_bottom.setVisibility(View.VISIBLE);

                cmf.replaceFragment(MainActivity.this, new Sharedcontacts_fr());
                toolbar.setTitle("Phone Book Post");

                break;

            case R.id.LL_notes:
                drawer.closeDrawer(GravityCompat.START);
                // layout_bottom.setVisibility(View.VISIBLE);.
                cmf.replaceFragment(MainActivity.this, new Diary_fr());
                toolbar.setTitle("My Diary");
                // AppUtil.startActivityWithAnimation(this, new Intent(this, NoteList.class));
                break;

            case R.id.LL_contactus:
                drawer.closeDrawer(GravityCompat.START);
                // layout_bottom.setVisibility(View.VISIBLE);
                cmf.replaceFragment(MainActivity.this, new Fragment_contactus());
                toolbar.setTitle("Help");

                break;

            case R.id.IV_notification:
                drawer.closeDrawer(GravityCompat.START);
                notificationAlert();

                break;


            case R.id.LL_profile:
                drawer.closeDrawer(GravityCompat.START);

                toolbar.setTitle("Profile");
                cmf.replaceFragment(MainActivity.this, new Profile_Fr());


                break;

            case R.id.LL_Settings:
                drawer.closeDrawer(GravityCompat.START);
                toolbar.setTitle("Settings");

                cmf.replaceFragment(MainActivity.this, new Fragment_settings());
                break;

            case R.id.LL_TermsCondition:
                startActivity(new Intent(MainActivity.this, TermAndConditionsAc.class));
                break;

            case R.id.LL_logout:
                try {

                    drawer.closeDrawer(GravityCompat.START);

                    cmf.myPreference.clearSharedPreference(MainActivity.this);

                    cmf.myPreference.setString(this, GlobalConstants.LOGIN_STATUS, "0");
                    cmf.myPreference.setString(this, GlobalConstants.NOTIFICATION, "0");
                    cmf.myPreference.setString(this, GlobalConstants.LEGALAGREEMENTCHECK, "1");
                    cmf.myPreference.setString(MainActivity.this, GlobalConstants.ISDOZEDISABLED, "1");

                    Intent intent = new Intent(this, SignInUpActivity.class);
                    startActivity(intent);
                    finish();

                } catch (Exception ex) {
                }
                break;
        }
    }

    private void notificationAlert() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        dialog.setContentView(R.layout.notification_alert);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        lp.gravity = Gravity.RIGHT;
        lp.gravity = Gravity.TOP;

      /*  dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/

        dialog.show();
    }


    @Override
    public void onDestroy() {
        // finish();
        super.onDestroy();
        Runtime.getRuntime().gc();
    }


    @Override
    protected void onResume() {
        super.onResume();
        cmf.hideKeyboard(toolbar);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST);
        }
        freeMemory();

       /* if (CommonFunctions.VisibleFragmentNm.equals("")) {
            CommonFunctions.VisibleFragmentNm = Tagvalue;
        }*/
        // getFromGpsTracker();
    }

    private void loadPermissions(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, perm)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{perm}, requestCode);
            }
        } else {
            DisableDozeMode();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DisableDozeMode();
                }
                break;
            }

        }
    }

    private void DisableDozeMode() {
        if (cmf.myPreference.getString(MainActivity.this, GlobalConstants.ISDOZEDISABLED).equals("1")) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = MainActivity.this.getPackageName();
            PowerManager pm = (PowerManager) MainActivity.this.getSystemService(MainActivity.this.POWER_SERVICE);
            if (pm.isIgnoringBatteryOptimizations(packageName)) {
                cmf.myPreference.setString(MainActivity.this, GlobalConstants.ISDOZEDISABLED, "1");
                // intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            } else {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                cmf.myPreference.setString(MainActivity.this, GlobalConstants.ISDOZEDISABLED, "1");
                MainActivity.this.startActivity(intent);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        freeMemory();
    }

    public void freeMemory() {
        // System.runFinalization();
        //   Runtime.getRuntime().gc();
        //   System.gc();
        cmf.cdr.freeMemory();
    }



  /*  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("-----------Settings", "Result OK");
                        //updateGPSStatus("GPS is Enabled in your device");
                        //startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        Log.e("-------->Settings", "Result Cancel");

                        break;
                }
                break;
        }
    }*/

    @Override
    public void onBackPressed() {
        try {
            reValidate();
            toolbar.setVisibility(View.VISIBLE);
            toolbar.setTitle("Family Care");
            int count = manager.getBackStackEntryCount();

            if (count == 1) {
                if (doubleBackToExitPressedOnce) {
                    moveTaskToBack(true);
                    System.exit(0);
                    super.onBackPressed();
                    //  finish();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;

                Snackbar snackbar;
                snackbar = Snackbar.make(LL_home, "Please click BACK again to exit", Snackbar.LENGTH_SHORT);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(getResources().getColor(R.color.theme_dark));
                snackbar.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                manager.popBackStackImmediate();
            }
            cmf.hideKeyboard(toolbar);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void ChangeTab(View view) {
        drawer.closeDrawer(GravityCompat.START);
        switch (view.getId()) {
            case R.id.tab_1:
                tabBackground(1);
                // remove previous loaded fragment..
                manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                toolbar.setTitle("Family Care");

                cmf.replaceFragment(MainActivity.this,new Home_fr());
                break;
            case R.id.tab_2:
                tabBackground(2);

                toolbar.setTitle("Phone Book Post");
                cmf.replaceFragment(MainActivity.this,new Sharedcontacts_fr());
                break;
            case R.id.tab_3:
                tabBackground(3);
                toolbar.setTitle("Family Post Message");
                cmf.replaceFragment(MainActivity.this, new FriendsList_fr());
                break;
            case R.id.tab_4:
                tabBackground(4);
                // toolbar.setTitle("Family Grocery List");
                toolbar.setTitle("Scan");

                cmf.replaceFragment(MainActivity.this, new Scan_fr());
                break;
            case R.id.tab_5:
                tabBackground(5);
                /*toolbar.setTitle("Family Care");
                fragment = new ShoppingSitesList_Fr();
                replaceFragment(fragment);*/
                startActivity(new Intent(this, ShoppingMain_Ac.class));
                break;

        }
    }

    private void tabBackground(int i) {
        reValidate();
        if (i == 1) {
            homet.setColorFilter(getResources().getColor(R.color.theme_color));
            TVHome.setTextColor(getResources().getColor(R.color.theme_color));
            tab_1.setBackgroundColor(getResources().getColor(R.color.white));
        } else if (i == 2) {
            searchTab.setColorFilter(getResources().getColor(R.color.theme_color));
            TVSearch.setTextColor(getResources().getColor(R.color.theme_color));
            tab_2.setBackgroundColor(getResources().getColor(R.color.white));
        } else if (i == 3) {
            likeTab.setColorFilter(getResources().getColor(R.color.theme_color));
            TVFav.setTextColor(getResources().getColor(R.color.theme_color));
            tab_3.setBackgroundColor(getResources().getColor(R.color.white));
        } else if (i == 4) {
            calenderTab.setColorFilter(getResources().getColor(R.color.theme_color));
            TVbh.setTextColor(getResources().getColor(R.color.theme_color));
            //tab_4.setBackgroundColor(getResources().getColor(R.color.white));
        } else if (i == 5) {
            profileTab.setColorFilter(getResources().getColor(R.color.theme_color));
            TVaccount.setTextColor(getResources().getColor(R.color.theme_color));
            tab_5.setBackgroundColor(getResources().getColor(R.color.white));
        }

    }

    private void reValidate() {
        TVHome.setTextColor(getResources().getColor(R.color.black));
        TVSearch.setTextColor(getResources().getColor(R.color.black));
        TVFav.setTextColor(getResources().getColor(R.color.black));
        TVbh.setTextColor(getResources().getColor(R.color.black));
        TVaccount.setTextColor(getResources().getColor(R.color.black));
        homet.setColorFilter(getResources().getColor(R.color.black));
        searchTab.setColorFilter(getResources().getColor(R.color.black));
        likeTab.setColorFilter(getResources().getColor(R.color.black));
        calenderTab.setColorFilter(getResources().getColor(R.color.black));
        profileTab.setColorFilter(getResources().getColor(R.color.black));
        tab_1.setBackgroundColor(getResources().getColor(R.color.tab_color));
        tab_2.setBackgroundColor(getResources().getColor(R.color.tab_color));
        tab_3.setBackgroundColor(getResources().getColor(R.color.tab_color));
        tab_4.setBackgroundColor(getResources().getColor(R.color.tab_color));
        tab_5.setBackgroundColor(getResources().getColor(R.color.tab_color));

    }

    //--------------------------------------------------------------SCOPE CLOSED------
}
