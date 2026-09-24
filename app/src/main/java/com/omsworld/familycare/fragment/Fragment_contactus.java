package com.omsworld.familycare.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.MyServiceListener;

import org.json.JSONObject;



public class Fragment_contactus extends Fragment implements View.OnClickListener {
    private View rootView;
    private TextView landlineno, indiano, serviceno, email;
    private Intent intent;
    private String tollfree, CallPhone;
    TextView UsWebsiteUrl, IndiaWebsiteUrl;
    TextView sendComment;
    EditText writeComment;
    String writeCommentes;
    CommonFunctions cmf;
    String MyUSER_ID;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contactus, container, false);
        initilize();
        return rootView;
    }

    private void initilize() {
        cmf=new CommonFunctions(getActivity());
        MyUSER_ID=cmf.myPreference.getString(getContext(),cmf.gc.USER_ID);

        landlineno = (TextView) rootView.findViewById(R.id.landlineno);
        indiano = (TextView) rootView.findViewById(R.id.indiano);
        serviceno = (TextView) rootView.findViewById(R.id.serviceno);
        email = (TextView) rootView.findViewById(R.id.email);
        UsWebsiteUrl = (TextView) rootView.findViewById(R.id.UsWebsite_Url);
        IndiaWebsiteUrl = (TextView) rootView.findViewById(R.id.India_Website_Url);

       // mprogressBar = (ProgressBar) rootView.findViewById(R.id.mprogressBar);
        sendComment = (TextView) rootView.findViewById(R.id.sendComment);
        sendComment.setOnClickListener(this);
        writeComment = (EditText) rootView.findViewById(R.id.writeComment);

        landlineno.setOnClickListener(this);
        indiano.setOnClickListener(this);
        serviceno.setOnClickListener(this);
        email.setOnClickListener(this);
        UsWebsiteUrl.setOnClickListener(this);
        IndiaWebsiteUrl.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.landlineno:
                intent = new Intent(Intent.ACTION_DIAL);
                tollfree = landlineno.getText().toString().trim();
                //CallPhone = tollfree.replaceAll("\\D+", "");
                intent.setData(Uri.parse("tel:" + tollfree));
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                }
                break;
            case R.id.indiano:
                intent = new Intent(Intent.ACTION_DIAL);
                tollfree = indiano.getText().toString().trim();
                //CallPhone = tollfree.replaceAll("\\D+", "");
                intent.setData(Uri.parse("tel:" + tollfree));
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                }
                break;

            case R.id.serviceno:
                Intent i1 = new Intent(Intent.ACTION_SEND);
                i1.setType("message/rfc822");
                i1.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@osoftec.com"});
                i1.putExtra(Intent.EXTRA_SUBJECT, "");
                i1.putExtra(Intent.EXTRA_TEXT, "");
                try {
                    startActivity(Intent.createChooser(i1, " "));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                }
                break;
            case R.id.email:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@osoftec.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "");
                i.putExtra(Intent.EXTRA_TEXT, "");
                try {
                    startActivity(Intent.createChooser(i, " "));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                }

                break;
            case R.id.UsWebsite_Url:
                Uri uri = Uri.parse("http://adectec.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.India_Website_Url:
                Uri uri1 = Uri.parse("http://osoftec.com");
                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri1);
                startActivity(intent1);
                break;
            case R.id.sendComment:
                if (!TextUtils.isEmpty(writeComment.getText().toString())) {

                    writeCommentes = writeComment.getText().toString();
                    writeComment.setText("");

                    SendCommentApi(writeCommentes);

                } else {
                    // Toast.makeText(getActivity(), "Please enter a comment.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

   private void SendCommentApi(final String writeCommentes) {
   new CallWebService("", getActivity(), cmf.urlList.user_suggestion, cmf.user_suggestion(MyUSER_ID,writeCommentes), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    //String Status = jsonObject.optString("status");
                    String Message = jsonObject.optString("message");
                    Toast.makeText(getContext(),Message,Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
            }
        });
    }


}


