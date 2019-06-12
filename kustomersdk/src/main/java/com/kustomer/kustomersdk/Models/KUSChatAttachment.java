package com.kustomer.kustomersdk.Models;

import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSChatAttachment extends KUSModel {
    //region Properties
    @Nullable
    private String name;
    @Nullable
    private Date createdAt;
    @Nullable
    private Date updatedAt;
    //endregion

    //region Initializer
    public KUSChatAttachment(JSONObject jsonObject) throws KUSInvalidJsonException {
        super(jsonObject);

        name = JsonHelper.stringFromKeyPath(jsonObject,"attributes.name");
        createdAt = JsonHelper.dateFromKeyPath(jsonObject,"attributes.createdAt");
        updatedAt = JsonHelper.dateFromKeyPath(jsonObject,"attributes.updatedAt");
    }

    @Override
    public String modelType(){
        return "attachment";
    }
    //endregion

    //region Getter & Setters

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@Nullable Date createdAt) {
        this.createdAt = createdAt;
    }

    @Nullable
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(@Nullable Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    //endregion
}
