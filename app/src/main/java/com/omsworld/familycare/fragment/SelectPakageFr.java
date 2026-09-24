package com.omsworld.familycare.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.omsworld.familycare.R;
import com.omsworld.familycare.activity.MainActivity;
import com.omsworld.familycare.activity.Payment_ConfermationAc;
import com.omsworld.familycare.api_call.AppUtil;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.omsworld.familycare.api_call.MyServiceListener;
import com.omsworld.familycare.model.PackageModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by omprakash.m on 5/3/2017.
 */

public class SelectPakageFr extends Fragment {
    @Bind(R.id.TV_havePromocode)
    TextView TVHavePromocode;
    @Bind(R.id.TV_promocode)
    TextView TVPromocode;
    @Bind(R.id.TV_promocodeDetails)
    TextView TVPromocodeDetails;
    @Bind(R.id.IV_cancelPromocode)
    ImageView IVCancelPromocode;
    @Bind(R.id.LL_promoDetails)
    LinearLayout LLPromoDetails;
    @Bind(R.id.btn_prev)
    Button btnPrev;
    @Bind(R.id.btn_next)
    Button btnNext;
    @Bind(R.id.activity_select_package)
    LinearLayout activitySelectPackage;
    @Bind(R.id.TV_totalAmount)
    TextView TVTotalAmount;
    @Bind(R.id.TV_SP_details)
    TextView TVSP_details;
    @Bind(R.id.LL_RDlinearlayout)
    LinearLayout LLRDlinearlayout;
    @Bind(R.id.RD_pakages)
    RadioGroup RDPakages;
    @Bind(R.id.ET_promocode)
    EditText ETPromocode;
    @Bind(R.id.et_YourReferalCode)
    EditText etYourReferalCode;
    @Bind(R.id.IV_applyreferral_code)
    Button IVApplyreferralCode;
    @Bind(R.id.LL_refrralCode)
    LinearLayout LLRefrralCode;
    @Bind(R.id.LL_ApplyPromo)
    LinearLayout LLApplyPromo;

    String PaymentStatusCode, countycd, user_id, userName, MOBILE_only, Email, CountryCodeID, refrralCode = "";
    int fromPageBundle = 0;

    private View rootView;
    private CommonFunctions cmf;
    private ArrayList<PackageModel> arrayList;
    private PackageModel packageModel;
    private String PackageId = "", pakageName = "", pakageAmount = "0.00", promoCode = "", pakageFinalAmount = "0.00", amountTypeSbl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.selectpakage_fr, container, false);
        cmf = new CommonFunctions(getActivity());
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cmf.bdl = this.getArguments();
        if (cmf.bdl != null) {
            fromPageBundle = cmf.bdl.getInt(cmf.gc.fromPage);
        }
        initilize();
        makeuserInvitee();
    }

    public void makeuserInvitee() {
        //from  SelectPakageFr  and  Fragment_Package  would be change condition for unpaid user (New change - 3/Aug/2017)
        //if you are guard 1081 ---->family(invitee) 1079 ---->PAid user 1080
        final String InviteeStatusID = cmf.myPreference.getString(getActivity(), GlobalConstants.INVITEE_STATUSID);
        final String PaymentStatusCode = cmf.myPreference.getString(getActivity(), GlobalConstants.PaymentStatusCode);
        if (InviteeStatusID.equals("1080") && !PaymentStatusCode.equals("1069")) {
            //invitee 1079
            cmf.myPreference.setString(getActivity(), GlobalConstants.INVITEE_STATUSID, "1079");
           // MainActivity.LeftMenueOpenForInvitee();
        }
    }

    private void initilize() {
        countycd = cmf.myPreference.getString(getActivity(), GlobalConstants.COUNTYCD);
        if (countycd.equals("+91")) {
            amountTypeSbl = "₹ ";
        } else {
            amountTypeSbl = "$ ";
            LLRefrralCode.setVisibility(View.GONE);
        }

        user_id = cmf.myPreference.getString(getActivity(), GlobalConstants.USER_ID);
        userName = cmf.myPreference.getString(getActivity(), GlobalConstants.USER_NAME);
        MOBILE_only = cmf.myPreference.getString(getActivity(), GlobalConstants.MOBILE_only);
        Email = cmf.myPreference.getString(getActivity(), GlobalConstants.EMAIL);
        CountryCodeID = cmf.myPreference.getString(getActivity(), GlobalConstants.COUNTRYCODEID);

        PaymentStatusCode = cmf.myPreference.getString(getActivity(), GlobalConstants.PaymentStatusCode);

        if (fromPageBundle == 1) {
            TVHavePromocode.setText("Have a promotion code ?");
            btnNext.setText("NEXT");
            btnPrev.setVisibility(View.VISIBLE);
            LLApplyPromo.setVisibility(View.GONE);
        } else {
            TVHavePromocode.setText("Have a promotion code ?");
            btnNext.setText("Subscribe");
            btnPrev.setVisibility(View.GONE);
            LLApplyPromo.setVisibility(View.VISIBLE);
        }

        TVSP_details.setText("Subscription charge:");
        TVTotalAmount.setText(amountTypeSbl + pakageAmount);

        getserverresponse();

        RDPakages.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                int radioButtonID = RDPakages.getCheckedRadioButtonId();
                if (radioButtonID != -1) {
                    View radioButton = RDPakages.findViewById(radioButtonID);
                    int position = (Integer) radioButton.getTag();
                    pakageAmount = arrayList.get(position).getAmount();
                    if (Double.parseDouble(pakageAmount) == 0.0) {
                        ETPromocode.setHint("Enter Promotion Code");
                    } else {
                        ETPromocode.setHint("Enter Promotion Code (optional)");
                    }
                    PackageId = arrayList.get(position).getPackageID();

                    //pakageName = arrayList.get(position).getPackageName();

                    pakageName = arrayList.get(position).getSetPackageDesc();

                    pakageFinalAmount = pakageAmount;

                    final NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));

                    //TVSP_details.setText(pakageName + "\n" + userName);
                    TVSP_details.setText(pakageName);
                    String abdas = defaultFormat.format(Double.parseDouble(pakageAmount));
                    String number = abdas.replace("$", " ");
                    TVTotalAmount.setText(amountTypeSbl + number);

                    promoCode = "";
                    //TVHavePromocode.setVisibility(View.VISIBLE);
                    if (fromPageBundle != 1) {
                        LLApplyPromo.setVisibility(View.VISIBLE);
                    }


                    LLPromoDetails.setVisibility(View.GONE);

                    //-------hide for another package..
                   /* final double amount = Double.parseDouble(pakageAmount);
                    TVHavePromocode.setVisibility(View.GONE);
                    if (amount == 0.0 ) {
                      TVHavePromocode.setVisibility(View.VISIBLE);
                    }*/
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.TV_havePromocode, R.id.IV_ApplyPromo, R.id.btn_prev, R.id.btn_next, R.id.IV_cancelPromocode, R.id.IV_applyreferral_code})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.IV_applyreferral_code:
                applyrRefrral();
                cmf.hideKeyboard(view);
                break;

            case R.id.TV_havePromocode:
                PromoCode();
                break;

            case R.id.IV_ApplyPromo:
                cmf.hideKeyboard(view);
                applyPromoCodeAPI();
                break;

            case R.id.btn_next:
                //******
                paymentProcess();
                break;
            case R.id.btn_prev:
                //******
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment_Package()).commit();
                break;
            case R.id.IV_cancelPromocode:
                //******
                int radioButtonID = RDPakages.getCheckedRadioButtonId();
                if (radioButtonID != -1) {
                    View radioButton = RDPakages.findViewById(radioButtonID);
                    int position = (Integer) radioButton.getTag();

                    PackageId = arrayList.get(position).getPackageID();
                    pakageAmount = arrayList.get(position).getAmount();
                    pakageName = arrayList.get(position).getPackageName();

                    pakageFinalAmount = pakageAmount;

                    TVSP_details.setText(pakageName + "\n" + userName);
                    TVTotalAmount.setText(amountTypeSbl + pakageAmount);

                    promoCode = "";
                    // TVHavePromocode.setVisibility(View.VISIBLE);
                    LLApplyPromo.setVisibility(View.VISIBLE);
                    LLPromoDetails.setVisibility(View.GONE);
                }

                break;

        }
    }

    private void getserverresponse() {
        arrayList = new ArrayList<>();

        new CallWebService(getActivity(), cmf.urlList.Package, cmf.Package(countycd, userName), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                arrayList.clear();

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(string);
                    JSONObject jsonObject1 = jsonObject.optJSONObject("Response");
                    Integer status = jsonObject1.optInt("Status");
                    String Message = jsonObject1.optString("Message");
                    if (status == 1) {
                        JSONArray jsonArray = jsonObject1.optJSONArray("Data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            packageModel = new PackageModel();
                            JSONObject jsonObject2 = jsonArray.optJSONObject(i);
                            String PackageID = jsonObject2.optString("PackageID");
                            String PackageName = jsonObject2.optString("PackageName");
                            String Amount = jsonObject2.optString("Amount");
                            String AmountType = jsonObject2.optString("AmountType");
                            String PackageDesc = jsonObject2.optString("PackageDesc");

                            packageModel.setPackageID(PackageID);
                            packageModel.setPackageName(PackageName);
                            packageModel.setAmount(Amount);
                            packageModel.setType(AmountType);
                            packageModel.setSetPackageDesc(PackageDesc);
                            arrayList.add(packageModel);
                        }
                        addview_radiobutton();
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
            }
        });
    }

    public void addview_radiobutton() {
        RDPakages.clearCheck();
        RDPakages.removeAllViews();
        for (int position = 0; position < arrayList.size(); position++) {
            RadioButton radioButton = new RadioButton(getActivity());
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = 10;
            params.leftMargin = 0;
            params.rightMargin = 0;
            params.bottomMargin = 10;

            radioButton.setId(Integer.parseInt(arrayList.get(position).getPackageID()));
            radioButton.setText(arrayList.get(position).getPackageName());
            radioButton.setTag(position);
            RDPakages.addView(radioButton, params);
        }
    }

    private void PromoCode() {/*
        if (PackageId.equals("")) {
            return;
        }
        promoCode = "";
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.forgetpassword_dialog);
        final ImageView pencil = (ImageView) dialog.findViewById(R.id.pencil);
        final Button lay11 = (Button) dialog.findViewById(R.id.lay11);

        final TextView account = (TextView) dialog.findViewById(R.id.account);
        final Button send = (Button) dialog.findViewById(R.id.send);
        final Button cancel = (Button) dialog.findViewById(R.id.cancel);
        final EditText ETemail = (EditText) dialog.findViewById(R.id.edt_email);

        ETemail.setHint("Please enter your promotion code.");
        account.setText("promotion code");
        lay11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        pencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmf.hideKeyboard(v);
                dialog.dismiss();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String NowPromoCode = ETemail.getText().toString();
                if (TextUtils.isEmpty(NowPromoCode)) {
                    // ETemail.setError("Email is incorrect");
                    return;
                } else {
                    cmf.hideKeyboard(v);

                    myservice = new CallWebService(getActivity(), cmf.urlList.promocode, cmf.promocode(PackageId, NowPromoCode), new MyServiceListener() {
                        @Override
                        public void onSuccess(String string) {
                            Log.d("---SelectPakage Fr", string);

                            try {
                                JSONObject flag = new JSONObject(string);
                                String Status = flag.getString("Status");
                                String promomessage = flag.getString("Message");

                                if (Status.equals("0")) {
                                    Toast.makeText(getActivity(), promomessage, Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    JSONObject data_flag = flag.getJSONObject("Data");
                                    String PromotioncodeAmount = data_flag.getString("Amount");
                                    String Discount = data_flag.getString("Discount");
                                    String PackageName = data_flag.getString("PackageName");
                                    /*//****
     *//*amount = PromotioncodeAmount;
                                    txtTotal.setText(amountType + data_flag.optString("Amount"));
                                    packagename = data_flag.optString("PackageName");
                                    txtPackage.setText(packagename);*//*
                                    //  Tst(getApplicationContext(), "Promotion code Applied");
                                    pakageFinalAmount = PromotioncodeAmount;
                                    TVTotalAmount.setText("₹ " + pakageFinalAmount);
                                    TVPromocodeDetails.setText(promomessage + " for " + PackageName);
                                    //---------------------------
                                    promoCode = NowPromoCode;
                                    TVPromocode.setText(promoCode);
                                    TVHavePromocode.setVisibility(View.GONE);
                                    LLPromoDetails.setVisibility(View.VISIBLE);
                                    //---------------------------
                                    dialog.dismiss();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailed() {
                            Toast.makeText(getActivity(), "Response on failed", Toast.LENGTH_SHORT).show();
                            TVTotalAmount.setText("₹ " + "213");
                            //---------------------------
                            TVPromocode.setText(promoCode);
                            TVHavePromocode.setVisibility(View.GONE);
                            LLPromoDetails.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        dialog.show();*/
    }

    private void applyPromoCodeAPI() {
        final String NowPromoCode = ETPromocode.getText().toString();

        if (PackageId.equals("")) {
            Toast.makeText(getActivity(), "Please select a subscription plan.", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(NowPromoCode)) {
            Toast.makeText(getActivity(), "Please enter promotion code.", Toast.LENGTH_SHORT).show();
            return;
        } else {

            new CallWebService(getActivity(), cmf.urlList.promocode, cmf.promocode(PackageId, NowPromoCode, CountryCodeID), new MyServiceListener() {
                @Override
                public void onSuccess(String string) {


                    try {
                        JSONObject flag = new JSONObject(string);
                        String Status = flag.getString("Status");
                        String promomessage = flag.getString("Message");

                        if (Status.equals("0")) {
                            Toast.makeText(getActivity(), promomessage, Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            JSONObject data_flag = flag.getJSONObject("Data");
                            String PromotioncodeAmount = data_flag.getString("Amount");
                            String Discount = data_flag.getString("Discount");
                            String PackageName = data_flag.getString("PackageName");

                            pakageFinalAmount = PromotioncodeAmount;
                            TVTotalAmount.setText("₹ " + pakageFinalAmount);
                            //TVPromocodeDetails.setText(promomessage + " for " + PackageName);
                            TVPromocodeDetails.setText(promomessage);
                            //---------------------------
                            promoCode = NowPromoCode;
                            TVPromocode.setText(promoCode);
                            // TVHavePromocode.setVisibility(View.GONE);
                            LLApplyPromo.setVisibility(View.GONE);
                            LLPromoDetails.setVisibility(View.VISIBLE);
                            //---------------------------
                            //---------------------------
                            // dialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //---------------------------
                        //  dialog.dismiss();
                    }
                }

                @Override
                public void onFailed() {
                    Toast.makeText(getActivity(), "Response on failed", Toast.LENGTH_SHORT).show();
                    //---------------------------
                    //dialog.dismiss();
                    //---------------------------
                    TVPromocode.setText(promoCode);
                    // TVHavePromocode.setVisibility(View.GONE);
                    LLApplyPromo.setVisibility(View.VISIBLE);
                    LLPromoDetails.setVisibility(View.GONE);
                }
            });
        }
    }

    private void applyrRefrral() {
        if (PackageId.equals("")) {
            Toast.makeText(getActivity(), "Please select a subscription plan.", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(etYourReferalCode.getText().toString())) {
            Toast.makeText(getActivity(), "Please enter referral code.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            refrralCode = "";
            new CallWebService(getActivity(), cmf.urlList.ValidateReferralCode, cmf.ValidateReferralCode(etYourReferalCode.getText().toString()), new MyServiceListener() {
                @Override
                public void onSuccess(String string) {

                    try {
                        JSONObject rootobj = new JSONObject(string);
                        Integer status = rootobj.optInt("Status");
                        String Message = rootobj.optString("Message");
                        if (status == 1) {
                            refrralCode = etYourReferalCode.getText().toString();
                            etYourReferalCode.setTextColor(Color.GREEN);
                        } else {
                            etYourReferalCode.setTextColor(Color.RED);
                        }
                        Toast.makeText(getActivity(), Message, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onFailed() {

                }
            });
        }
    }

    private void paymentProcess() {
        final double amount = Double.parseDouble(pakageFinalAmount);
        if (amount == 0.0 && promoCode.equals("")) {
            //show promo alert
            PromoCode();
        } else {

            new CallWebService(getActivity(), cmf.urlList.ProcessPayment, cmf.ProcessPayment(user_id, PackageId, promoCode, cmf.device_id(), pakageFinalAmount, refrralCode), new MyServiceListener() {
                @Override
                public void onSuccess(String string) {

                    try {
                        JSONObject rootobj = new JSONObject(string);
                        Integer status = rootobj.optInt("Status");
                        String Message = rootobj.optString("Message");
                        if (status == 1) {
                            JSONArray dataarray = rootobj.getJSONArray("Data");
                            JSONObject zeroData = dataarray.optJSONObject(0);

                            if (amount == 0.0) {
                                String ActivationCode = zeroData.getString("ActivationCode");
                                cmf.myPreference.setString(getActivity(), GlobalConstants.ACTIVATIONCODE, ActivationCode);
                                cmf.myPreference.setString(getActivity(), GlobalConstants.PaymentStatusCode, "1069");
                                cmf.myPreference.setString(getActivity(), GlobalConstants.INVITEE_STATUSID, "1080");
                                cmf.myPreference.setBoolean(getActivity(), GlobalConstants.USER_PAYED, true);

                                Intent intent = new Intent(getActivity(), Payment_ConfermationAc.class);
                                intent.putExtra(GlobalConstants.PAY_SUB_RESPONSE, Message);
                                AppUtil.startActivityWithAnimation(getActivity(), intent);
                            } else {
                                String SubscriptionID = zeroData.getString("SubscriptionID");
                                Intent intent1=null;
                                if (countycd.equals("+91")) {
                                   // intent1 = new Intent(getActivity(), Payumoney_Activity.class);
                                    intent1.putExtra(GlobalConstants.SUBSCRIPTION_ID, SubscriptionID);
                                    intent1.putExtra(GlobalConstants.PACKAGE_AMOUNT, pakageFinalAmount);
                                    intent1.putExtra(GlobalConstants.PHONE_NUMBER, MOBILE_only);
                                    intent1.putExtra(GlobalConstants.USER_NAME, userName);
                                    intent1.putExtra(GlobalConstants.EMAIL, Email);
                                    AppUtil.startActivityWithAnimation(getActivity(), intent1);
                                } else {
                                   // intent1 = new Intent(getActivity(), BrainTreePaymentActivity.class);
                                    intent1.putExtra(GlobalConstants.SUBSCRIPTION_ID, SubscriptionID);
                                    intent1.putExtra(GlobalConstants.PAYUMONEYKEY, "1234");
                                    intent1.putExtra(GlobalConstants.PACKAGE_AMOUNT, pakageFinalAmount);
                                    intent1.putExtra(GlobalConstants.PHONE_NUMBER, MOBILE_only);
                                    intent1.putExtra(GlobalConstants.USER_NAME, userName);
                                    intent1.putExtra(GlobalConstants.EMAIL, Email);
                                    AppUtil.startActivityWithAnimation(getActivity(), intent1);

                                    //-----------------------
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), Message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onFailed() {

                }
            });
        }

        /*double amount = Double.parseDouble(pakageFinalAmount);
        if (amount >0.0) {
            if (countycd.equals("+91")) {
                Intent intent1 = new Intent(getActivity(), Payumoney_Activity.class);
                AppUtil.startActivityWithAnimation(getActivity(), intent1);
            } else {
                Intent intent = new Intent(getActivity(), BrainTreePaymentActivity.class);
                AppUtil.startActivityWithAnimation(getActivity(), intent);
            }
        } else {

            Intent intent = new Intent(getActivity(), Payment_ConfermationAc.class);
            intent.putExtra(GlobalConstants.PAY_SUB_RESPONSE, "Pass message");
            AppUtil.startActivityWithAnimation(getActivity(), intent);
        }*/
        //Calling Payment Activity"....
    }

    //--------------------------------Closed-----------------
}

