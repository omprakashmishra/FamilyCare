package com.omsworld.familycare.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.omsworld.familycare.R;
import com.omsworld.familycare.Shopping.ScanActivity;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.DateTime_c;
import com.omsworld.familycare.api_call.MyServiceListener;

import org.json.JSONObject;

import butterknife.ButterKnife;


public class Scan_fr extends Fragment implements View.OnClickListener {

    TextView TVBarcodeText;
    TextView TVdateTime;
    EditText ETExtraComment;
    ImageView IVGoto;
    ImageView IVShare;
    Button BTAddToGrocery;
    ImageView IVBarcode;

    String USER_ID, barcodeText;
    private View rootView;
    private CommonFunctions cmf;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(getActivity(), ScanActivity.class));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.scan_fr, container, false);
        cmf = new CommonFunctions(getActivity());
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initilize();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!cmf.gc.scan_result.equals("")) {
            // Toast.makeText(getActivity(), scan_result, Toast.LENGTH_SHORT).show();
            TVBarcodeText.setText(cmf.gc.scan_result);
            barcodeText = cmf.gc.scan_result;
            String currentTime = DateTime_c.getInstance().formateDateTime("currentDateTime");
            cmf.myPreference.setString(getActivity(), cmf.gc.LAST_SCAN_RESULT, "Last Scan:" + currentTime + "=" + barcodeText);
            cmf.gc.scan_result = "";
        }
    }

    private void initilize() {
        TVBarcodeText = (TextView) rootView.findViewById(R.id.TV_barcodeText);
        TVdateTime = (TextView) rootView.findViewById(R.id.TV_dateTime);
        ETExtraComment = (EditText) rootView.findViewById(R.id.ET_extraComment);
        IVGoto = (ImageView) rootView.findViewById(R.id.IV_goto);
        IVShare = (ImageView) rootView.findViewById(R.id.IV_share);
        BTAddToGrocery = (Button) rootView.findViewById(R.id.BT_addToGrocery);
        IVBarcode = (ImageView) rootView.findViewById(R.id.IV_barcode);

        IVGoto.setOnClickListener(this);
        IVShare.setOnClickListener(this);
        BTAddToGrocery.setOnClickListener(this);
        IVBarcode.setOnClickListener(this);
        //-----------------------------------------------

        USER_ID = cmf.myPreference.getString(getActivity(), cmf.gc.USER_ID);

        String string = cmf.myPreference.getString(getActivity(), cmf.gc.LAST_SCAN_RESULT);
        String part1 = "";
        try {
            String[] parts = string.split("=");
            // part1 = parts[0]; // 004
            TVdateTime.setText(parts[0]);
            TVBarcodeText.setText(parts[1]);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.IV_goto:
                openInWeb();
                break;
            case R.id.IV_share:
                share();
                break;
            case R.id.BT_addToGrocery:
                createGroceryList();
                break;
            case R.id.IV_barcode:
                startActivity(new Intent(getActivity(), ScanActivity.class));
                break;
        }
    }

    private void openInWeb() {
        try {
            String openText = TVBarcodeText.getText().toString();
            if (!openText.equals("")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(openText));
                startActivity(browserIntent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void share() {
        String item =TVBarcodeText.getText().toString() + " \n" +  ETExtraComment.getText().toString();
        if (TVBarcodeText.getText().toString().equals("")) {
            return;
        }
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, item);
        sendIntent.setType("text/plain");
        getActivity().startActivity(sendIntent);
    }

    private void createGroceryList() {
        String item =TVBarcodeText.getText().toString() + " \n" +  ETExtraComment.getText().toString();
        if (TVBarcodeText.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Please Add something for shopping.", Toast.LENGTH_LONG).show();
        } else {
            new CallWebService(getActivity(), cmf.urlList.add_groceries_list, cmf.add_groceries_list(USER_ID, item), new MyServiceListener() {
                @Override
                public void onSuccess(String string) {
                    try {
                        /// Toast.makeText(getActivity(), string, Toast.LENGTH_LONG).show();
                        JSONObject jsonObject = new JSONObject(string);
                        String Status = jsonObject.optString("status");
                        String Message = jsonObject.optString("Message");
                        Toast.makeText(getActivity(), Message, Toast.LENGTH_LONG).show();


                    } catch (Exception ex) {
                        Toast.makeText(getActivity(), "Please Try Again", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailed() {
                }
            });
        }
    }


}

