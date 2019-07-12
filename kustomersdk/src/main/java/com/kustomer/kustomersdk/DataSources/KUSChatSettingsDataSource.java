package com.kustomer.kustomersdk.DataSources;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Helpers.KUSLog;
import com.kustomer.kustomersdk.Interfaces.KUSChatAvailableListener;
import com.kustomer.kustomersdk.Helpers.KUSLocalization;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Models.KUSChatSettings;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Utils.JsonHelper;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSChatSettingsDataSource extends KUSObjectDataSource implements Serializable {

    //region Initializer
    public KUSChatSettingsDataSource(@NonNull KUSUserSession userSession) {
        super(userSession);
    }
    //endregion

    //region public Methods
    @Override
    void performRequest(@NonNull KUSRequestCompletionListener completionListener) {
        if (getUserSession() == null) {
            completionListener.onCompletion(new Error(), null);
            return;
        }

        final Locale locale = KUSLocalization.getSharedInstance().getUserLocale();

        getUserSession().getRequestManager().performRequestType(KUSRequestType.KUS_REQUEST_TYPE_GET,
                KUSConstants.URL.SETTINGS_ENDPOINT,
                new HashMap<String, Object>() {{
                        put(KUSConstants.HeaderKeys.K_KUSTOMER_LANGUAGE_KEY,
                                locale != null ? locale.getLanguage() : "");
                    }},
                true,
                completionListener);
    }

    @NonNull
    @Override
    KUSModel objectFromJson(@Nullable JSONObject jsonObject) throws KUSInvalidJsonException {
        return new KUSChatSettings(jsonObject);
    }

    public void isChatAvailable(@NonNull final KUSChatAvailableListener listener) {

        performRequest(new KUSRequestCompletionListener() {
            @Override
            public void onCompletion(@Nullable final Error error, @Nullable JSONObject response) {

                KUSChatSettings settings = null;
                try {
                    settings = (KUSChatSettings) objectFromJson(
                            JsonHelper.jsonObjectFromKeyPath(response, "data"));

                } catch (KUSInvalidJsonException e) {
                    KUSLog.kusLogError(e.getMessage());
                }

                if (error == null && settings != null)
                    listener.onSuccess(settings.getEnabled());
                else
                    listener.onFailure();
            }
        });

    }
    //endregion
}
