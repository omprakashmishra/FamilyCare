package com.omsworld.familycare.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.omsworld.familycare.api_call.MyServiceListener;
import com.omsworld.familycare.api_call.UrlList;
import com.omsworld.familycare.model.JoinSafeJoinModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by ram.sinha on 3/7/2017.
 */

public class JoinSafeJoinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    public List<JoinSafeJoinModel> mDataset;
    public Context context;
    Dialog canceljob;
    String fromPage;
    private Button ok, no;
    private CommonFunctions cmf;

    public JoinSafeJoinAdapter(List<JoinSafeJoinModel> mDataset, Context context, String fromPage) {
        this.mDataset = mDataset;
        this.context = context;
        this.fromPage = fromPage;
        cmf = new CommonFunctions(context);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ContactViewHolder) {
            ContactViewHolder contactViewHolder = (ContactViewHolder) holder;

            contactViewHolder.TVName.setText(mDataset.get(position).getUser_name());
            //  contactViewHolder.TV_Email.setText(mDataset.get(position).getNearAndDearEmail());
            contactViewHolder.TV_Phone.setText(mDataset.get(position).getUser_mobile());

            Picasso.with(context).load(mDataset.get(position).getUser_image())
                    .placeholder(R.drawable.invitation_ic)
                    .error(R.drawable.about_us_ic)
                    .into(contactViewHolder.IV_userDp);

            contactViewHolder.okButton.setOnClickListener(this);
            contactViewHolder.okButton.setTag(position);
            contactViewHolder.Bn_declineR.setOnClickListener(this);
            contactViewHolder.Bn_declineR.setTag(position);

        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.joinsafezoneiteme_listview, viewGroup, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.okButton:
                Integer position = (Integer) view.getTag();
                DialogAcceptWithoutCode(position);
               // DialogAcceptWithoutCode(position);
                break;
            case R.id.Bn_declineR:
                Integer positions = (Integer) view.getTag();
                DialogDecline(positions);
                break;
        }
    }

    public void DialogDecline(int positions) {
        {
            final int pos = positions;
            canceljob = new Dialog(context);
            canceljob.setContentView(R.layout.dialog_deletefriend);
            canceljob.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ok = (Button) canceljob.findViewById(R.id.ok);
            no = (Button) canceljob.findViewById(R.id.no);

            TextView TV_Message = (TextView) canceljob.findViewById(R.id.message);
            String username = mDataset.get(positions).getNearAndDearName();
            TV_Message.setText("Are you sure you want to decline invitation from " + username + "?");

            ok.setVisibility(View.VISIBLE);
            ok.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DeclineCallApi(pos);
                    canceljob.dismiss();
                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    canceljob.dismiss();
                }
            });
            canceljob.show();
        }
    }

    public void DialogAcceptWithoutCode(int positions) {
        final int pos = positions;
        AcceptWithoutCodeApi(pos);

    }

    public void DeclineCallApi(int postion) {
        //String InvitationID = "151041";//commonFunctions.myPreference.getString(this, GlobalConstants.TRACKINGUSERID);
        String to_phone = mDataset.get(postion).getUser_mobile();
        String from_phone = cmf.myPreference.getString(context, GlobalConstants.MOBILE_only);
        final int post = postion;
        new CallWebService(context, cmf.urlList.family_request_action, cmf.family_request_action(from_phone, to_phone, "2"), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {

                try {
                    JSONObject flag = new JSONObject(string);
                    String Status = flag.getString("success");
                    String message = flag.getString("message");
                    if (Status.equals("1")) {
                        mDataset.remove(post);
                        canceljob.dismiss();
                        notifyDataSetChanged();
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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

    public void AcceptWithoutCodeApi(int postion) {
        String to_phone = mDataset.get(postion).getUser_mobile();
        String from_phone = cmf.myPreference.getString(context, GlobalConstants.MOBILE_only);
        final int post = postion;
        new CallWebService(context, cmf.urlList.family_request_action, cmf.family_request_action(from_phone, to_phone, "1"), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {

                try {
                    JSONObject flag = new JSONObject(string);
                    String Status = flag.getString("success");
                    String message = flag.getString("message");
                    if (Status.equals("1")) {
                        mDataset.remove(post);
                        notifyDataSetChanged();
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed() {
            }
        });
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        LinearLayout TrackingLinearLayoutOnClick;
        CircleImageView IV_userDp;
        private TextView TVName, TV_Email, TV_Phone/* TVMobileNumber, TVFistName, TVMobileAliasName, TVDateTime*/;
        private Button Bn_declineR, okButton, BT_firstletter;

        public ContactViewHolder(View v) {
            super(v);
            TVName = (TextView) v.findViewById(R.id.Nameid);
            TV_Email = (TextView) v.findViewById(R.id.TV_Email);
            TV_Phone = (TextView) v.findViewById(R.id.TV_Phone);
            Bn_declineR = (Button) v.findViewById(R.id.Bn_declineR);
            okButton = (Button) v.findViewById(R.id.okButton);
            BT_firstletter = (Button) v.findViewById(R.id.BT_firstletter);
            IV_userDp = (CircleImageView) v.findViewById(R.id.IV_userDp);
        }
    }

  /*  public static void replaceFragment(Fragment fragment) {
        *//*fragment = new Fragment_aboutus();
                            replaceFragment(fragment);*//*
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }*/

}
