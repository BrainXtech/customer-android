package com.kustomer.kustomersdk.DataSources;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Models.KUSClientActivity;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class KUSClientActivityDataSource extends KUSObjectDataSource {

    @Nullable
    private String previousPageName;
    @Nullable
    private String currentPageName;
    private double currentPageSeconds;
    //endregion

    //region LifeCycle
    public KUSClientActivityDataSource(@NonNull KUSUserSession userSession,
                                       @Nullable String previousPageName,
                                       @Nullable String currentPageName,
                                       double currentPageSeconds) {
        super(userSession);

        if (currentPageName == null)
            throw new AssertionError("Should not fetch client activity without a current page!");

        this.previousPageName = previousPageName;
        this.currentPageName = currentPageName;
        this.currentPageSeconds = currentPageSeconds;
    }
    //endregion

    //region Public Methods
    @NonNull
    public List<Double> getIntervals() {
        KUSClientActivity clientActivity = (KUSClientActivity) getObject();
        return clientActivity != null ? clientActivity.getIntervals() : new ArrayList<Double>();
    }

    @Nullable
    public Date getCreatedAt() {
        KUSClientActivity clientActivity = (KUSClientActivity) getObject();
        return clientActivity != null ? clientActivity.getCreatedAt() : null;
    }

    //endregion

    //region SubClass Method
    @Override
    public void performRequest(@NonNull KUSRequestCompletionListener completionListener) {
        if (getUserSession() == null) {
            completionListener.onCompletion(new Error(), null);
            return;
        }

        HashMap<String, Object> params = new HashMap<>();

        if (previousPageName != null) {
            params.put("previousPage", previousPageName);
        }

        params.put("currentPage", currentPageName);
        params.put("currentPageSeconds", currentPageSeconds);

        getUserSession().getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_POST,
                KUSConstants.URL.CLIENT_ACTIVITY_ENDPOINT,
                params,
                true,
                completionListener
        );
    }

    @NonNull
    @Override
    KUSModel objectFromJson(@Nullable JSONObject jsonObject) throws KUSInvalidJsonException {
        return new KUSClientActivity(jsonObject);
    }
    //endregion

    //region Getter & Setter

    @Nullable
    public String getPreviousPageName() {
        return previousPageName;
    }

    public void setPreviousPageName(@Nullable String previousPageName) {
        this.previousPageName = previousPageName;
    }

    @Nullable
    public String getCurrentPageName() {
        return currentPageName;
    }

    public void setCurrentPageName(@Nullable String currentPageName) {
        this.currentPageName = currentPageName;
    }

    public double getCurrentPageSeconds() {
        return currentPageSeconds;
    }

    public void setCurrentPageSeconds(double currentPageSeconds) {
        this.currentPageSeconds = currentPageSeconds;
    }

    //endregion
}
