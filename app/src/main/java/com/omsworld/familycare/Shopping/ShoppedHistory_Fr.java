package com.omsworld.familycare.Shopping;

/**
 * Created by rupesh.m on 2/5/2018.
 */

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.omsworld.familycare.R;
import com.omsworld.familycare.SharedContacts.SearchModel;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.DateTime_c;
import com.omsworld.familycare.api_call.GlobalConstants;
import com.omsworld.familycare.api_call.MyServiceListener;
import com.omsworld.familycare.model.ShoppedHistory_Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ShoppedHistory_Fr extends Fragment implements View.OnClickListener {


    final Calendar c = Calendar.getInstance();
    ShoppedHistory_Model model;
    ShoppedHistory_Adapter adapter;
    ArrayList<ShoppedHistory_Model> list = new ArrayList<>();
    CommonFunctions cmf;
    RecyclerView RV_groceryHistoryList;
    TextView TV_totalAmount,TV_row_name,TV_row_price,TV_fromDate,TV_toDate,TV_searchTotalPrice;
    float searchTotalPrice=0;
    String searchedName,searchedPrice;
    ImageView IV_search;
    //----------------------------------------datePicker
    DatePickerDialog datePickerDialog;
    int mYear = c.get(Calendar.YEAR); // current year
    int mMonth = c.get(Calendar.MONTH); // current month
    int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
    String fromdT,toDT;
    private View rootView;
    private ProgressBar mprogressBar;
    String AspnetUserID;




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.shopped_history_fr, container, false);
        initilize();
        return rootView;
    }

    private void initilize() {
        cmf = new CommonFunctions(getActivity());
        RV_groceryHistoryList = (RecyclerView) rootView.findViewById(R.id.RV_groceryHistoryList);
        RV_groceryHistoryList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mprogressBar = (ProgressBar) rootView.findViewById(R.id.mprogressBar);
        TV_totalAmount = (TextView) rootView.findViewById(R.id.TV_totalAmount);

        TV_row_name = (TextView) rootView.findViewById(R.id.TV_row_name);
        TV_row_price = (TextView) rootView.findViewById(R.id.TV_row_price);
        TV_fromDate = (TextView) rootView.findViewById(R.id.TV_fromDate);
        TV_toDate = (TextView) rootView.findViewById(R.id.TV_toDate);
        IV_search = (ImageView) rootView.findViewById(R.id.IV_search);
        TV_searchTotalPrice = (TextView) rootView.findViewById(R.id.TV_searchTotalPrice);

        TV_fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDateDialog(1);
            }
        });
        TV_toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDateDialog(2);
            }
        });

        IV_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_item();
            }
        });
          AspnetUserID = cmf.myPreference.getString(getActivity(), GlobalConstants.USER_ID);//"151041";
        shoppedhistory();
    }

    private void search_item(){
        if(!TV_fromDate.getText().toString().isEmpty() && !TV_toDate.getText().toString().isEmpty()) {
            searchTotalPrice=0;
            searchedName="";
            searchedPrice="";
            final List<ShoppedHistory_Model> filteredModelList = filter(list, fromdT, toDT);
            adapter.setFilter(filteredModelList);
            adapter.notifyDataSetChanged();
            TV_searchTotalPrice.setText("Total Item "+filteredModelList.size()+"    Total Price :"+String.valueOf(searchTotalPrice));
            TV_row_name.setText(searchedName);
            TV_row_price.setText(searchedPrice);

        }
    }

    private List<ShoppedHistory_Model> filter(List<ShoppedHistory_Model> models, String fromDate, String toDate) {
        final List<ShoppedHistory_Model> filteredMlList = new ArrayList<>();
        for (ShoppedHistory_Model model : models) {
            final String compareDate = model.getAdded_date().toLowerCase();

            if (DateTime_c.getInstance().dateExistInFromToDate(fromDate,toDate,compareDate)) {
                filteredMlList.add(model);
                searchTotalPrice = searchTotalPrice + Float.valueOf(model.getPrice());
                searchedName=searchedName+model.getAdded_by_name()+"\n";
                searchedPrice=searchedPrice+model.getPrice()+"\n";
            }
        }
        return filteredMlList;
    }

    private void getDateDialog(final int fromTo) {
        // date picker dialog
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
               // Toast.makeText(getActivity(), "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year, Toast.LENGTH_LONG).show();
                if (fromTo == 1) {
                    TV_fromDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    //yyyy-MM-dd HH:mm:ss
                    fromdT=year+ "-" + (monthOfYear + 1) + "-" + dayOfMonth + " 00:00:00";
                    getDateDialog(2);
                    Toast.makeText(getActivity(),"Choose last date. ",Toast.LENGTH_LONG).show();
                } else if (fromTo == 2) {
                    TV_toDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    toDT=year+ "-" + (monthOfYear + 1) + "-" + dayOfMonth + " 00:00:00";
                    search_item();
                }
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void shoppedhistory() {

        list.clear();
        new CallWebService("", getActivity(), cmf.urlList.shopped_groceries_history, cmf.user_id(AspnetUserID), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    mprogressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(string);
                    String Status = jsonObject.optString("success");
                    double flotAmount = 0.00;
                    if (Status.equals("1")) {
                        JSONArray jsonArray = jsonObject.optJSONArray("shopped_note");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            JSONArray jsonArray2 = jsonObject1.optJSONArray("added_item");

                            for (int j = 0; j < jsonArray2.length(); j++) {
                                JSONObject jsonObject2 = jsonArray2.optJSONObject(j);
                                String added_by_name = jsonObject2.optString("added_by_name");
                                //--------------------------
                                String price = jsonObject2.optString("price");
                                try {
                                    flotAmount = flotAmount + Float.valueOf(price);
                                } catch (Exception ex) {
                                }
                                //---------------------
                                String added_date = jsonObject2.optString("added_date");

                                JSONArray jsonArray3 = jsonObject2.optJSONArray("item");
                                String itemName = "";
                                for (int k = 0; k < jsonArray3.length(); k++) {
                                    JSONObject jsonObject3 = jsonArray3.optJSONObject(k);
                                    //String.valueOf(k)+"."+
                                    itemName = itemName + String.valueOf(k) + ".  " + jsonObject3.optString("item_name") + "\n";
                                }
                                // Toast.makeText(getActivity(), itemName, Toast.LENGTH_LONG).show();

                                model = new ShoppedHistory_Model();
                                model.setItem_name(itemName);
                                model.setItem_count(String.valueOf(jsonArray3.length()));
                                model.setAdded_by_name(added_by_name);
                                model.setAdded_date(added_date);
                                model.setPrice(price);
                                list.add(model);
                            }
                        }
                        //----------
                        TV_totalAmount.setText("Total Amount: " + String.valueOf(flotAmount));
                        adapter = new ShoppedHistory_Adapter(getActivity(), list);
                        RV_groceryHistoryList.setAdapter(adapter);

                    } else {

                    }
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
                mprogressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.landlineno:

                break;

        }
    }

}
