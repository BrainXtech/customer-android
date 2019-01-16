package com.kustomer.kustomersdk.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.kustomer.kustomersdk.API.KUSUserSession;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSSharedPreferences {
    //region Properties
    private static final String PREFERENCE_FILE_KEY = "kustomer_app_preferences";
    private static final String TRACKING_TOKEN_PREFERENCE = "tracking_token_pref";
    private static final String DID_CAPTURE_EMAIL_PREFERENCE = "email_capture_pref";
    private static final String FORM_ID_PREFERENCE = "form_id_pref";
    private static final String OPEN_CHAT_SESSIONS_COUNT = "open_chat_session_count_pref";
    private static final String SHOULD_HIDE_CONVERSATION_BUTTON = "should_hide_conversation_button_pref";
    private SharedPreferences sharedPref;
    //endregion

    //region Initializer
    public KUSSharedPreferences(Context context, KUSUserSession userSession) {
        String suiteName = userSession.getOrgName() + "_" + PREFERENCE_FILE_KEY;
        sharedPref = context.getSharedPreferences(
                suiteName, MODE_PRIVATE);
    }
    //endregion

    //region Basic Methods
    private void saveInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private int getInt(String key) {
        return sharedPref.getInt(key, 0);
    }

    private void saveBoolean(String key, boolean check) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, check);
        editor.apply();
    }

    private boolean getBoolean(String key) {
        return sharedPref.getBoolean(key, false);
    }

    private void saveString(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String getString(String key) {
        return sharedPref.getString(key, null);
    }

    //endregion

    //region Public Methods
    public void setDidCaptureEmail(boolean didCaptureEmail) {
        saveBoolean(DID_CAPTURE_EMAIL_PREFERENCE, didCaptureEmail);
    }

    public boolean getDidCaptureEmail() {
        return getBoolean(DID_CAPTURE_EMAIL_PREFERENCE);
    }

    public void setTrackingToken(String trackingToken) {
        saveString(TRACKING_TOKEN_PREFERENCE, trackingToken);
    }

    public String getTrackingToken() {
        return getString(TRACKING_TOKEN_PREFERENCE);
    }

    public void setFormId(String formId) {
        saveString(FORM_ID_PREFERENCE, formId);
    }

    public String getFormId() {
        return getString(FORM_ID_PREFERENCE);
    }

    public void reset() {
        sharedPref.edit().clear().apply();
    }

    public int getOpenChatSessionsCount(){
     return getInt(OPEN_CHAT_SESSIONS_COUNT);
    }

    public void setOpenChatSessionsCount(int value){
        saveInt(OPEN_CHAT_SESSIONS_COUNT,value);
    }

    public void setShouldHideConversationButton(boolean shouldHideNewConversationButtonInClosedChat) {
        saveBoolean(SHOULD_HIDE_CONVERSATION_BUTTON, shouldHideNewConversationButtonInClosedChat);
    }

    public boolean getShouldHideConversationButton() {
        return getBoolean(SHOULD_HIDE_CONVERSATION_BUTTON);
    }

    //endregion

}
