package com.omsworld.familycare.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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
import com.omsworld.familycare.activity.MainActivity;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.omsworld.familycare.api_call.MyServiceListener;
import com.omsworld.familycare.api_call.UrlList;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.omsworld.familycare.activity.MainActivity.LeftMenueOpenForPiad;


public class Fragment_Package extends Fragment implements View.OnClickListener {
    @Bind(R.id.tvPackage_Name)
    TextView tvPackageName;
    @Bind(R.id.tvStartDateValue)
    TextView tvStartDateValue;
    @Bind(R.id.tvEndDateValue)
    TextView tvEndDateValue;
    @Bind(R.id.tvRenewDateValue)
    TextView tvRenewDateValue;
    @Bind(R.id.tv_packageamount)
    TextView tvPackageamount;
    @Bind(R.id.Bt_renewSubscription)
    Button BtRenewSubscription;
    @Bind(R.id.tvPaymentStatusDescription)
    TextView tvPaymentStatusDescription;
    @Bind(R.id.TV_paymentdate)
    TextView TVpaymentdate;
    @Bind(R.id.IV_PaymentDoneSeverFailed)
    ImageView IVPaymentDoneSeverFailed;
    String subscriptionID, paymentStatus, paymentId;
    @Bind(R.id.TV_forusPayment)
    TextView TVForusPayment;

    @Bind(R.id.LL_paymentlink)
    LinearLayout LLpaymentlink;

    @Bind(R.id.LL_profileUI)
    LinearLayout LLProfileUI;

    private View rootView;
    private CommonFunctions commonFunctions;
    private String UserName, Email, MobileNo, CountryCode, UserID, PaymentStatusID;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.coc_user_package_activity, container, false);
        commonFunctions = new CommonFunctions(getActivity());
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initilize();

        String PaymentDoneSeverFailed = commonFunctions.myPreference.getString(getActivity(), GlobalConstants.PaymentDoneSeverFailed);
        if (PaymentDoneSeverFailed.equals("1")) {
            commonFunctions.myPreference.setString(getActivity(), GlobalConstants.PAYMENTSTATUS, "1069");
            commonFunctions.myPreference.setBoolean(getActivity(), GlobalConstants.USER_PAYED, true);

            subscriptionID = commonFunctions.myPreference.getString(getActivity(), GlobalConstants.PAYMENTsubscriptionID);
            paymentStatus = commonFunctions.myPreference.getString(getActivity(), GlobalConstants.paymentStatus);
            paymentId = commonFunctions.myPreference.getString(getActivity(), GlobalConstants.PAYMENTpaymentId);

            IVPaymentDoneSeverFailed.setVisibility(View.VISIBLE);

            PaymentDoneSeverFailedUpadeAPI(subscriptionID, paymentStatus, paymentId);
        }
    }

    public void makeuserInvitee() {
        //from  SelectPakageFr  and  Fragment_Package  would be change condition for unpaid user (New change - 3/Aug/2017)
        //if you are guard 1081 ---->family(invitee) 1079 ---->PAid user 1080
        final String InviteeStatusID = commonFunctions.myPreference.getString(getActivity(), GlobalConstants.INVITEE_STATUSID);
        final String PaymentStatusCode = commonFunctions.myPreference.getString(getActivity(), GlobalConstants.PaymentStatusCode);
        if (InviteeStatusID.equals("1080") && !PaymentStatusCode.equals("1069")) {
            //invitee 1079
            commonFunctions.myPreference.setString(getActivity(), GlobalConstants.INVITEE_STATUSID, "1079");
//            MainActivity.LeftMenueOpenForInvitee();
        }
    }

    private void initilize() {

        UserID = commonFunctions.myPreference.getString(getActivity(), GlobalConstants.USER_ID);
        UserName = commonFunctions.myPreference.getString(getActivity(), GlobalConstants.USER_NAME);
        Email = commonFunctions.myPreference.getString(getActivity(), GlobalConstants.EMAIL);
        MobileNo = commonFunctions.myPreference.getString(getActivity(), GlobalConstants.MOBILE_only);
        CountryCode = commonFunctions.myPreference.getString(getActivity(), GlobalConstants.COUNTYCD);

    }

    private void PackageDetails() {

        new CallWebService(getActivity(), UrlList.UserInfo, commonFunctions.UserInfo(UserName), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                Log.d("----------->Fragment Package--API-->UserInfo" + string, "");
                // Toast.makeText(getActivity(),string,Toast.LENGTH_SHORT).show();
                try {
                    final JSONObject jsonObject = new JSONObject(string);
                    int Status = jsonObject.optInt("Status");
                    String Message = jsonObject.optString("Message");
                    if (Status == 1) {
                        JSONObject jsonObject1 = jsonObject.optJSONObject("data");
                        tvPackageName.setText(jsonObject1.getString("PackageName"));

                        LLProfileUI.setVisibility(View.VISIBLE);
                        if (jsonObject1.getString("PackageName").toLowerCase().equals("lifetime")) {
                            BtRenewSubscription.setVisibility(View.GONE);
                        }
                        tvPackageamount.setText(jsonObject1.getString("Amount"));
                        tvStartDateValue.setText(jsonObject1.getString("StartDate"));
                        tvEndDateValue.setText(jsonObject1.getString("EndDate"));
                        tvRenewDateValue.setText(jsonObject1.getString("ActivationCode"));

                        String paymentStatus = jsonObject1.getString("PaymentStatusDescription");
                        PaymentStatusID = jsonObject1.getString("PaymentStatusID");


                        BtRenewSubscription.setText("Renew Subscription");
                        tvPaymentStatusDescription.setText(jsonObject1.getString("PaymentStatusDescription"));
                        if (PaymentStatusID.equals("1069")) {
                            tvPaymentStatusDescription.setTextColor(getResources().getColor(R.color.green));
                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
                            df.setTimeZone(TimeZone.getDefault());
                            String taskstartdate = df.format(Long.valueOf(jsonObject1.getString("PaymentDate")));
                            TVpaymentdate.setText("(" + taskstartdate + ")");
                        } else if (paymentStatus.equals("Awaiting Payment")) {
                            tvPaymentStatusDescription.setTextColor(getResources().getColor(R.color.dark_red));
                            BtRenewSubscription.setText("Pay");
                        } else if (paymentStatus.equals("Payment Failed")) {
                            tvPaymentStatusDescription.setTextColor(getResources().getColor(R.color.brown));
                            BtRenewSubscription.setText("Pay");
                        } else {
                            tvPaymentStatusDescription.setTextColor(getResources().getColor(R.color.dark_red));
                            BtRenewSubscription.setText("Pay");
                        }

                        //-----------------In case user have us country..
                        if (!CountryCode.equals("+91")) {

                            if (PaymentStatusID.equals("0")) {
                                //fist time payment not done yet
                                LLProfileUI.setVisibility(View.GONE);
                                LLpaymentlink.setVisibility(View.VISIBLE);

                                final String PaymentUrl = jsonObject1.getString("PaymentUrl");

                                TVForusPayment.setMovementMethod(LinkMovementMethod.getInstance());
                                //final String url="<a href ="+PaymentUrl+" target=\"_blank\" >Make Payment</a>";
                                TVForusPayment.setText(Html.fromHtml("<a href =" + PaymentUrl + " target=\"_blank\" >Click here to Subscribe.</a>"));

                                BtRenewSubscription.setText("Resend Subscription Link");

                            } else if (PaymentStatusID.equals("1069")) {
                                // if user has payed
                                LLProfileUI.setVisibility(View.VISIBLE);
                                LLpaymentlink.setVisibility(View.GONE);
                                BtRenewSubscription.setVisibility(View.GONE);

                                commonFunctions.myPreference.setString(getActivity(), GlobalConstants.PaymentStatusCode, "1069");
                                commonFunctions.myPreference.setString(getActivity(), GlobalConstants.INVITEE_STATUSID, "1080");
                                commonFunctions.myPreference.setBoolean(getActivity(), GlobalConstants.USER_PAYED, true);

                                final String ActivationCode = jsonObject1.optString("ActivationCode");
                                commonFunctions.myPreference.setString(getActivity(), GlobalConstants.ACTIVATIONCODE, ActivationCode);

                                LeftMenueOpenForPiad();

                            } else {
                                // if user payment expire.
                                LLProfileUI.setVisibility(View.VISIBLE);
                                LLpaymentlink.setVisibility(View.GONE);
                                BtRenewSubscription.setText("Resend Subscription Link");
                            }
                        }

                    } else {
                        Toast.makeText(getActivity(), Message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
                Log.e("----------->Fragment Package--API-->UserInfo", "Failed");
            }
        });
    }

    private void PaymentDoneSeverFailedUpadeAPI(String subscriptionID, String paymentStatus, String paymentId) {
        new CallWebService(getActivity(), commonFunctions.urlList.UPDATEPAYMENT, commonFunctions.UpdatePayment(subscriptionID, paymentStatus, paymentId), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                Log.e("-----FragmentPackage-----API--UPDATEPAYMENT--", "" + string);
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    JSONObject jsonObject1 = jsonObject.optJSONObject("Response");
                    int Status = jsonObject1.optInt("Status");
                    String Message = jsonObject1.optString("Message");
                    if (Status == 1) {
                        //call new api for details
                        commonFunctions.myPreference.setString(getActivity(), GlobalConstants.PaymentDoneSeverFailed, "0");
                        IVPaymentDoneSeverFailed.setVisibility(View.GONE);
                        PackageDetails();
                    } else {
                        Toast.makeText(getActivity(), Message, Toast.LENGTH_SHORT).show();
                        PackageDetails();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), "Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed() {
                PackageDetails();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        PackageDetails();
        makeuserInvitee();
    }

    @OnClick({R.id.IV_PaymentDoneSeverFailed, R.id.Bt_renewSubscription, R.id.TV_forusPayment})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.IV_PaymentDoneSeverFailed:

                break;
            case R.id.TV_forusPayment:

                break;
            case R.id.Bt_renewSubscription:
                //set visibility....
                /*Intent RenewalPagage = new Intent(getActivity(), SelectPackageActivity.class);
                RenewalPagage.putExtra(GlobalConstants.USER_NAME, UserName);
                RenewalPagage.putExtra(GlobalConstants.EMAIL, Email);
                RenewalPagage.putExtra(GlobalConstants.PHONE_NUMBER, MobileNo);
                RenewalPagage.putExtra(GlobalConstants.COUNTYCD, CountryCode);
                startActivity(RenewalPagage);*/
                if (!CountryCode.equals("+91")) {
                    //for us remove payment gatway.
                    ResendEmailForPayment();
                } else {
                    SelectPakageFr fragment = new SelectPakageFr();
                    commonFunctions.bdl.putInt(commonFunctions.gc.fromPage, 1);
                    fragment.setArguments(commonFunctions.bdl);

                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }

                break;
        }
    }

    private void ResendEmailForPayment() {
        new CallWebService(getActivity(), UrlList.ResendMail, commonFunctions.UserID(UserID), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    Log.d("----------ResendMail---->", string);
                    JSONObject jsonObject = new JSONObject(string);
                    int Status = jsonObject.optInt("Status");
                    String Message = jsonObject.optString("Message");

                    // Toast.makeText(getActivity(),"Mail has sent, please check you mail.",Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), Message, Toast.LENGTH_LONG).show();
                } catch (Exception ex) {

                }
            }

            @Override
            public void onFailed() {
                Log.e("----------ResendMail---->", "Fialed responce");
            }
        });
    }

}


