package com.kustomer.kustomersdk.Models;

import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KUSClientActivity extends KUSModel {

    //region Properties
    @Nullable
    private List<Double> intervals;
    @Nullable
    private String currentPage;
    @Nullable
    private String previousPage;
    private double currentPageSeconds;
    @Nullable
    private Date createdAt;
    //endregion

    //region LifeCycle
    public KUSClientActivity(JSONObject json) throws KUSInvalidJsonException {
        super(json);

        JSONArray intervalsArray = JsonHelper.arrayFromKeyPath(json, "attributes.intervals");

        if (intervalsArray != null)
            this.intervals = arrayListFromJsonArray(intervalsArray, "seconds");

        currentPage = JsonHelper.stringFromKeyPath(json, "attributes.currentPage");
        previousPage = JsonHelper.stringFromKeyPath(json, "attributes.previousPage");
        currentPageSeconds = JsonHelper.doubleFromKeyPath(json, "attributes.currentPageSeconds");
        createdAt = JsonHelper.dateFromKeyPath(json, "attributes.createdAt");
    }

    @Override
    public String modelType() {
        return "client_activity";
    }
    //endregion

    //region Private Methods
    private List<Double> arrayListFromJsonArray(JSONArray array, String id) {
        List<Double> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObject = array.getJSONObject(i);
                list.add(jsonObject.getDouble(id));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;
    }
    //endregion

    //region Getters & Setters

    @Nullable
    public List<Double> getIntervals() {
        return intervals;
    }

    public void setIntervals(@Nullable List<Double> intervals) {
        this.intervals = intervals;
    }

    @Nullable
    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(@Nullable String currentPage) {
        this.currentPage = currentPage;
    }

    @Nullable
    public String getPreviousPage() {
        return previousPage;
    }

    public void setPreviousPage(@Nullable String previousPage) {
        this.previousPage = previousPage;
    }

    public double getCurrentPageSeconds() {
        return currentPageSeconds;
    }

    public void setCurrentPageSeconds(Double currentPageSeconds) {
        this.currentPageSeconds = currentPageSeconds;
    }

    @Nullable
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@Nullable Date createdAt) {
        this.createdAt = createdAt;
    }

    //endregion
}
