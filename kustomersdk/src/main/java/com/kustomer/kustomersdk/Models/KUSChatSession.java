package com.kustomer.kustomersdk.Models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;
import com.kustomer.kustomersdk.Helpers.KUSDate;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Helpers.KUSLog;
import com.kustomer.kustomersdk.Kustomer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.kustomer.kustomersdk.Utils.JsonHelper.dateFromKeyPath;
import static com.kustomer.kustomersdk.Utils.JsonHelper.stringFromKeyPath;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSChatSession extends KUSModel implements Serializable {

    //region Properties
    @Nullable
    private String preview;
    @Nullable
    private String trackingId;

    @Nullable
    private Date createdAt;
    @Nullable
    private Date lastSeenAt;
    @Nullable
    private Date lastMessageAt;
    @Nullable
    private Date lockedAt;

    @Nullable
    private String satisfactionFormId;
    @Nullable
    private String satisfactionStatus;
    @Nullable
    private Date satisfactionLockedAt;
    //endregion

    //region Initializer
    public KUSChatSession() {
    }

    public KUSChatSession(@Nullable JSONObject json) throws KUSInvalidJsonException {
        super(json);

        preview = stringFromKeyPath(json, "attributes.preview");
        trackingId = stringFromKeyPath(json, "attributes.trackingId");

        createdAt = dateFromKeyPath(json, "attributes.createdAt");
        lastSeenAt = dateFromKeyPath(json, "attributes.lastSeenAt");
        lastMessageAt = dateFromKeyPath(json, "attributes.lastMessageAt");
        lockedAt = dateFromKeyPath(json, "attributes.lockedAt");

        satisfactionFormId = stringFromKeyPath(json, "attributes.satisfaction.id");
        satisfactionStatus = stringFromKeyPath(json, "attributes.satisfaction.status");
        satisfactionLockedAt = dateFromKeyPath(json, "attributes.satisfaction.lockedAt");
    }
    //endregion

    //region Public Methods
    @NonNull
    public static KUSChatSession tempSessionFromChatMessage(@NonNull KUSChatMessage message)
            throws KUSInvalidJsonException {

        JSONObject attributes = new JSONObject();
        try {
            message.getBody();
            attributes.put("preview", message.getBody());
            attributes.put("createdAt", KUSDate.stringFromDate(message.getCreatedAt() != null ?
                    message.getCreatedAt() : Calendar.getInstance().getTime()));

            attributes.put("lastSeenAt", KUSDate.stringFromDate(message.getCreatedAt() != null ?
                    message.getCreatedAt() : Calendar.getInstance().getTime()));

            attributes.put("lastMessageAt", KUSDate.stringFromDate(message.getCreatedAt() != null ?
                    message.getCreatedAt() : Calendar.getInstance().getTime()));
        } catch (JSONException e) {
            KUSLog.kusLogError(e.getMessage());
        }

        JSONObject messageJSON = new JSONObject();
        try {
            messageJSON.put("type", "chat_session");
            messageJSON.put("id", message.getSessionId() != null ? message.getSessionId() : "");
            messageJSON.put("attributes", attributes);
        } catch (JSONException e) {
            KUSLog.kusLogError(e.getMessage());
        }

        return new KUSChatSession(messageJSON);
    }

    @NonNull
    @Override
    public String toString() {
        //Missing %p (this)
        return String.format("<%s : oid: %s; preview: %s>", this.getClass(), this.getId(), this.preview);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;

        if (obj == null)
            return false;

        if (!obj.getClass().equals(KUSChatSession.class))
            return false;

        KUSChatSession chatSession = (KUSChatSession) obj;

        return Objects.equals(chatSession.getId(),this.getId())
                && Objects.equals(this.preview,chatSession.preview)
                && Objects.equals(this.lastSeenAt,chatSession.lastSeenAt)
                && Objects.equals(this.lastMessageAt,chatSession.lastMessageAt)
                && Objects.equals(this.createdAt,chatSession.createdAt);
    }

    @Override
    public int compareTo(@NonNull KUSModel kusModel) {
        KUSChatSession chatSession = (KUSChatSession) kusModel;
        int date;
        if (chatSession.lockedAt != null && this.lockedAt != null)
            date = chatSession.lockedAt.compareTo(this.lockedAt);
        else if (chatSession.lockedAt != null)
            return -1;
        else if (this.lockedAt != null)
            return 1;
        else if (this.createdAt == null && chatSession.createdAt == null)
            date = 0;
        else if (this.createdAt == null)
            date = 1;
        else if (chatSession.createdAt == null)
            date = -1;
        else
            date = chatSession.createdAt.compareTo(this.createdAt);

        int parent = super.compareTo(kusModel);
        return date == 0 ? parent : date;
    }

    @NonNull
    @Override
    public String modelType() {
        return "chat_session";
    }
    //endregion

    //region Accessors
    @Nullable
    public String getPreview() {
        return preview;
    }

    public void setPreview(@Nullable String preview) {
        this.preview = preview;
    }

    @Nullable
    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(@Nullable String trackingId) {
        this.trackingId = trackingId;
    }

    @Nullable
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@Nullable Date createdAt) {
        this.createdAt = createdAt;
    }

    @Nullable
    public Date getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(@Nullable Date lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    @Nullable
    public Date getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(@Nullable Date lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public void setLockedAt(@Nullable Date lockedAt) {
        this.lockedAt = lockedAt;
    }

    @Nullable
    public Date getLockedAt() {
        return lockedAt;
    }

    @Nullable
    public String getSatisfactionFormId() {
        return satisfactionFormId;
    }

    @Nullable
    public String getSatisfactionStatus() {
        return satisfactionStatus;
    }

    @Nullable
    public Date getSatisfactionLockedAt() {
        return satisfactionLockedAt;
    }
    //endregion
}
