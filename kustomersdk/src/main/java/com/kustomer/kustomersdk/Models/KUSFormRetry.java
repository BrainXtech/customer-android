package com.kustomer.kustomersdk.Models;

import android.support.annotation.NonNull;

import org.json.JSONArray;

/**
 * Created by Junaid on 3/22/2018.
 */

public class KUSFormRetry extends KUSRetry {

    //region Properties
    @NonNull
    private JSONArray messagesJSON;
    @NonNull
    private String formId;
    @NonNull
    private KUSChatMessage lastUserChatMessage;
    //endregion

    //region LifeCycle
    public KUSFormRetry(@NonNull JSONArray messagesJSON,@NonNull String formId,
                        @NonNull KUSChatMessage lastUserChatMessage ){

        this.messagesJSON = messagesJSON;
        this.formId = formId;
        this.lastUserChatMessage = lastUserChatMessage;

    }
    //endregion

    //region Getters

    @NonNull
    public JSONArray getMessagesJSON() {
        return messagesJSON;
    }

    @NonNull
    public String getFormId() {
        return formId;
    }

    @NonNull
    public KUSChatMessage getLastUserChatMessage() {
        return lastUserChatMessage;
    }

    //endregion
}
