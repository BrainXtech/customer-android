package com.kustomer.kustomersdk.Interfaces;

import android.app.PendingIntent;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Junaid on 3/19/2018.
 */

public interface KUSKustomerListener {
    boolean kustomerShouldDisplayInAppNotification();
    @NonNull PendingIntent getPendingIntent(@NonNull Context context);
}
