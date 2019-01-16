package com.kustomer.kustomersdk.Models;

import org.json.JSONArray;

/**
 * Created by Junaid on 3/22/2018.
 */

public class KUSFormRetry extends KUSRetry {

    //region Properties
    private JSONArray messagesJSON;
    private String formId;
    private KUSChatMessage lastUserChatMessage;
    //endregion

    //region LifeCycle
    public KUSFormRetry(JSONArray messagesJSON, String formId, KUSChatMessage lastUserChatMessage ){

        this.messagesJSON = messagesJSON;
        this.formId = formId;
        this.lastUserChatMessage = lastUserChatMessage;

    }
    //endregion

    //region Getters

    public JSONArray getMessagesJSON() {
        return messagesJSON;
    }

    public String getFormId() {
        return formId;
    }

    public KUSChatMessage getLastUserChatMessage() {
        return lastUserChatMessage;
    }

    //endregion
}
