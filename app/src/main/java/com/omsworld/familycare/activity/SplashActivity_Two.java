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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omsworld.familycare.BuildConfig;
import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.AppUtil;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.omsworld.familycare.api_call.MyServiceListener;
import com.omsworld.familycare.api_call.UrlList;

import org.json.JSONException;
import org.json.JSONObject;

import static com.omsworld.familycare.setting.BaseActivity.myservice;

public class SplashActivity_Two extends Activity {
    private CommonFunctions cmf;
    private ImageView Iv_splash;

    private static int stophandler = 0;
    private Intent intent;
    private Bundle bundle;
    private int isUpdateAPIcalled = 0;
    private RelativeLayout RL_top, RL_bottom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_two);

        if (cmf.myPreference.getString(SplashActivity_Two.this, GlobalConstants.LOGIN_STATUS).equals("1")) {
            intent = new Intent(SplashActivity_Two.this, MainActivity.class);
            AppUtil.startActivityWithAnimation(this, intent);
            finish();
        }else{
            initview();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        stophandler = 0;
    }

    private void initview() {
        cmf = new CommonFunctions(SplashActivity_Two.this);
        Iv_splash = (ImageView) findViewById(R.id.Iv_splash);

        RL_top = (RelativeLayout) findViewById(R.id.RL_top);
        RL_bottom = (RelativeLayout) findViewById(R.id.RL_bottom);

        /*final Animation myAnim = AnimationUtils.loadAnimation(SplashActivity_Two.this, R.anim.bounce);
        Iv_splash.startAnimation(myAnim);*/
        final Animation topAnim = AnimationUtils.loadAnimation(SplashActivity_Two.this, R.anim.down_from_top);
        RL_top.startAnimation(topAnim);
        final Animation bottomAnim = AnimationUtils.loadAnimation(SplashActivity_Two.this, R.anim.bottom_down);
        RL_bottom.startAnimation(bottomAnim);
        //for version control comment..
        // callonmainthread("on");
        //-----------
        bottomAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
              //  callonmainthread("off");
                passintent();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

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

        String UserID = cmf.myPreference.getString(SplashActivity_Two.this, GlobalConstants.USER_ID);
        myservice = new CallWebService("", SplashActivity_Two.this, UrlList.UPDATE_APK, cmf.updateApk_Param(UserID, "packageName", "ANDROID", versionCode), new MyServiceListener() {
            @Override
            public void onSuccess(String response) {
                Log.d("-----version check-->", response);

                try {
                    stophandler = 1;
                    JSONObject jsonObject = new JSONObject(response);
                    int Status = jsonObject.optInt("Status");
                    String Message = jsonObject.optString("Message");
                    final int isForcefullyUpdate = jsonObject.optInt("isForcefullyUpdate");

                    if (Status == 1) {
                        final Animation myAnim2 = AnimationUtils.loadAnimation(SplashActivity_Two.this, R.anim.zoom_in_out);
                        Iv_splash.startAnimation(myAnim2);

                        final Dialog updateapp = new Dialog(SplashActivity_Two.this);
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
                    if (cmf.myPreference.getString(SplashActivity_Two.this, GlobalConstants.LOGIN_STATUS).equals("1")) {
                        String InviteeStatusID = jsonObject.getString("InviteeStatusID");
                        String PaymentStatusCode = jsonObject.getString("PaymentStatusCode");

                        // cmf.myPreference.setString(SplashActivity_Two.this, GlobalConstants.INVITEE_STATUSID, InviteeStatusID);
                        cmf.myPreference.setString(SplashActivity_Two.this, GlobalConstants.PaymentStatusCode, PaymentStatusCode);
                        if (!PaymentStatusCode.equals("1069")) {
                            cmf.myPreference.setBoolean(SplashActivity_Two.this, GlobalConstants.USER_PAYED, false);
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
                if (cmf.myPreference.getString(SplashActivity_Two.this, GlobalConstants.LOGIN_STATUS).equals("1")) {
                    intent = new Intent(SplashActivity_Two.this, MainActivity.class);
                    //intent.putExtras(bundle);

                    AppUtil.startActivityWithAnimation(this, intent);
                    finish();

                } else {
                    Intent intent = new Intent(SplashActivity_Two.this, SignInUpActivity.class);
                    //  startActivity(intent);
                    // finish();
                    AppUtil.startActivityWithAnimation(this, intent);
                    finish();
                }
            } else {
                if (cmf.myPreference.getString(SplashActivity_Two.this, GlobalConstants.LOGIN_STATUS).equals("1")) {
                    intent = new Intent(SplashActivity_Two.this, MainActivity.class);
                 /* startActivity(intent);
                finish();*/
                    AppUtil.startActivityWithAnimation(this, intent);
                    finish();

                } else {
                    Intent intent = new Intent(SplashActivity_Two.this, SignInUpActivity.class);
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
