package com.kustomer.kustomersdk.Models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONObject;

import java.util.Date;

public class KUSHoliday extends KUSModel {

    //region Properties
    @Nullable
    private String name;
    @Nullable
    private Date startDate;
    @Nullable
    private Date endDate;
    private boolean enabled;
    //endregion

    //region Initializer
    public KUSHoliday(@Nullable JSONObject json) throws KUSInvalidJsonException {
        super(json);

        name = JsonHelper.stringFromKeyPath(json,"attributes.name");
        startDate = JsonHelper.dateFromKeyPath(json,"attributes.startDate");
        endDate = JsonHelper.dateFromKeyPath(json,"attributes.endDate");
        enabled = JsonHelper.boolFromKeyPath(json,"attributes.enabled");
    }
    //endregion

    //region Class methods
    @NonNull
    public String modelType(){
        return "holiday";
    }
    //endregion

    //region Accessors

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public Date getStartDate() {
        return startDate;
    }

    @Nullable
    public Date getEndDate() {
        return endDate;
    }

    public boolean getEnabled() {
        return enabled;
    }

    //endregion
}
