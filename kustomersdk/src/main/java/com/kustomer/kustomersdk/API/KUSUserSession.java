package com.kustomer.kustomersdk.API;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;
import com.kustomer.kustomersdk.DataSources.KUSChatSessionsDataSource;
import com.kustomer.kustomersdk.DataSources.KUSChatSettingsDataSource;
import com.kustomer.kustomersdk.DataSources.KUSDelegateProxy;
import com.kustomer.kustomersdk.DataSources.KUSFormDataSource;
import com.kustomer.kustomersdk.DataSources.KUSPaginatedDataSource;
import com.kustomer.kustomersdk.DataSources.KUSScheduleDataSource;
import com.kustomer.kustomersdk.DataSources.KUSTrackingTokenDataSource;
import com.kustomer.kustomersdk.DataSources.KUSUserDataSource;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Helpers.KUSLog;
import com.kustomer.kustomersdk.Helpers.KUSSharedPreferences;
import com.kustomer.kustomersdk.Interfaces.KUSPaginatedDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Kustomer;
import com.kustomer.kustomersdk.Managers.KUSStatsManager;
import com.kustomer.kustomersdk.Models.KUSChatSession;
import com.kustomer.kustomersdk.Models.KUSCustomerDescription;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Models.KUSTrackingToken;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSUserSession implements Serializable, KUSPaginatedDataSourceListener {

    //region Properties
    @Nullable
    private String orgId;
    @Nullable
    private String orgName;
    @Nullable
    private String organizationName; //UserFacing

    @Nullable
    private KUSChatSessionsDataSource chatSessionsDataSource;
    @Nullable
    private KUSChatSettingsDataSource chatSettingsDataSource;
    @Nullable
    private KUSTrackingTokenDataSource trackingTokenDataSource;
    @Nullable
    private KUSFormDataSource formDataSource;
    @Nullable
    private KUSScheduleDataSource scheduleDataSource;

    @Nullable
    private HashMap<String, KUSUserDataSource> userDataSources;
    @Nullable
    private HashMap<String, KUSChatMessagesDataSource> chatMessagesDataSources;

    @Nullable
    private KUSRequestManager requestManager;
    @Nullable
    private KUSPushClient pushClient;
    @Nullable
    private KUSDelegateProxy delegateProxy;
    @Nullable
    private KUSClientActivityManager activityManager;
    @Nullable
    private KUSStatsManager statsManager;

    @Nullable
    private KUSSharedPreferences sharedPreferences;

    boolean shouldCaptureEmail;
    //endregion

    //region LifeCycle
    public KUSUserSession(@Nullable String orgName, @Nullable String orgId, boolean reset) {
        this.orgName = orgName;
        this.orgId = orgId;

        if (this.orgName != null && this.orgName.length() > 0) {
            String firstLetter = this.orgName.substring(0, 1).toUpperCase();
            this.organizationName = firstLetter.concat(this.orgName.substring(1));
        }

        if (reset) {
            getTrackingTokenDataSource().reset();
            if (getSharedPreferences() != null)
                getSharedPreferences().reset();
        }

        getChatSettingsDataSource().fetch();
        getScheduleDataSource().fetch();
        getPushClient();

        getChatSessionsDataSource().addListener(this);
        getChatSessionsDataSource().fetchLatest();
    }

    public KUSUserSession(@Nullable String orgName, @Nullable String orgId) {
        this(orgName, orgId, false);
    }

    //endregion


    //region public methods
    public void removeAllListeners() {
        if (pushClient != null)
            pushClient.removeAllListeners();

        if (chatSessionsDataSource != null)
            chatSessionsDataSource.removeAllListeners();

        if (chatSettingsDataSource != null)
            chatSettingsDataSource.removeAllListeners();

        if (trackingTokenDataSource != null)
            trackingTokenDataSource.removeAllListeners();

        if (formDataSource != null)
            formDataSource.removeAllListeners();

        if (userDataSources != null && userDataSources.keySet().size() > 0)
            for (String key : userDataSources.keySet()) {
                KUSUserDataSource dataSource = userDataSources.get(key);

                if (dataSource != null)
                    dataSource.removeAllListeners();
            }

        if (chatMessagesDataSources != null && chatMessagesDataSources.keySet().size() > 0)
            for (String key : chatMessagesDataSources.keySet()) {
                KUSChatMessagesDataSource dataSource = chatMessagesDataSources.get(key);

                if (dataSource != null)
                    dataSource.removeAllListeners();
            }
    }

    @Nullable
    public KUSChatMessagesDataSource chatMessageDataSourceForSessionId(@Nullable String sessionId) {
        if (sessionId == null || sessionId.isEmpty())
            return null;

        KUSChatMessagesDataSource chatMessagesDataSource = getChatMessagesDataSources().get(sessionId);
        if (chatMessagesDataSource == null) {
            chatMessagesDataSource = new KUSChatMessagesDataSource(this, sessionId);
            getChatMessagesDataSources().put(sessionId, chatMessagesDataSource);
        }
        return chatMessagesDataSource;
    }

    @Nullable
    public KUSUserDataSource userDataSourceForUserId(@Nullable String userId) {
        if (userId == null || userId.length() == 0 || userId.equals("__team"))
            return null;

        KUSUserDataSource userDataSource = getUserDataSources().get(userId);
        if (userDataSource == null) {
            userDataSource = new KUSUserDataSource(this, userId);
            getUserDataSources().put(userId, userDataSource);
        }

        return userDataSource;
    }

    public void submitEmail(@NonNull String emailAddress) {

        if (getSharedPreferences() != null)
            getSharedPreferences().setDidCaptureEmail(true);

        final WeakReference<KUSUserSession> weakReference = new WeakReference<>(this);
        KUSCustomerDescription customerDescription = new KUSCustomerDescription();
        customerDescription.setEmail(emailAddress);

        describeCustomer(customerDescription, new KUSCustomerCompletionListener() {
            @Override
            public void onComplete(boolean success, Error error) {
                if (error != null || !success) {
                    KUSLog.kusLogError(String.format("Error submitting email: %s",
                            error != null ? error.toString() : ""));
                    return;
                }

                if (weakReference.get() != null)
                    weakReference.get().getTrackingTokenDataSource().fetch();
            }
        });
    }

    public void describeCustomer(@Nullable KUSCustomerDescription customerDescription,
                                 @Nullable final KUSCustomerCompletionListener listener) {

        if (customerDescription == null)
            throw new AssertionError("Attempted to describe a customer with null description.");

        HashMap<String, Object> formData = customerDescription.formData();

        if (formData.size() == 0)
            throw new AssertionError("Attempted to describe a customer with no attributes set");

        // If email is passed in custom description
        if (customerDescription.getEmail() != null && getSharedPreferences() != null) {
            getSharedPreferences().setDidCaptureEmail(true);
        }

        getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_PATCH,
                KUSConstants.URL.CURRENT_CUSTOMER_ENDPOINT,
                formData,
                true,
                new KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(Error error, JSONObject response) {
                        if (listener != null)
                            listener.onComplete(error == null, error);
                    }
                }
        );
    }


    //endregion

    //region Getters

    @NonNull
    public KUSChatSessionsDataSource getChatSessionsDataSource() {
        if (chatSessionsDataSource == null)
            chatSessionsDataSource = new KUSChatSessionsDataSource(this);
        return chatSessionsDataSource;
    }

    @NonNull
    public KUSChatSettingsDataSource getChatSettingsDataSource() {
        if (chatSettingsDataSource == null)
            chatSettingsDataSource = new KUSChatSettingsDataSource(this);
        return chatSettingsDataSource;
    }

    @NonNull
    public KUSTrackingTokenDataSource getTrackingTokenDataSource() {
        if (trackingTokenDataSource == null)
            trackingTokenDataSource = new KUSTrackingTokenDataSource(this);
        return trackingTokenDataSource;
    }

    @NonNull
    public KUSFormDataSource getFormDataSource() {
        if (formDataSource == null)
            formDataSource = new KUSFormDataSource(this);
        return formDataSource;
    }

    @NonNull
    public KUSRequestManager getRequestManager() {
        if (requestManager == null)
            requestManager = new KUSRequestManager(this);
        return requestManager;
    }

    @NonNull
    public KUSPushClient getPushClient() {
        if (pushClient == null)
            pushClient = new KUSPushClient(this);
        return pushClient;
    }

    @Nullable
    public KUSSharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            try {
                sharedPreferences = new KUSSharedPreferences(Kustomer.getContext(), this);
            } catch (NullPointerException ignore) {
            }
        }
        return sharedPreferences;
    }

    @NonNull
    public HashMap<String, KUSUserDataSource> getUserDataSources() {
        if (userDataSources == null)
            userDataSources = new HashMap<>();
        return userDataSources;
    }

    @NonNull
    public HashMap<String, KUSChatMessagesDataSource> getChatMessagesDataSources() {
        if (chatMessagesDataSources == null)
            chatMessagesDataSources = new HashMap<>();
        return chatMessagesDataSources;
    }

    @NonNull
    public KUSClientActivityManager getActivityManager() {
        if (activityManager == null)
            activityManager = new KUSClientActivityManager(this);

        return activityManager;
    }

    @NonNull
    public KUSScheduleDataSource getScheduleDataSource() {
        if (scheduleDataSource == null)
            scheduleDataSource = new KUSScheduleDataSource(this);

        return scheduleDataSource;
    }

    @NonNull
    public KUSStatsManager getStatsManager() {
        if (statsManager == null)
            statsManager = new KUSStatsManager(this);

        return statsManager;
    }
    //endregion

    //region Accessors

    @NonNull
    public KUSDelegateProxy getDelegateProxy() {
        if (delegateProxy == null)
            delegateProxy = new KUSDelegateProxy();

        return delegateProxy;
    }

    public void setDelegateProxy(@Nullable KUSDelegateProxy delegateProxy) {
        this.delegateProxy = delegateProxy;
    }

    @Nullable
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(@Nullable String orgId) {
        this.orgId = orgId;
    }

    @Nullable
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(@Nullable String orgName) {
        this.orgName = orgName;
    }

    @Nullable
    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(@Nullable String organizationName) {
        this.organizationName = organizationName;
    }

    public boolean isShouldCaptureEmail() {

        KUSTrackingToken trackingToken = (KUSTrackingToken) getTrackingTokenDataSource().getObject();
        if (trackingToken != null) {
            if (trackingToken.getVerified()) {
                return false;
            }

            if (getSharedPreferences() != null)
                return !getSharedPreferences().getDidCaptureEmail();
        }
        return false;
    }

    //endregion

    //region Callback
    @Override
    public void onLoad(KUSPaginatedDataSource dataSource) {
        if (dataSource == chatSessionsDataSource) {
            dataSource.removeListener(this);

            for (KUSModel model : chatSessionsDataSource.getList()) {
                KUSChatSession chatSession = (KUSChatSession) model;
                //Fetch any messages that might contribute to unread count
                KUSChatMessagesDataSource messagesDataSource = chatMessageDataSourceForSessionId(chatSession.getId());
                boolean hasUnseen = chatSession.getLastSeenAt() == null
                        || (chatSession.getLastMessageAt() != null
                        && chatSession.getLastMessageAt().after(chatSession.getLastSeenAt()));
                if (hasUnseen && messagesDataSource != null && !messagesDataSource.isFetched())
                    messagesDataSource.fetchLatest();
            }
        }
    }

    @Override
    public void onError(KUSPaginatedDataSource dataSource, Error error) {

    }

    @Override
    public void onContentChange(KUSPaginatedDataSource dataSource) {

    }

    //endregion

    //region Interface
    public interface KUSCustomerCompletionListener {
        void onComplete(boolean success, Error error);
    }
    //endregion
}
