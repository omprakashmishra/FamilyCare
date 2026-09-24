package com.omsworld.familycare.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.AppUtil;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.omsworld.familycare.api_call.MyServiceListener;
import com.omsworld.familycare.setting.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VerifyPin extends BaseActivity {

    private static final String TAG = "VerifyOTPActivity";
    @Bind(R.id.IV_backpress)
    ImageView IVBackpress;
    @Bind(R.id.TV_verifytext)
    TextView TVVerifytext;
    @Bind(R.id.header_text_register)
    TextView headerTextRegister;
    @Bind(R.id.txtOTP)
    EditText txtOTP;
    @Bind(R.id.RL_codelayout)
    RelativeLayout RLCodelayout;

    @Bind(R.id.btnContinue)
    Button btnContinue;
    @Bind(R.id.TV_resendotp)
    TextView TVResendotp;


    private String OTP = "";
    private String mobile;

    private Dialog dialog;
    private String DeviceTokenID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        ButterKnife.bind(this);
        DeviceTokenID = commonFunctions.myPreference.getString(this, GlobalConstants.Firebasetoken);


        // txtMessage.setText("");
        //   txtOTP.setBackgroundColor(ContextCompat.getColor(this, R.color.ColortxtdeActive));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //ContactName = bundle.getString(GlobalConstants.USER_NAME);
            mobile = bundle.getString("mobile");
            headerTextRegister.setText(mobile);
           // Tst(this, "Your OTP has been sent to " + mobile);
            // lblheader.setText("Your OTP has been sent to ******" + mobile);
        }
        SetRegisterButtonColor(2);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                OTP = txtOTP.getText().toString().trim();
                if (OTP.isEmpty()) {
                    //txtMessage.setText("Enter OTP");
                    Tst(VerifyPin.this, "Enter OTP");
                    txtOTP.requestFocus();
                    return;
                }
                // myservice = new CallWebService(VerifyPin.this, urlList.Verifypin, commonFunctions.Verifypin(device_id(), ContactName, OTP), VerifyPin.this);
                if (isInternetAvailable()) {
                    if (OTP.isEmpty()) {
                        Tst(VerifyPin.this, "Enter OTP");
                        txtOTP.requestFocus();
                        return;
                    }
                    myservice = new CallWebService(VerifyPin.this, urlList.verify_otp, commonFunctions.verify_otp(mobile, OTP), VerifyPin.this);
                } else {
                    // Tst(VerifyPin.this, getResources().getString(R.string.CheckInternet));
                    Tst_Snake(findViewById(R.id.txtOTP), getResources().getString(R.string.CheckInternet));
                    return;
                }
            }
        });
    }


    @Override
    public void onSuccess(String string) {
        responseListner(string);
        super.onSuccess(string);
        //Tst(this, string);
    }

    private void responseListner(String Json) {
        try {
            JSONObject flag = new JSONObject(Json);

            String Status = flag.getString("status");
            //   String message = flag.getString("message");
            //----------
            // startActivity(new Intent(VerifyPin.this,MainActivity.class));
            if (Status.equals("0")) {
                // Tst(this, message);
                Tst_Snake(findViewById(R.id.txtOTP), "Enter correct otp code !");
                return;
            } else {
                JSONObject mainf=flag.optJSONObject("user_info");

                String user_id = mainf.optString("user_id");
                String Email = mainf.optString("user_email");
                String UserName = mainf.optString("user_name");
                String MobileNo = mainf.optString("user_mob");
                String user_img = mainf.optString("user_img");
                String family_id = mainf.optString("family_id");
                String family_name = mainf.optString("family_name");

                commonFunctions.myPreference.setString(this, GlobalConstants.USER_ID, user_id);
                commonFunctions.myPreference.setString(this, GlobalConstants.USER_NAME, UserName);
                commonFunctions.myPreference.setString(this, GlobalConstants.MOBILE_only, MobileNo);
                commonFunctions.myPreference.setString(this, GlobalConstants.EMAIL, Email);
                commonFunctions.myPreference.setString(this, GlobalConstants.USER_IMAGE, user_img);
                commonFunctions.myPreference.setString(this, GlobalConstants.LOGIN_STATUS, "1");

                commonFunctions.myPreference.setString(this, GlobalConstants.FAMILY_ID, family_id);
                commonFunctions.myPreference.setString(this, GlobalConstants.FAMILY_NAME, family_name);

                Intent intent = new Intent(VerifyPin.this, MainActivity.class);
                intent.putExtra("from","otp");
                AppUtil.startActivityWithAnimation(VerifyPin.this, intent);
                finish();
            }
        } catch (JSONException jex) {

        } catch (Exception ex) {
        }
    }

    @Override
    public void onFailed() {
        super.onFailed();
    }

    private void SetRegisterButtonColor(int flag) {

        if (flag == 1) {
            btnContinue.setEnabled(false);
            btnContinue.setBackgroundColor(Color.GRAY);

        } else if (flag == 2) {
            btnContinue.setEnabled(true);
            btnContinue.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (flag == 3) {
            // ContinueButton.setBackground(gd);
            btnContinue.setEnabled(true);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

         AppUtil.finishActivityWithAnimationRight(VerifyPin.this);
    }

    @OnClick(R.id.TV_resendotp)
    public void onClick() {
        resendOTP_API();
    }

    private void resendOTP_API() {
        new CallWebService(this, urlList.ResendOtp, commonFunctions.ResendOTP("userId",mobile), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {

                try {
                    JSONObject flag = new JSONObject(string);
                    String Status = flag.optString("status");
                    String message = flag.optString("message");
                    if (Status.equals("1")) {
                        Toast.makeText(VerifyPin.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed() {

            }


        });
    }

    //=======================================================Closed================
}

