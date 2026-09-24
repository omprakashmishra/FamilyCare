package com.omsworld.familycare.Shopping;

import java.util.ArrayList;

/**
 * Created by rupesh.m on 2/15/2018.
 */

public class GroceryListModel {
    private String added_date;
    public ArrayList<AddedItemModel> addedItemModels;

    public String getAdded_date() {
        return added_date;
    }

    public void setAdded_date(String added_date) {
        this.added_date = added_date;
    }

    public ArrayList<AddedItemModel> getAddedItemModels() {
        return addedItemModels;
    }

    public void setAddedItemModels(ArrayList<AddedItemModel> addedItemModels) {
        this.addedItemModels = addedItemModels;
    }
}
