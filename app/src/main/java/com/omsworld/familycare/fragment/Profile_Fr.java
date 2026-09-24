package com.omsworld.familycare.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.omsworld.familycare.R;
import com.omsworld.familycare.ServicesControl.CollegeJobService;
import com.omsworld.familycare.activity.MainActivity;
import com.omsworld.familycare.activity.SignInUpActivity;
import com.omsworld.familycare.api_call.AppUtil;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.FieldUtils;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.omsworld.familycare.api_call.MyServiceListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

/**
 * Created by omprakash.m on 4/10/2017.
 */

public class Profile_Fr extends Fragment implements View.OnClickListener {
    View rootView;
    //----------
    public static final int GALLERY_REQUEST = 100;
    public static final int CODE_OPEN_IMAGE_GALLEY = 1001;
    private final int RESULT_CROP = 400;
    public static Toolbar toolbar;
    public String AspnetUserID, UserID, name, mail, phone, user_image, InvitationCode, PaymentStatusCode,dob,about;

    RelativeLayout RLImageUpload;
    ImageView IVDeactiveaccount,IVDelete,IVProfileImg, iv_upload,IV_changePass;
    EditText ETName, ETPhone,ETMail,ET_about;
    TextView tv_dob;
    Button BTUpdate;
    ScrollView srollview;
    ProgressBar mprogressBar;

    Uri selectedImage;

    private CommonFunctions cmf;
    private String inviteestatusid, CountryCodeId;
    private String beforeDeleteMsg, beforeDeactivateMsg, referralHelpMsg = "";
    private String imagePath = "";
    private File uploadImageCaptured;
    private String fileextension;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile_fr, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intview();
    }

    private void Intview() {
        MainActivity.toolbar.setTitle("Profile");
        cmf = new CommonFunctions(getActivity());
        setnewView();
        // GetProfileAPICall();
    }

    private void setnewView() {
        AspnetUserID = cmf.myPreference.getString(getActivity(), GlobalConstants.ASPNETUSERID);
        UserID = cmf.myPreference.getString(getActivity(), GlobalConstants.USER_ID);
        name = cmf.myPreference.getString(getActivity(), GlobalConstants.USER_NAME);
        mail = cmf.myPreference.getString(getActivity(), GlobalConstants.EMAIL);
        phone = cmf.myPreference.getString(getActivity(), GlobalConstants.MOBILE_only);
        user_image = cmf.myPreference.getString(getActivity(), GlobalConstants.USER_IMAGE);
        user_image = cmf.myPreference.getString(getActivity(), GlobalConstants.USER_IMAGE);
        dob = cmf.myPreference.getString(getActivity(), GlobalConstants.USER_DOB);
        about = cmf.myPreference.getString(getActivity(), GlobalConstants.USER_ABOUT);

        //--------------------------------------------------------------
        RLImageUpload = (RelativeLayout) rootView.findViewById(R.id.RL_imageUpload);
        IVDeactiveaccount = (ImageView) rootView.findViewById(R.id.IV_deactiveaccount);
        IVDelete = (ImageView) rootView.findViewById(R.id.IV_delete);
        IV_changePass = (ImageView) rootView.findViewById(R.id.IV_changePass);
        ETName = (EditText) rootView.findViewById(R.id.ET_Name);
        ETPhone = (EditText) rootView.findViewById(R.id.ET_Phone);
        ETMail = (EditText) rootView.findViewById(R.id.ET_Mail);
        BTUpdate = (Button) rootView.findViewById(R.id.BT_Update);
        srollview = (ScrollView) rootView.findViewById(R.id.srollview);
        mprogressBar = (ProgressBar) rootView.findViewById(R.id.mprogressBar);
        IVProfileImg = (ImageView) rootView.findViewById(R.id.IV_profile_img);
        iv_upload = (ImageView) rootView.findViewById(R.id.iv_upload);
        tv_dob = (TextView) rootView.findViewById(R.id.tv_dob);
        ET_about = (EditText) rootView.findViewById(R.id.ET_about);

        iv_upload.setOnClickListener(this);
        IVDeactiveaccount.setOnClickListener(this);
        IVDelete.setOnClickListener(this);
        BTUpdate.setOnClickListener(this);
        IV_changePass.setOnClickListener(this);
        //--------------------------------------------------------------
        setprofileData(name, phone, mail, dob, about);

      //  headderTab();
    }

    private void headderTab() {
        final TextView TV_tab1 = (TextView) rootView.findViewById(R.id.tv_male);
        final TextView TV_tab2 = (TextView) rootView.findViewById(R.id.tv_female);

        TV_tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTab(TV_tab1, 1);
                setTab(TV_tab2, 0);

            }
        });
        TV_tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setTab(TV_tab1, 0);
                setTab(TV_tab2, 1);

            }
        });


    }

    private void setTab(TextView tab, int isSelected) {
        if (isSelected == 1) {
            tab.setBackgroundColor(getResources().getColor(R.color.theme_color));
            tab.setTextColor(getResources().getColor(R.color.white));
        } else if (isSelected == 0) {
            tab.setBackground(getResources().getDrawable(R.drawable.border));
            tab.setTextColor(getResources().getColor(R.color.theme_color));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_upload:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    loadPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, GALLERY_REQUEST);
                } else {
                    openGallery();
                }
                break;
            case R.id.IV_deactiveaccount:
                break;
            case R.id.IV_delete:
                //  DeleteAccount();
                break;
            case R.id.BT_Update:
                cmf.hideKeyboard(BTUpdate);
                APICallForUPDATE();
                break;
            case R.id.IV_changePass:
                ResetPaasword();
                break;
        }
    }


    private void APICallForUPDATE() {
        if (validate()) {
            new CallWebService(getActivity(), cmf.urlList.profile_edit, cmf.profile_edit(UserID, name, mail,dob,about), new MyServiceListener() {
                @Override
                public void onSuccess(String string) {

                    try {
                        JSONObject obj = new JSONObject(string);
                        String Status = obj.optString("status");
                        String message = obj.optString("message");
                        if (Status.equals("0")) {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            //Tst_Snake(findViewById(R.id.tv_SignUp), "Please provide correct credentials.");
                            return;
                        } else {
                            cmf.myPreference.setString(getActivity(), GlobalConstants.USER_NAME, name);
                            cmf.myPreference.setString(getActivity(), GlobalConstants.EMAIL, mail);
                            cmf.myPreference.setString(getActivity(), GlobalConstants.USER_DOB, dob);
                            cmf.myPreference.setString(getActivity(), GlobalConstants.USER_ABOUT, about);
                            setprofileData(name, phone, mail, dob, about);
                        }
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailed() {

                }
            });

        }
    }

    public boolean validate() {
        mail = ETMail.getText().toString();
        name = ETName.getText().toString();
        if (FieldUtils.isBlank(mail)) {
            Toast.makeText(getActivity(), "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (name.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            Toast.makeText(getActivity(), "Please enter a valid name.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    //-----------------------------------------------------------------forget password
    private void ResetPaasword() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.reset_password_dialog);
        final ImageView pencil = (ImageView) dialog.findViewById(R.id.pencil);
        final Button lay11 = (Button) dialog.findViewById(R.id.lay11);

        final Button send = (Button) dialog.findViewById(R.id.send);
        final Button cancel = (Button) dialog.findViewById(R.id.cancel);
        final EditText ETold_pass = (EditText) dialog.findViewById(R.id.edt_oldpass);
        final EditText edt_pass = (EditText) dialog.findViewById(R.id.edt_pass);
        final EditText ETConfirm_pass = (EditText) dialog.findViewById(R.id.edt_con_pass);
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
                dialog.dismiss();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String oldpass = ETold_pass.getText().toString();
                String pass = edt_pass.getText().toString();
                String Confirm_pass = ETConfirm_pass.getText().toString();

                if (TextUtils.isEmpty(oldpass)) {
                    Toast.makeText(getActivity(), "Please enter the current password.", Toast.LENGTH_LONG).show();
                    return;
                } else if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(getActivity(), "Please enter the new password", Toast.LENGTH_LONG).show();
                    return;
                } else if (TextUtils.isEmpty(Confirm_pass)) {
                    Toast.makeText(getActivity(), "Please reconfirm the entered password. ", Toast.LENGTH_LONG).show();
                    return;
                } else if (!pass.equals(Confirm_pass)) {
                    Toast.makeText(getActivity(), "Passwords entered do not match. Please try again.", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    // call service listener..
                    new CallWebService(getActivity(), cmf.urlList.change_pass, cmf.change_pass(UserID, oldpass, pass, Confirm_pass), new MyServiceListener() {
                        @Override
                        public void onSuccess(String string) {
                            try {
                                JSONObject flag = new JSONObject(string);
                                String status = flag.optString("status");
                                String message = flag.optString("message");
                                if (status.equals("1")) {
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                    Signout();
                                } else {
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException je) {
                                // Tst(getApplication(), "The request could not be completed. Please try again.");
                                dialog.dismiss();
                            } catch (Exception ex) {
                            }
                        }

                        @Override
                        public void onFailed() {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        dialog.show();
    }

    private void Signout() {
        String Devicetoken = cmf.myPreference.getString(getActivity(), GlobalConstants.Firebasetoken);
        cmf.myPreference.clearSharedPreference(getActivity());
        final Intent itnt = new Intent(getActivity(), CollegeJobService.class);
        itnt.putExtra(cmf.gc.fromPage, "stopService");
        getActivity().startService(itnt);
        cmf.myPreference.setString(getActivity(), GlobalConstants.Firebasetoken, Devicetoken);
        cmf.myPreference.setString(getActivity(), GlobalConstants.NOTIFICATION, "0");
        cmf.myPreference.setString(getActivity(), GlobalConstants.LEGALAGREEMENTCHECK, "1");
        AppUtil.startActivityWithAnimation(getActivity(), new Intent(getActivity(), SignInUpActivity.class));
    }

    private void DeleteAccount() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.forgetpassword_dialog);
        final ImageView pencil = (ImageView) dialog.findViewById(R.id.pencil);
        pencil.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.deactivate_account));

        final TextView account = (TextView) dialog.findViewById(R.id.account);
        final Button lay11 = (Button) dialog.findViewById(R.id.lay11);

        final Button send = (Button) dialog.findViewById(R.id.send);
        final Button cancel = (Button) dialog.findViewById(R.id.cancel);
        final EditText edt_email = (EditText) dialog.findViewById(R.id.edt_email);
        //   edt_email.setError("Enter Your Password");
        edt_email.setHint("Enter Your Password.");

        account.setText(beforeDeleteMsg);
        edt_email.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);


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
                dialog.dismiss();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String oldpass = edt_email.getText().toString();


                if (TextUtils.isEmpty(oldpass)) {
                    Toast.makeText(getActivity(), "Please enter the password.", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    // call service listener..
                    new CallWebService(getActivity(), cmf.urlList.DeleteAccount, cmf.DeletedMyAccount(AspnetUserID, oldpass, "DELETE"), new MyServiceListener() {
                        @Override
                        public void onSuccess(String string) {

                            try {
                                JSONObject flag = new JSONObject(string);
                                String status = flag.optString("Status");
                                String message = flag.optString("Message");
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                if (status.equals("1")) {
                                    dialog.dismiss();
                                    Signout();
                                }
                            } catch (JSONException je) {
                                // Tst(getApplication(), "The request could not be completed. Please try again.");
                                dialog.dismiss();
                            } catch (Exception ex) {
                            }
                        }

                        @Override
                        public void onFailed() {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        dialog.show();
    }

    private void DeactivateAccount() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.forgetpassword_dialog);
        final ImageView pencil = (ImageView) dialog.findViewById(R.id.pencil);
        pencil.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.deactive_icon));

        final TextView account = (TextView) dialog.findViewById(R.id.account);
        final Button lay11 = (Button) dialog.findViewById(R.id.lay11);

        final Button send = (Button) dialog.findViewById(R.id.send);
        final Button cancel = (Button) dialog.findViewById(R.id.cancel);
        final EditText edt_email = (EditText) dialog.findViewById(R.id.edt_email);
        //   edt_email.setError("Enter Your Password");
        edt_email.setHint("Enter Your Password.");

        account.setText(beforeDeactivateMsg);
        edt_email.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);


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
                dialog.dismiss();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String oldpass = edt_email.getText().toString();

                if (TextUtils.isEmpty(oldpass)) {
                    Toast.makeText(getActivity(), "Please enter the password.", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    // call service listener..
                    new CallWebService(getActivity(), cmf.urlList.DeleteAccount, cmf.DeletedMyAccount(AspnetUserID, oldpass, "DEACTIVE"), new MyServiceListener() {
                        @Override
                        public void onSuccess(String string) {

                            try {
                                JSONObject flag = new JSONObject(string);
                                String status = flag.optString("Status");
                                String message = flag.optString("Message");
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                if (status.equals("1")) {
                                    dialog.dismiss();
                                    Signout();
                                }
                            } catch (JSONException je) {
                                // Tst(getApplication(), "The request could not be completed. Please try again.");
                                dialog.dismiss();
                            } catch (Exception ex) {
                            }
                        }

                        @Override
                        public void onFailed() {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        dialog.show();
    }


    private void GetProfileAPICall() {
        new CallWebService(getActivity(), cmf.urlList.GetProfile, cmf.UserID(UserID), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {

                try {
                    JSONObject obj = new JSONObject(string);
                    String Status = obj.optString("Status");
                    String message = obj.optString("Message");
                    beforeDeleteMsg = obj.optString("beforeDeleteMsg");
                    beforeDeactivateMsg = obj.optString("beforeDeactivateMsg");
                    referralHelpMsg = obj.optString("referralHelpMsg");
                    if (Status.equals("0")) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        JSONArray jsonArray = obj.getJSONArray("Data");
                        JSONObject jsonObject = jsonArray.optJSONObject(0);
                        String UserName = jsonObject.optString("UserName");
                        String MobileNumber = jsonObject.optString("MobileNumber");
                        String EmailID = jsonObject.optString("EmailID");
                        InvitationCode = jsonObject.optString("InvitationCode");
                        PaymentStatusCode = jsonObject.optString("PaymentStatusCode");
                        inviteestatusid = jsonObject.optString("InviteeStatusID");

                        setprofileData(UserName, MobileNumber, EmailID, InvitationCode, PaymentStatusCode);
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

    private void setprofileData(String UserName, String MobileNumber, String EmailID, String dob, String about) {
        try {
            ETName.setText(UserName);
            ETPhone.setText(MobileNumber);
            ETMail.setText(EmailID);
            tv_dob.setText(dob);
            ET_about.setText(about);

            Picasso.with(getActivity()).load(user_image).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(IVProfileImg, new Callback() {
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
        }
    }

    public void sharing(String refercode) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "HOC Referral Code");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "I'm sharing my referral code, Use my referral code when you buy subscription in HOC app. My referral code is : " + refercode);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    //-----------------------------------------------------IMAGE UPLOAD--------
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(intent, CODE_OPEN_IMAGE_GALLEY);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadPermissions(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(), perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), perm)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{perm}, requestCode);
            }
        } else {
            switch (requestCode) {
                case GALLERY_REQUEST: {
                    startGallery();
                }
                break;
            }
        }
    }

    //----------------------------------------------------------For Image Both Multi Part and Base 64-----
    protected void startGallery() {
        // TODO Auto-generated method stub
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST
                    && resultCode == RESULT_OK) {
                selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                // for getting file extension
                ContentResolver cR = getActivity().getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                fileextension = mime.getExtensionFromMimeType(cR.getType(selectedImage));
                if (fileextension == null) {
                    fileextension = "jpg";
                }
                if (cursor != null) {
                    cursor.moveToFirst();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    //previously code
                  /*  cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);*/
                    imagePath = cursor.getString(column_index);
                    cursor.close();
                } else {
                    //imagePath = getRealPathFromURI(selectedImage);
                    imagePath = getRealPathFromURI2(selectedImage);
                }
                if (imagePath == null) {
                    imagePath = selectedImage.getPath();
                    try {
                        Bitmap photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                        File f = new File(Environment.getExternalStorageDirectory()
                                + File.separator + "test.jpg");
                        try {
                            if (f.exists())
                                f.delete();
                            f.createNewFile();
                            FileOutputStream fo = new FileOutputStream(f);
                            fo.write(bytes.toByteArray());
                            fo.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (f.exists()) {
                            imagePath = f.getAbsolutePath().toString();
                        } else {
                            Toast.makeText(getActivity(), "Image Not Supportable", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                IVProfileImg.setTag(imagePath);
                try {

                    uploadImageCaptured = new File(imagePath);
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    // IVProfileImg.setImageBitmap(bm);
                    performCrop(imagePath);
                    // upload_image_inServer_MultiPart();


                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (requestCode == RESULT_CROP) {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        // Bundle extras = data.getExtras();
                        // Bitmap selectedBitmap = extras.getParcelable("data");
                        Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        IVProfileImg.setImageBitmap(selectedBitmap);
                        selectedImage = data.getData();
                        upload_image_inServer_Base64();
                    } catch (Exception ex) {
                    }
                }
            }
        }
    }

 /*   public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }*/

    private String getRealPathFromURI2(Uri contentUri) {
        imagePath = contentUri.getPath();
        try {
            Bitmap photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentUri);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
            File f = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "mi.jpg");
            try {
                if (f.exists())
                    f.delete();
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (f.exists()) {
                imagePath = f.getAbsolutePath().toString();
            } else {
                Toast.makeText(getActivity(), "Image Not Supportable", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }


    private void upload_image_inServer_Base64() {
        //---->imagePath

        try {
            final InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            final String encodedImage = encodeImage(selectedImage);
            mprogressBar.setVisibility(View.VISIBLE);
            new CallWebService("", getActivity(), cmf.urlList.upload_image, cmf.android_img_up(UserID, encodedImage), new MyServiceListener() {
                @Override
                public void onSuccess(String string) {

                    try {
                        mprogressBar.setVisibility(View.GONE);
                        JSONObject obj = new JSONObject(string);
                        String Status = obj.optString("status");
                        String message = obj.optString("message");
                        if (Status.equals("1")) {
                            // Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            user_image = message;
                            cmf.myPreference.setString(getActivity(), GlobalConstants.USER_IMAGE, message);
                            //Tst_Snake(findViewById(R.id.tv_SignUp), "Please provide correct credentials.");
                            return;
                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed() {

                }
            });
        } catch (Exception ex) {
        }
    }

    private void performCrop(String picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);
            cropIntent.setDataAndType(contentUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, RESULT_CROP);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //-----------------For multiPart.........
  /*  private String upload_image_inServer_MultiPart() {
        String strReturn = "";
        //use ---> edit_image.php
        String uploadUrl = "http://108.170.54.215/App_development/Tracking/Api/edit_image.php";
        try {
            final HttpPost post = new HttpPost(uploadUrl);
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            File file = new File((String) IVProfileImg.getTag());


            multipartEntityBuilder.addPart("user_id", new StringBody(UserID));
            multipartEntityBuilder.addPart("file", new FileBody(new File(IVProfileImg.getTag().toString()), "test.jpg", "image/jpeg", "UTF-8"));


            MyHttpEntity.ProgressListener progressListener =
                    new MyHttpEntity.ProgressListener() {
                        @Override
                        public void transferred(float progress) {
                            Log.d("TAG ", "PROGRESS:------" + progress);
                            //new UploadImageAsync();
                        }
                    };
            post.setEntity(new MyHttpEntity(multipartEntityBuilder.build(),
                    progressListener));
            InputStream is = null;


            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        HttpResponse httpResponse = new DefaultHttpClient().execute(post);
                        HttpEntity httpEntity = httpResponse.getEntity();
                        String Response = EntityUtils.toString(httpEntity);
                        Log.d("Response:", Response);
                        JSONObject obj = new JSONObject(Response);
                        String Status = obj.optString("status");
                        String message = obj.optString("message");
                        if (Status.equals("1")) {
                            // Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            user_image=message;
                            cmf.myPreference.setString(getActivity(), GlobalConstants.USER_IMAGE,message);
                            //Tst_Snake(findViewById(R.id.tv_SignUp), "Please provide correct credentials.");
                            return;
                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                        //  is = httpEntity.getContent();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    *//*BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    Log.e("new", "5");
                    StringBuilder sb = new StringBuilder("");
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                  //  is.close();
                    strReturn = sb.toString();*//*
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

        return strReturn;
    }
    */


    //***************** Create One class******************
   /* package com.omsworld.familycare.setting;
    import org.apache.http.HttpEntity;
    import org.apache.http.entity.HttpEntityWrapper;
    import java.io.FilterOutputStream;
    import java.io.IOException;
    import java.io.OutputStream;
     public class MyHttpEntity extends HttpEntityWrapper {
        private ProgressListener progressListener;

        public MyHttpEntity(final HttpEntity entity, final ProgressListener progressListener) {
            super(entity);
            this.progressListener = progressListener;
        }

        public  interface ProgressListener {
            void transferred(float progress);
        }

        @Override
        public void writeTo(OutputStream outstream) throws IOException {
            super.writeTo(new ProgressOutputStream(outstream,this.progressListener,this.getContentLength()));
        }


        public static class ProgressOutputStream extends FilterOutputStream {

            private final ProgressListener progressListener;
            private long transferred;
            private long total;

            public ProgressOutputStream(final OutputStream outputStream,
                                        final ProgressListener progressListener,
                                        long total) {

                super(outputStream);
                this.progressListener = progressListener;
                this.transferred = 0;
                this.total = total;
            }

            @Override
            public void write(byte[] buffer, int offset, int length) throws IOException {

                out.write(buffer, offset, length);
                this.transferred += length;
                this.progressListener.transferred(this._getCurrentProgress());
            }

            @Override
            public void write(byte[] buffer) throws IOException {

                out.write(buffer);
                this.transferred++;
                this.progressListener.transferred(this._getCurrentProgress());
            }

            private float _getCurrentProgress() {
                return ((float) this.transferred / this.total) * 100;
            }
        }
    }*/


    //========================================
}