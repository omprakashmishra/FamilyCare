package com.omsworld.familycare.ServicesControl;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.omsworld.familycare.api_call.MyServiceListener;

import java.util.Map;

/**
 * Created by omprakash.m on 8/5/2017.
 */

public final class CallWebForService {

    final MyServiceListener myListener;
    private Context mycontext;
    private String murl;
    private Map<String, String> map_vlaue;
    private static RequestQueue requestQueue;
    private static StringRequest stringRequest;

    public CallWebForService(String NoProgress, Context context, String url, final Map<String, String> map, MyServiceListener Listener) {

        this.mycontext = context;
        this.murl = url;
        this.myListener = Listener;
        this.map_vlaue = map;
        Log.d("----murl--", url);
        //Log.d("-----context-",context.toString());
        Log.d("----map--", map.toString());
        webservices();
    }


    private void webservices() {
        try {
            requestQueue = Volley.newRequestQueue(mycontext);
            stringRequest = new StringRequest(Request.Method.POST, murl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            myListener.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            myListener.onFailed();
                            Log.e("-----VolleyError--->", error.toString());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // map_vlaue = new HashMap<String,String>();
                    return map_vlaue;
                }
            };
            stringRequest.setShouldCache(false);
            RetryPolicy policy = new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);


        } catch (Exception ex) {
        }
    }


}
