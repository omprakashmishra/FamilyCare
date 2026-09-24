package com.omsworld.familycare.Shopping;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.MyServiceListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omprakash.m on 1/22/2018.
 */

public class GroceryList_Adapter extends RecyclerView.Adapter<GroceryList_Adapter.MyViewHolder> implements View.OnClickListener {

    public List<AddedItemModel> arrayList;
    public Context context;
    String UpdateDateTime;
    CommonFunctions cmf;
    private LayoutInflater LIoffer;
    String USER_ID;

    public GroceryList_Adapter(Context context, List<AddedItemModel> arrayList, String USER_ID) {

        this.arrayList = arrayList;
        this.context = context;
        this.USER_ID = USER_ID;
        LIoffer = LayoutInflater.from(context);
        cmf = new CommonFunctions(context);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LIoffer.inflate(R.layout.grocery_list_item, parent, false);
        // MyViewHolder holder = new MyViewHolder(view);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        //  model = arrayList.get(position);
        final AddedItemModel Lstmodel = arrayList.get(position);
        holder.TV_added_by.setText(Lstmodel.getAdded_by_name());
        holder.TV_itemName.setText(Lstmodel.getNote());

        if (Lstmodel.getIsItemShopped().equals("1")) {
            holder.RL_row.setBackgroundResource(R.color.transparent);
            holder.CB_isShopped.setChecked(true);
            holder.CB_isShopped.setClickable(false);
            holder.TV_itemName.setPaintFlags(holder.TV_itemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {

            holder.CB_isShopped.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        GroceryList_Fr.ShopedItem.add(Lstmodel.getId());
                        holder.IV_item_delete.setVisibility(View.GONE);
                    } else {
                        holder.IV_item_delete.setVisibility(View.VISIBLE);
                        for (int i = 0; i < GroceryList_Fr.ShopedItem.size(); i++) {
                            if (GroceryList_Fr.ShopedItem.get(i).equals(Lstmodel.getId())) {
                                GroceryList_Fr.ShopedItem.remove(i);

                            }
                        }
                    }
                    GroceryList_Fr.shoppedItem();
                }
            });
        }
        holder.IV_item_delete.setTag(position);
        holder.IV_item_delete.setOnClickListener(this);

        // change time format from server text...
        try {
           /* String dateTime = Lstmodel.getAdded_date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = df.parse(dateTime);
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            calendar.setTime(date);
            String Year=String.valueOf(calendar.get(Calendar.YEAR));
            String Day=String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            String Month=new SimpleDateFormat("MMM").format(calendar.getTime());*/
           /* holder.tv_month.setText(Month);
            holder.tv_year.setText( Year);
            holder.tv_day.setText(Day);*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onClick(View view) {
        Integer position = (Integer) view.getTag();
        switch (view.getId()) {
            case R.id.IV_item_delete:
                if (USER_ID.equals(arrayList.get(position).getAdded_by()) && arrayList.get(position).getIsItemShopped().equals("0")) {
                    deleteItem(arrayList.get(position).getId(), position);
                }
                break;
        }
    }


    private void deleteItem(String itemId, final int position) {
        new CallWebService(context, cmf.urlList.delete_groceries_item, cmf.delete_groceries_item("", itemId), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    int Status = jsonObject.optInt("status");
                    String Message = jsonObject.optString("message");
                    if (Status == 1) {
                        arrayList.remove(position);
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

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView TV_added_by, TV_itemName;
        public ImageView IV_item_delete;
        public CheckBox CB_isShopped;
        RelativeLayout RL_row;


        public MyViewHolder(View itemView) {
            super(itemView);

            TV_added_by = (TextView) itemView.findViewById(R.id.TV_added_by);
            TV_itemName = (TextView) itemView.findViewById(R.id.TV_itemName);
            IV_item_delete = (ImageView) itemView.findViewById(R.id.IV_item_delete);
            CB_isShopped = (CheckBox) itemView.findViewById(R.id.CB_isShopped);
            RL_row = (RelativeLayout) itemView.findViewById(R.id.RL_row);
        }
    }
}
