package com.kustomer.kustomersdk.DataSources;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.kustomer.kustomersdk.Activities.KUSSessionsActivity;
import com.kustomer.kustomersdk.Interfaces.KUSKustomerListener;
import com.kustomer.kustomersdk.Kustomer;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSDelegateProxy {
    //region Properties
    private KUSKustomerListener listener;
    //endregion

    //region Methods
    public boolean shouldDisplayInAppNotification(){
        if(listener != null)
            return listener.kustomerShouldDisplayInAppNotification();

        return true;
    }

    public PendingIntent getPendingIntent(Context context){
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

    public KUSKustomerListener getListener() {
        return listener;
    }

    public void setListener(KUSKustomerListener listener) {
        this.listener = listener;
    }

    //endregion
}
