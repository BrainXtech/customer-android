package com.kustomer.kustomersdk.Models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.Enums.KUSChatMessageDirection;
import com.kustomer.kustomersdk.Enums.KUSChatMessageState;
import com.kustomer.kustomersdk.Enums.KUSChatMessageType;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Kustomer;
import com.kustomer.kustomersdk.Utils.JsonHelper;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Junaid on 1/20/2018.
 */


public class KUSChatMessage extends KUSModel {

    //region Properties
    @Nullable
    private String trackingId;
    @Nullable
    private String body;
    @Nullable
    private URL imageUrl;
    @Nullable
    private List attachmentIds;

    @Nullable
    private Date createdAt;
    @Nullable
    private Date importedAt;
    @Nullable
    private KUSChatMessageDirection direction;
    @Nullable
    private String sentById;
    @Nullable
    private String campaignId;
    @Nullable
    private KUSChatMessageType type;
    private KUSChatMessageState state;
    @Nullable
    private String value;
    //endregion

    //region Initializer
    public KUSChatMessage() {

    }

    public KUSChatMessage(JSONObject json) throws KUSInvalidJsonException {
        this(json, KUSChatMessageType.KUS_CHAT_MESSAGE_TYPE_TEXT, null);
    }

    public KUSChatMessage(JSONObject json, @Nullable KUSChatMessageType type, @Nullable URL imageUrl)
            throws KUSInvalidJsonException {
        super(json);

        state = KUSChatMessageState.KUS_CHAT_MESSAGE_STATE_SENT;
        trackingId = JsonHelper.stringFromKeyPath(json, "attributes.trackingId");
        body = JsonHelper.stringFromKeyPath(json, "attributes.body");
        this.type = type;
        this.imageUrl = imageUrl;

        JSONArray attachmentArray = JsonHelper.arrayFromKeyPath(json,
                "relationships.attachments.data");

        if (attachmentArray != null)
            this.attachmentIds = arrayListFromJsonArray(attachmentArray, "id");

        this.createdAt = JsonHelper.dateFromKeyPath(json, "attributes.createdAt");
        this.importedAt = JsonHelper.dateFromKeyPath(json, "attributes.importedAt");
        this.direction = KUSChatMessageDirectionFromString(JsonHelper.stringFromKeyPath(json,
                "attributes.direction"));
        this.sentById = JsonHelper.stringFromKeyPath(json, "relationships.sentBy.data.id");
        this.campaignId = JsonHelper.stringFromKeyPath(json, "relationships.campaign.data.id");
    }

    //endregion

    //region Public Methods
    public static boolean KUSChatMessageSentByUser(KUSChatMessage message) {

        return message != null && message.direction == KUSChatMessageDirection.KUS_CHAT_MESSAGE_DIRECTION_IN;

    }

    public static boolean KUSMessagesSameSender(KUSChatMessage message1, KUSChatMessage message2) {
        return message1 != null
                && message2 != null
                && message1.direction == message2.direction
                && (message1.sentById != null && message1.sentById.equalsIgnoreCase(message2.sentById));
    }

    @Nullable
    private static KUSChatMessageDirection KUSChatMessageDirectionFromString(String str) {
        if (str == null)
            return null;

        return str.equalsIgnoreCase("in")
                ? KUSChatMessageDirection.KUS_CHAT_MESSAGE_DIRECTION_IN
                : KUSChatMessageDirection.KUS_CHAT_MESSAGE_DIRECTION_OUT;
    }

    @NonNull
    public static URL attachmentUrlForMessageId(String messageId, String attachmentId)
            throws MalformedURLException {
        String imageUrlString = String.format(KUSConstants.URL.ATTACHMENT_ENDPOINT,
                Kustomer.getSharedInstance().getUserSession().getOrgName(),
                Kustomer.hostDomain(),
                messageId,
                attachmentId);

        return new URL(imageUrlString);
    }

    @Override
    public String modelType() {
        return "chat_message";
    }

    @NonNull
    @Override
    public String toString() {
        //Missing %p (this)
        return String.format("<%s : oid: %s; body: %s>", this.getClass(), this.getId(), this.body);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this)
            return true;
        if (!obj.getClass().equals(this.getClass()))
            return false;

        KUSChatMessage chatMessage = (KUSChatMessage) obj;

        if (chatMessage.state != this.state)
            return false;
        if (chatMessage.direction != this.direction)
            return false;
        if (chatMessage.type != this.type)
            return false;
        if (chatMessage.attachmentIds != null && this.attachmentIds != null
                && !chatMessage.attachmentIds.equals(this.attachmentIds))
            return false;
        if (chatMessage.attachmentIds == null || this.attachmentIds == null)
            return false;
        if (!chatMessage.getId().equals(this.getId()))
            return false;
        if (!Objects.equals(chatMessage.createdAt, this.createdAt))
            return false;
        if (chatMessage.importedAt != null && !chatMessage.importedAt.equals(this.importedAt))
            return false;
        if (!Objects.equals(chatMessage.body, this.body))
            return false;

        return true;
    }

    @Override
    public int compareTo(@NonNull KUSModel kusModel) {
        KUSChatMessage message = (KUSChatMessage) kusModel;

        int date;
        if (this.createdAt == null && message.createdAt == null)
            date = 0;
        else if (this.createdAt == null)
            date = 1;
        else if (message.createdAt == null)
            date = -1;
        else
            date = message.createdAt.compareTo(this.createdAt);

        int parent = super.compareTo(kusModel);
        return date == 0 ? parent : date;
    }
    //endregion

    //region Private Methods
    private List<String> arrayListFromJsonArray(JSONArray array, String id) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObject = array.getJSONObject(i);
                list.add(jsonObject.getString(id));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;
    }
    //endregion

    //region Accessors

    @Nullable
    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(@Nullable String trackingId) {
        this.trackingId = trackingId;
    }

    @Nullable
    public String getBody() {
        return body;
    }

    public void setBody(@Nullable String body) {
        this.body = body;
    }

    @Nullable
    public URL getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@Nullable URL imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Nullable
    public List getAttachmentIds() {
        return attachmentIds;
    }

    public void setAttachmentIds(ArrayList attachmentIds) {
        this.attachmentIds = attachmentIds;
    }

    @Nullable
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@Nullable Date createdAt) {
        this.createdAt = createdAt;
    }

    @Nullable
    public Date getImportedAt() {
        return importedAt;
    }

    public void setImportedAt(@Nullable Date importedAt) {
        this.importedAt = importedAt;
    }

    @Nullable
    public KUSChatMessageDirection getDirection() {
        return direction;
    }

    public void setDirection(@Nullable KUSChatMessageDirection direction) {
        this.direction = direction;
    }

    @Nullable
    public String getSentById() {
        return sentById;
    }

    public void setSentById(@Nullable String sentById) {
        this.sentById = sentById;
    }

    @Nullable
    public KUSChatMessageType getType() {
        return type;
    }

    public void setType(@Nullable KUSChatMessageType type) {
        this.type = type;
    }

    @NonNull
    public KUSChatMessageState getState() {
        return state;
    }

    public void setState(@NonNull KUSChatMessageState state) {
        this.state = state;
    }

    @Nullable
    public String getValue() {
        return value;
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

    @Nullable
    public String getCampaignId() {
        return campaignId;
    }

    //endregion

}
