package com.kustomer.kustomersdk.Models;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Helpers.KUSLog;
import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class KUSSchedule extends KUSModel {
    //region Properties
    @Nullable
    private String name;
    @Nullable
    private JSONObject hours;
    @Nullable
    private String timezone;
    private boolean enabled;

    @NonNull
    private ArrayList<KUSHoliday> holidays;
    //endregion

    //region Initializer
    public KUSSchedule (@Nullable JSONObject json) throws KUSInvalidJsonException {
        super(json);

        name = JsonHelper.stringFromKeyPath(json,"attributes.name");
        hours = JsonHelper.jsonObjectFromKeyPath(json,"attributes.hours");
        timezone = JsonHelper.stringFromKeyPath(json,"attributes.timezone");
        enabled = JsonHelper.boolFromKeyPath(json,"attributes.default");
        holidays = new ArrayList<>();
    }
    //endregion

    //region Class methods
    @NonNull
    public String modelType(){
        return "schedule";
    }

    public void addIncludedWithJSON(JSONArray jsonArray){
        super.addIncludedWithJSON(jsonArray);

        holidays.clear();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                KUSHoliday object = new KUSHoliday(jsonObject);
                holidays.add(object);
            } catch (JSONException | KUSInvalidJsonException e) {
                KUSLog.kusLogError(e.getMessage());
            }
        }
    }
    //endregion

    //region Accessors

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public JSONObject getHours() {
        return hours;
    }

    @Nullable
    public String getTimezone() {
        return timezone;
    }

    public boolean getEnabled() {
        return enabled;
    }

    @NonNull
    public ArrayList<KUSHoliday> getHolidays() {
        return holidays;
    }

    //endregion
}
