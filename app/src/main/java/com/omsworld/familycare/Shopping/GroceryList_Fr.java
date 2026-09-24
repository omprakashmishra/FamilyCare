package com.omsworld.familycare.Shopping;

/**
 * Created by rupesh.m on 2/5/2018.
 */

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.omsworld.familycare.ClickListener.RecyclerItemClickListener;
import com.omsworld.familycare.R;
import com.omsworld.familycare.SharedContacts.Fullsrc;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.MyServiceListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class GroceryList_Fr extends Fragment implements View.OnClickListener {
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.CAMERA};
    View rootView;
    EditText mBodyText;
    CommonFunctions cmf;
    String USER_ID, user_name;
    TextView TV_addGrocery, TV_dateTime, TV_shop_unshop;
    public static TextView TV_headTest;
    EditText ET_finalPrice;
    String lastgroceryList = "", nowGrceryList = "";
    ImageView IV_backDate, IV_share_item, IV_forwordDate, IV_more;
    GroceryList_Adapter adapter;
    private ImageView IV_barcode;
    private int openedListPostion = 0;
    private int start_shopping = 0;
    //------------------------------
    RecyclerView RV_groceryList;
    private ArrayList<GroceryListModel> groceryListarraylist = new ArrayList<>();
    private ArrayList<AddedItemModel> additemarraylist = new ArrayList<>();
    private AddedItemModel addedItemModel;
    private GroceryListModel groceryListModel;
    public static ArrayList ShopedItem = new ArrayList();
    private ProgressBar mprogressBar;
    Dialog dialog_shopped_item;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.grocery_list_fr, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialization();
    }

    private void initialization() {
        cmf = new CommonFunctions(getActivity());
        USER_ID = cmf.myPreference.getString(getActivity(), cmf.gc.USER_ID);
        user_name = cmf.myPreference.getString(getActivity(), cmf.gc.USER_NAME);

        groceryListarraylist.clear();
        additemarraylist.clear();
        ShopedItem.clear();
        openedListPostion = 0;
        start_shopping = 0;

        IV_barcode = (ImageView) rootView.findViewById(R.id.IV_barcode);
        IV_share_item = (ImageView) rootView.findViewById(R.id.IV_share_item);
        TV_addGrocery = (TextView) rootView.findViewById(R.id.TV_addGrocery);
        TV_shop_unshop = (TextView) rootView.findViewById(R.id.TV_shop_unshop);
        TV_headTest = (TextView) rootView.findViewById(R.id.TV_headTest);
        ET_finalPrice = (EditText) rootView.findViewById(R.id.ET_finalPrice);


        IV_barcode.setOnClickListener(this);
        TV_addGrocery.setOnClickListener(this);
        IV_share_item.setOnClickListener(this);
        TV_shop_unshop.setOnClickListener(this);

        IV_forwordDate = (ImageView) rootView.findViewById(R.id.IV_forwordDate);
        IV_backDate = (ImageView) rootView.findViewById(R.id.IV_backDate);
        IV_forwordDate.setOnClickListener(this);
        IV_backDate.setOnClickListener(this);

        mBodyText = (EditText) rootView.findViewById(R.id.body);
        mBodyText.setMovementMethod(LinkMovementMethod.getInstance());
        verifyStoragePermissions(getActivity());

        RV_groceryList = (RecyclerView) rootView.findViewById(R.id.RV_groceryList);
        RV_groceryList.setLayoutManager(new LinearLayoutManager(getActivity()));
        RV_groceryList.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), RV_groceryList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (start_shopping == 0) {
                            is_shopping_start();
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                })
        );

        TV_dateTime = (TextView) rootView.findViewById(R.id.TV_dateTime);
        IV_backDate = (ImageView) rootView.findViewById(R.id.IV_backDate);

        mprogressBar = (ProgressBar) rootView.findViewById(R.id.mprogressBar);
        familyGroceryList();


        IV_more = (ImageView) rootView.findViewById(R.id.IV_more);
        IV_more.setOnClickListener(this);


        mBodyText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    nowGrceryList = mBodyText.getText().toString();
                    if (start_shopping == 0) {
                        createGroceryList();
                    }
                    return true;
                }
                return false;
            }
        });
    }


    private void familyGroceryList() {
        new CallWebService("", getActivity(), cmf.urlList.groceries_list, cmf.user_id(USER_ID), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    mprogressBar.setVisibility(View.GONE);
                    // Toast.makeText(getActivity(), string, Toast.LENGTH_LONG).show();
                    JSONObject jsonObject = new JSONObject(string);
                    String success = jsonObject.optString("success");
                    if (!success.equals("1")) {
                        return;
                    }
                    JSONArray jsonarray = jsonObject.optJSONArray("shopping_note");
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonObject1 = jsonarray.optJSONObject(i);
                        groceryListModel = new GroceryListModel();
                        additemarraylist = new ArrayList<>();
                        String added_date = jsonObject1.optString("added_date");
                        groceryListModel.setAdded_date(added_date);
                        JSONArray jsonarray2 = jsonObject1.optJSONArray("added_item");
                        for (int j = 0; j < jsonarray2.length(); j++) {
                            addedItemModel = new AddedItemModel();
                            JSONObject jsonobject3 = jsonarray2.optJSONObject(j);
                            String id = jsonobject3.optString("id");
                            String added_by = jsonobject3.optString("added_by");
                            String added_by_name = jsonobject3.optString("added_by_name");
                            String note = jsonobject3.optString("note");
                            String added_date2 = jsonobject3.optString("added_date");
                            String is_shopped = jsonobject3.optString("is_shopped");
                            addedItemModel.setId(id);
                            addedItemModel.setAdded_by(added_by);
                            addedItemModel.setAdded_by_name(added_by_name);
                            addedItemModel.setAdded_date(added_date2);
                            addedItemModel.setNote(note);
                            addedItemModel.setIsItemShopped(is_shopped);
                            additemarraylist.add(addedItemModel);
                        }
                        groceryListModel.setAddedItemModels(additemarraylist);
                        groceryListarraylist.add(groceryListModel);
                    }
                    /*dateValidate(groceryListarraylist.get(0).getAdded_date());
                    adapter = new GroceryList_Adapter(getActivity(), groceryListarraylist.get(0).getAddedItemModels());
                    RV_groceryList.setAdapter(adapter);*/
                    validateGroceryItem(0);

                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
                mprogressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Please Try Again..", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void dateValidate(String dateTime) {
        try {
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            //String dateTime = arrayList.get(position).getTime();

            Date date = df.parse(dateTime);

            //for current date
            final String C_Year = String.valueOf(calendar.get(Calendar.YEAR));
            final String C_Day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            final String C_Month = new SimpleDateFormat("MMM").format(calendar.getTime());
            //  String C_Time = new SimpleDateFormat("HH:mm").format(calendar.getTime());

            //for server date
            calendar.setTime(date);

            final String Year = String.valueOf(calendar.get(Calendar.YEAR));
            final String Day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            final String Month = new SimpleDateFormat("MMM").format(calendar.getTime());
            final String Time = new SimpleDateFormat("HH:mm").format(calendar.getTime());

            if (C_Year.equals(Year)) {
                if (C_Month.equals(Month) && C_Day.equals(Day)) {
                    //  last_chatTime=Time;
                    TV_dateTime.setText("Today");
                } else {
                    //  last_chatTime=Day + " " + Month;
                    TV_dateTime.setText(Day + " " + Month);
                }
            } else {
                // last_chatTime=arrayList.get(position).getTime();
                TV_dateTime.setText(dateTime);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, MY_CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.IV_barcode:
                startActivity(new Intent(getActivity(), ScanActivity.class));
                break;
            case R.id.TV_addGrocery:
                nowGrceryList = mBodyText.getText().toString();
                if (start_shopping == 0) {
                    createGroceryList();
                } else {
                    compteShopping();
                }
                break;

            case R.id.IV_share_item:
                shareGroceryForShop(openedListPostion);
                break;
            case R.id.TV_shop_unshop:
                is_shopping_start();
                break;
            case R.id.IV_backDate:
                openedListPostion++;
                if (openedListPostion < groceryListarraylist.size()) {
                    validateGroceryItem(openedListPostion);
                    IV_forwordDate.setVisibility(View.VISIBLE);
                }
                if (openedListPostion == groceryListarraylist.size() - 1) {
                    IV_backDate.setVisibility(View.GONE);
                }

                break;
            case R.id.IV_forwordDate:
                openedListPostion--;
                if (openedListPostion >= 0) {
                    validateGroceryItem(openedListPostion);
                    IV_backDate.setVisibility(View.VISIBLE);
                }
                if (openedListPostion == 0) {
                    IV_forwordDate.setVisibility(View.GONE);
                }
                break;
            case R.id.IV_more:
                alreadyShoppedItem();
                break;

        }
    }

    public void alreadyShoppedItem() {
        dialog_shopped_item = new Dialog(getActivity());
        dialog_shopped_item.setContentView(R.layout.dialog_full_screen);
        dialog_shopped_item.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog_shopped_item.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView ok = (TextView) dialog_shopped_item.findViewById(R.id.ok);
        TextView no = (TextView) dialog_shopped_item.findViewById(R.id.no);

        TextView TV_Message = (TextView) dialog_shopped_item.findViewById(R.id.message);
        //  String username = mDataset.get(positions).getNearAndDearName();
        TV_Message.setText("Upload your shopped items or list.");

        final EditText view_grocery_item = (EditText) dialog_shopped_item.findViewById(R.id.view_custom);
        view_grocery_item.setMovementMethod(LinkMovementMethod.getInstance());
        verifyStoragePermissions(getActivity());
        final EditText ET_finalPrc = (EditText) dialog_shopped_item.findViewById(R.id.ET_finalPrc);


        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String itm = view_grocery_item.getText().toString();
                String prc = ET_finalPrc.getText().toString();
                if (cmf.isEmpty(itm) || cmf.isEmpty(prc)) {
                    Toast.makeText(getActivity(), "Please Enter All Things.", Toast.LENGTH_LONG).show();
                    return;
                }
                new CallWebService(getActivity(), cmf.urlList.direct_purchase, cmf.direct_purchase(USER_ID, itm, prc),
                        new MyServiceListener() {
                            @Override
                            public void onSuccess(String string) {
                                try {
                                    JSONObject jsonObject = new JSONObject(string);
                                    String Status = jsonObject.optString("status");
                                    String Message = jsonObject.optString("message");
                                    if (Status.equals("1")) {
                                        dialog_shopped_item.dismiss();
                                    }
                                    Toast.makeText(getActivity(), Message, Toast.LENGTH_LONG).show();
                                } catch (Exception ex) {
                                    Toast.makeText(getActivity(), "Please Try Again", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailed() {
                            }
                        });
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog_shopped_item.dismiss();
            }
        });
        dialog_shopped_item.show();

    }

    private void compteShopping() {
        if (ShopedItem.size() == 0) {
            Toast.makeText(getActivity(), "Please Shop something from shop list item", Toast.LENGTH_LONG).show();
        } else {
            String itemId = "";
            for (int i = 0; i < ShopedItem.size(); i++) {
                if (i == ShopedItem.size() - 1) {
                    itemId = itemId + ShopedItem.get(i);
                } else {
                    itemId = itemId + ShopedItem.get(i) + ",";
                }
            }

            new CallWebService(getActivity(), cmf.urlList.add_shopped_groceries, cmf.add_shopped_groceries(USER_ID
                    , itemId, ET_finalPrice.getText().toString()), new MyServiceListener() {
                @Override
                public void onSuccess(String string) {
                    try {
                        /// Toast.makeText(getActivity(), string, Toast.LENGTH_LONG).show();
                        JSONObject jsonObject = new JSONObject(string);
                        String Status = jsonObject.optString("status");
                        String Message = jsonObject.optString("message");
                        if (Status.equals("1")) {
                            Toast.makeText(getActivity(), "Thank You " + cmf.myPreference.getString(getActivity(), cmf.gc.USER_NAME) + " For Shopping."
                                    , Toast.LENGTH_LONG).show();

                            cmf.hideKeyboard(ET_finalPrice);
                            ET_finalPrice.setText("");
                            TV_shop_unshop.setText("shop now");

                            TV_headTest.setText(" You Have Shopped ");
                            TV_headTest.setText("Scan Grocery Product.");

                            ET_finalPrice.setVisibility(View.GONE);
                            ET_finalPrice.setVisibility(View.GONE);
                            mBodyText.setVisibility(View.VISIBLE);
                            IV_barcode.setVisibility(View.VISIBLE);

                            initialization();
                        } else {
                            Toast.makeText(getActivity(), Message, Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception ex) {
                        Toast.makeText(getActivity(), "Please Try Again", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailed() {
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!cmf.gc.scan_result.equals("")) {
            // Toast.makeText(getActivity(), scan_result, Toast.LENGTH_SHORT).show();
            mBodyText.setText(mBodyText.getText().toString() + "\n" + cmf.gc.scan_result);
            cmf.gc.scan_result = "";
        }
    }

    private void createGroceryList() {
        if (lastgroceryList.equals(nowGrceryList) || nowGrceryList.equals("")) {
            Toast.makeText(getActivity(), "Please Add something for shopping", Toast.LENGTH_LONG).show();

        } else {
            mprogressBar.setVisibility(View.VISIBLE);
            new CallWebService("", getActivity(), cmf.urlList.add_groceries_list, cmf.add_groceries_list(USER_ID, nowGrceryList), new MyServiceListener() {
                @Override
                public void onSuccess(String string) {
                    try {
                        mprogressBar.setVisibility(View.GONE);
                        /// Toast.makeText(getActivity(), string, Toast.LENGTH_LONG).show();
                        JSONObject jsonObject = new JSONObject(string);
                        String Status = jsonObject.optString("status");
                        String Message = jsonObject.optString("message");
                        if (Status.equals("1")) {
                            lastgroceryList = nowGrceryList;
                            //---------------------
                            addedItemModel = new AddedItemModel();
                            String id = jsonObject.optString("item_id");
                            String added_by = USER_ID;
                            String added_by_name = user_name;
                            String note = nowGrceryList;
                            String added_date2 = "";

                            addedItemModel.setId(id);
                            addedItemModel.setAdded_by(added_by);
                            addedItemModel.setAdded_by_name(added_by_name);
                            addedItemModel.setAdded_date(added_date2);
                            addedItemModel.setNote(note);
                            addedItemModel.setIsItemShopped("0");
                            groceryListarraylist.get(0).getAddedItemModels().add(addedItemModel);

                            validateGroceryItem(0);

                            //----------
                            mBodyText.setText("");

                        } else {
                            //  Toast.makeText(getActivity(), Message, Toast.LENGTH_LONG).show();
                        }
                        Toast.makeText(getActivity(), Message, Toast.LENGTH_LONG).show();

                    } catch (Exception ex) {
                        //Toast.makeText(getActivity(), "Please Try Again", Toast.LENGTH_LONG).show();
                        familyGroceryList();
                    }
                }

                @Override
                public void onFailed() {
                    mprogressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void validateGroceryItem(int position) {
        try {
            dateValidate(groceryListarraylist.get(position).getAdded_date());
            adapter = new GroceryList_Adapter(getActivity(), groceryListarraylist.get(position).getAddedItemModels(), USER_ID);
            RV_groceryList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void shareGroceryForShop(int position) {
        try {
            //----share text..
            String item = "";
            item = groceryListarraylist.get(position).getAdded_date();

            for (int i = 0; i < groceryListarraylist.get(position).getAddedItemModels().size(); i++) {
                item = item + "\n" + groceryListarraylist.get(position).getAddedItemModels().get(i).getNote();
            }
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Family Care Shopping List" + item);
            startActivity(Intent.createChooser(shareIntent, ""));

        } catch (Exception ex) {
        }

    }

    private void is_shopping_start() {
        // fist time set text shop now
        if (TV_shop_unshop.getText().toString().equals("shop now")) {
            TV_shop_unshop.setText("cancel");
            TV_headTest.setText(" Today Item Shopped 0");
            ET_finalPrice.setVisibility(View.VISIBLE);
            ET_finalPrice.setVisibility(View.VISIBLE);
            IV_barcode.setVisibility(View.GONE);
            mBodyText.setVisibility(View.GONE);

            start_shopping = 1;
            shoppedItem();

        } else if (TV_shop_unshop.getText().toString().equals("cancel")) {
            TV_shop_unshop.setText("shop now");

            TV_headTest.setText(" You Have Shopped ");
            TV_headTest.setText("Scan Grocery Product.");

            ET_finalPrice.setVisibility(View.GONE);
            ET_finalPrice.setVisibility(View.GONE);
            mBodyText.setVisibility(View.VISIBLE);
            IV_barcode.setVisibility(View.VISIBLE);
            start_shopping = 0;

            ShopedItem.clear();
            validateGroceryItem(0);
        }
    }

    public static void shoppedItem() {
        TV_headTest.setText(" Today Item Shopped " + ShopedItem.size());
    }

    //===========================================CLOSED==================================
}
