package com.kustomer.kustomersdk.Views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.Utils.KUSUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junaid on 1/30/2018.
 */

public class KUSMultipleAvatarsView extends FrameLayout {

    //region Properties
    private static final int STROKE_WIDTH_IN_DP = 3;
    private static final int AVATAR_SIZE_IN_DP = 35;

    private int maximumAvatarsToDisplay;
    @NonNull
    private ArrayList<KUSAvatarImageView> avatarImageViews;
    @Nullable
    private List<String> userIds;
    @Nullable
    KUSUserSession userSession;
    //endregion

    //region Initializer
    public KUSMultipleAvatarsView(@NonNull Context context) {
        super(context);
        avatarImageViews = new ArrayList<>();
    }

    public KUSMultipleAvatarsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        avatarImageViews = new ArrayList<>();
    }

    public KUSMultipleAvatarsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        avatarImageViews = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public KUSMultipleAvatarsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        avatarImageViews = new ArrayList<>();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setMaximumAvatarsToDisplay(getResources().getInteger(R.integer.kusMaximumAvatarsToDisplay));
    }

    //endregion

    //region Public Methods
    public void initWithUserSession(@Nullable KUSUserSession userSession) {
        this.userSession = userSession;
        rebuildAvatarViews();
    }

    public void setUserIds(@NonNull List<String> userIds) {
        if (this.userIds != null && this.userIds.equals(userIds))
            return;

        this.userIds = userIds;
        rebuildAvatarViews();
    }
    //endregion

    //region Private Methods
    void setMaximumAvatarsToDisplay(int maximumAvatarsToDisplay) {
        this.maximumAvatarsToDisplay = Math.max(maximumAvatarsToDisplay, 1);
        rebuildAvatarViews();
    }

    void rebuildAvatarViews() {

        int marginCounter = 0;
        if (userSession == null)
            return;

        for (KUSAvatarImageView avatarImageView : avatarImageViews)
            removeView(avatarImageView);

        ArrayList<KUSAvatarImageView> avatarImageViews = new ArrayList<>();


        if (userIds != null) {
            for (int i = 0; i < Math.min(userIds.size(), maximumAvatarsToDisplay); i++) {
                String userId = userIds.get(i);
                KUSAvatarImageView userAvatarView = new KUSAvatarImageView(getContext());
                userAvatarView.setStrokeWidth(STROKE_WIDTH_IN_DP);
                userAvatarView.initWithUserSession(userSession);
                userAvatarView.setUserId(userId);
                avatarImageViews.add(userAvatarView);
            }
        }

        if (avatarImageViews.size() < maximumAvatarsToDisplay) {
            KUSAvatarImageView companyAvatarView = new KUSAvatarImageView(getContext());
            companyAvatarView.setStrokeWidth(STROKE_WIDTH_IN_DP);
            companyAvatarView.initWithUserSession(userSession);
            avatarImageViews.add(companyAvatarView);
        }

        for (int i = avatarImageViews.size() - 1; i >= 0; i--) {
            KUSAvatarImageView avatarImageView = avatarImageViews.get(i);
            LayoutParams avatarLayoutParams = new LayoutParams(
                    (int) KUSUtils.dipToPixels(getContext(), AVATAR_SIZE_IN_DP),
                    (int) KUSUtils.dipToPixels(getContext(), AVATAR_SIZE_IN_DP), Gravity.END);

            avatarLayoutParams.setMarginEnd((int) KUSUtils.dipToPixels(getContext(),
                    AVATAR_SIZE_IN_DP / 2) * marginCounter);

            avatarImageView.setLayoutParams(avatarLayoutParams);
            addView(avatarImageView);

            marginCounter++;
        }

        this.avatarImageViews = avatarImageViews;
    }
    //endregion
}
