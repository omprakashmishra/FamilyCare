package com.omsworld.familycare.adapter;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class SentFriendAdapterList extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    public List<JoinSafeJoinModel> mDataset;
    public static Context context;
    Dialog canceljob;
    TextView TV_Message;
    Button ok;
    String InvitationIDs;
    String phone;
    private CommonFunctions cmf;

    public SentFriendAdapterList(List<JoinSafeJoinModel> mDataset, Context context) {
        this.mDataset = mDataset;
        this.context = context;
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
            contactViewHolder.TVName.setText(mDataset.get(position).getNearAndDearName());
            contactViewHolder.TV_Phone.setText(mDataset.get(position).getNearAndDearMobileNumber());
            contactViewHolder.TV_UserStatus.setText(mDataset.get(position).getUserStatus());


            if ((mDataset.get(position).getIsDeclined().equals("1"))) {
                contactViewHolder.IV_retry.setVisibility(View.VISIBLE);
                //contactViewHolder.IV_Delet.setVisibility(View.VISIBLE);
                // contactViewHolder.LL_layoutSentList.setBackground(R.drawable.layerdark);
                contactViewHolder.LL_layoutSentList.setBackgroundResource(R.drawable.layerdark);
            } else {
                contactViewHolder.IV_retry.setVisibility(View.GONE);
                // contactViewHolder.IV_Delet.setVisibility(View.GONE);
                contactViewHolder.LL_layoutSentList.setBackgroundResource(0);
            }
            if ((mDataset.get(position).getIsAccepted().equals("1"))) {
                contactViewHolder.IV_Delet.setVisibility(View.GONE);
            } else {
                contactViewHolder.IV_Delet.setVisibility(View.VISIBLE);
            }
            contactViewHolder.IV_retry.setOnClickListener(this);
            contactViewHolder.IV_retry.setTag(position);
            contactViewHolder.IV_phonebookImage.setOnClickListener(this);
            contactViewHolder.IV_phonebookImage.setTag(position);
            phone = contactViewHolder.TV_Phone.getText().toString().trim();

            contactViewHolder.IV_Delet.setOnClickListener(this);
            contactViewHolder.IV_Delet.setTag(position);
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sentfriend_list, viewGroup, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.IV_retry:
                Integer position = (Integer) view.getTag();
                // Toast.makeText(context, "Retry", Toast.LENGTH_SHORT).show();
                String InvitationID = mDataset.get(position).getInvitationID();
                resentRequest(InvitationID, position);
                break;
            case R.id.IV_phonebookImage:

                Intent intent = new Intent(Intent.ACTION_DIAL);
                String Phone = phone;
                //String CallPhone = Phone.replaceAll("\\D+", "");
                intent.setData(Uri.parse("tel:" + Phone));
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                }
                break;
            case R.id.IV_Delet:
                Integer position1 = (Integer) view.getTag();
                String InvitationID1 = mDataset.get(position1).getInvitationID();
                // Toast.makeText(context, "cancel", Toast.LENGTH_SHORT).show();
                deleteSentRequest(InvitationID1, position1, mDataset.get(position1).getNearAndDearName());
                break;
        }
    }

    private void resentRequest(String InvitationID, final int position) {

        new CallWebService(context, UrlList.RESENDINVITATION, cmf.Resendinvitation(InvitationID), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {

                try {
                    JSONObject jsonObject = new JSONObject(string);
                    int Status = jsonObject.optInt("Status");
                    String Message = jsonObject.optString("Message");
                    JSONArray jsonArray = jsonObject.optJSONArray("SentData");
                    if (Status == 1) {
                        mDataset.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
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

    private void deleteSentRequest(String InvitationID, final int position, final String NearAndDearName) {
        new CallWebService(context, UrlList.DELETEUSER, cmf.DeleteUser(InvitationID, NearAndDearName), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {

                try {
                    JSONObject jsonObject = new JSONObject(string);
                    int Status = jsonObject.optInt("Status");
                    String Message = jsonObject.optString("Message");
                    JSONArray jsonArray = jsonObject.optJSONArray("SentData");

                    if (Status == 1) {
                        mDataset.remove(position);
                        notifyDataSetChanged();
                        //  Toast.makeText(context, Message,Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {

                }
            }

            @Override
            public void onFailed() {

            }
        });


    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView TVName, TV_Phone, TV_UserStatus;
        private ImageView IV_retry, IV_phonebookImage, IV_Delet;
        LinearLayout LL_layoutSentList;

        public ContactViewHolder(View v) {
            super(v);
            TVName = (TextView) v.findViewById(R.id.TV_Name);
            TV_Phone = (TextView) v.findViewById(R.id.TV_Phone);
            IV_retry = (ImageView) v.findViewById(R.id.IV_retry);
            IV_phonebookImage = (ImageView) v.findViewById(R.id.IV_phonebookImage);
            IV_Delet = (ImageView) v.findViewById(R.id.IV_Delet);
            TV_UserStatus = (TextView) v.findViewById(R.id.TV_UserStatus);
            LL_layoutSentList = (LinearLayout) v.findViewById(R.id.LL_layoutSentList);

            boolean USER_PAYED = cmf.myPreference.getBoolean(context, GlobalConstants.USER_PAYED);
            if (!USER_PAYED) {
                IV_retry.setVisibility(View.GONE);
            }
        }
    }


}
