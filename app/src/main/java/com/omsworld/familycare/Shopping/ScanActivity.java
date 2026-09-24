package com.omsworld.familycare.Shopping;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;
import com.omsworld.familycare.api_call.GlobalConstants;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by rupesh.m on 2/2/2018.
 */

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        //  Activity_register.ScancodeText = rawResult.getText().toString();
        GlobalConstants.scan_result = rawResult.getText().toString();
        finish();
    }
}