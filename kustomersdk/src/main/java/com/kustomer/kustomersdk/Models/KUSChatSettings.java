package com.kustomer.kustomersdk.Models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.Enums.KUSBusinessHoursAvailability;
import com.kustomer.kustomersdk.Enums.KUSVolumeControlMode;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONObject;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSChatSettings extends KUSModel implements Serializable {
    //region Properties
    @Nullable
    private String teamName;
    @Nullable
    private URL teamIconURL;
    @Nullable
    private String greeting;
    @Nullable
    private String activeFormId;
    @Nullable
    private String pusherAccessKey;
    private boolean enabled;

    @NonNull
    private KUSBusinessHoursAvailability availability;
    @Nullable
    private String offHoursImageUrl;
    @Nullable
    private String offHoursMessage;

    @Nullable
    private String waitMessage;
    @Nullable
    private String customWaitMessage;

    private int timeOut;
    private int promptDelay;
    private boolean hideWaitOption;

    @NonNull
    private ArrayList<String> followUpChannels;

    private boolean useDynamicWaitMessage;
    private boolean markDoneAfterTimeout;
    private boolean volumeControlEnabled;
    private boolean closableChat;
    private boolean singleSessionChat;
    private boolean noHistory;
    private boolean shouldShowTypingIndicatorCustomerWeb;
    private boolean shouldShowTypingIndicatorWeb;

    @NonNull
    private KUSVolumeControlMode volumeControlMode;

    private int upfrontWaitThreshold;
    private boolean showKustomerBranding;
    //endregion

    //region Initializer
    public KUSChatSettings(@Nullable JSONObject json) throws KUSInvalidJsonException {
        super(json);

        teamName = JsonHelper.stringFromKeyPath(json, "attributes.teamName");
        teamIconURL = JsonHelper.urlFromKeyPath(json, "attributes.teamIconUrl");
        greeting = JsonHelper.stringFromKeyPath(json, "attributes.greeting");
        activeFormId = JsonHelper.stringFromKeyPath(json, "attributes.activeForm");
        pusherAccessKey = JsonHelper.stringFromKeyPath(json, "attributes.pusherAccessKey");
        enabled = JsonHelper.boolFromKeyPath(json, "attributes.enabled");

        closableChat = JsonHelper.boolFromKeyPath(json, "attributes.closableChat");
        waitMessage = JsonHelper.stringFromKeyPath(json, "attributes.waitMessage");
        singleSessionChat = JsonHelper.boolFromKeyPath(json, "attributes.singleSessionChat");
        noHistory = JsonHelper.boolFromKeyPath(json, "attributes.noHistory");

        customWaitMessage = JsonHelper.stringFromKeyPath(json, "attributes.volumeControl.customWaitMessage");
        timeOut = JsonHelper.integerFromKeyPath(json, "attributes.volumeControl.timeout");
        promptDelay = JsonHelper.integerFromKeyPath(json, "attributes.volumeControl.promptDelay");
        hideWaitOption = JsonHelper.boolFromKeyPath(json, "attributes.volumeControl.hideWaitOption");
        followUpChannels = JsonHelper.stringsArrayListFromKeyPath(json, "attributes.volumeControl.followUpChannels");
        useDynamicWaitMessage = JsonHelper.boolFromKeyPath(json, "attributes.volumeControl.useDynamicWaitMessage");
        markDoneAfterTimeout = JsonHelper.boolFromKeyPath(json, "attributes.volumeControl.markDoneAfterTimeout");
        volumeControlEnabled = JsonHelper.boolFromKeyPath(json, "attributes.volumeControl.enabled");

        offHoursMessage = JsonHelper.stringFromKeyPath(json,"attributes.offhoursMessage");
        offHoursImageUrl = JsonHelper.stringFromKeyPath(json,"attributes.offhoursImageUrl");
        availability = getKUSBusinessHoursAvailabilityFromString(JsonHelper.stringFromKeyPath(json,"attributes.offhoursDisplay"));

        volumeControlMode = kusVolumeControlModeFromString(JsonHelper.stringFromKeyPath(json,"attributes.volumeControl.mode"));
        upfrontWaitThreshold = JsonHelper.integerFromKeyPath(json,"attributes.volumeControl.upfrontWaitThreshold");
        showKustomerBranding = JsonHelper.boolFromKeyPath(json, "attributes.showBrandingIdentifier");

        shouldShowTypingIndicatorCustomerWeb = JsonHelper.boolFromKeyPath(json,
                "attributes.showTypingIndicatorCustomerWeb");
        shouldShowTypingIndicatorWeb = JsonHelper.boolFromKeyPath(json,
                "attributes.showTypingIndicatorWeb");
    }

    @Override
    public String modelType() {
        return "chat_settings";
    }
    //endregion

    //region Private Methods
    private KUSVolumeControlMode kusVolumeControlModeFromString(String string){

        if(string == null)
            return KUSVolumeControlMode.KUS_VOLUME_CONTROL_MODE_UNKNOWN;

        if(string.equals("upfront")){
            return KUSVolumeControlMode.KUS_VOLUME_CONTROL_MODE_UPFRONT;
        }else if (string.equals("delayed")){
            return KUSVolumeControlMode.KUS_VOLUME_CONTROL_MODE_DELAYED;
        }

        return KUSVolumeControlMode.KUS_VOLUME_CONTROL_MODE_UNKNOWN;
    }

    private String stringSanitizedReply(String autoReply) {
        if (autoReply != null)
            return autoReply.trim().length() > 0 ? autoReply.trim() : null;
        else
            return null;
    }

    private static KUSBusinessHoursAvailability getKUSBusinessHoursAvailabilityFromString(String string) {
        if(string == null)
            return KUSBusinessHoursAvailability.KUS_BUSINESS_HOURS_AVAILABILITY_HIDE_CHAT;

        switch (string) {
            case "online":
                return KUSBusinessHoursAvailability.KUS_BUSINESS_HOURS_AVAILABILITY_ONLINE;
            case "offline":
                return KUSBusinessHoursAvailability.KUS_BUSINESS_HOURS_AVAILABILITY_OFFLINE;
            default:
                return KUSBusinessHoursAvailability.KUS_BUSINESS_HOURS_AVAILABILITY_HIDE_CHAT;
        }
    }
    //endregion

    //region Accessors

    @Nullable
    public String getTeamName() {
        return teamName;
    }

    @Nullable
    public URL getTeamIconURL() {
        return teamIconURL;
    }

    @Nullable
    public String getGreeting() {
        return greeting;
    }

    @Nullable
    public String getActiveFormId() {
        return activeFormId;
    }

    @Nullable
    public String getPusherAccessKey() {
        return pusherAccessKey;
    }

    public boolean getEnabled() {
        return enabled;
    }

    @Nullable
    public String getCustomWaitMessage() {
        return customWaitMessage;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public int getPromptDelay() {
        return promptDelay;
    }

    public boolean isHideWaitOption() {
        return hideWaitOption;
    }

    @NonNull
    public ArrayList<String> getFollowUpChannels() {
        return followUpChannels;
    }

    public boolean isUseDynamicWaitMessage() {
        return useDynamicWaitMessage;
    }

    public boolean isMarkDoneAfterTimeout() {
        return markDoneAfterTimeout;
    }

    public boolean isVolumeControlEnabled() {
        return volumeControlEnabled;
    }

    @Nullable
    public String getWaitMessage() {
        return waitMessage;
    }

    public boolean getHideWaitOption() {
        return hideWaitOption;
    }

    public boolean getUseDynamicWaitMessage() {
        return useDynamicWaitMessage;
    }

    public boolean getMarkDoneAfterTimeout() {
        return markDoneAfterTimeout;
    }

    public boolean getVolumeControlEnabled() {
        return volumeControlEnabled;
    }

    public boolean getClosableChat() {
        return closableChat;
    }

    public boolean getSingleSessionChat() {
        return singleSessionChat;
    }

    public boolean getNoHistory() {
        return noHistory;
    }

    @NonNull
    public KUSBusinessHoursAvailability getAvailability() {
        return availability;
    }

    @Nullable
    public String getOffHoursImageUrl() {
        return offHoursImageUrl;
    }

    @Nullable
    public String getOffHoursMessage() {
        return offHoursMessage;
    }

    @NonNull
    public KUSVolumeControlMode getVolumeControlMode() {
        return volumeControlMode;
    }

    public int getUpfrontWaitThreshold() {
        return upfrontWaitThreshold;
    }

    public boolean shouldShowKustomerBranding() {
        return showKustomerBranding;
    }

    public boolean getShouldShowTypingIndicatorCustomerWeb() {
        return shouldShowTypingIndicatorCustomerWeb;
    }

    public boolean getShouldShowTypingIndicatorWeb() {
        return shouldShowTypingIndicatorWeb;
    }

    //endregion
}
