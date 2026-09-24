package com.omsworld.familycare.api_call;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.omsworld.familycare.R;
import com.omsworld.familycare.activity.MainActivity;
import com.omsworld.familycare.setting.ConnectionDetector;

import java.util.HashMap;
import java.util.Map;


public class CommonFunctions {
    public  final GlobalConstants gc = new GlobalConstants();
    public static MySharedPereference myPreference = MySharedPereference.getInstance();
    public static String VisibleFragmentNm="";
    private static Context context;
    public ConnectionDetector cdr;
    public Bundle bdl = new Bundle();
    public UrlList urlList;
    public AddressFromLatLong addressfrmltlg;


    public CommonFunctions(Context mcontext) {
        this.context = mcontext;
        addressfrmltlg = new AddressFromLatLong(context);
        cdr = new ConnectionDetector(context);

        final String isAcDEleted = myPreference.getString(mcontext, gc.ACCOUNT_ISDELETED);
        if (isAcDEleted.equals("1")) {
            Signout();
        }
    }

    public static boolean replaceFragment(final  Context mcontext,final Fragment fragment) {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    FragmentManager manager=((MainActivity)mcontext).getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();

                    transaction.replace(R.id.fragment_container, fragment);
                    // manager.popBackStack();
                    transaction.addToBackStack(null);
                    transaction.commit();

                }
            }, 200);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void back_fragment(){
        FragmentManager manager=((MainActivity)context).getSupportFragmentManager();
        int count = manager.getBackStackEntryCount();
        if (count != 0) {
            manager.popBackStackImmediate();
        }
    }


    public static String service_response() {
        final GlobalConstants gc = new GlobalConstants();
        return myPreference.getString(context, gc.RESPOSE_CLG_JB_SERVICE);
    }

    public static Map<String, String> DeleteUser(String InvitationID, String NearAndDearName) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("InvitationID", InvitationID);
        params.put("NearAndDearName", NearAndDearName);
        return params;
    }

    public static Map<String, String> Resendinvitation(String InvitationID) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("InvitationID", InvitationID);
        return params;

    }

    public void hideKeyboard(View view) {
        try {
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception Ex) {
        }
    }
    public void openKeyboard(View view) {
        try {
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }
        } catch (Exception Ex) {
        }
    }

    public boolean isEmpty(String  string) {
        try {
            if (string == null ||string.trim().length()==0) {
                return true;

            }else {
                return false;
            }
        } catch (Exception Ex) {
            return true;
        }
    }


    public String device_id() {
        String deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        // Toast.makeText(this, deviceId, Toast.LENGTH_SHORT).show();
        return deviceId;
    }

    //************************************************************************************************
    public Map<String, String> reg_with_mob(String UserName, String Email, String MobileNo, String Password) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("UserName", UserName);
        params.put("Email", Email);
        params.put("MobileNo", MobileNo);
        params.put("Password", Password);

        return params;
    }

    public Map<String, String> LOG_IN(String email, String password) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);
        return params;
    }

    public Map<String, String> verify_otp(String user_mob, String otp) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_mob", user_mob);
        params.put("otp", otp);
        return params;
    }

    public Map<String, String> forgot_pass(String mobile) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("mobile", mobile);
        return params;
    }

    public Map<String, String> request_to_join(String from_phone, String to_phone) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("from_phone", from_phone);
        params.put("to_phone", to_phone);
        return params;
    }
    public Map<String, String> add_family_on_group(String user_id, String family_id,String member_mob) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("family_id", family_id);
        params.put("member_mob", member_mob);
        return params;
    }

    public Map<String, String> add_phone_book(String user_id, String name,String phone, String comment) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("name", name);
        params.put("phone", phone);
        params.put("comment", comment);

        return params;
    }


    public Map<String, String> freinds_list(String mobile) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", mobile);
        return params;
    }

    public Map<String, String> Login(String users_name, String users_password, String DeviceID, String DeviceTokenID) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("Username", users_name);
        params.put("Password", users_password);
        params.put("DeviceID", DeviceID);
        params.put("DeviceTokenID", DeviceTokenID);
        params.put("DeviceCategory", "ANDROID");
        return params;
    }

    public Map<String, String> resend_otp(String mobile) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_mob", mobile);
        return params;
    }

    public Map<String, String> friend_request_list(String user_id) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        return params;
    }

    public Map<String, String> phone_book_list(String user_id,String family_id) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("family_id", family_id);
        return params;
    }

    public Map<String, String> edit_phone_book(String id,String user_id,String name,String phone,String comment) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("user_id", user_id);
        params.put("name", name);
        params.put("phone", phone);
        params.put("comment", comment);
        return params;
    }
     public Map<String, String> delete_phone_book(String user_id,String phone) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("phone", phone);
        return params;
    }

    public Map<String, String> family_request_action(String user_id, String family_id, String action) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("family_id", family_id);
        params.put("action", action);
        return params;
    }

    public Map<String, String> insert_position(String user_id,String family_id, String Battery, String Latitude, String Longitude, String Address) {
        final HashMap<String, String> mapp = new HashMap<String, String>();
        mapp.put("user_id", user_id);
        // mapp.put("user_id", "2");
        mapp.put("Battery", Battery);
        mapp.put("lat", Latitude);
        mapp.put("lng", Longitude);
        mapp.put("address", Address);
        mapp.put("family_id", family_id);
        return mapp;
    }  //------------------------------------------------

    //http://108.170.54.215/App_development/Tracking/Api/change_pass.php?user_id=1&& old_pass=123&&new_pass=1234567
    public Map<String, String> change_pass(String AspnetUserID, String OldPassword, String NewPassword, String ConfirmPassword) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", AspnetUserID);
        params.put("old_pass", OldPassword);
        params.put("new_pass", NewPassword);
        return params;
    }


    public Map<String, String> profile_edit(String UserID, String user_name, String Email, String dob, String about) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", UserID);
        params.put("user_name", user_name);
        params.put("user_email", Email);
        params.put("dob", dob);
        params.put("about", about);
        params.put("gender", ".");
        return params;
    }

    public Map<String, String> android_img_up(String UserID, String file) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", UserID);
        params.put("user_img", file);
        return params;
    }

    public Map<String, String> ResendOTP(String UserID, String user_mob) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", UserID);
        params.put("user_mob", user_mob);
        return params;
    }

    public Map<String, String> add_diary(String UserID, String subject, String note) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", UserID);
        params.put("subject", subject);
        params.put("note", note);

        return params;
    }
    public Map<String, String> edit_diary(String UserID,String note_id, String subject, String note) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", UserID);
        params.put("note_id", note_id);
        params.put("subject", subject);
        params.put("note", note);

        return params;
    }

    public Map<String, String> delete_diary(String UserID,String note_id) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", UserID);
        params.put("note_id", note_id);

        return params;
    }

    public Map<String, String> user_chat_details(String user_id,String freind_id) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("freind_id", freind_id);
        return params;
    }



    public Map<String, String> user_chat(String user_id,String freind_id,String message) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("freind_id", freind_id);
        params.put("message", message);
        params.put("type", "text");
        return params;
    }

    public Map<String, String> user_suggestion(String user_id,String message) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("message", message);
        return params;
    }

    public Map<String, String> add_groceries_list(String user_id,String note) {
        final HashMap<String, String> params = new HashMap<String, String>();

        params.put("user_id", user_id);
        params.put("note", note);
        return params;
    }

    public Map<String, String> delete_groceries_item(String user_id,String item_id) {
        final HashMap<String, String> params = new HashMap<String, String>();

        params.put("user_id", user_id);
        params.put("item_id", item_id);
        return params;
    }

    public Map<String, String> add_shopped_groceries(String user_id,String item,String FinalPrice) {
        final HashMap<String, String> params = new HashMap<String, String>();

        params.put("user_id", user_id);
        params.put("item", item);
        params.put("price", FinalPrice);
        return params;
    }
    public Map<String, String> direct_purchase(String user_id,String item,String FinalPrice) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("note", item);
        params.put("price", FinalPrice);
        return params;
    }
    public Map<String, String> push_registration(String user_id,String token_id) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("device_id", device_id());
        params.put("token_id", token_id);
        return params;
    }

    public Map<String, String> user_id(String user_id) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        return params;
    }


    public Map<String, String> family_group_info(String user_id,String family_id) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("family_id", family_id);
        return params;
    }
    public Map<String, String> remove_family_group_member(String user_id,String family_id,String member_mob) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("family_id", family_id);
        params.put("member_mob", member_mob);
        return params;
    }
    public Map<String, String> likeUnlike(String user_id,String news_id) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("news_id", news_id);

        return params;
    }

    public Map<String, String> create_family_group(String family_id,String user_id,String group_name) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("family_id", family_id);
        params.put("user_id", user_id);
        params.put("group_name", group_name);
        return params;
    }

    //************************************************************************************************


    public Map<String, String> Acceptinvitation(String NearAndDearUserID, String AspnetUserID, String ActivationCode) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("NearAndDearUserID", NearAndDearUserID);
        params.put("AspnetUserID", AspnetUserID);
        params.put("ActivationCode", ActivationCode);

        // {"InvitationID":2,"AspnetUserID":"151041","ActivationCode":"159039"}
        return params;
    }

    public Map<String, String> Declienduser(String NearAndDearUserID, String AspnetUserID) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("NearAndDearUserID", NearAndDearUserID);
        params.put("AspnetUserID", AspnetUserID);
        return params;

    }

    public Map<String, String> AcceptSOSRequest(String InteractionID, String ASPNETUSERID) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("InteractionID", InteractionID);
        params.put("NearAndDearUserId", ASPNETUSERID);
        return params;
    }

    public Map<String, String> DeclineSOSRequest(String InteractionID, String ASPNETUSERID) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("InteractionID", InteractionID);
        params.put("NearAndDearUserId", ASPNETUSERID);
        return params;
    }


    public Map<String, String> EditProfile(String UserID, String Email, String Mobile, String ActivationCodeForGuard, String CountryCode) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("UserID", UserID);
        params.put("Email", Email);
        params.put("Mobile", Mobile);
        params.put("ActivationCode", ActivationCodeForGuard);
        params.put("CountryCode", CountryCode);
        return params;
    }

    //----------------if account is deleted
    private void Signout() {
        String Devicetoken = myPreference.getString(context, GlobalConstants.Firebasetoken);
        myPreference.clearSharedPreference(context);

        myPreference.setString(context, GlobalConstants.Firebasetoken, Devicetoken);
        myPreference.setString(context, GlobalConstants.NOTIFICATION, "0");
        myPreference.setString(context, GlobalConstants.LEGALAGREEMENTCHECK, "1");

        Toast.makeText(context, "Your account has been deleted.", Toast.LENGTH_LONG).show();
    }


    public boolean isLtLgNtEmpty(String Lat, String Long) {
        if (Lat.equals("") || Lat.equals("0") || Lat.equals("0.0") || Lat.equals(null) || Lat == null || Lat.isEmpty() || Long.equals("") || Long.equals("0") || Long.equals(null) || Long.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public final String doubleToString(final double dbl_vlue) {
        String string_value = "";
        try {
            string_value = String.valueOf(dbl_vlue);
        } catch (Exception Ex) {
        }
        return string_value;
    }

    public Map<String, String> ChangePassword(String AspnetUserID, String OldPassword, String NewPassword, String ConfirmPassword) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("AspnetUserID", AspnetUserID);
        params.put("OldPassword", OldPassword);
        params.put("NewPassword", NewPassword);
        params.put("ConfirmPassword", ConfirmPassword);
        return params;
    }

    public Map<String, String> UserID(String UserID) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("UserID", UserID);
        params.put("user_id", UserID);
        return params;
    }

    public Map<String, String> empty_() {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("om's", "om's");
        return params;
    }
    public Map<String, String> shopping_site_list(String id) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        return params;
    }

    public Map<String, String> Package(String CountryCode, String UserName) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("CountryCode", CountryCode);
        params.put("UserName", UserName);
        return params;
    }

    public Map<String, String> requestforTrackOther(String AspnetUserID, String NearAndDearUserID) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("AspnetUserID", AspnetUserID);
        params.put("NearAndDearUserID", NearAndDearUserID);
        return params;
    }

    public Map<String, String> GetOrganization(String CountyCD) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("CountryCode", CountyCD);
        return params;
    }

    public Map<String, String> AcceptInvitationUsingInvitationCode(String AspnetUserId, String ActivationCode) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("AspnetUserID", AspnetUserId);
        params.put("ActivationCode", ActivationCode);
        return params;
    }

    //new Update 23/3/2017
    public Map<String, String> TrackingUserMappingAspnetUsers(String AspnetUserID) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("AspnetUserID", AspnetUserID);

        return params;
    }

    public Map<String, String> promocode(String PackageId, String PromotionCode, String CountryCodeID) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("PackageId", PackageId);
        params.put("PromotionCode", PromotionCode);
        params.put("CountryCodeID", CountryCodeID);
        return params;
    }

    public Map<String, String> AspnetUserID(String AspnetUserID) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("AspnetUserID", AspnetUserID);

        return params;
    }

    public Map<String, String> AspnetIdInteractionId(String AspnetUserID, String InteractionID) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("AspnetUserID", AspnetUserID);
        params.put("InteractionID", InteractionID);

       /* params.put("AspnetUserID", "");
        params.put("InteractionID", InteractionID);*/
        return params;
    }

    public Map<String, String> RENEWAL(String UserID, String PakageID, String PromotionCode, String DeviceID, String Amount) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("UserID", UserID);
        params.put("PakageID", PakageID);
        params.put("PromotionCode", PromotionCode);
        params.put("DeviceID", DeviceID);
        params.put("Amount", Amount);

        return params;
    }

    public Map<String, String> isJobDone(String InteractionID, String UpdatedBy) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("InteractionID", InteractionID);
        params.put("UpdatedBy", UpdatedBy);
        return params;
    }

    public Map<String, String> UserInfo(String UserName) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("UserName", UserName);
        return params;
    }

    public Map<String, String> updateApk_Param(String UserID, String AppPackageName, String AppType, String AppVersion) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("UserID", UserID);
        params.put("AppPackageName", AppPackageName);
        params.put("AppType", AppType);
        params.put("AppVersion", AppVersion);
        return params;
    }

    public Map<String, String> CreateNearandDear(String Name, String AspnetUserID, String ActivationCode, String MobileNO, String CountryCode, String Email) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("AspnetUserID", AspnetUserID);
        params.put("ActivationCode", ActivationCode);
        params.put("MobileNO", MobileNO);
        params.put("CountryCode", CountryCode);
        params.put("Name", Name);
        params.put("Email", Email);
        return params;
        //{"AspnetUserID":10036,"ActivationCode":789654,"MobileNO":9891331199}
    }

    public Map<String, String> UserLocationHistory(String AspnetUserId, String Battery, String Latitude, String Longitude, String Country, String State, String City, String Address) {
        final HashMap<String, String> srvc = new HashMap<String, String>();
        // param.put("AspnetUserId", "151043");
        srvc.put("AspnetUserId", AspnetUserId);
        srvc.put("Battery", Battery);
        srvc.put("Latitude", Latitude);
        srvc.put("Longitude", Longitude);
        srvc.put("Address", Address);

        //for college app

        return srvc;
    }  //------------------------------------------------


    public Map<String, String> Registration(String UserName, String Email, String CountryCode, String MobileNo, String Password, String InviteeStatusID, String DeviceID, String ActivationCodeForGuard, String DeviceTokenID) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("UserName", UserName);
        params.put("Email", Email);
        params.put("CountryCode", CountryCode);
        params.put("MobileNo", MobileNo);
        params.put("Password", Password);
        params.put("DeviceID", DeviceID);
        params.put("InviteeStatusID", InviteeStatusID);
        params.put("DeviceTokenID", DeviceTokenID);
        params.put("DeviceCategory", "ANDROID");
        params.put("ActivationCode", ActivationCodeForGuard);

        //{"UserName":"Raji6v1231","Email":"r6aji711b@gmail.com","CountryCode":"+91","MobileNo":"9916661112","Password":"123","DeviceID":"324234534", "InviteeStatusID":1080,"PackageID":1, "OrganizationID":1}
        return params;
    }


    public Map<String, String> braintreegatewaytransactionrequest(String nonce,
                                                                  String Amount, String OrderID, String fromPage, String CreditCardNo,
                                                                  String CVVNo, String CardHolderName, String ExpiryMonth, String ExpiryYear, String UserID, String isAutoRenewal) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("nonce", nonce);
        params.put("Amount", Amount);
        params.put("OrderID", OrderID);
        params.put("FromPage", fromPage);
        params.put("CreditCardNo", CreditCardNo);
        params.put("CVVNo", CVVNo);
        params.put("CardHolderName", CardHolderName);
        params.put("ExpiryMonth", ExpiryMonth);
        params.put("ExpiryYear", ExpiryYear);
        params.put("UserID", UserID);
        params.put("isAutoRenewal", isAutoRenewal);
        return params;
    }

    public Map<String, String> PayAvailableCardList(String OrderID, String PaymentCredentialID, String Amount, String isAutoRenewal) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("OrderID", OrderID);
        params.put("PaymentCredentialID", PaymentCredentialID);
        params.put("Amount", Amount);
        params.put("isAutoRenewal", isAutoRenewal);

        return params;
    }

    public Map<String, String> UpdatePayment(String SubscriptionID, String PaymentStatus, String PaymentId) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("SubscriptionID", SubscriptionID);
        params.put("PaymentStatus", PaymentStatus);
        params.put("isAutoRenewal", PaymentId);

        return params;
    }

    public Map<String, String> BraintreegatewayTokenkey(String InteractionID) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("InteractionID", "");
        return params;
    }

    public Map<String, String> BraintreegatewayTokenCard(String UserID) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("UserID", UserID);
        return params;
    }

    public Map<String, String> GETPAYUMONEYCREDENTIALS() {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("", "");
        return params;
    }

    public Map<String, String> AlarmOnOff(String AspnetUserID, String IsAlarmMute, String IsFriendsTracking) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("AspnetUserID", AspnetUserID);
        params.put("IsAlarmMute", IsAlarmMute);
        params.put("IsFriendsTracking", IsFriendsTracking);
        return params;
    }

    public Map<String, String> AlarmOff(String AspnetUserID) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("AspnetUserID", AspnetUserID);
        return params;
    }

    public Map<String, String> MappingInviteeRegistration(String aspnetUserId, String activationcode) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("AspnetUserId", aspnetUserId);
        params.put("ActivationCode", activationcode);

        return params;
    }

    public Map<String, String> ProcessPayment(String UserID, String PakageID, String PromotionCode, String DeviceID, String Amount, String ReferralCode) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("UserID", UserID);
        params.put("PakageID", PakageID);
        params.put("PromotionCode", PromotionCode);
        params.put("DeviceID", DeviceID);
        params.put("Amount", Amount);
        params.put("MessageType", "Registration");
        params.put("ReferralCode", ReferralCode);
        return params;
    }


    public Map<String, String> ValidateReferralCode(String PropertyValue) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("PropertyValue", PropertyValue);
        return params;

    }

    public Map<String, String> SOSHelperList(String userInteractionID, String aspnetuserid, String NearAndDearUserName) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("InteractionID", userInteractionID);
        params.put("AspnetUserID", aspnetuserid);
        params.put("NearAndDearUserName", NearAndDearUserName);
        return params;
    }

    public Map<String, String> jobDetal(String UserID) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("UserID", UserID);
        return params;
    }

    public Map<String, String> upload_file(String InteractionID, String DocumentName, String FileName, String FileSize, String UserName) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("InteractionID", InteractionID);
        params.put("DocumentName", DocumentName);
        params.put("FileName", FileName);
        params.put("FileSize", FileSize);
        params.put("UserName", UserName);
        return params;
    }


    public Map<String, String> ReferralCreateVendor(String UserName, String AspnetUserID, String AccountNo, String AccountHolderName
            , String IFSCCODE, String NameofBank) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("UserName", UserName);
        params.put("AspnetUserID", AspnetUserID);
        params.put("AccountNo", AccountNo);
        params.put("AccountHolderName", AccountHolderName);
        params.put("IFSCCODE", IFSCCODE);
        params.put("NameofBank", NameofBank);


        return params;
    }

    public Map<String, String> GetVendorDetails(String AspnetUserID) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("AspnetUserID", AspnetUserID);


        return params;
    }

    public Map<String, String> spamdetail(String DocumentID) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("DocumentID", DocumentID);
        return params;
    }

    public Map<String, String> DeletedMyAccount(String AspnetUserID, String Password, String Type) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("AspnetUserID", AspnetUserID);
        params.put("Password", Password);
        params.put("Type", Type);
        return params;
    }

    public Map<String, String> FilesizeParam(String UserID, String FileSize) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("UserID", UserID);
        params.put("FileSize", FileSize);
        return params;
    }

    public Map<String, String> deletedocument(String DocumentID, String DELETE) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("DocumentID", DocumentID);
        params.put("Type", DELETE);
        return params;
    }


    public Map<String, String> UpdateIncidentName(String InteractionID, String IncidentName, String IncidentDesc, String UserName) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("InteractionID", InteractionID);
        params.put("IncidentName", IncidentName);
        params.put("IncidentDesc", IncidentDesc);
        params.put("UserName", UserName);
        return params;
    }

    public Map<String, String> GetIncidentCommentSummary(String InteractionID) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("InteractionID", InteractionID);

        return params;
    }


    public Map<String, String> InsertIncidentComment(String InteractionID, String IncidentName, String UserName, String AspnetUserID) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("InteractionID", InteractionID);
        params.put("IncidentName", IncidentName);
        params.put("UserName", UserName);
        params.put("AspnetUserID", AspnetUserID);
        return params;
    }


}