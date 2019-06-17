package com.kustomer.kustomersdk.Models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONObject;

import java.net.URL;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSUser extends KUSModel {
    //region Properties
    @Nullable
    private String displayName;
    @Nullable
    private URL avatarURL;
    //endregion

    //region Initializer
    public KUSUser(@Nullable JSONObject json) throws KUSInvalidJsonException {
        super(json);
        displayName = JsonHelper.stringFromKeyPath(json,"attributes.displayName");
        avatarURL = JsonHelper.urlFromKeyPath(json,"attributes.avatarUrl");
    }

    @NonNull
    @Override
    public String modelType(){
        return "user";
    }
    //endregion

    //region Accessors

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    public URL getAvatarURL() {
        return avatarURL;
    }

    //endregion
}
