package com.omsworld.familycare.api_call;

import android.app.Activity;
import android.content.Intent;

import com.omsworld.familycare.R;


/**
 * Created by ram.sinha on 12/17/2016.
 */

public class AppUtil {

    public static void startActivityWithAnimation(Activity activity, Intent intent) {
        try {
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } catch (Exception ex) {
        }

    }

    public static void finishActivityWithAnimation(Activity activity) {
        try {
            activity.finish();
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } catch (Exception ex) {
        }

    }

    public static void finishActivityWithAnimationRight(Activity activity) {
        try {
            activity.finish();
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } catch (Exception ex) {
        }

    }


    public static void startActivityForResultWithAnimation(Activity activity, Intent intent, int
            requestCode) {
        try {
            activity.startActivityForResult(intent, requestCode);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } catch (Exception ex) {
        }

    }


}





