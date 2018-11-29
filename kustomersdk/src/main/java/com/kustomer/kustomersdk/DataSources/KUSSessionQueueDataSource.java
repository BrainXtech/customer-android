package com.kustomer.kustomersdk.DataSources;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Models.KUSSessionQueue;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import org.json.JSONObject;

public class KUSSessionQueueDataSource extends KUSObjectDataSource {

    //region Properties
    private String sessionId;
    //endregion

    //region Initializer
    public KUSSessionQueueDataSource(KUSUserSession userSession, String sessionId) {
        super(userSession);

        this.sessionId = sessionId;
    }
    //endregion

    //region subclass methods
    @Override
    void performRequest(KUSRequestCompletionListener completionListener){
        String endpoint = String.format(KUSConstants.URL.SESSION_QUEUE_ENDPOINT,sessionId);
        getUserSession().getRequestManager().getEndpoint(
                endpoint,
                true,
                completionListener
        );
    }

    @Override
    KUSModel objectFromJson(JSONObject jsonObject) throws KUSInvalidJsonException {
        return new KUSSessionQueue(jsonObject);
    }
    //endregion

}
