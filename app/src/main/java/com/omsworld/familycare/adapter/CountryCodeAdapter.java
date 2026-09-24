package com.omsworld.familycare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.omsworld.familycare.R;
import com.omsworld.familycare.model.CountryCodeModel;

import java.util.List;


/**
 * Created by rupesh.m on 3/14/2017.
 */

public class CountryCodeAdapter extends ArrayAdapter<CountryCodeModel> {
    Context context;
    List<CountryCodeModel> mDataset;
    LayoutInflater inflter;


    public CountryCodeAdapter(Context ctx, int txtViewResourceId, List<CountryCodeModel> mDataset) {
        super(ctx, txtViewResourceId, mDataset);
        this.context = ctx;
        this.mDataset = mDataset;
        this.inflter = (LayoutInflater.from(ctx));
    }

    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override
    public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        final CountryCodeModel itemClg = mDataset.get(position);
        View mySpinner = inflter.inflate(R.layout.item_collages, parent, false);
        TextView main_text = (TextView) mySpinner.findViewById(R.id.TV_collage);
        main_text.setText(itemClg.CountryName);

        return mySpinner;
    }


}
