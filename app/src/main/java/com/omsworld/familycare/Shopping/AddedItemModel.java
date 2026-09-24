package com.omsworld.familycare.Shopping;

/**
 * Created by rupesh.m on 2/15/2018.
 */

public class AddedItemModel {
    private String id;
    private String added_by;
    private String added_by_name;
    private String note;
    private String added_date;
    private String isItemShopped;

    public String getIsItemShopped() {
        return isItemShopped;
    }

    public void setIsItemShopped(String isItemShopped) {
        this.isItemShopped = isItemShopped;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdded_by() {
        return added_by;
    }

    public void setAdded_by(String added_by) {
        this.added_by = added_by;
    }

    public String getAdded_by_name() {
        return added_by_name;
    }

    public void setAdded_by_name(String added_by_name) {
        this.added_by_name = added_by_name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAdded_date() {
        return added_date;
    }

    public void setAdded_date(String added_date) {
        this.added_date = added_date;
    }
}
