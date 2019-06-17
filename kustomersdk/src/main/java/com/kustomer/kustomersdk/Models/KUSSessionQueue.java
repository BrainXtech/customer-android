package com.kustomer.kustomersdk.Models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONObject;

import java.util.Date;

public class KUSSessionQueue extends KUSModel {

    //region Properties
    @Nullable
    private Date enteredAt;
    private int estimatedWaitTimeSeconds;
    private int latestWaitTimeSeconds;
    @Nullable
    private String name;
    //endregion

    //region Initializer
    public KUSSessionQueue(JSONObject json) throws KUSInvalidJsonException {
        super(json);

        enteredAt = JsonHelper.dateFromKeyPath(json,"attributes.enteredAt");
        estimatedWaitTimeSeconds = JsonHelper.integerFromKeyPath(json,"attributes.estimatedWaitTimeSeconds");
        latestWaitTimeSeconds = JsonHelper.integerFromKeyPath(json,"attributes.latestWaitTimeSeconds");
        name = JsonHelper.stringFromKeyPath(json,"attributes.name");

    }
    //endregion

    //region Class methods
    @NonNull
    public String modelType(){
        return "session_queue";
    }
    //endregion


    //region Accessors

    @Nullable
    public Date getEnteredAt() {
        return enteredAt;
    }

    public int getEstimatedWaitTimeSeconds() {
        return estimatedWaitTimeSeconds;
    }

    public int getLatestWaitTimeSeconds() {
        return latestWaitTimeSeconds;
    }

    @Nullable
    public String getName() {
        return name;
    }

    //endregion
}
