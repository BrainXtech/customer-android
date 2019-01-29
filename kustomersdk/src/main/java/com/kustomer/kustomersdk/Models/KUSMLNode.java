package com.kustomer.kustomersdk.Models;

import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class KUSMLNode extends KUSModel {

    //region Properties
    @Nullable
    private String displayName;
    @Nullable
    private String nodeId;
    @Nullable
    private ArrayList<KUSMLNode> childNodes;
    //endregion

    //region Initializer

    public KUSMLNode(JSONObject json) throws KUSInvalidJsonException {
        super(json);

        displayName = JsonHelper.stringFromKeyPath(json, "displayName");
        nodeId = JsonHelper.stringFromKeyPath(json, "id");
        childNodes = KUSMLNode.objectsFromJSONs(JsonHelper.arrayFromKeyPath(json, "children"));
    }
    //endregion


    //region Class methods

    public String modelType() {
        return null;
    }

    public boolean enforcesModelType() {
        return false;
    }

    @Nullable
    public static ArrayList<KUSMLNode> objectsFromJSONs(JSONArray jsonArray) {
        if (jsonArray == null)
            return null;

        ArrayList<KUSMLNode> arrayList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject json = (JSONObject) jsonArray.get(i);
                KUSMLNode node = new KUSMLNode(json);
                arrayList.add(node);
            } catch (JSONException | KUSInvalidJsonException ignore) {
            }
        }

        return arrayList;
    }
    //endregion

    //region Accessors

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    public String getNodeId() {
        return nodeId;
    }

    @Nullable
    public ArrayList<KUSMLNode> getChildNodes() {
        return childNodes;
    }

    //endregion
}
