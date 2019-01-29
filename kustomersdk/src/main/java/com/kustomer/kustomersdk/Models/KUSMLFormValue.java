package com.kustomer.kustomersdk.Models;

import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONObject;

import java.util.ArrayList;

public class KUSMLFormValue extends KUSModel {

    //region Properties
    @Nullable
    private String displayName;
    private boolean lastNodeRequired;
    @Nullable
    private ArrayList<KUSMLNode> mlNodes;
    //endregion

    //region Initializer
    KUSMLFormValue(JSONObject json) throws KUSInvalidJsonException {
        super(json);

        displayName = JsonHelper.stringFromKeyPath(json, "displayName");
        lastNodeRequired = JsonHelper.boolFromKeyPath(json, "lastNodeRequired");
        mlNodes = KUSMLNode.objectsFromJSONs(JsonHelper.arrayFromKeyPath(json, "tree.children"));
    }
    //endregion

    //region Class methods
    public String modelType() {
        return null;
    }

    public boolean enforcesModelType() {
        return false;
    }
    //endregion

    //region Accessors

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    public boolean getLastNodeRequired() {
        return lastNodeRequired;
    }

    @Nullable
    public ArrayList<KUSMLNode> getMlNodes() {
        return mlNodes;
    }

    //endregion
}
