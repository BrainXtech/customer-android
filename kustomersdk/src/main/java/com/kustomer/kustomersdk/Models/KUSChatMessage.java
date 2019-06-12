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

import static com.kustomer.kustomersdk.Utils.JsonHelper.dateFromKeyPath;
import static com.kustomer.kustomersdk.Utils.JsonHelper.stringFromKeyPath;

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
    @NonNull
    private List attachmentIds = new ArrayList();

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

    @NonNull
    private KUSChatMessageType type;
    @NonNull
    private KUSChatMessageState state;
    @Nullable
    private String value;
    //endregion

    //region Initializer
    public KUSChatMessage() {
        state = KUSChatMessageState.KUS_CHAT_MESSAGE_STATE_SENT;
        this.type = KUSChatMessageType.KUS_CHAT_MESSAGE_TYPE_TEXT;
    }

    public KUSChatMessage(@Nullable JSONObject json) throws KUSInvalidJsonException {
        this(json, KUSChatMessageType.KUS_CHAT_MESSAGE_TYPE_TEXT, null);
    }

    public KUSChatMessage(@Nullable JSONObject json,
                          @NonNull KUSChatMessageType type,
                          @Nullable URL imageUrl) throws KUSInvalidJsonException {
        super(json);

        state = KUSChatMessageState.KUS_CHAT_MESSAGE_STATE_SENT;
        this.type = type;
        this.imageUrl = imageUrl;

        trackingId = stringFromKeyPath(json, "attributes.trackingId");
        body = stringFromKeyPath(json, "attributes.body");

        JSONArray attachmentArray = JsonHelper.jsonArrayFromKeyPath(json, "relationships.attachments.data");

        if (attachmentArray != null)
            this.attachmentIds = arrayListFromJsonArray(attachmentArray, "id");

        this.createdAt = dateFromKeyPath(json, "attributes.createdAt");
        this.importedAt = dateFromKeyPath(json, "attributes.importedAt");
        this.direction = KUSChatMessageDirectionFromString(stringFromKeyPath(json, "attributes.direction"));
        this.sentById = stringFromKeyPath(json, "relationships.sentBy.data.id");
        this.campaignId = stringFromKeyPath(json, "relationships.campaign.data.id");
    }

    //endregion

    //region Public Methods
    public static boolean KUSChatMessageSentByUser(@Nullable KUSChatMessage message) {

        return message != null && message.direction == KUSChatMessageDirection.KUS_CHAT_MESSAGE_DIRECTION_IN;
    }

    public static boolean KUSMessagesSameSender(@Nullable KUSChatMessage message1,
                                                @Nullable KUSChatMessage message2) {
        return message1 != null
                && message2 != null
                && message1.direction == message2.direction
                && (message1.sentById != null && message1.sentById.equalsIgnoreCase(message2.sentById));
    }

    @Nullable
    private static KUSChatMessageDirection KUSChatMessageDirectionFromString(@Nullable String str) {

        if (str == null)
            return null;

        return str.equalsIgnoreCase("in")
                ? KUSChatMessageDirection.KUS_CHAT_MESSAGE_DIRECTION_IN
                : KUSChatMessageDirection.KUS_CHAT_MESSAGE_DIRECTION_OUT;
    }

    @NonNull
    public static URL attachmentUrlForMessageId(@Nullable String messageId,
                                                @Nullable String attachmentId) throws MalformedURLException {
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
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;

        if (obj == null)
            return false;

        if (!obj.getClass().equals(this.getClass()))
            return false;

        KUSChatMessage chatMessage = (KUSChatMessage) obj;

        if (chatMessage.state != this.state)
            return false;
        if (!Objects.equals(chatMessage.direction, this.direction))
            return false;
        if (chatMessage.type != this.type)
            return false;
        if (!chatMessage.attachmentIds.equals(this.attachmentIds))
            return false;
        if (!chatMessage.getId().equals(this.getId()))
            return false;
        if (!Objects.equals(chatMessage.createdAt, this.createdAt))
            return false;
        if (!Objects.equals(chatMessage.importedAt, this.importedAt))
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
    @NonNull
    private List<String> arrayListFromJsonArray(@NonNull JSONArray array, String id) {
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

    @NonNull
    public String getBody() {
        return body != null ? body : "";
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

    @NonNull
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

    @NonNull
    public KUSChatMessageType getType() {
        return type;
    }

    public void setType(@NonNull KUSChatMessageType type) {
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
