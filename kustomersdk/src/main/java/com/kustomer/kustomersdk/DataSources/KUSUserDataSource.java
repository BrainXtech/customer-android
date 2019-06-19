package com.kustomer.kustomersdk.DataSources;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Models.KUSUser;

import org.json.JSONObject;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSUserDataSource extends KUSObjectDataSource {
    //region Properties
    @NonNull
    private String userId;
    //endregion

    //region Initializer
    public KUSUserDataSource(@NonNull KUSUserSession userSession,@NonNull String userId){
        super(userSession);
        this.userId = userId;
    }
    //endregion

    //region Public Methods
    @Override
    void performRequest(@NonNull KUSRequestCompletionListener completionListener){
        if(getUserSession() == null) {
            completionListener.onCompletion(new Error(), null);
            return;
        }

        String endPoint = String.format("/c/v1/users/%s",userId);
        getUserSession().getRequestManager().getEndpoint(
                endPoint,
                true,
                completionListener
        );
    }

    @NonNull
    @Override
    KUSModel objectFromJson(@Nullable JSONObject jsonObject) throws KUSInvalidJsonException {
        return new KUSUser(jsonObject);
    }
    //endregion
}
