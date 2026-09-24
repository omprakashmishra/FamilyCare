package com.omsworld.familycare.api_call;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.omsworld.familycare.R;

/**
 * Created by omprakash.m on 8/1/2017.
 */

public class MyPlayer {
    public static MediaPlayer mp;

    public void mp_start(Context context) {
        try {
            mp = MediaPlayer.create(context, R.raw.alarmupdated);
            if (mp.isPlaying()) {
                return;
            } else {
                mp.start();
            }
        } catch (Exception ex) {
            Log.e("-----Mediaplayerexception", ex.toString());
        }
    }

    public void mp_stop(Context context) {
        try {
            mp = MediaPlayer.create(context, R.raw.alarmupdated);
            if (mp.isPlaying()) {
                mp.stop();
            }
        } catch (Exception ex) {
            Log.e("-----Mediaplayerexception", ex.toString());
        }
    }
}
