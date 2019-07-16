package com.kustomer.kustomersdk.DataSources;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.Activities.KUSSessionsActivity;
import com.kustomer.kustomersdk.Interfaces.KUSKustomerListener;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSDelegateProxy {
    //region Properties
    @Nullable
    private KUSKustomerListener listener;
    //endregion

    //region Methods
    public boolean shouldDisplayInAppNotification(){
        if(listener != null)
            return listener.kustomerShouldDisplayInAppNotification();

        return true;
    }

    @Nullable
    public PendingIntent getPendingIntent(@NonNull Context context){
        if(listener != null)
            return listener.getPendingIntent(context);
        else {
            Intent intent = new Intent(context, KUSSessionsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return PendingIntent.getActivity(context, 0, intent, 0);
        }
    }
    //endregion

    //region Accessors & Mutators

    @Nullable
    public KUSKustomerListener getListener() {
        return listener;
    }

    public void setListener(@Nullable KUSKustomerListener listener) {
        this.listener = listener;
    }

    //endregion
}
