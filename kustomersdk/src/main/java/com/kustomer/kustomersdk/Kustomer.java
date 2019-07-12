package com.kustomer.kustomersdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.text.TextUtils;
import android.util.Base64;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Activities.KUSKnowledgeBaseActivity;
import com.kustomer.kustomersdk.Activities.KUSSessionsActivity;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Helpers.KUSLocalization;
import com.kustomer.kustomersdk.Interfaces.KUSChatAvailableListener;
import com.kustomer.kustomersdk.Interfaces.KUSIdentifyListener;
import com.kustomer.kustomersdk.Interfaces.KUSKustomerListener;
import com.kustomer.kustomersdk.Interfaces.KUSLogOptions;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Managers.KUSNetworkStateManager;
import com.kustomer.kustomersdk.Managers.KUSVolumeControlTimerManager;
import com.kustomer.kustomersdk.Models.KUSCustomerDescription;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Junaid on 1/20/2018.
 */

public class Kustomer {

    //region Properties
    @NonNull
    private static Context mContext;
    @Nullable
    private static Kustomer sharedInstance = null;

    @Nullable
    private KUSUserSession userSession;

    @Nullable
    private String apiKey;
    @Nullable
    private String orgId;
    @Nullable
    private String orgName;

    @Nullable
    private static String hostDomainOverride = null;
    private static int logOptions = KUSLogOptions.KUSLogOptionInfo | KUSLogOptions.KUSLogOptionErrors;
    @Nullable
    KUSKustomerListener mListener;
    //endregion

    //region LifeCycle
    @NonNull
    public static Kustomer getSharedInstance() {
        if (sharedInstance == null)
            sharedInstance = new Kustomer();

        return sharedInstance;
    }
    //endregion

    //region Class Methods

    public static void init(@NonNull Context context, @NonNull String apiKey) throws AssertionError {
        mContext = context.getApplicationContext();

        EmojiCompat.Config emojiConfig = new BundledEmojiCompatConfig(mContext);
        EmojiCompat.init(emojiConfig);

        KUSLocalization.getSharedInstance().updateKustomerLocaleWithFallback(mContext);
        KUSNetworkStateManager.getSharedInstance().startObservingNetworkState();
        getSharedInstance().setApiKey(apiKey);

        try {
            ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                    .newBuilder(context, getSharedInstance().getOkHttpClientForFresco())
                    .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                    .setDownsampleEnabled(true)
                    .build();

            Fresco.initialize(context, config);
        } catch (Exception ignore) {
        }
    }

    public static void setListener(@NonNull KUSKustomerListener listener) {
        getSharedInstance().mSetListener(listener);
    }

    public static void describeConversation(@NonNull JSONObject customAttributes) {
        getSharedInstance().mDescribeConversation(customAttributes);
    }

    public static void describeNextConversation(@NonNull JSONObject customAttributes) {
        getSharedInstance().mDescribeNextConversation(customAttributes);
    }

    public static void describeCustomer(@NonNull KUSCustomerDescription customerDescription) {
        getSharedInstance().mDescribeCustomer(customerDescription);
    }

    /**
     * Returns the identification status in listener on background thread.
     *
     * @param externalToken A valid JWT web token to identify user
     * @param listener      The callback which will receive identification status.
     */
    public static void identify(@NonNull String externalToken, @Nullable KUSIdentifyListener listener) {
        getSharedInstance().mIdentify(externalToken, listener);
    }

    public static void resetTracking() {
        getSharedInstance().mResetTracking();
    }

    public static void setCurrentPageName(@NonNull String currentPageName) {
        getSharedInstance().mSetCurrentPageName(currentPageName);
    }

    public static int getUnreadMessageCount() {
        return getSharedInstance().mGetUnreadMessageCount();
    }

    /**
     * Returns the chat status in listener on background thread.
     *
     * @param listener The callback which will receive chat status.
     */
    public static void isChatAvailable(@NonNull KUSChatAvailableListener listener) {
        getSharedInstance().mIsChatAvailable(listener);
    }

    public static void showSupport(@NonNull Activity activity) {
        Intent intent = new Intent(activity, KUSSessionsActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.kus_slide_up, R.anim.kus_stay);
    }

    public static void presentKnowledgeBase(@NonNull Activity activity) {
        Intent intent = new Intent(activity, KUSKnowledgeBaseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        if (KUSLocalization.getSharedInstance().isLTR())
            activity.overridePendingTransition(R.anim.kus_slide_left, R.anim.kus_stay);
        else
            activity.overridePendingTransition(R.anim.kus_slide_left_rtl, R.anim.kus_stay);
    }

    public static void presentCustomWebPage(@NonNull Activity activity, @NonNull String url) {
        Intent intent = new Intent(activity, KUSKnowledgeBaseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KUSConstants.Keys.K_KUSTOMER_URL_KEY, url);

        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.kus_slide_left, R.anim.kus_stay);
    }

    public static void setLocale(@NonNull Locale locale) {
        getSharedInstance().mSetLocale(locale);
    }

    @NonNull
    public static String getLocalizedString(@NonNull String key) {
        return getSharedInstance().mGetString(key);
    }

    public static void setFormId(@NonNull String formId) {
        getSharedInstance().mSetFormId(formId);
    }

    public static int getOpenConversationsCount() {
        return getSharedInstance().mGetOpenConversationsCount();
    }

    public static void hideNewConversationButtonInClosedChat(Boolean status) {
        getSharedInstance().mHideNewConversationButtonInClosedChat(status);
    }

    public static void showSupportWithMessage(Activity activity, String message, JSONObject customAttributes) {
        getSharedInstance().mShowSupportWithMessage(activity, message, customAttributes);
    }

    public static void showSupportWithMessage(Activity activity, String message) {
        getSharedInstance().mShowSupportWithMessage(activity, message, null);
    }
    //endregion

    //region Private Methods
    @NonNull
    private OkHttpClient getOkHttpClientForFresco() {
        return new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
                        Request originalRequest = chain.request(); //Current Request

                        //tracking token
                        String trackingToken = Kustomer.getSharedInstance().getUserSession()
                                .getTrackingTokenDataSource().getCurrentTrackingToken();

                        Request requestWithToken; //The request with the access token which we will use if we have one instead of the original
                        requestWithToken = originalRequest.newBuilder()
                                .addHeader(KUSConstants.Keys.K_KUSTOMER_TRACKING_TOKEN_HEADER_KEY,
                                        trackingToken != null ? trackingToken : "")
                                .build();
                        Response response = chain.proceed(requestWithToken); //proceed with the request and get the response

                        if (response.body() != null && response.code() != HttpURLConnection.HTTP_OK) {
                            response.body().close();
                        }
                        return response;
                    }
                })
                .build();
    }

    private void mSetListener(@Nullable KUSKustomerListener listener) {
        mListener = listener;
        if (userSession != null)
            userSession.getDelegateProxy().setListener(listener);
    }

    private void mDescribeConversation(@Nullable JSONObject customAttributes) {
        if (customAttributes == null)
            throw new AssertionError("Attempted to describe a conversation with no attributes set");

        if (!customAttributes.keys().hasNext() || userSession == null)
            return;

        userSession.getChatSessionsDataSource().describeActiveConversation(customAttributes);
    }

    private void mDescribeNextConversation(@Nullable JSONObject customAttributes) {
        if (customAttributes == null)
            throw new AssertionError("Attempted to describe a conversation with no attributes set");

        if (!customAttributes.keys().hasNext() || userSession == null)
            return;

        userSession.getChatSessionsDataSource().describeNextConversation(customAttributes);
    }

    private void mDescribeCustomer(@Nullable KUSCustomerDescription customerDescription) {
        if (userSession != null)
            userSession.describeCustomer(customerDescription, null);
    }

    private void mIdentify(@Nullable final String externalToken, @Nullable final KUSIdentifyListener listener) {
        if (externalToken == null) {
            throw new AssertionError("Kustomer expects externalToken to be non-null");
        }

        if (externalToken.isEmpty() || userSession == null) {
            if (listener != null)
                listener.onComplete(false);

            return;
        }

        HashMap<String, Object> params = new HashMap<String, Object>() {{
            put("externalToken", externalToken);
        }};

        final WeakReference<KUSUserSession> instance = new WeakReference<>(this.userSession);
        userSession.getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_POST,
                KUSConstants.URL.IDENTITY_ENDPOINT,
                params,
                true,
                new KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(@Nullable final Error error, @Nullable JSONObject response) {
                        instance.get().getTrackingTokenDataSource().fetch();
                        if (listener != null) {
                            listener.onComplete(error == null);
                        }
                    }
                }
        );
    }

    private void mResetTracking() {
        String currentPage = userSession != null ?
                userSession.getActivityManager().getCurrentPageName()
                : null;

        // Create a new userSession and release the previous one
        if (userSession != null) {
            userSession.removeAllListeners();
            KUSVolumeControlTimerManager.getSharedInstance().removeVcTimers();
        }

        userSession = new KUSUserSession(orgName, orgId, true);

        // Update the new userSession with the previous state
        userSession.getDelegateProxy().setListener(mListener);
        userSession.getActivityManager().setCurrentPageName(currentPage);
    }

    private void mSetCurrentPageName(String currentPageName) {
        if (userSession != null)
            userSession.getActivityManager().setCurrentPageName(currentPageName);
    }

    private int mGetUnreadMessageCount() {
        return userSession != null ?
                userSession.getChatSessionsDataSource().totalUnreadCountExcludingSessionId(null)
                : 0;
    }

    private void mIsChatAvailable(@Nullable KUSChatAvailableListener listener) {

        // Get latest settings from server
        if (userSession != null) {
            userSession.getChatSettingsDataSource().fetch();
            userSession.getChatSettingsDataSource().isChatAvailable(listener);
        } else {
            if (listener != null)
                listener.onFailure();
        }
    }

    private void setApiKey(@Nullable String apiKey) {
        if (apiKey == null) {
            throw new AssertionError("Kustomer requires a valid API key");
        }

        if (apiKey.length() == 0) {
            return;
        }

        String[] apiKeyParts = apiKey.split("[.]");

        if (apiKeyParts.length <= 2)
            throw new AssertionError("Kustomer API key has unexpected format");

        JSONObject tokenPayload;
        try {
            tokenPayload = jsonFromBase64EncodedJsonString(apiKeyParts[1]);
            this.apiKey = apiKey;
            orgId = tokenPayload.getString(KUSConstants.Keys.K_KUSTOMER_ORG_ID_KEY);
            orgName = tokenPayload.getString(KUSConstants.Keys.K_KUSTOMER_ORG_NAME_KEY);

            if (orgName.length() == 0)
                throw new AssertionError("Kustomer API key missing expected field: orgName");

            userSession = new KUSUserSession(orgName, orgId);
            userSession.getDelegateProxy().setListener(mListener);
        } catch (JSONException ignore) {
        }
    }

    @NonNull
    private JSONObject jsonFromBase64EncodedJsonString(@NonNull String base64EncodedJson) throws JSONException {
        byte[] array = Base64.decode(base64EncodedJson, Base64.NO_PADDING);
        return new JSONObject(new String(array));
    }

    private void mSetLocale(@Nullable Locale locale) {
        KUSLocalization.getSharedInstance().setUserLocale(locale);
    }

    @NonNull
    private String mGetString(@NonNull String key) {
        return KUSLocalization.getSharedInstance().localizedString(mContext, key);
    }

    private void mSetFormId(@NonNull String formId) {
        if (getUserSession().getSharedPreferences() != null)
            getUserSession().getSharedPreferences().setFormId(formId);
    }

    private int mGetOpenConversationsCount() {
        if (getUserSession().getSharedPreferences() != null)
            return getUserSession().getSharedPreferences().getOpenChatSessionsCount();
        return 0;
    }

    private void mHideNewConversationButtonInClosedChat(boolean status) {
        if (getUserSession().getSharedPreferences() != null)
            getUserSession().getSharedPreferences().setShouldHideConversationButton(status);
    }

    private void mShowSupportWithMessage(@NonNull Activity activity,
                                         @Nullable String message,
                                         @Nullable JSONObject customAttributes) {
        if (TextUtils.isEmpty(message))
            throw new AssertionError("Requires a valid message to create chat session.");

        getUserSession().getChatSessionsDataSource().setMessageToCreateNewChatSession(message);

        if (customAttributes != null)
            mDescribeNextConversation(customAttributes);

        showSupport(activity);
    }
    //endregion

    //region Public Methods
    @NonNull
    public static String sdkVersion() {
        return BuildConfig.VERSION_NAME;
    }

    @NonNull
    public static String hostDomain() {
        return hostDomainOverride != null ? hostDomainOverride : KUSConstants.URL.HOST_NAME;
    }

    public static int getLogOptions() {
        return logOptions;
    }

    public static void setLogOptions(int kusLogOptions) {
        logOptions = kusLogOptions;
    }

    public static void setHostDomain(String hostDomain) {
        hostDomainOverride = hostDomain;
    }

    @NonNull
    public static Context getContext() {
        return mContext;
    }

    @NonNull
    public KUSUserSession getUserSession() {
        if (userSession == null)
            throw new AssertionError("Kustomer needs to be initialized before use");

        return userSession;
    }

    //endregion

}
