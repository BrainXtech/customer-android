package com.kustomer.kustomersdk.Models;

import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;

import org.json.JSONObject;

import static com.kustomer.kustomersdk.Utils.JsonHelper.boolFromKeyPath;
import static com.kustomer.kustomersdk.Utils.JsonHelper.stringFromKeyPath;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSTrackingToken extends KUSModel {

    //region Properties
    @Nullable
    private String trackingId;
    @Nullable
    private String token;
    private boolean verified;
    //endregion

    //region Initializer
    public KUSTrackingToken (JSONObject json) throws KUSInvalidJsonException {
        super(json);

        trackingId = stringFromKeyPath(json, "attributes.trackingId");
        token = stringFromKeyPath(json, "attributes.token");
        verified = boolFromKeyPath(json, "attributes.verified");
    }
    //endregion


    public String modelType() {
        return "tracking_token";
    }

    //region Accessors

    @Nullable
    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(@Nullable String trackingId) {
        this.trackingId = trackingId;
    }

    @Nullable
    public String getToken() {
        return token;
    }

    public void setToken(@Nullable String token) {
        this.token = token;
    }

    public boolean getVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    //endregion
}
