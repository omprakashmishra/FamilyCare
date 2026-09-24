package com.omsworld.familycare.SharedContacts;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.omsworld.familycare.Chat.FriendsList_fr;
import com.omsworld.familycare.R;
import com.omsworld.familycare.activity.MainActivity;
import com.omsworld.familycare.adapter.CountryCodeAdapter;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.omsworld.familycare.api_call.MyServiceListener;
import com.omsworld.familycare.model.CountryCodeModel;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by rupesh.m on 12/17/2016.
 */

public class Sharedcontacts_fr extends Fragment implements MyServiceListener {
    public static int list_SelectedPos;
    public SlidingUpPanelLayout slidingPannel;
    @Bind(R.id.IV_shrt)
    ImageView IVShrt;
    @Bind(R.id.IV_addContact)
    ImageView IVAddContact;
    @Bind(R.id.IV_shareMessage)
    ImageView IVshareMessage;
    List<SearchModel> vendorList = new ArrayList<>();
    List<SearchModel> timp_contact_list = new ArrayList<>();
    SearchAdapter searchAdapter;
    RecyclerView RVVendors;
    EditText TVSearch;
    CommonFunctions cmf;
    String user_id,family_id;
    //-----------------
    String Phone, Name, Email;
    Dialog canceljob;
    TextView TV_ActivitionCode;
    Spinner SPCountrycode;
    CountryCodeModel countryCodeModel;
    CountryCodeAdapter countryCodeAdapter;
    List<CountryCodeModel> spinnerArray = new ArrayList<>();
    String countyCd;
    private View rootView;
    private Button ok;
    private EditText text, TV_Name, TV_Phone, ET_InvtCounty_code, TV_Email;
    private ImageView IV_phonebook;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.shared_contacts_fr, container, false);
        cmf = new CommonFunctions(getActivity());
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Initview();
    }

    private void Initview() {
        RVVendors = (RecyclerView) rootView.findViewById(R.id.RV_Vendors);
        TVSearch = (EditText) rootView.findViewById(R.id.TV_search);
        RVVendors.setLayoutManager(new LinearLayoutManager(getActivity()));
        user_id = cmf.myPreference.getString(getActivity(), cmf.gc.USER_ID);
        family_id = cmf.myPreference.getString(getActivity(), cmf.gc.FAMILY_ID);


        slidingPannel = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        slidingPannel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i("TAG", "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i("TAG", "onPanelStateChanged " + newState);
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    contactEditDelete(list_SelectedPos);
                }
            }
        });
        // slidingPannel.setDragView(rootView.findViewById(R.id.IV_shrt));
        slidingPannel.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingPannel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            }
        });

        if(vendorList.size()>0){
            searchAdapter = new SearchAdapter(vendorList, getActivity(), slidingPannel);
            RVVendors.setAdapter(searchAdapter);
        }else {
            new CallWebService(getActivity(), cmf.urlList.phone_book_list, cmf.phone_book_list(user_id,family_id), Sharedcontacts_fr.this);
        }

        //----------------
        // search suggestions using the edittext widget

        TVSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (vendorList.isEmpty()) {
                    return;
                }
                final List<SearchModel> filteredModelList = filter(vendorList, s.toString());

                searchAdapter.setFilter(filteredModelList);
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private List<SearchModel> filter(List<SearchModel> models, String query) {
        query = query.toLowerCase();

        final List<SearchModel> filteredModelList = new ArrayList<>();
        for (SearchModel model : models) {
            final String text = model.name.toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public void onSuccess(String string) {
        loadDataServices(string);
    }

    @Override
    public void onFailed() {
    }

    private void loadDataServices(String JsonString) {
        // RVServices = (RecyclerView) getView().findViewById(R.id.RV_services);
        try {
            Log.d("---Sharedcontacts_fr->", JsonString);
            JSONObject RootFlag = new JSONObject(JsonString);
           /* if(!RootFlag.optString("result").equals("1")){
                Toast.makeText(getApplicationContext(),"Please try again...!",Toast.LENGTH_SHORT).show();
            return;
            }*/
            String success = RootFlag.optString("success");
            if (!success.equals("1")) {
                return;
            }

            JSONArray vendorService = RootFlag.getJSONArray("your_phone_book");
            for (int i = 0; i < vendorService.length(); i++) {
                JSONObject serviceFlag = vendorService.getJSONObject(i);


                String name = serviceFlag.optString("name");
                String id = serviceFlag.optString("id");
                String phone = serviceFlag.optString("phone");
                String comment = serviceFlag.optString("comment");
                String added_date = serviceFlag.optString("added_date");
                // String url = "https://n6-img-fp.akamaized.net/free-vector/button-the-best_1012-102.jpg?size=338&ext=jpg";
                //---------------------------------------------------
                SearchModel servicesModel = new SearchModel();

                servicesModel.name = name;
                servicesModel.id = id;
                servicesModel.phone = phone;
                servicesModel.comment = comment;
                servicesModel.added_date = added_date;
                servicesModel.added_by = "me";
                // servicesModel.url = url;
                vendorList.add(servicesModel);
            }
            //==============================
            JSONArray freinds_phone_book = RootFlag.getJSONArray("freinds_phone_book");
            for (int i = 0; i < freinds_phone_book.length(); i++) {
                JSONObject serviceFlag = freinds_phone_book.getJSONObject(i);

                String added_by = serviceFlag.optString("user_fullname");
               // String added_userId = serviceFlag.optString("user_id");

                JSONArray subArray = serviceFlag.optJSONArray("save_phone");
                for (int j = 0; j < subArray.length(); j++) {
                    JSONObject subobj = subArray.getJSONObject(j);
                    String name = subobj.optString("name");
                    String phone = subobj.optString("phone");
                    String comment = subobj.optString("comment");
                    String added_date = subobj.optString("added_date");
                    //---------------------------------------------------
                    SearchModel servicesModel = new SearchModel();
                    servicesModel.name = name;
                    servicesModel.id = "";
                    servicesModel.phone = phone;
                    servicesModel.comment = comment;
                    servicesModel.added_date = added_date;
                    servicesModel.added_by = added_by;
                    // servicesModel.url = url;
                    vendorList.add(servicesModel);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            // Toast.makeText(getApplicationContext(),"Please try again...!",Toast.LENGTH_SHORT).show();
        }
        searchAdapter = new SearchAdapter(vendorList, getActivity(), slidingPannel);
        RVVendors.setAdapter(searchAdapter);
    }


    public void shortingItem() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.filter);
        dialog.setTitle("This is my custom dialog box");
        dialog.setCancelable(true);
        // there are a lot of settings, for dialog, check them all out!
        // set up radiobutton
        final RadioButton rd1 = (RadioButton) dialog.findViewById(R.id.rd);
        final RadioButton rd2 = (RadioButton) dialog.findViewById(R.id.rd_2);
        rd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rd2.setChecked(false);
                Collections.sort(vendorList, new SearchModel().ascending);
                searchAdapter.setFilter(vendorList);
            }
        });
        rd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rd1.setChecked(false);
                Collections.sort(vendorList, new SearchModel().descending);
                searchAdapter.setFilter(vendorList);
            }
        });

        // now that the dialog is set up, it's time to show it
        dialog.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.IV_shrt, R.id.IV_addContact, R.id.IV_shareMessage})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.IV_shrt:
                shortingItem();
                break;
            case R.id.IV_addContact:
                Dialog();
                break;
            case R.id.IV_shareMessage:
                MainActivity.toolbar.setTitle("Family Post Message");
                cmf.replaceFragment(getActivity(),new FriendsList_fr() );
                // MainActivity.replaceFragment(new Chat_Fr());
                break;
        }
    }

    //--------------------------for add contacts....
    private void setcountryCodeAPI() {
        new CallWebService("", getActivity(), cmf.urlList.Country_Code, cmf.GetOrganization(""), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {

                try {
                    JSONObject jsonObject = new JSONObject(string);
                    int Status = jsonObject.optInt("Status");
                    String Message = jsonObject.optString("Message");
                    if (Status == 1) {
                        JSONArray DataArray = jsonObject.optJSONArray("Data");
                        for (int i = 0; i < DataArray.length(); i++) {
                            JSONObject Dataobj = DataArray.getJSONObject(i);
                            String CountryCode = Dataobj.optString("CountryCode");
                            String CountryName = Dataobj.optString("CountryName");
                            countryCodeModel = new CountryCodeModel();
                            countryCodeModel.CountryCode = CountryCode;
                            countryCodeModel.CountryName = CountryName;
                            spinnerArray.add(countryCodeModel);
                        }
                        countryCodeAdapter = new CountryCodeAdapter(getActivity(), R.layout.item_collages, spinnerArray);
                        SPCountrycode.setAdapter(countryCodeAdapter);
                        countryCodeAdapter.notifyDataSetChanged();
                        for (int i = 0; i < spinnerArray.size(); i++) {
                            if (spinnerArray.get(i).CountryCode.contains(countyCd)) {
                                SPCountrycode.setSelection(i);
                            }
                        }
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

    private void Dialog() {

        canceljob = new Dialog(getActivity());
        canceljob.setContentView(R.layout.dialog_invitationfriend);
        canceljob.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ok = (Button) canceljob.findViewById(R.id.ok);
        ok.setText("Add Family Book");

        TV_Name = (EditText) canceljob.findViewById(R.id.TV_Name);
        TV_Phone = (EditText) canceljob.findViewById(R.id.TV_Phone);
        TV_Email = (EditText) canceljob.findViewById(R.id.TV_Email);
        TV_Email.setHint("Enter extra comment");

        TV_ActivitionCode = (TextView) canceljob.findViewById(R.id.TV_ActivitionCode);
        // TV_ActivitionCode.setText(" ADD EMERGENCY CONTACT.\nInvitation Code " + cmf.myPreference.getString(getActivity(), GlobalConstants.ACTIVATIONCODE));
        TV_ActivitionCode.setText(" ADD EMERGENCY CONTACT");
        IV_phonebook = (ImageView) canceljob.findViewById(R.id.IV_phonebook);
        ET_InvtCounty_code = (EditText) canceljob.findViewById(R.id.ET_InvtCounty_code);
        ET_InvtCounty_code.setText(cmf.myPreference.getString(getActivity(), GlobalConstants.COUNTYCD));
        setcountryCodeAPI();
        SPCountrycode = (Spinner) canceljob.findViewById(R.id.SP_Countrycode);
        countyCd = cmf.myPreference.getString(getActivity(), GlobalConstants.COUNTRYCODEID);
        SPCountrycode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countyCd = spinnerArray.get(position).CountryCode.toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        IV_phonebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
            }
        });
        ok.setVisibility(View.VISIBLE);

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Name = TV_Name.getText().toString();
                Phone = TV_Phone.getText().toString();
                Email = TV_Email.getText().toString();

                //   countyCd = ET_InvtCounty_code.getText().toString();
                if (Phone.length() < 9) {
                    Toast.makeText(getActivity(), "Please enter a valid mobile number.", Toast.LENGTH_SHORT).show();
                } else {
                    if (countyCd.equals("0")) {
                        Toast.makeText(getActivity(), "Please select country code", Toast.LENGTH_SHORT).show();
                    } else {
                        cmf.hideKeyboard(v);
                        DialogInvitationApiCall(Name, Phone, countyCd, Email);
                        /*if (!TV_Email.getText().toString().equals("")) {
                            if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                                TV_Email.setError("Please enter a valid email address.");
                            } else {
                                DialogInvitationApiCall(Name, Phone, countyCd, Email);
                            }
                        } else {
                            DialogInvitationApiCall(Name, Phone, countyCd, Email);
                        }*/

                    }
                }
                cmf.hideKeyboard(v);
            }
        });
        canceljob.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                Cursor c = null;
                try {
                    c = getActivity().getContentResolver().query(uri, new String[]{
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.Contacts.DISPLAY_NAME},
                            null, null, null);
                    if (c != null && c.moveToFirst()) {
                        String mobilenumber = c.getString(0);
                        int namedisplay = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                        String name = c.getString(namedisplay);
                        setname_Number(name, mobilenumber);
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
    }

    private void DialogInvitationApiCall(final String Name, final String Phone, final String CountryCode, final String extracomments) {
        final String to_ph = Phone.replaceAll("\\s", "");
        canceljob.dismiss();
        new CallWebService(getActivity(), cmf.urlList.add_phone_book, cmf.add_phone_book(user_id, Name, to_ph, extracomments), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {

                try {
                    JSONObject jsonObject = new JSONObject(string);
                    String Status = jsonObject.optString("status");
                    String Message = jsonObject.optString("message");
                    if (Status.equals("1")) {
                        Toast.makeText(getActivity(), Message, Toast.LENGTH_SHORT).show();
                        // SentFriendApiCall();
                        //-----------------------Add new Data
                        SearchModel servicesModel = new SearchModel();
                        servicesModel.name = Name;
                        servicesModel.id = user_id;
                        servicesModel.phone = to_ph;
                        servicesModel.comment = extracomments;
                        servicesModel.added_date = "Now";
                        servicesModel.added_by = "me";
                        // servicesModel.url = url;
                        vendorList.add(servicesModel);
                        Collections.sort(vendorList, new SearchModel().ascending);
                        searchAdapter.setFilter(vendorList);
                        searchAdapter.notifyDataSetChanged();
                        //----------------------

                    } else {
                        canceljob.show();
                        Toast.makeText(getActivity(), Message, Toast.LENGTH_SHORT).show();
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

    public void setname_Number(String name, String mobilenumber) {

        // remove white space..
        String phn = mobilenumber.replaceAll("\\s+", "");

        TV_Phone.setText("" + phoneValidate(phn));
        TV_Name.setText(name);
    }

    private String phoneValidate(final String phn) {

        String pn = "";
        try {
            if (phn.contains("+91")) {
                pn = phn.replace("+91", "");
            } else if (phn.contains("+1")) {
                pn = phn.replace("+1", "");
            } else if (phn.contains("(")) {
                pn = phn.replaceAll("[()]", "");
                pn = pn.replaceAll("-", "");
            }
        } catch (Exception ex) {
            pn = phn;
        }

        return pn;
    }

    //-----------------------------------Contact edit or delete..
    private void contactEditDelete(final int list_SelectedPos) {
        final EditText ET_Name = (EditText) rootView.findViewById(R.id.ET_Name);
        final EditText ET_Phone = (EditText) rootView.findViewById(R.id.ET_Phone);
        final EditText TV_Extracomment = (EditText) rootView.findViewById(R.id.TV_Extracomment);

        ET_Name.setText(vendorList.get(list_SelectedPos).name);
        ET_Phone.setText(vendorList.get(list_SelectedPos).phone);
        TV_Extracomment.setText(vendorList.get(list_SelectedPos).comment);

        ImageView IV_delete = (ImageView) rootView.findViewById(R.id.IV_delete);
        IV_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timp_contact_list.add(vendorList.get(list_SelectedPos));
                vendorList.remove(list_SelectedPos);
                searchAdapter.notifyDataSetChanged();
                slidingPannel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                cmf.hideKeyboard(IVShrt);
                deletePhone(ET_Phone.getText().toString());
            }
        });

        ImageView IV_save = (ImageView) rootView.findViewById(R.id.IV_save);
        IV_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id=vendorList.get(list_SelectedPos).id;
                String nm=ET_Name.getText().toString();
                String ph=ET_Phone.getText().toString();
                String cmnt=TV_Extracomment.getText().toString();


                // selected item add in temp list
                timp_contact_list.add(vendorList.get(list_SelectedPos));
                //selected item delete from mail list
                vendorList.remove(list_SelectedPos);

                // updated item add in mail list.
                SearchModel servicesModel = new SearchModel();
                servicesModel.name = nm;
                servicesModel.id = id;
                servicesModel.phone = ph;
                servicesModel.comment = cmnt;
                servicesModel.added_date = "Now";
                servicesModel.added_by = "me";
                // servicesModel.url = url;
                vendorList.add(servicesModel);
                searchAdapter.notifyDataSetChanged();
                cmf.hideKeyboard(IVShrt);
                editPhone(id,nm,ph,cmnt);
            }
        });
    }

    private void editPhone(String id, String nm, String ph, String cmnt){
        new CallWebService("",getActivity(), cmf.urlList.edit_phone_book, cmf.edit_phone_book(id,user_id,nm,ph,cmnt), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    slidingPannel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                    JSONObject RootFlag = new JSONObject(string);
                    String success = RootFlag.optString("status");
                    if (success.equals("1")) {
                        timp_contact_list.clear();
                    }else {
                        vendorList.remove(list_SelectedPos);
                        vendorList.add(timp_contact_list.get(list_SelectedPos));
                        timp_contact_list.clear();
                        searchAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Please try again.",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {}
            }
            @Override
            public void onFailed() {}
        });
    }
    private void deletePhone(final  String phone ){
        new CallWebService("",getActivity(), cmf.urlList.delete_phone_book, cmf.delete_phone_book(user_id,phone), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject RootFlag = new JSONObject(string);
                    String success = RootFlag.optString("status");
                    if (success.equals("1")) {
                        timp_contact_list.clear();
                    }else {
                        vendorList.add(timp_contact_list.get(list_SelectedPos));
                        searchAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Please try again.",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {}
            }

            @Override
            public void onFailed() {
            }
        });
    }


    //==============================================Closed==================================
}

