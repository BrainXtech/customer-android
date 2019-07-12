package com.kustomer.kustomersdk.Views;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kustomer.kustomersdk.API.KUSSessionQueuePollingManager;
import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;
import com.kustomer.kustomersdk.DataSources.KUSObjectDataSource;
import com.kustomer.kustomersdk.DataSources.KUSPaginatedDataSource;
import com.kustomer.kustomersdk.DataSources.KUSUserDataSource;
import com.kustomer.kustomersdk.Helpers.KUSDate;
import com.kustomer.kustomersdk.Interfaces.KUSChatMessagesDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSObjectDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSSessionQueuePollingListener;
import com.kustomer.kustomersdk.Models.KUSChatSettings;
import com.kustomer.kustomersdk.Models.KUSSessionQueue;
import com.kustomer.kustomersdk.Models.KUSTypingIndicator;
import com.kustomer.kustomersdk.Models.KUSUser;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.Utils.KUSUtils;

/**
 * Created by Junaid on 1/30/2018.
 */

public class KUSToolbar extends Toolbar implements KUSObjectDataSourceListener,
        KUSChatMessagesDataSourceListener,
        KUSSessionQueuePollingListener {
    //region Properties
    @Nullable
    private String sessionId;
    private boolean showLabel;
    private boolean showBackButton;
    private boolean showDismissButton;
    private boolean extraLargeSize;
    @Nullable
    private String waitingMessage;

    @Nullable
    KUSUserSession mUserSession;
    @Nullable
    KUSChatMessagesDataSource chatMessagesDataSource;
    @Nullable
    KUSUserDataSource userDataSource;

    @NonNull
    TextView tvName;
    @NonNull
    TextView tvWaiting;
    @NonNull
    TextView tvGreetingMessage;
    @NonNull
    TextView tvToolbarUnreadCount;
    @NonNull
    KUSMultipleAvatarsView kusMultipleAvatarsView;
    @NonNull
    View ivBack;
    @NonNull
    View ivClose;
    @NonNull
    ViewGroup toolbarInnerLayout;
    @Nullable
    OnToolbarItemClickListener listener;
    //endregion

    //region Initializer
    public KUSToolbar(@NonNull Context context) {
        super(context);
    }

    public KUSToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KUSToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
        setListeners();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mUserSession != null)
            mUserSession.getChatSettingsDataSource().removeListener(this);

        if (mUserSession != null)
            mUserSession.getChatSessionsDataSource().removeListener(this);

        if (userDataSource != null)
            userDataSource.removeListener(this);

        if (chatMessagesDataSource != null) {
            if (chatMessagesDataSource.getSessionQueuePollingManager() != null)
                chatMessagesDataSource.getSessionQueuePollingManager().removeListener(this);

            chatMessagesDataSource.removeListener(this);
        }
    }

    //endregion

    //region Public Methods
    public void initWithUserSession(@Nullable KUSUserSession userSession) {
        mUserSession = userSession;
        kusMultipleAvatarsView.initWithUserSession(userSession);

        if (mUserSession != null) {
            mUserSession.getChatSettingsDataSource().addListener(this);
            mUserSession.getChatSessionsDataSource().addListener(this);
            mUserSession.getScheduleDataSource().addListener(this);
        }
        updateTextLabel();
        updateBackButtonBadge();
    }
    //endregion

    //region Private Methods
    private void initViews() {
        tvName = findViewById(R.id.tvName);
        tvWaiting = findViewById(R.id.tvWaiting);
        tvGreetingMessage = findViewById(R.id.tvGreetingMessage);
        kusMultipleAvatarsView = findViewById(R.id.multipleAvatarViews);
        ivBack = findViewById(R.id.ivBack);
        ivClose = findViewById(R.id.ivClose);
        tvToolbarUnreadCount = findViewById(R.id.tvToolbarUnreadCount);
        toolbarInnerLayout = findViewById(R.id.toolbarInnerLayout);

        if (showLabel) {
            tvName.setVisibility(VISIBLE);
            tvGreetingMessage.setVisibility(VISIBLE);
            tvWaiting.setVisibility(VISIBLE);
        } else {
            tvName.setVisibility(GONE);
            tvGreetingMessage.setVisibility(GONE);
            tvWaiting.setVisibility(GONE);
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams avatarlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        avatarlp.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams vlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        vlp.gravity = Gravity.CENTER;

        if (extraLargeSize) {
            tvName.setTextSize(15f);
            tvGreetingMessage.setTextSize(13f);
            tvWaiting.setTextSize(13f);

            lp.setMargins(0, (int) KUSUtils.dipToPixels(getContext(), 40),
                    0, (int) KUSUtils.dipToPixels(getContext(), 40));

            avatarlp.setMargins(0, (int) KUSUtils.dipToPixels(getContext(), 4),
                    0, (int) KUSUtils.dipToPixels(getContext(), 4));

            vlp.setMargins((int) KUSUtils.dipToPixels(getContext(), 4),
                    (int) KUSUtils.dipToPixels(getContext(), 4),
                    (int) KUSUtils.dipToPixels(getContext(), 4),
                    (int) KUSUtils.dipToPixels(getContext(), 4));

            kusMultipleAvatarsView.setLayoutParams(avatarlp);
            tvName.setLayoutParams(vlp);
            tvGreetingMessage.setLayoutParams(vlp);
            tvWaiting.setLayoutParams(vlp);
            toolbarInnerLayout.setLayoutParams(lp);

            tvGreetingMessage.setVisibility(VISIBLE);

            if (mUserSession != null) {
                KUSChatSettings chatSettings = (KUSChatSettings) mUserSession.getChatSettingsDataSource().getObject();
                int isVCGreetingVisible = chatSettings != null && chatSettings.isVolumeControlEnabled() ? VISIBLE : GONE;
                tvWaiting.setVisibility(isVCGreetingVisible);
            }

        } else {
            tvName.setTextSize(13f);
            tvGreetingMessage.setTextSize(11f);
            tvWaiting.setTextSize(11f);

            lp.setMargins(0, (int) KUSUtils.dipToPixels(getContext(), 10),
                    0, (int) KUSUtils.dipToPixels(getContext(), 5));

            avatarlp.setMargins(0, (int) KUSUtils.dipToPixels(getContext(), 2),
                    0, (int) KUSUtils.dipToPixels(getContext(), 2));

            vlp.setMargins((int) KUSUtils.dipToPixels(getContext(), 4), 0,
                    (int) KUSUtils.dipToPixels(getContext(), 4), 0);

            kusMultipleAvatarsView.setLayoutParams(avatarlp);
            tvName.setLayoutParams(vlp);
            tvGreetingMessage.setLayoutParams(vlp);
            tvWaiting.setLayoutParams(vlp);
            toolbarInnerLayout.setLayoutParams(lp);

            tvGreetingMessage.setVisibility(VISIBLE);
            tvWaiting.setVisibility(GONE);
        }

        updateTextLabel();
    }

    private void setListeners() {
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onToolbarBackPressed();
            }
        });

        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onToolbarClosePressed();
            }
        });
    }

    private void updateTextLabel() {
        if (userDataSource != null) {
            userDataSource.removeListener(this);
        }

        if (chatMessagesDataSource != null && mUserSession != null)
            userDataSource = mUserSession.userDataSourceForUserId(chatMessagesDataSource.getFirstOtherUserId());

        KUSUser firstOtherUser = null;
        if (userDataSource != null) {
            userDataSource.addListener(this);
            firstOtherUser = (KUSUser) userDataSource.getObject();
        }

        String responderName = "";
        if (firstOtherUser != null) {
            responderName = firstOtherUser.getDisplayName();
        }


        if (mUserSession == null)
            return;

        KUSChatSettings chatSettings = (KUSChatSettings) mUserSession.getChatSettingsDataSource().getObject();

        if (chatSettings == null) {
            return;
        }

        if (responderName == null || responderName.length() == 0) {
            if (!TextUtils.isEmpty(chatSettings.getTeamName()))
                responderName = chatSettings.getTeamName();
            else
                responderName = mUserSession.getOrganizationName();
        }
        tvName.setText(responderName);


        String waitMessage;

        if (waitingMessage != null) {
            waitMessage = waitingMessage;
        } else if (chatSettings.getUseDynamicWaitMessage()) {
            waitMessage = chatSettings.getWaitMessage();
        } else {
            waitMessage = chatSettings.getCustomWaitMessage();
        }

        if (extraLargeSize) {
            if (mUserSession.getScheduleDataSource().isActiveBusinessHours()) {
                tvGreetingMessage.setText(chatSettings.getGreeting());
            } else {
                tvGreetingMessage.setText(chatSettings.getOffHoursMessage());
            }

            tvWaiting.setText(waitMessage);

        } else {
            if (!mUserSession.getScheduleDataSource().isActiveBusinessHours()) {
                tvGreetingMessage.setText(chatSettings.getOffHoursMessage());
            } else if (chatSettings.getVolumeControlEnabled()) {
                tvGreetingMessage.setText(waitMessage);
            } else {
                tvGreetingMessage.setText(chatSettings.getGreeting());
            }
        }

    }

    private void updateBackButtonBadge() {
        int unreadCount = 0;
        if (mUserSession != null)
            unreadCount = mUserSession.getChatSessionsDataSource().totalUnreadCountExcludingSessionId(sessionId);

        if (unreadCount > 0) {
            tvToolbarUnreadCount.setText(String.valueOf(unreadCount));
            tvToolbarUnreadCount.setVisibility(VISIBLE);
        } else {
            tvToolbarUnreadCount.setVisibility(INVISIBLE);
        }
    }
    //endregion

    //region Accessors & Mutators
    @Nullable
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        if (this.sessionId != null && this.sessionId.equals(sessionId))
            return;

        if (sessionId == null)
            return;

        this.sessionId = sessionId;


        if (chatMessagesDataSource != null) {

            if (chatMessagesDataSource.getSessionQueuePollingManager() != null)
                chatMessagesDataSource.getSessionQueuePollingManager().removeListener(this);

            chatMessagesDataSource.removeListener(this);
        }

        chatMessagesDataSource = mUserSession != null ?
                mUserSession.getChatMessagesDataSources().get(sessionId)
                : null;
        if (chatMessagesDataSource != null) {
            chatMessagesDataSource.addListener(this);

            if (chatMessagesDataSource.getSessionQueuePollingManager() != null)
                chatMessagesDataSource.getSessionQueuePollingManager().addListener(this);

            kusMultipleAvatarsView.setUserIds(chatMessagesDataSource.getOtherUserIds());

            boolean isVolumeControlPollingActive = chatMessagesDataSource.getSessionQueuePollingManager() != null
                    && chatMessagesDataSource.getSessionQueuePollingManager().getPollingStarted()
                    && !chatMessagesDataSource.getSessionQueuePollingManager().getPollingCanceled();

            if (isVolumeControlPollingActive) {
                KUSSessionQueue sessionQueue = chatMessagesDataSource.getSessionQueuePollingManager().getSessionQueue();

                if (sessionQueue != null)
                    waitingMessage = KUSDate.humanReadableUpfrontVCWaitingTimeFromSeconds(
                            getContext(), sessionQueue.getEstimatedWaitTimeSeconds());
            }
        }

        updateTextLabel();
        updateBackButtonBadge();
    }

    public boolean isShowLabel() {
        return showLabel;
    }

    public void setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
        initViews();
    }

    public boolean isShowBackButton() {
        return showBackButton;
    }

    public void setShowBackButton(boolean showBackButton) {
        this.showBackButton = showBackButton;

        if (showBackButton)
            ivBack.setVisibility(VISIBLE);
        else
            ivBack.setVisibility(INVISIBLE);
    }

    public boolean isShowDismissButton() {
        return showDismissButton;
    }

    public void setShowDismissButton(boolean showDismissButton) {
        this.showDismissButton = showDismissButton;
    }

    public boolean isExtraLargeSize() {
        return extraLargeSize;
    }

    public void setExtraLargeSize(boolean extraLargeSize) {
        this.extraLargeSize = extraLargeSize;

        initViews();
    }

    public void setListener(@Nullable OnToolbarItemClickListener listener) {
        this.listener = listener;
    }

    //endregion

    //region Listeners
    @Override
    public void objectDataSourceOnLoad(@NonNull KUSObjectDataSource dataSource) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateTextLabel();
            }
        };
        handler.post(runnable);
    }

    @Override
    public void objectDataSourceOnError(@NonNull KUSObjectDataSource dataSource, @Nullable Error error) {

    }

    @Override
    public void onLoad(@NonNull KUSPaginatedDataSource dataSource) {

    }

    @Override
    public void onError(@NonNull KUSPaginatedDataSource dataSource, @Nullable Error error) {

    }

    @Override
    public void onContentChange(@NonNull final KUSPaginatedDataSource dataSource) {

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dataSource == chatMessagesDataSource && chatMessagesDataSource != null) {
                    kusMultipleAvatarsView.setUserIds(chatMessagesDataSource.getOtherUserIds());
                    updateTextLabel();
                } else if (mUserSession != null && dataSource == mUserSession.getChatSessionsDataSource()) {
                    updateBackButtonBadge();
                }
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onCreateSessionId(@NonNull KUSChatMessagesDataSource source, @NonNull final String mSessionId) {


        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mSessionId.equals(sessionId))
                    return;

                sessionId = mSessionId;
                if (chatMessagesDataSource != null)
                    chatMessagesDataSource.removeListener(KUSToolbar.this);

                if (mUserSession != null) {
                    chatMessagesDataSource = mUserSession.chatMessageDataSourceForSessionId(sessionId);
                    if (chatMessagesDataSource != null) {
                        chatMessagesDataSource.addListener(KUSToolbar.this);
                        kusMultipleAvatarsView.setUserIds(chatMessagesDataSource.getOtherUserIds());
                    }
                }

                updateTextLabel();
                updateBackButtonBadge();

            }
        };
        handler.post(runnable);
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
    public void onPollingStarted(@NonNull KUSSessionQueuePollingManager manager) {

    }

    @Override
    public void onSessionQueueUpdated(@NonNull KUSSessionQueuePollingManager manager, @NonNull KUSSessionQueue sessionQueue) {
        waitingMessage = KUSDate.humanReadableUpfrontVCWaitingTimeFromSeconds(
                getContext(), sessionQueue.getEstimatedWaitTimeSeconds());

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateTextLabel();

            }
        };
        handler.post(runnable);
    }

    @Override
    public void onPollingEnd(@NonNull KUSSessionQueuePollingManager manager) {
    }

    @Override
    public void onPollingCanceled(@NonNull KUSSessionQueuePollingManager manager) {
        waitingMessage = null;

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateTextLabel();

            }
        };
        handler.post(runnable);
    }

    @Override
    public void onFailure(@Nullable Error error, @NonNull KUSSessionQueuePollingManager manager) {

    }
    //endregion

    //region Interface
    public interface OnToolbarItemClickListener {
        void onToolbarBackPressed();

        void onToolbarClosePressed();
    }
    //endregion

}
