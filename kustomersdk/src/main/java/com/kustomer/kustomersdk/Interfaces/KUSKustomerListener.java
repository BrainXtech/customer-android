package com.kustomer.kustomersdk.Interfaces;

import android.app.PendingIntent;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Junaid on 3/19/2018.
 */

public interface KUSKustomerListener {
    boolean kustomerShouldDisplayInAppNotification();
    @Nullable
    PendingIntent getPendingIntent(@NonNull Context context);
}
