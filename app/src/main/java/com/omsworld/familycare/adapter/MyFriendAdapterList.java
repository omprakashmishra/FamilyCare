package com.omsworld.familycare.adapter;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omsworld.familycare.Chat.Chat_Fr;
import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.MyServiceListener;
import com.omsworld.familycare.api_call.UrlList;
import com.omsworld.familycare.fragment.FamilyMembers_Fr;
import com.omsworld.familycare.model.JoinSafeJoinModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyFriendAdapterList extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    public static Context context;
    static CommonFunctions cmf;
    public List<JoinSafeJoinModel> mDataset;
    Dialog canceljob;
    TextView TV_Message;
    Button ok, no;
    String InvitationIDs;
    String phone;
    String myUserId, IsFamilyAdmin, IsFamilyId;


    public MyFriendAdapterList(List<JoinSafeJoinModel> mDataset, Context context) {
        this.mDataset = mDataset;
        this.context = context;
        cmf = new CommonFunctions(context);
        myUserId = cmf.myPreference.getString(context, cmf.gc.USER_ID);
        IsFamilyAdmin = cmf.myPreference.getString(context, cmf.gc.IsFamilyAdmin);
        IsFamilyId = cmf.myPreference.getString(context, cmf.gc.FAMILY_ID);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ContactViewHolder) {
            final ContactViewHolder contactViewHolder = (ContactViewHolder) holder;


           /* if (mDataset.get(position).getOnlineStatus().equals("Offline")) {
                contactViewHolder.TV_Status.setText("Offline");
                //contactViewHolder.TV_Status.setText(mDataset.get(position).g());
                contactViewHolder.TV_Status.setTextColor(Color.parseColor("#ff471a"));
            } else {
                contactViewHolder.TV_Status.setText("Online");
                contactViewHolder.TV_Status.setTextColor(Color.parseColor("#008000"));
            }*/

            contactViewHolder.RL_friends.setVisibility(View.GONE);
            contactViewHolder.RL_accept.setVisibility(View.GONE);


            if (mDataset.get(position).getRequest_type().equals("freind_request")) {
                contactViewHolder.RL_accept.setVisibility(View.VISIBLE);
                contactViewHolder.TV_familyName.setText("Family: " + mDataset.get(position).getFamily_name());
                contactViewHolder.Nameid.setText("Owner: " + mDataset.get(position).getUser_name());
                contactViewHolder.TV_Phone_request.setText(mDataset.get(position).getUser_mobile());

                /*contactViewHolder.TV_Status.setText(mDataset.get(position).getAddress());
                contactViewHolder.TV_datetime.setText(mDataset.get(position).getTime());*/

            } else if (mDataset.get(position).getRequest_type().equals("sent_request")) {
                //WIP...
            } else {
                //friend list..

                //-----------------------
                if (IsFamilyAdmin.equals("1")) {
                    // owner can not exit self
                    if (mDataset.get(position).getUser_id().equals(myUserId)) {
                        contactViewHolder.IV_decline_exit.setVisibility(View.GONE);
                    } else {
                        contactViewHolder.IV_decline_exit.setVisibility(View.VISIBLE);
                        contactViewHolder.IV_decline_exit.setOnClickListener(this);
                        contactViewHolder.IV_decline_exit.setTag(position);
                    }
                } else {
                    //  not owner but other can exit self only..
                    if (mDataset.get(position).getUser_id().equals(myUserId)) {
                        contactViewHolder.IV_decline_exit.setVisibility(View.VISIBLE);
                        contactViewHolder.IV_decline_exit.setOnClickListener(this);
                        contactViewHolder.IV_decline_exit.setTag(position);
                        contactViewHolder.RL_chatNotif.setVisibility(View.GONE);
                        contactViewHolder.IV_phonebookImage.setVisibility(View.GONE);
                    }
                }
                //------------------
                contactViewHolder.RL_friends.setVisibility(View.VISIBLE);
                contactViewHolder.TVName.setText(mDataset.get(position).getUser_name());
                contactViewHolder.TV_Status.setText(mDataset.get(position).getAddress());
                contactViewHolder.TV_datetime.setText(mDataset.get(position).getTime());

                if (mDataset.get(position).getMember_status().equals("Owner")) {
                    contactViewHolder.TV_Phone.setText("Family Admin " + mDataset.get(position).getUser_mobile());
                    contactViewHolder.TV_Phone.setTextColor(context.getResources().getColor(R.color.green));
                } else {
                    contactViewHolder.TV_Phone.setText(mDataset.get(position).getUser_mobile());
                }

                loadImage(mDataset.get(position).getUser_image(),contactViewHolder.CIV_profile_image,contactViewHolder.mprogressBar);

            }


            contactViewHolder.TV_datetime.setText(mDataset.get(position).getTime());


            contactViewHolder.IV_phonebookImage.setOnClickListener(this);
            contactViewHolder.IV_phonebookImage.setTag(position);

            contactViewHolder.RL_chatNotif.setOnClickListener(this);
            contactViewHolder.RL_chatNotif.setTag(position);

            phone = contactViewHolder.TV_Phone.getText().toString().trim();


            contactViewHolder.BT_accept.setOnClickListener(this);
            contactViewHolder.BT_accept.setTag(position);
            contactViewHolder.Bn_declineR.setOnClickListener(this);
            contactViewHolder.Bn_declineR.setTag(position);

            contactViewHolder.RL_friends.setOnClickListener(this);
            contactViewHolder.RL_friends.setTag(position);
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.myfriends_itemlistview, viewGroup, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onClick(View view) {
        Integer position = (Integer) view.getTag();
        switch (view.getId()) {
            case R.id.IV_decline_exit:

                String fmid = mDataset.get(position).getFamily_id();
                String mobile = mDataset.get(position).getUser_mobile();

                String admin = cmf.myPreference.getString(context, cmf.gc.IsFamilyAdmin);
                if (admin.equals("1")) {
                    //http://108.170.54.215/App_development/Tracking/Api/remove_family_group_member.php?user_id=2&&family_id=1&&member_mob=1346546546
                    exitFromJoinedFamily(position, cmf.urlList.remove_family_group_member,
                            cmf.remove_family_group_member(myUserId, fmid, mobile));
                } else {
                    //http://108.170.54.215/App_development/Tracking/Api/leave_family_group.php?user_id=1&&family_id=1
                    exitFromJoinedFamily(position, cmf.urlList.leave_family_group,
                            cmf.family_group_info(myUserId, fmid));
                }

                break;
            case R.id.IV_phonebookImage:
                String Phone = mDataset.get(position).getUser_mobile();
                phoneCall(Phone);
                break;
            case R.id.RL_chatNotif:
                Fragment fragment = new Chat_Fr();
                cmf.bdl.putString("friend_Name", mDataset.get(position).getUser_name());
                cmf.bdl.putString("friend_Img", mDataset.get(position).getUser_image());
                cmf.bdl.putString("friend_Id", mDataset.get(position).getUser_id());
                cmf.bdl.putString("friend_Phone", mDataset.get(position).getUser_mobile());
                fragment.setArguments(cmf.bdl);

                cmf.replaceFragment(context, fragment);
                break;

            case R.id.BT_accept:
                acceptFamilyRequest(position);
                break;
            case R.id.Bn_declineR:
                DeleteRequest(position);
                break;
            case R.id.RL_friends:
                view_profile(position);
                break;
        }
    }

    private void phoneCall(String Phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        // String Phone = phone;
        //String CallPhone = Phone.replaceAll("\\D+", "");
        intent.setData(Uri.parse("tel:" + Phone));
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
        }
    }

    private void loadImage(String image, ImageView IV_profile_image , final ProgressBar mprogressBar){
        try {
            mprogressBar.setVisibility(View.VISIBLE);
            Picasso.with(context).load(image).placeholder(R.drawable.user_ic)
                    .error(R.drawable.user_ic)

                    .into(IV_profile_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            mprogressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            mprogressBar.setVisibility(View.GONE);
                        }
                    });
        } catch (Exception ex) {
            mprogressBar.setVisibility(View.GONE);
        }
    }

    private void view_profile(final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(true);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.user_info_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        //-------------------------------------------------------------------------------

        final ImageView IV_header = (ImageView) dialog.findViewById(R.id.IV_header);
        final TextView tv_name = (TextView) dialog.findViewById(R.id.tv_name);
        final TextView tv_about = (TextView) dialog.findViewById(R.id.tv_about);
        final TextView tv_dob = (TextView) dialog.findViewById(R.id.tv_dob);
        final TextView TV_last_seen = (TextView) dialog.findViewById(R.id.TV_last_seen);
        final TextView TV_address = (TextView) dialog.findViewById(R.id.TV_address);
        final ImageView IV_navigate = (ImageView) dialog.findViewById(R.id.IV_navigate);
        final ImageView IV_message = (ImageView) dialog.findViewById(R.id.IV_message);
        final ImageView IV_call = (ImageView) dialog.findViewById(R.id.IV_call);
        final TextView BT_cancel = (TextView) dialog.findViewById(R.id.TV_close);
        final ProgressBar  mprogressBar = (ProgressBar) dialog.findViewById(R.id.mprogressBar);


        loadImage(mDataset.get(position).getUser_image(),IV_header,mprogressBar);
        tv_name.setText(mDataset.get(position).getUser_name());
        TV_address.setText(mDataset.get(position).getAddress());
        tv_about.setText(mDataset.get(position).getMember_status());
        TV_last_seen.setText("Last Seen- "+mDataset.get(position).getTime());

//-------------------------------------------------------------------------------

        IV_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleMapNavigation(mDataset.get(position).getLat(), mDataset.get(position).getLng());
            }
        });
        IV_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new Chat_Fr();
                cmf.bdl.putString("friend_Name", mDataset.get(position).getUser_name());
                cmf.bdl.putString("friend_Img", mDataset.get(position).getUser_image());
                cmf.bdl.putString("friend_Id", mDataset.get(position).getUser_id());
                cmf.bdl.putString("friend_Phone", mDataset.get(position).getUser_mobile());
                fragment.setArguments(cmf.bdl);
                cmf.replaceFragment(context, fragment);
                dialog.dismiss();
            }
        });
        IV_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Phone = mDataset.get(position).getUser_mobile();
                phoneCall(Phone);
                dialog.dismiss();
            }
        });
        BT_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void googleMapNavigation(String SneedyLat, String SneedyLong) {
        String Snewlattitude = cmf.myPreference.getString(context, cmf.gc.JOB_NEW_LATITUDE);
        String Snewlongitude = cmf.myPreference.getString(context, cmf.gc.JOB_NEW_LONGITUDE);
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + Snewlattitude + "," + Snewlongitude + "&daddr=" + SneedyLat + "," + SneedyLong));
        //   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void acceptFamilyRequest(int positions) {
        final int pos = positions;
        canceljob = new Dialog(context);
        canceljob.setContentView(R.layout.dialog_deletefriend);
        canceljob.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ok = (Button) canceljob.findViewById(R.id.ok);
        no = (Button) canceljob.findViewById(R.id.no);

        TextView TV_Message = (TextView) canceljob.findViewById(R.id.message);
        String fmnm = mDataset.get(positions).getFamily_name();
        if (IsFamilyAdmin.equals("1")) {
            TV_Message.setText("Are you sure you want to change from Family ?");
        } else if (IsFamilyId.length() != 0 && !IsFamilyAdmin.equals("1")) {
            TV_Message.setText("Are you sure you want to change your Family ?");
        } else {
            TV_Message.setText("Are you sure you want to Join this Family ?");
        }


        ok.setVisibility(View.VISIBLE);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AcceptWithoutCodeApi(pos);
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

    // for new request
    public void AcceptWithoutCodeApi(int postion) {
        String requestFamilyId = mDataset.get(postion).getFamily_id();
        // String from_phone = cmf.myPreference.getString(context, GlobalConstants.MOBILE_only);
        final int post = postion;
        String action = "";
        if (IsFamilyAdmin.equals("1")) {
            action = "3";
        } else if (IsFamilyId.length() != 0 && !IsFamilyAdmin.equals("1")) {
            action = "4";
        } else {
            action = "1";
        }
        //http://108.170.54.215/App_development/Tracking/Api/family_request_action.php?user_id=18&&family_id=1&&action=1
        new CallWebService(context, cmf.urlList.family_request_action, cmf.family_request_action(myUserId, requestFamilyId, action), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject flag = new JSONObject(string);
                    String Status = flag.getString("success");
                    String message = flag.getString("message");
                    if (Status.equals("1")) {
                        // mDataset.remove(post);
                        //notifyDataSetChanged();

                        // String familyName=flag.getString("family_name");
                        // IsFamilyId=flag.getString("family_id");
                        // cmf.myPreference.setString(context,cmf.gc.FAMILY_ID,IsFamilyId);
                        // cmf.myPreference.setString(context,cmf.gc.FAMILY_NAME,familyName);
                        //  cmf.myPreference.setString(context,cmf.gc.IsFamilyAdmin,"0");
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        cmf.replaceFragment(context, new FamilyMembers_Fr());
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

    public void DeleteRequest(int positions) {
        {
            final int pos = positions;
            canceljob = new Dialog(context);
            canceljob.setContentView(R.layout.dialog_deletefriend);
            canceljob.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ok = (Button) canceljob.findViewById(R.id.ok);
            no = (Button) canceljob.findViewById(R.id.no);

            TextView TV_Message = (TextView) canceljob.findViewById(R.id.message);
            String fmnm = mDataset.get(positions).getFamily_name();
            TV_Message.setText("Are you sure you want to delete family request ?");

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

    public void DeclineCallApi(int postion) {
        String requestFamilyId = mDataset.get(postion).getFamily_id();
        final int post = postion;
        // http://108.170.54.215/App_development/Tracking/Api/family_request_action.php?user_id=18&&family_id=1&&action=2
        new CallWebService(context, UrlList.family_request_action, cmf.family_request_action(myUserId, requestFamilyId, "2"), new MyServiceListener() {
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
    //------------------------------------------

    public void exitFromJoinedFamily(final int position, final String url, final Map<String, String> params) {
        canceljob = new Dialog(context);
        canceljob.setContentView(R.layout.dialog_deletefriend);
        canceljob.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ok = (Button) canceljob.findViewById(R.id.ok);
        no = (Button) canceljob.findViewById(R.id.no);

        TV_Message = (TextView) canceljob.findViewById(R.id.message);
        String username = mDataset.get(position).getUser_name();
        if (IsFamilyAdmin.equals("1")) {
            TV_Message.setText("Are you sure you want to remove " + username + " from your FAMILY list?");
        } else {
            TV_Message.setText("Are you sure you want to leave from current Family?");
        }
        ok.setText("YES");
        ok.setVisibility(View.VISIBLE);
        no.setVisibility(View.VISIBLE);
        no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                canceljob.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                canceljob.dismiss();
                DeleteUserCallApi(position, url, params);

            }
        });
        canceljob.show();
    }

    public void DeleteUserCallApi(final int position, String url, Map<String, String> params) {
        String InvitationID = InvitationIDs;
        new CallWebService(context, url, params, new MyServiceListener() {
            @Override
            public void onSuccess(String string) {

                Log.d("---------->Request--->", string);
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    String Status = jsonObject.optString("status");
                    String message = jsonObject.optString("message");
                    if (Status.equals("1")) {
                      /*  mDataset.remove(position);
                        notifyDataSetChanged();

                        Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();*/

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        cmf.replaceFragment(context, new FamilyMembers_Fr() );

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

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar mprogressBar;
        Button BT_accept, Bn_declineR;
        private TextView TVName, TV_Address, TV_Phone, TV_Status, TV_datetime, TV_familyName;
        private TextView Nameid, TV_Phone_request;
        private ImageView IV_decline_exit, IV_phonebookImage;
        private CircleImageView CIV_profile_image;
        private RelativeLayout RL_chatNotif, RL_friends, RL_accept;

        public ContactViewHolder(View v) {
            super(v);
            TVName = (TextView) v.findViewById(R.id.TV_Name);
            RL_chatNotif = (RelativeLayout) v.findViewById(R.id.RL_chatNotif);
            RL_friends = (RelativeLayout) v.findViewById(R.id.RL_friends);
            TV_Address = (TextView) v.findViewById(R.id.TV_Address);
            TV_Phone = (TextView) v.findViewById(R.id.TV_Phone);
            TV_Status = (TextView) v.findViewById(R.id.TV_LocationStatus);
            TV_datetime = (TextView) v.findViewById(R.id.TV_datetime);
            TV_familyName = (TextView) v.findViewById(R.id.TV_familyName);
            IV_decline_exit = (ImageView) v.findViewById(R.id.IV_decline_exit);
            IV_phonebookImage = (ImageView) v.findViewById(R.id.IV_phonebookImage);
            CIV_profile_image = (CircleImageView) v.findViewById(R.id.CIV_profile_image);
            mprogressBar = (ProgressBar) v.findViewById(R.id.mprogressBar);

            // for new request..
            RL_accept = (RelativeLayout) v.findViewById(R.id.RL_accept);
            Nameid = (TextView) v.findViewById(R.id.Nameid);
            TV_Phone_request = (TextView) v.findViewById(R.id.TV_Phone_request);
            Bn_declineR = (Button) v.findViewById(R.id.Bn_declineR);
            BT_accept = (Button) v.findViewById(R.id.BT_accept);
        }
    }
}
