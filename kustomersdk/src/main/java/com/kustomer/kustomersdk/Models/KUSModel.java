package com.kustomer.kustomersdk.Models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import static com.kustomer.kustomersdk.Utils.JsonHelper.stringFromKeyPath;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSModel implements Comparable<KUSModel>, Serializable {

    //region Properties
    private String id;
    @Nullable
    private String orgId;
    @Nullable
    private String customerId;
    @Nullable
    private String sessionId;
    @Nullable
    private String originalJSON;
    //endregion

    //region Initializer
    public KUSModel() {
    }

    public KUSModel(@Nullable JSONObject json) throws KUSInvalidJsonException {
        if (json == null)
            throw new KUSInvalidJsonException("Json object should not be null.");

        //Reject any objects  where the model type doesn't match, if enforced
        String type = stringFromKeyPath(json, "type");
        String classType = modelType();

        if (enforcesModelType() && (type == null || !type.equals(classType)))
            throw new KUSInvalidJsonException("Model Type not matched.");

        //Make sure there is an object id
        String objectId = stringFromKeyPath(json, "id");
        if (objectId == null)
            throw new KUSInvalidJsonException("Object Id not found.");

        id = objectId;
        originalJSON = json.toString();

        this.orgId = stringFromKeyPath(json, "relationships.org.data.id");
        this.customerId = stringFromKeyPath(json, "relationships.customer.data.id");
        this.sessionId = stringFromKeyPath(json, "relationships.session.data.id");
    }
    //endregion

    //region Methods
    public String modelType() {
        return null;
    }

    protected boolean enforcesModelType() {
        return true;
    }

    @NonNull
    @Override
    public String toString() {
        //Missing %p (this)
        return String.format("<%s : id: %s>", this.getClass(), this.id);
    }

    public int hash() {
        return this.id.hashCode();
    }

    public void addIncludedWithJSON(JSONArray jsonArray) {
        // TODO: Need to rethink how can we improve this function
    }

    // Helper Methods

    @Override
    public int compareTo(@NonNull KUSModel kusModel) {
        if (kusModel.id != null)
            return kusModel.id.compareTo(this.id);
        else
            return -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(KUSModel.class)) {
            return false;
        }

        KUSModel kus = (KUSModel) obj;
        return kus.id.equals(this.id)
                && kus.orgId != null && kus.orgId.equals(this.orgId)
                && kus.customerId != null && kus.customerId.equals(this.customerId)
                && kus.sessionId != null && kus.sessionId.equals(this.sessionId);
    }
    //endregion

    //region Accessors

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Nullable
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(@Nullable String orgId) {
        this.orgId = orgId;
    }

    @Nullable
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(@Nullable String customerId) {
        this.customerId = customerId;
    }

    @Nullable
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(@Nullable String sessionId) {
        this.sessionId = sessionId;
    }

    public JSONObject getOriginalJSON() {
        try {
            return new JSONObject(originalJSON);
        } catch (JSONException e) {
            return null;
        }
    }

    public void setOriginalJSON(JSONObject originalJSON) {
        this.originalJSON = originalJSON.toString();
    }

    //endregion
}

