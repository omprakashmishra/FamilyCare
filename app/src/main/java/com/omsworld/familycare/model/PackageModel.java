package com.omsworld.familycare.model;

import java.io.Serializable;

/**
 * Created by rupesh.m on 12/19/2016.
 */

public class PackageModel implements Serializable {

    private String PackageID;
    private String PackageName;
    private String Amount;
    private String Type;
    private String DisplayAmount;
    private String DisplayAmountType;
    private String PayableAmount;

    public String getSetPackageDesc() {
        return setPackageDesc;
    }

    public void setSetPackageDesc(String setPackageDesc) {
        this.setPackageDesc = setPackageDesc;
    }

    private String setPackageDesc;

    public String getDisplayAmount() {
        return DisplayAmount;
    }

    public void setDisplayAmount(String displayAmount) {
        DisplayAmount = displayAmount;
    }

    public String getDisplayAmountType() {
        return DisplayAmountType;
    }

    public void setDisplayAmountType(String displayAmountType) {
        DisplayAmountType = displayAmountType;
    }

    public String getPayableAmount() {
        return PayableAmount;
    }

    public void setPayableAmount(String payableAmount) {
        PayableAmount = payableAmount;
    }

    public String getPackageID() {
        return PackageID;
    }

    public void setPackageID(String packageID) {
        PackageID = packageID;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

}
