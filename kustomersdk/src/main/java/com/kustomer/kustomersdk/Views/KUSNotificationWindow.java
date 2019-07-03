package com.kustomer.kustomersdk.Views;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;
import com.kustomer.kustomersdk.DataSources.KUSUserDataSource;
import com.kustomer.kustomersdk.Enums.KUSChatMessageType;
import com.kustomer.kustomersdk.Helpers.KSize;
import com.kustomer.kustomersdk.Helpers.KUSDate;
import com.kustomer.kustomersdk.Helpers.KUSImage;
import com.kustomer.kustomersdk.Helpers.KUSLocalization;
import com.kustomer.kustomersdk.Kustomer;
import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.Models.KUSChatSession;
import com.kustomer.kustomersdk.Models.KUSChatSettings;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Models.KUSUser;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.Receivers.NotificationDismissReceiver;
import com.kustomer.kustomersdk.Utils.KUSUtils;

import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.support.v4.app.NotificationCompat.CATEGORY_CALL;
import static com.kustomer.kustomersdk.Utils.KUSConstants.BundleName.NOTIFICATION_ID_BUNDLE_KEY;

/**
 * Created by Junaid on 3/19/2018.
 */

public class KUSNotificationWindow {

    //region Properties
    private static final int FONT_SIZE = 12;
    private static final int IMAGE_SIZE_IN_DP = 40;
    private static final int DISMISS_DURATION_MILLISECOND = 4000;
    private static final String NOTIFICATION_CHANNEL_ID = "Default";

    @Nullable
    private static KUSNotificationWindow notificationWindow;

    @Nullable
    private Context mContext;

    @Nullable
    private KUSUserSession mUserSession;
    @Nullable
    private KUSChatMessagesDataSource chatMessagesDataSource;
    @Nullable
    private KUSChatSession chatSession;

    private static int notificationId;
    //endregion

    //region Initializer
    public static KUSNotificationWindow getSharedInstance() {
        if (notificationWindow == null) {
            notificationWindow = new KUSNotificationWindow();
        }

        return notificationWindow;
    }
    //endregion

    //region Public Methods
    public void showNotification(@NonNull KUSChatSession mChatSession,
                                 @NonNull Context context, final boolean shouldAutoDismiss) {
        clearPreviousNotification();

        mContext = context;
        chatSession = mChatSession;

        //Updating language configuration
        KUSLocalization.getSharedInstance().updateConfig(mContext);

        mUserSession = Kustomer.getSharedInstance().getUserSession();
        chatMessagesDataSource = mUserSession != null ?
                mUserSession.chatMessageDataSourceForSessionId(chatSession.getId())
                : null;

        KUSUserDataSource userDataSource = mUserSession != null && chatMessagesDataSource != null ?
                mUserSession.userDataSourceForUserId(chatMessagesDataSource.getFirstOtherUserId())
                : null;

        KUSUser user = null;
        if (userDataSource != null) {
            user = (KUSUser) userDataSource.getObject();
            if (user == null && !userDataSource.isFetching()) {
                userDataSource.fetch();
            }
        }

        KUSChatSettings chatSettings = mUserSession != null ?
                (KUSChatSettings) mUserSession.getChatSettingsDataSource().getObject()
                : null;
        if (mUserSession != null &&
                chatSettings == null && !this.mUserSession.getChatSettingsDataSource().isFetching()) {
            mUserSession.getChatSettingsDataSource().fetch();
        }

        String name = "";
        if (user != null && user.getDisplayName() != null)
            name = user.getDisplayName();
        else if (chatSettings != null && chatSettings.getTeamName() != null)
            name = chatSettings.getTeamName();
        else if (mUserSession != null && mUserSession.getOrganizationName() != null)
            name = mUserSession.getOrganizationName();

        final Bitmap placeHolderImage = KUSImage.defaultAvatarBitmapForName(mContext,
                new KSize((int) KUSUtils.dipToPixels(mContext, IMAGE_SIZE_IN_DP),
                        (int) KUSUtils.dipToPixels(mContext, IMAGE_SIZE_IN_DP)),
                name,
                0,
                FONT_SIZE);


        if (chatSettings == null)
            return;

        URL iconURL = user != null && user.getAvatarURL() != null ? user.getAvatarURL() : chatSettings.getTeamIconURL();

        if (iconURL != null) {
            try {
                Glide.with(mContext)
                        .asBitmap()
                        .load(iconURL.toString())
                        .circleCrop()
                        .dontAnimate()
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Bitmap> target, boolean isFirstResource) {
                                displayNotification(placeHolderImage, shouldAutoDismiss);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model,
                                                           Target<Bitmap> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                displayNotification(resource, shouldAutoDismiss);
                                return false;
                            }
                        })
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource,
                                                        @Nullable Transition<? super Bitmap> transition) {

                            }
                        });

            } catch (IllegalArgumentException e) {
                Log.d("Kustomer", e.getMessage());
            }
        } else {
            displayNotification(placeHolderImage, shouldAutoDismiss);
        }

    }
    //endregion

    //region Private Methods
    private void clearPreviousNotification() {
        NotificationManagerCompat.from(Kustomer.getContext()).cancel(notificationId);
    }

    private void createNotificationChannelForOreoAndAbove() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            CharSequence name = "Message Notification";
            String description = "Shows messages received from support";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mContext != null ?
                    (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE)
                    : null;

            if (notificationManager != null)
                notificationManager.createNotificationChannel(mChannel);
        }
    }

    @Nullable
    private String getResponderName() {
        if (mUserSession == null)
            return null;

        KUSUserDataSource userDataSource = chatMessagesDataSource != null ?
                mUserSession.userDataSourceForUserId(chatMessagesDataSource.getFirstOtherUserId())
                : null;

        KUSUser firstOtherUser = userDataSource != null ? (KUSUser) userDataSource.getObject() : null;

        String responderName = firstOtherUser != null ? firstOtherUser.getDisplayName() : null;

        if (responderName == null || responderName.length() == 0) {
            KUSChatSettings chatSettings = (KUSChatSettings) mUserSession.getChatSettingsDataSource().getObject();

            responderName = chatSettings != null && !TextUtils.isEmpty(chatSettings.getTeamName()) ?
                    chatSettings.getTeamName() : mUserSession.getOrganizationName();
        }

        return responderName;
    }

    @Nullable
    private KUSChatMessage getlatestMessage() {
        if (chatMessagesDataSource == null)
            return null;

        for (KUSModel model : chatMessagesDataSource.getList()) {
            KUSChatMessage message = (KUSChatMessage) model;
            if (message.getType() == KUSChatMessageType.KUS_CHAT_MESSAGE_TYPE_TEXT) {
                return message;
            }
        }

        return null;
    }

    @Nullable
    private String getSubtitleText() {
        //Subtitle text (from last message, or preview text)
        KUSChatMessage latestTextMessage = getlatestMessage();

        String subtitleText = null;
        if (latestTextMessage != null) {
            subtitleText = latestTextMessage.getBody().isEmpty() ?
                    latestTextMessage.getBody()
                    : chatSession != null ? chatSession.getPreview() : null;
        }

        return subtitleText;
    }

    @Nullable
    private Date getDate() {
        //Date text (from last message date, or session created at)
        KUSChatMessage latestTextMessage = getlatestMessage();

        Date sessionDate = null;
        if (latestTextMessage != null) {
            sessionDate = latestTextMessage.getCreatedAt() != null ?
                    latestTextMessage.getCreatedAt()
                    : chatSession != null ? chatSession.getCreatedAt() : null;
        }

        return sessionDate;
    }

    private int getUnreadCount() {
        if (mUserSession == null || chatSession == null || chatMessagesDataSource == null)
            return 0;

        Date sessionLastSeenAt = mUserSession.getChatSessionsDataSource().lastSeenAtForSessionId(chatSession.getId());
        return chatMessagesDataSource.unreadCountAfterDate(sessionLastSeenAt);
    }

    private void displayNotification(@NonNull Bitmap bitmap, boolean shouldAutoDismiss) {
        notificationId = new Random().nextInt();
        createNotificationChannelForOreoAndAbove();

        //Title text (from last responder, chat settings or organization name)
        String responderName = getResponderName();

        //Subtitle text (from last message, or preview text)
        String subtitleText = getSubtitleText();

        // Create an explicit intent for an Activity in your app
        PendingIntent pendingIntent = mContext != null ?
                Kustomer.getSharedInstance().getUserSession().getDelegateProxy().getPendingIntent(mContext)
                : null;

        PendingIntent dummyIntent = PendingIntent.getActivity(mContext, 0, new Intent(), 0);
        //Create Sound Uri
        Uri soundUri = Uri.parse(String.format(Locale.getDefault(),
                "android.resource://%s/%d", mContext.getPackageName(), R.raw.kus_message_received));

        RemoteViews view = createNotificationView(
                bitmap,
                String.format(mContext.getString(R.string.com_kustomer_chat_with) + " %s", responderName),
                subtitleText, getDate(), getUnreadCount());

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.color.transparent)
                .setContent(view)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(new long[0])
                .setSound(soundUri)
                .setFullScreenIntent(dummyIntent, true)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setCategory(CATEGORY_CALL)
                .setOngoing(true);

        //Add dismiss button in case of persistent notification
        if (!shouldAutoDismiss) {

            RemoteViews expandedView = createExpandedNotificationView(
                    bitmap,
                    String.format(mContext.getString(R.string.com_kustomer_chat_with) + " %s", responderName),
                    subtitleText, getDate(), getUnreadCount());

            Intent dismiss = new Intent(mContext, NotificationDismissReceiver.class);
            dismiss.putExtra(NOTIFICATION_ID_BUNDLE_KEY, notificationId);

            PendingIntent pIntentNegative = PendingIntent.getBroadcast(mContext, notificationId,
                    dismiss, PendingIntent.FLAG_CANCEL_CURRENT);

            if (expandedView != null)
                expandedView.setOnClickPendingIntent(R.id.closeButton, pIntentNegative);
            mBuilder.setCustomBigContentView(expandedView);
        }

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(notificationId, mBuilder.build());

        if (shouldAutoDismiss) {
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    notificationManager.cancel(notificationId);
                }
            };
            handler.postDelayed(runnable, DISMISS_DURATION_MILLISECOND);
        }

    }

    @Nullable
    private RemoteViews createNotificationView(@NonNull Bitmap image,
                                               @NonNull String title,
                                               @Nullable String subtitle,
                                               @Nullable Date date, int unreadCount) {
        if (mContext == null)
            return null;

        RemoteViews contentView;
        if (KUSLocalization.getSharedInstance().isLTR())
            contentView = new RemoteViews(mContext.getPackageName(), R.layout.kus_item_notification_ltr);
        else
            contentView = new RemoteViews(mContext.getPackageName(), R.layout.kus_item_notification_rtl);

        if (unreadCount < 1)
            unreadCount = 1;

        contentView.setImageViewBitmap(R.id.avatarImage, image);
        contentView.setTextViewText(R.id.tvNotificationTitle, title);
        contentView.setTextViewText(R.id.tvNotificationSubtitle, subtitle);
        contentView.setTextViewText(R.id.tvNotificationDate,
                KUSDate.humanReadableTextFromDate(Kustomer.getContext(), date));
        contentView.setTextViewText(R.id.tvUnreadCount, String.valueOf(unreadCount));

        return contentView;

    }

    @Nullable
    private RemoteViews createExpandedNotificationView(@NonNull Bitmap image,
                                                       @NonNull String title,
                                                       @Nullable String subtitle,
                                                       @Nullable Date date, int unreadCount) {
        if (mContext == null)
            return null;

        RemoteViews contentView;

        if (KUSLocalization.getSharedInstance().isLTR())
            contentView = new RemoteViews(mContext.getPackageName(), R.layout.kus_item_notification_expanded_ltr);
        else
            contentView = new RemoteViews(mContext.getPackageName(), R.layout.kus_item_notification_expanded_rtl);

        if (unreadCount < 1)
            unreadCount = 1;

        contentView.setImageViewBitmap(R.id.avatarImage, image);
        contentView.setTextViewText(R.id.tvNotificationTitle, title);
        contentView.setTextViewText(R.id.tvNotificationSubtitle, subtitle);
        contentView.setTextViewText(R.id.tvNotificationDate,
                KUSDate.humanReadableTextFromDate(Kustomer.getContext(), date));
        contentView.setTextViewText(R.id.tvUnreadCount, String.valueOf(unreadCount));
        contentView.setTextViewText(R.id.closeButton, mContext.getResources().getString(R.string.com_kustomer_dismiss));

        return contentView;

    }
    //endregion
}
