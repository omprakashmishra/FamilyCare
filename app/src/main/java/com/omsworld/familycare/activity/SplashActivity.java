package com.omsworld.familycare.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.omsworld.familycare.BuildConfig;
import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.AppUtil;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.omsworld.familycare.api_call.MyServiceListener;
import com.omsworld.familycare.api_call.UrlList;

import io.fabric.sdk.android.Fabric;
import org.json.JSONException;
import org.json.JSONObject;

import static com.omsworld.familycare.setting.BaseActivity.myservice;

public class SplashActivity extends Activity {
    private static int stophandler = 0;
    CommonFunctions cmf;
    ImageView Iv_splash;
    private Intent intent;
    private Bundle bundle;
    private LinearLayout LL_prgressBarLayout;
    private int isUpdateAPIcalled = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        stophandler = 0;
        initview();
    }

    private void initview() {
        cmf = new CommonFunctions(SplashActivity.this);
        Iv_splash = (ImageView) findViewById(R.id.Iv_splash);
        LL_prgressBarLayout = (LinearLayout) findViewById(R.id.LL_prgressBarLayout);

        final Animation myAnim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.bounce);
        Iv_splash.startAnimation(myAnim);

        if (cmf.myPreference.getString(SplashActivity.this, GlobalConstants.LOGIN_STATUS).equals("1")) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
            AppUtil.startActivityWithAnimation(this, intent);
            finish();
        }else{
            //for version control comment..
            callonmainthread("off");
            //-----------
            // callonmainthread("on");
        }

    }


    private void callonmainthread(final String versioncontrol) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (versioncontrol.equals("off")) {
                    passintent();
                } else {
                    if (stophandler == 1) {
                        handler.removeCallbacks(this);
                    } else {
                        update_verson_ApiCall();
                        handler.postDelayed(this, 1500);
                    }
                }

            }
        }, 1500);
    }


    private void update_verson_ApiCall() {
        if (isUpdateAPIcalled == 1) {
            return;
        }
        isUpdateAPIcalled = 1;
        String versionCode = String.valueOf(BuildConfig.VERSION_CODE);
        LL_prgressBarLayout.setVisibility(View.VISIBLE);
        String UserID = cmf.myPreference.getString(SplashActivity.this, GlobalConstants.USER_ID);
        myservice = new CallWebService("", SplashActivity.this, UrlList.UPDATE_APK, cmf.updateApk_Param(UserID, "packageName", "ANDROID", versionCode), new MyServiceListener() {
            @Override
            public void onSuccess(String response) {
                Log.d("-----version check-->", response);
                LL_prgressBarLayout.setVisibility(View.GONE);
                try {
                    stophandler = 1;
                    JSONObject jsonObject = new JSONObject(response);
                    int Status = jsonObject.optInt("Status");
                    String Message = jsonObject.optString("Message");
                    final int isForcefullyUpdate = jsonObject.optInt("isForcefullyUpdate");

                    if (Status == 1) {
                        final Animation myAnim2 = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.zoom_in_out);
                        Iv_splash.startAnimation(myAnim2);

                        final Dialog updateapp = new Dialog(SplashActivity.this);
                        updateapp.setCancelable(false);
                        updateapp.setContentView(R.layout.dialog_yes_no);
                        updateapp.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        Button ok = (Button) updateapp.findViewById(R.id.ok);
                        Button no = (Button) updateapp.findViewById(R.id.no);
                        ok.setText("Update");
                        no.setText("Cancel");
                        ok.setVisibility(View.VISIBLE);
                        no.setVisibility(View.VISIBLE);
                        TextView text = (TextView) updateapp.findViewById(R.id.text);
                        text.setText(Message);
                        if (isForcefullyUpdate == 1) {
                            no.setVisibility(View.GONE);
                        }
                        ok.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                updateapp.dismiss();
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("play store app url"));
                                startActivity(intent);
                            }
                        });
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateapp.dismiss();
                                passintent();
                            }
                        });
                        updateapp.show();
                    } else {
                        passintent();
                    }
                    //check user status as well payment status....
                    if (cmf.myPreference.getString(SplashActivity.this, GlobalConstants.LOGIN_STATUS).equals("1")) {
                        String InviteeStatusID = jsonObject.getString("InviteeStatusID");
                        String PaymentStatusCode = jsonObject.getString("PaymentStatusCode");

                        // cmf.myPreference.setString(SplashActivity.this, GlobalConstants.INVITEE_STATUSID, InviteeStatusID);
                        cmf.myPreference.setString(SplashActivity.this, GlobalConstants.PaymentStatusCode, PaymentStatusCode);
                        if (!PaymentStatusCode.equals("1069")) {
                            cmf.myPreference.setBoolean(SplashActivity.this, GlobalConstants.USER_PAYED, false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    passintent();
                } catch (Exception ex) {
                    Toast.makeText(getApplication(), "Please try agin project under maintenance.", Toast.LENGTH_SHORT).show();
                    passintent();
                }
            }

            @Override
            public void onFailed() {
                LL_prgressBarLayout.setVisibility(View.GONE);
                passintent();
            }
        });
    }

    private void passintent() {
        stophandler = 1;
        //--------------------------> notification bundle..
        try {
            bundle = getIntent().getExtras();
            if (bundle != null) {
                if (cmf.myPreference.getString(SplashActivity.this, GlobalConstants.LOGIN_STATUS).equals("1")) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                    //intent.putExtras(bundle);

                    AppUtil.startActivityWithAnimation(this, intent);
                    finish();

                } else {
                    Intent intent = new Intent(SplashActivity.this, SignInUpActivity.class);
                    //  startActivity(intent);
                    // finish();
                    AppUtil.startActivityWithAnimation(this, intent);
                    finish();
                }
            } else {
                if (cmf.myPreference.getString(SplashActivity.this, GlobalConstants.LOGIN_STATUS).equals("1")) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                 /* startActivity(intent);
                finish();*/
                    AppUtil.startActivityWithAnimation(this, intent);
                    finish();

                } else {
                    Intent intent = new Intent(SplashActivity.this, SignInUpActivity.class);
                    //startActivity(intent);
                    // finish();
                    AppUtil.startActivityWithAnimation(this, intent);
                    finish();
                }
            }
        } catch (Exception ex) {
        }
    }

}
