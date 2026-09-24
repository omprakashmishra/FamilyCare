package com.omsworld.familycare.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.AppUtil;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.omsworld.familycare.api_call.MyServiceListener;
import com.omsworld.familycare.setting.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rupesh.m on 12/16/2016.
 */

public class SignInUpActivity extends BaseActivity {

    public static final String SOURCE = "SignInUpActivity";
    private static final String TAG = "SignInUpActivity";
    @Bind(R.id.et_Email)
    EditText etEmail;
    @Bind(R.id.et_Password)
    EditText etPassword;
    @Bind(R.id.tv_ForgotPassword)
    TextView tvForgotPassword;
    @Bind(R.id.bt_SignIn)
    Button btSignIn;
    @Bind(R.id.tv_SignUp)
    TextView tvSignUp;
    View view;
    @Bind(R.id.edt_username)
    EditText edtUsername;
    @Bind(R.id.mprogressBar)
    ProgressBar mprogressBar;
    String nm, ph, pin;
    int STATUS_CDODE = 0;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (commonFunctions.myPreference.getString(this, GlobalConstants.LOGIN_STATUS).equals("1")) {
            GotoNextActivityClearTop(this, MainActivity.class);
        }
        //-----------------------------
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);
        edtUsername.setVisibility(View.GONE);
        etPassword.setVisibility(View.GONE);
    }

    private void registerDeviceId(String userId) {
        mprogressBar.setVisibility(View.VISIBLE);
         if (isInternetAvailable()) {
            // String deviceId=commonFunctions.device_id();
             String Devicetoken = commonFunctions.myPreference.getString(this, GlobalConstants.Firebasetoken);
            new CallWebService("",SignInUpActivity.this, urlList.push_registration, commonFunctions.push_registration(userId, Devicetoken), new MyServiceListener() {
                @Override
                public void onSuccess(String string) {
                    mprogressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(SignInUpActivity.this, MainActivity.class);
                    AppUtil.startActivityWithAnimation(SignInUpActivity.this, intent);
                }

                @Override
                public void onFailed() {
                    mprogressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    @OnClick({R.id.tv_ForgotPassword, R.id.bt_SignIn, R.id.tv_SignUp, R.id.IV_OK, R.id.tv_trydemo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_ForgotPassword:
                ForgetPaasword();
                break;
            case R.id.bt_SignIn:

                break;
            case R.id.tv_trydemo:
                ph="8802455031";
                pin="ad";
                mprogressBar.setVisibility(View.VISIBLE);
                Login();
                break;
            case R.id.tv_SignUp:
                Intent intent = new Intent(SignInUpActivity.this, SignUpActivity.class);
                AppUtil.startActivityWithAnimation(SignInUpActivity.this, intent);
                break;
            case R.id.IV_OK:
                commonFunctions.hideKeyboard(view);
                if (isInternetAvailable()) {
                    if (mprogressBar.getVisibility() == View.GONE) {
                        ph = etEmail.getText().toString();
                        nm = edtUsername.getText().toString();
                        pin = etPassword.getText().toString();
                        mprogressBar.setVisibility(View.VISIBLE);
                        if (STATUS_CDODE == 1)
                            Login();
                        if (STATUS_CDODE == 2)
                            Registration();
                        if (STATUS_CDODE == 0)
                            ValidateMobile();
                    }
                } else {
                    Tst_Snake(view, "Internet connection failed !");
                }
                break;
        }
    }


    private void ResponceListner(String string) {
        try {
            JSONObject flag = new JSONObject(string);
            String Status = flag.optString("status");
            String message = flag.optString("message");

            if (Status.equals("0")) {
                // Tst(this, message);
                Tst_Snake(findViewById(R.id.tv_SignUp), "Please provide correct pin for " + ph);
                return;
            } else if (Status.equals("2")) {
                String MobileNumber = flag.optString("mobile");
                ResendOTP(MobileNumber);
            } else {
                JSONObject mainf = flag.optJSONObject("user_info");

                String user_id = mainf.getString("user_id");
                String Email = mainf.getString("user_email");
                String UserName = mainf.getString("user_name");
                String MobileNo = mainf.getString("user_mob");
                String user_img = mainf.getString("user_img");
                String family_id = mainf.getString("family_id");
                String family_name = mainf.getString("family_name");
                String IsFamilyAdmin = mainf.getString("isFamilyAdmin");

                commonFunctions.myPreference.setString(this, GlobalConstants.USER_ID, user_id);
                commonFunctions.myPreference.setString(this, GlobalConstants.USER_NAME, UserName);
                commonFunctions.myPreference.setString(this, GlobalConstants.MOBILE_only, MobileNo);
                commonFunctions.myPreference.setString(this, GlobalConstants.EMAIL, Email);
                commonFunctions.myPreference.setString(this, GlobalConstants.USER_IMAGE, user_img);
                commonFunctions.myPreference.setString(this, GlobalConstants.LOGIN_STATUS, "1");

                commonFunctions.myPreference.setString(this, GlobalConstants.FAMILY_ID, family_id);
                commonFunctions.myPreference.setString(this, GlobalConstants.FAMILY_NAME, family_name);
                commonFunctions.myPreference.setString(this, GlobalConstants.IsFamilyAdmin, IsFamilyAdmin);

                registerDeviceId(user_id);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ex) {
        }

    }

    //-----------------------------------------------------------------forget password
    private void ForgetPaasword() {

        dialog = new Dialog(SignInUpActivity.this);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.forgetpassword_dialog);
        final ImageView pencil = (ImageView) dialog.findViewById(R.id.pencil);
        final Button lay11 = (Button) dialog.findViewById(R.id.lay11);

        final Button send = (Button) dialog.findViewById(R.id.send);
        final Button cancel = (Button) dialog.findViewById(R.id.cancel);
        final EditText ETemail = (EditText) dialog.findViewById(R.id.edt_email);
        ETemail.setHint("Please enter your mobile No.");
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
                if (TextUtils.isEmpty(email)) {
                    ETemail.setError("Please enter an mobile No.");
                    return;
                } else {
                    commonFunctions.hideKeyboard(v);
                    // call service listener..
                    if (isInternetAvailable()) {

                        myservice = new CallWebService(SignInUpActivity.this, urlList.forgot_pass, commonFunctions.forgot_pass(email), new MyServiceListener() {
                            @Override
                            public void onSuccess(String string) {
                                try {
                                    Log.d("--------", string);
                                    JSONObject flag = new JSONObject(string);
                                    String status = flag.optString("status");
                                    String message = flag.optString("message");
                                    if (status.equals("1")) {
                                        // Tst(getApplication(), "Your password has been sent to your email.");
                                        Tst(getApplication(), message);
                                        dialog.dismiss();
                                    } else {
                                        Tst(getApplication(), message);
                                    }
                                } catch (JSONException je) {
                                    Tst(getApplication(), "The request could not be completed. Please try again.");
                                    dialog.dismiss();
                                } catch (Exception ex) {
                                }
                            }

                            @Override
                            public void onFailed() {
                                dialog.dismiss();
                            }
                        });
                    } else {
                        Tst_Snake(findViewById(R.id.bt_SignIn), "Internet connection failed !");
                    }
                }
            }
        });

        dialog.show();
    }

    private void ResendOTP(final String MobileNumber) {
        new CallWebService(SignInUpActivity.this, urlList.resend_otp, commonFunctions.resend_otp(MobileNumber), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    Log.d("--------", string);
                    JSONObject flag = new JSONObject(string);
                    String status = flag.optString("status");
                    String message = flag.optString("message");
                    if (status.equals("1")) {
                        Intent intent = new Intent(SignInUpActivity.this, VerifyPin.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("mobile", MobileNumber);
                        intent.putExtras(bundle);
                        AppUtil.startActivityWithAnimation(SignInUpActivity.this, intent);
                        finish();
                        Tst(getApplication(), message);

                    } else {
                        Tst(getApplication(), message);
                    }
                } catch (JSONException je) {
                    Tst(getApplication(), "The request could not be completed. Please try again.");
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
                dialog.dismiss();
            }
        });
    }

    public boolean validate(String value) {
        if (value.trim().isEmpty()) {
            mprogressBar.setVisibility(View.GONE);
            Tst_Snake(mprogressBar, "Please provide correct credentials.");
            return false;
        }
        return true;
    }

    private boolean isValidMobile(String phone) {
         if(!Pattern.matches("[a-zA-Z]+", phone)) {
            if(phone.length() < 6 || phone.length() > 13) {
                // if(phone.length() != 10) {

                Tst_Snake(mprogressBar,"Enter Valid Number");
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public void onclick_terms_condition(View v) {
        AppUtil.startActivityWithAnimation(SignInUpActivity.this, new Intent(SignInUpActivity.this, TermAndConditionsAc.class));
    }

    public void ValidateMobile() {
        mprogressBar.setVisibility(View.GONE);
        if (isValidMobile(ph) ) {
            mprogressBar.setVisibility(View.VISIBLE);
            new CallWebService("", SignInUpActivity.this, urlList.registration_step1, commonFunctions.reg_with_mob("nm", "eml", ph, "pin"), new MyServiceListener() {
                @Override
                public void onSuccess(String string) {
                    mprogressBar.setVisibility(View.GONE);
                    try {
                        JSONObject flag = new JSONObject(string);
                        String Status = flag.optString("status");
                        String message = flag.optString("message");
                        if (Status.equals("0")) {
                            Tst(getApplicationContext(), message);
                            return;
                        } else if (Status.equals("1")) {
                            // for enter pin
                            STATUS_CDODE = 1;
                            // etEmail.setVisibility(View.GONE);
                            etPassword.setVisibility(View.VISIBLE);
                        } else if (Status.equals("2")) {
                            // for registration
                            STATUS_CDODE = 2;
                            etEmail.setVisibility(View.GONE);
                            edtUsername.setVisibility(View.VISIBLE);
                            etPassword.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception ex) {
                    }
                }

                @Override
                public void onFailed() {
                    mprogressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    public void Login() {
        //for status 1
        if (isValidMobile(ph))
            new CallWebService("", SignInUpActivity.this, urlList.LOG_IN, commonFunctions.LOG_IN(ph, pin), new MyServiceListener() {
                @Override
                public void onSuccess(String string) {
                    mprogressBar.setVisibility(View.GONE);
                    ResponceListner(string);
                }

                @Override
                public void onFailed() {
                    mprogressBar.setVisibility(View.GONE);
                }
            });
    }

    public void Registration() {
        //for status 2
        if (validate(ph) && validate(nm) && validate(pin))
            new CallWebService("", SignInUpActivity.this, urlList.registration_step2, commonFunctions.reg_with_mob(nm, "", ph, pin), new MyServiceListener() {
                @Override
                public void onSuccess(String string) {
                    mprogressBar.setVisibility(View.GONE);
                    try {
                        Log.d("--------", string);
                        JSONObject flag = new JSONObject(string);
                        String status = flag.optString("status");
                        String message = flag.optString("message");
                        if (status.equals("1")) {
                            Intent intent = new Intent(SignInUpActivity.this, VerifyPin.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("mobile", ph);
                            intent.putExtras(bundle);
                            AppUtil.startActivityWithAnimation(SignInUpActivity.this, intent);
                            finish();
                            Tst(getApplication(), message);

                        } else {
                            Tst(getApplication(), message);
                        }
                    } catch (JSONException je) {
                        Tst(getApplication(), "The request could not be completed. Please try again.");
                    }
                }

                @Override
                public void onFailed() {
                    mprogressBar.setVisibility(View.GONE);
                }
            });
    }

    public void hidekeyboard(View view) {
        commonFunctions.hideKeyboard(view);
    }
    //================================================================================
}


