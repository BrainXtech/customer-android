package com.kustomer.kustomersdk.DataSources;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Helpers.KUSDate;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Helpers.KUSLog;
import com.kustomer.kustomersdk.Interfaces.KUSChatMessagesDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSChatSessionCompletionListener;
import com.kustomer.kustomersdk.Interfaces.KUSFormCompletionListener;
import com.kustomer.kustomersdk.Interfaces.KUSObjectDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.Models.KUSChatSession;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Models.KUSTypingIndicator;
import com.kustomer.kustomersdk.Utils.JsonHelper;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSChatSessionsDataSource extends KUSPaginatedDataSource
        implements KUSChatMessagesDataSourceListener, KUSObjectDataSourceListener {

    //region Properties
    @Nullable
    private JSONObject pendingCustomChatSessionAttributes;
    @Nullable
    private JSONObject pendingCustomChatSessionAttributesForNextConversation;
    @NonNull
    private HashMap<String, Date> localLastSeenAtBySessionId;
    @Nullable
    private String messageToCreateNewChatSession;
    //endregion

    //region Initializer
    public KUSChatSessionsDataSource(@NonNull KUSUserSession userSession) {
        super(userSession);

        localLastSeenAtBySessionId = new HashMap<>();
        addListener(this);

        if (getUserSession() != null)
            getUserSession().getChatSettingsDataSource().addListener(this);
    }

    @Override
    public List<KUSModel> objectsFromJSON(@Nullable JSONObject jsonObject) {

        ArrayList<KUSModel> arrayList = null;

        KUSModel model = null;
        try {
            model = new KUSChatSession(jsonObject);
        } catch (KUSInvalidJsonException e) {
            KUSLog.kusLogError(e.getMessage());
        }

        if (model != null) {
            arrayList = new ArrayList<>();
            arrayList.add(model);
        }

        return arrayList;
    }
    //endregion

    //region KUSPaginatedDataSource methods
    public void fetchLatest() {
        if (getUserSession() == null)
            return;

        if (!getUserSession().getChatSettingsDataSource().isFetched()) {
            getUserSession().getChatSettingsDataSource().fetch();
            return;
        }
        super.fetchLatest();
    }

    @Nullable
    public URL getFirstUrl() {
        if (getUserSession() == null)
            return null;

        return getUserSession().getRequestManager().urlForEndpoint(KUSConstants.URL.CHAT_SESSIONS_ENDPOINT);
    }

    @Nullable
    public String getMessageToCreateNewChatSession() {
        return messageToCreateNewChatSession;
    }
    //endregion

    //region Public Methods
    public void upsertNewSessions(@Nullable List<KUSModel> chatSessions) {
        if (chatSessions == null)
            return;
        if (chatSessions.size() > 1)
            Collections.reverse(chatSessions);

        upsertAll(chatSessions);
    }

    public void createSessionWithTitle(@NonNull String title,
                                       @Nullable final KUSChatSessionCompletionListener listener) {
        if (getUserSession() == null) {
            if (listener != null)
                listener.onComplete(new Error(), null);
            return;
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("title", title);

        getUserSession().getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_POST,
                KUSConstants.URL.CHAT_SESSIONS_ENDPOINT,
                params, true, new KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(Error error, JSONObject response) {
                        if (error != null) {
                            if (listener != null)
                                listener.onComplete(error, null);

                            return;
                        }

                        KUSChatSession session = null;
                        try {
                            session = new KUSChatSession(JsonHelper.jsonObjectFromKeyPath(response, "data"));
                        } catch (KUSInvalidJsonException e) {
                            KUSLog.kusLogError(e.getMessage());
                        }

                        if (session != null) {
                            checkShouldFlushCustomAttributesToChatSessionId(session.getId());

                            ArrayList<KUSModel> sessions = new ArrayList<>();
                            sessions.add(session);

                            upsertAll(sessions);
                        }

                        if (listener != null)
                            listener.onComplete(null, session);

                    }
                });
    }

    public void updateLastSeenAtForSessionId(@Nullable String sessionId,
                                             @Nullable final KUSChatSessionCompletionListener listener) {
        if (getUserSession() == null) {
            if (listener != null)
                listener.onComplete(new Error(), null);
            return;
        }

        if (sessionId == null || sessionId.length() == 0) {
            if (listener != null)
                listener.onComplete(new Error("Session id missing"), null);

            return;
        }

        Date lastSeenAtDate = Calendar.getInstance().getTime();
        localLastSeenAtBySessionId.put(sessionId, lastSeenAtDate);
        final String lastSeenAtString = KUSDate.stringFromDate(lastSeenAtDate);

        boolean isLocalSession = sessionId.equals(KUSConstants.ChatSession.TEMP_SESSION_ID);
        if (isLocalSession) {
            KUSChatSession session = (KUSChatSession) findById(sessionId);

            if (session == null)
                return;

            removeAll(Collections.singletonList((KUSModel) session));
            session.setLastSeenAt(lastSeenAtDate);
            upsertAll(Collections.singletonList((KUSModel) session));
            return;
        }

        String url = String.format(KUSConstants.URL.CHAT_SESSIONS_ENDPOINT + "/%s", sessionId);
        HashMap<String, Object> params = new HashMap<String, Object>() {{
            put("lastSeenAt", lastSeenAtString);
        }};

        getUserSession().getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_PUT,
                url,
                params,
                true,
                new KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(Error error, JSONObject response) {
                        if (error != null && listener != null) {
                            listener.onComplete(error, null);
                            return;
                        }

                        KUSChatSession session = null;
                        try {
                            session = new KUSChatSession(JsonHelper.jsonObjectFromKeyPath(response, "data"));
                        } catch (KUSInvalidJsonException e) {
                            KUSLog.kusLogError(e.getMessage());
                        }

                        if (session != null) {
                            ArrayList<KUSModel> sessions = new ArrayList<>();
                            sessions.add(session);

                            upsertAll(sessions);
                        }

                        if (listener != null)
                            listener.onComplete(null, session);
                    }
                }

        );
    }

    public void updateLocallyLastSeenAtForSessionId(@Nullable String sessionId) {
        if (TextUtils.isEmpty(sessionId)) {
            return;
        }
        localLastSeenAtBySessionId.put(sessionId, Calendar.getInstance().getTime());
    }

    public void submitFormMessages(@NonNull final JSONArray messages,
                                   @NonNull String formId,
                                   @Nullable final KUSFormCompletionListener listener) {
        if (getUserSession() == null) {
            if (listener != null)
                listener.onComplete(new Error(), null, null);
            return;
        }

        final WeakReference<KUSChatSessionsDataSource> weakReference = new WeakReference<>(this);
        getUserSession().getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_POST,
                String.format(KUSConstants.URL.FORMS_RESPONSES_ENDPOINT, formId),
                new HashMap<String, Object>() {{
                    put("messages", messages);
                }},
                true,
                new KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(Error error, JSONObject response) {
                        if (error != null) {
                            if (listener != null)
                                listener.onComplete(error, null, null);

                            return;
                        }

                        KUSChatSession chatSession = null;
                        ArrayList<KUSModel> chatMessages = new ArrayList<>();
                        JSONArray includedModelsJSON = JsonHelper.jsonArrayFromKeyPath(response, "included");

                        if (includedModelsJSON != null) {
                            for (int i = 0; i < includedModelsJSON.length(); i++) {
                                try {
                                    JSONObject includedModelJSON = includedModelsJSON.getJSONObject(i);
                                    String type = JsonHelper.stringFromKeyPath(includedModelJSON, "type");

                                    if (type != null && type.equals(new KUSChatSession().modelType())) {
                                        chatSession = new KUSChatSession(includedModelJSON);
                                    } else if (type != null && type.equals(new KUSChatMessage().modelType())) {
                                        KUSChatMessage chatMessage = new KUSChatMessage(includedModelJSON);
                                        chatMessages.add(chatMessage);
                                    }

                                } catch (JSONException | KUSInvalidJsonException e) {
                                    KUSLog.kusLogError(e.getMessage());
                                }
                            }
                        }

                        if (chatSession != null) {
                            checkShouldFlushCustomAttributesToChatSessionId(chatSession.getId());

                            final KUSChatSession finalChatSession = chatSession;
                            weakReference.get().upsertAll(new ArrayList<KUSModel>() {{
                                add(finalChatSession);
                            }});
                        }

                        if (listener != null)
                            listener.onComplete(null, chatSession, chatMessages);

                    }
                }
        );
    }

    public void describeActiveConversation(@NonNull JSONObject customAttributes) {
        JSONObject pendingCustomChatSessionAttributes = new JSONObject();

        if (this.pendingCustomChatSessionAttributes != null) {
            Iterator iterator = this.pendingCustomChatSessionAttributes.keys();

            while (iterator.hasNext()) {
                String key = iterator.next().toString();

                try {
                    pendingCustomChatSessionAttributes.put(key, this.pendingCustomChatSessionAttributes.get(key));
                } catch (JSONException e) {
                    KUSLog.kusLogError(e.getMessage());
                }
            }
        }
        Iterator iterator;
        iterator = customAttributes.keys();

        while (iterator.hasNext()) {
            String key = iterator.next().toString();

            try {
                pendingCustomChatSessionAttributes.put(key, customAttributes.get(key));
            } catch (JSONException e) {
                KUSLog.kusLogError(e.getMessage());
            }
        }
        this.pendingCustomChatSessionAttributes = pendingCustomChatSessionAttributes;

        KUSChatSession mostRecentSession = getMostRecentSession();

        String mostRecentSessionId = null;

        if (mostRecentSession != null)
            mostRecentSessionId = mostRecentSession.getId();

        if (mostRecentSessionId != null)
            flushCustomAttributes(pendingCustomChatSessionAttributes, mostRecentSessionId);
    }

    public void describeNextConversation(@NonNull JSONObject customAttributes) {
        JSONObject pendingCustomChatSessionAttributesForNextConversation = new JSONObject();

        if (this.pendingCustomChatSessionAttributesForNextConversation != null) {
            Iterator iterator = this.pendingCustomChatSessionAttributesForNextConversation.keys();

            while (iterator.hasNext()) {
                String key = iterator.next().toString();

                try {
                    pendingCustomChatSessionAttributesForNextConversation.put(key,
                            this.pendingCustomChatSessionAttributesForNextConversation.get(key));

                } catch (JSONException e) {
                    KUSLog.kusLogError(e.getMessage());
                }
            }
        }

        Iterator iterator = customAttributes.keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();

            try {
                pendingCustomChatSessionAttributesForNextConversation.put(key, customAttributes.get(key));
            } catch (JSONException e) {
                KUSLog.kusLogError(e.getMessage());
            }
        }

        this.pendingCustomChatSessionAttributesForNextConversation = pendingCustomChatSessionAttributesForNextConversation;

    }
    //endregion

    //region internal methods

    public int getOpenChatSessionsCount() {
        int count = 0;
        KUSChatSession chatSession;
        for (KUSModel model : getList()) {
            chatSession = (KUSChatSession) model;
            if (chatSession.getLockedAt() == null) {
                count++;
            }
        }
        return count;
    }

    public void setMessageToCreateNewChatSession(@Nullable String messageToCreateNewChatSession) {
        this.messageToCreateNewChatSession = messageToCreateNewChatSession;
    }

    public int getOpenProactiveCampaignsCount() {
        if (getUserSession() == null)
            return 0;

        int count = 0;
        KUSChatSession chatSession;
        for (KUSModel model : getList()) {
            chatSession = (KUSChatSession) model;
            KUSChatMessagesDataSource chatDataSource = getUserSession().chatMessageDataSourceForSessionId(chatSession.getId());
            if (chatSession.getLockedAt() == null && chatDataSource != null
                    && !chatDataSource.isAnyMessageByCurrentUser()) {
                count += 1;
            }
        }
        return count;
    }
    //endregion

    //region Private Methods

    private void checkShouldFlushCustomAttributesToChatSessionId(@NonNull String chatSessionId) {
        boolean hasChatSessionAttributes = pendingCustomChatSessionAttributes != null ||
                pendingCustomChatSessionAttributesForNextConversation != null;

        if (!hasChatSessionAttributes)
            return;

        JSONObject pendingCustomChatSessionAttributes = new JSONObject();

        if (this.pendingCustomChatSessionAttributes != null) {
            Iterator<String> keys = this.pendingCustomChatSessionAttributes.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    pendingCustomChatSessionAttributes.put(key,
                            this.pendingCustomChatSessionAttributes.get(key));
                } catch (JSONException ignored) {
                }
            }
        }

        if (this.pendingCustomChatSessionAttributesForNextConversation != null) {
            Iterator<String> keys = this.pendingCustomChatSessionAttributesForNextConversation.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    pendingCustomChatSessionAttributes.put(key,
                            this.pendingCustomChatSessionAttributesForNextConversation.get(key));
                } catch (JSONException ignored) {
                }
            }

            this.pendingCustomChatSessionAttributesForNextConversation = null;
        }

        flushCustomAttributes(pendingCustomChatSessionAttributes, chatSessionId);
    }

    private void flushCustomAttributes(@NonNull final JSONObject customAttributes,
                                       @NonNull String chatSessionId) {
        if (getUserSession() == null)
            return;

        HashMap<String, Object> formData = new HashMap<String, Object>() {{
            put("custom", customAttributes);
        }};

        String endpoint = String.format(KUSConstants.URL.CONVERSATIONS_ENDPOINT + "/%s", chatSessionId);
        getUserSession().getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_PATCH,
                endpoint,
                formData,
                true,
                new KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(Error error, JSONObject response) {
                        if (getUserSession() == null)
                            return;

                        if (error != null) {
                            KUSLog.kusLogError(String.format("Error updating chat attributes: %s", error));
                        }
                    }
                }
        );
    }
    //endregion

    //region Helper methods
    @Nullable
    public KUSChatSession getMostRecentSession() {
        Date mostRecentMessageAt = null;
        KUSChatSession mostRecentSession = null;

        for (KUSModel model : getList()) {
            KUSChatSession chatSession = (KUSChatSession) model;

            if (chatSession.getLockedAt() == null) {

                if (mostRecentMessageAt == null) {
                    mostRecentMessageAt = chatSession.getLastMessageAt();
                    mostRecentSession = chatSession;

                } else if (chatSession.getLastMessageAt() != null
                        && mostRecentMessageAt.before(chatSession.getLastMessageAt())) {
                    mostRecentMessageAt = chatSession.getLastMessageAt();
                    mostRecentSession = chatSession;
                }
            }
        }

        return mostRecentSession != null ? mostRecentSession : (KUSChatSession) getFirst();
    }

    @Nullable
    public KUSChatSession mostRecentNonProactiveCampaignOpenSession() {
        if (getUserSession() == null)
            return (KUSChatSession) getFirst();

        Date mostRecentMessageAt = null;
        KUSChatSession mostRecentSession = null;

        for (KUSModel model : getList()) {
            KUSChatSession chatSession = (KUSChatSession) model;
            KUSChatMessagesDataSource chatDataSource =
                    getUserSession().chatMessageDataSourceForSessionId(chatSession.getId());

            if (chatSession.getLockedAt() == null && chatDataSource != null
                    && chatDataSource.isAnyMessageByCurrentUser()) {

                if (mostRecentMessageAt == null) {
                    mostRecentMessageAt = chatSession.getLastMessageAt();
                    mostRecentSession = chatSession;

                } else if (mostRecentMessageAt.before(chatSession.getLastMessageAt())) {
                    mostRecentMessageAt = chatSession.getLastMessageAt();
                    mostRecentSession = chatSession;
                }
            }
        }
        return mostRecentSession != null ? mostRecentSession : (KUSChatSession) getFirst();
    }

    @Nullable
    public Date lastSeenAtForSessionId(@NonNull String sessionId) {
        KUSChatSession chatSession = (KUSChatSession) findById(sessionId);
        Date chatSessionDate = null;
        if (chatSession != null)
            chatSessionDate = chatSession.getLastSeenAt();
        Date localDate = localLastSeenAtBySessionId.get(sessionId);

        if (chatSessionDate != null) {
            if (localDate != null)
                return chatSessionDate.after(localDate) ? chatSessionDate : localDate;
            else
                return chatSessionDate;
        } else
            return localDate;
    }

    public int totalUnreadCountExcludingSessionId(@Nullable String excludedSessionId) {
        if (getUserSession() == null)
            return 0;

        int count = 0;
        for (KUSModel model : getList()) {
            KUSChatSession session = (KUSChatSession) model;

            String sessionId = session.getId();
            if (excludedSessionId != null && excludedSessionId.equals(sessionId)) {
                continue;
            }

            KUSChatMessagesDataSource messagesDataSource = getUserSession().chatMessageDataSourceForSessionId(sessionId);
            Date sessionLastSeenAt = lastSeenAtForSessionId(sessionId);

            int unreadCountForSession = messagesDataSource != null
                    ? messagesDataSource.unreadCountAfterDate(sessionLastSeenAt)
                    : 0;

            count += unreadCountForSession;
        }

        return count;
    }
    //endregion

    //region Listener
    @Override
    public void onLoad(KUSPaginatedDataSource dataSource) {

    }

    @Override
    public void onError(KUSPaginatedDataSource dataSource, Error error) {

    }

    @Override
    public void onContentChange(KUSPaginatedDataSource dataSource) {
        if (getUserSession() != null && dataSource == this) {
            if (getUserSession().getSharedPreferences() != null)
                getUserSession().getSharedPreferences().setOpenChatSessionsCount(getOpenChatSessionsCount());

            for (KUSModel model : getList()) {
                KUSChatSession chatSession = (KUSChatSession) model;
                KUSChatMessagesDataSource messagesDataSource = getUserSession()
                        .chatMessageDataSourceForSessionId(chatSession.getId());
                if (messagesDataSource != null)
                    messagesDataSource.addListener(this);
            }

        } else if (dataSource.getClass().equals(KUSChatMessagesDataSource.class)) {
            sort();
            notifyAnnouncersOnContentChange();
        }
    }

    @Override
    public void onCreateSessionId(KUSChatMessagesDataSource source, String sessionId) {

    }

    @Override
    public void onReceiveTypingUpdate(@NonNull KUSChatMessagesDataSource source,
                                      @Nullable KUSTypingIndicator typingIndicator) {
        //No need to do anything here
    }

    @Override
    public void onSatisfactionResponseLoaded(@NonNull KUSChatMessagesDataSource dataSource) {
        //No need to do anything here
    }

    @Override
    public void objectDataSourceOnLoad(KUSObjectDataSource dataSource) {
        fetchLatest();
    }

    @Override
    public void objectDataSourceOnError(KUSObjectDataSource dataSource, Error error) {
        if (!dataSource.isFetched()) {
            notifyAnnouncersOnError(error);
        }
    }

    //endregion

}
