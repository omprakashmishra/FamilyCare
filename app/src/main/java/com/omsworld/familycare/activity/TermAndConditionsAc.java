package com.omsworld.familycare.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.AppUtil;
import com.omsworld.familycare.api_call.CommonFunctions;


public class TermAndConditionsAc extends Activity {
    final String mimeType = "text/html";
    final String encoding = "UTF-8";
    WebView WVMessage;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term_and_conditions_ac);
        Intview();
    }

    private void Intview() {
        CommonFunctions cmf = new CommonFunctions(this);
        boolean checkcdr = cmf.cdr.isConnectingToInternet();
        // Log.d("------->Call webservice connection--", String.valueOf(checkcdr));
        if (!checkcdr) {
            Toast.makeText(this, "Please connect to internet", Toast.LENGTH_SHORT).show();
            //getFragmentManager().popBackStack();
        } else {
            final ProgressDialog progressdialog = new ProgressDialog(this);
            progressdialog.setCancelable(false);
            progressdialog.setTitle("Loading …");
            progressdialog.show();
            WVMessage = (WebView) findViewById(R.id.WV_Termcondition);
            WVMessage.loadUrl(cmf.urlList.term_condition);
            WVMessage.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    progressdialog.dismiss();
                }

            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppUtil.finishActivityWithAnimation(this);
    }


    public void onclick_IVBack(View view) {
        AppUtil.finishActivityWithAnimation(this);
    }
}
