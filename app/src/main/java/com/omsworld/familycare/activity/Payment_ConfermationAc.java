package com.omsworld.familycare.activity;

import android.content.Intent;
import android.os.Bundle;

import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.omsworld.familycare.setting.BaseActivity;


import butterknife.ButterKnife;
import butterknife.OnClick;

public class Payment_ConfermationAc extends BaseActivity {

    TextView tvUsername;
    TextView tvPackagename;
    Button BtBackToLogin;
    TextView tvActivationCode;
    WebView WVMessage;

    private String ActivationCode, SubscriptionID, UserName, PackageName;
    final String mimeType = "text/html";
    final String encoding = "UTF-8";
    CommonFunctions cmf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment__confermation_ac);
        ButterKnife.bind(this);
        initview();

    }

    private void initview() {
        idset();
        cmf = new CommonFunctions(this);
        UserName = getIntent().getStringExtra(GlobalConstants.USER_NAME);
        PackageName = getIntent().getStringExtra(GlobalConstants.PACKAGE_NAME);
        SubscriptionID = getIntent().getStringExtra(GlobalConstants.SUBSCRIPTION_ID);
        ActivationCode = getIntent().getStringExtra("ActivationCode");

        tvUsername.setText(UserName);
        tvPackagename.setText(PackageName);
        tvActivationCode.setText(ActivationCode);

        String html = getIntent().getStringExtra(GlobalConstants.PAY_SUB_RESPONSE);
        WVMessage.loadDataWithBaseURL("", html, mimeType, encoding, "");
    }

    private void idset() {
        tvUsername=(TextView)findViewById(R.id.tv_username);
        tvPackagename  =(TextView)findViewById(R.id.tv_packagename);
        BtBackToLogin=(Button) findViewById(R.id.Bt_back_to_login);
        tvActivationCode =(TextView)findViewById(R.id.tv_activation_code);
        WVMessage=(WebView)findViewById(R.id.WV_message);
    }

    @OnClick(R.id.Bt_back_to_login)
    public void onClick() {
        Intent intent = new Intent(Payment_ConfermationAc.this, MainActivity.class);
        if (cmf.myPreference.getString(this, GlobalConstants.PaymentStatusCode).equals("1069")) {
            intent.putExtra("paymentProcess", "done");
        }
        startActivity(intent);
        finish();
       /* if (commonFunctions.myPreference.getString(Payment_ConfermationAc.this, GlobalConstants.LOGIN_STATUS).equals("1")) {
            Intent intent = new Intent(Payment_ConfermationAc.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
             GotoNextActivityClearTop(Payment_ConfermationAc.this, SignInActivity.class);
        }*/

    }
}
