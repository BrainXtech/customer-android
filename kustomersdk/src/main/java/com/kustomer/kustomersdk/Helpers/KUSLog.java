package com.kustomer.kustomersdk.Helpers;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kustomer.kustomersdk.Interfaces.KUSLogOptions;
import com.kustomer.kustomersdk.Kustomer;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSLog {

    //region Private Methods
    private static void kusLogMessage(int req, @NonNull String value){
        if((Kustomer.getLogOptions() & req) > 0){
            Log.d("Kustomer",value);
        }
    }
    //endregion

    //region Public Methods
    public static void kusLogInfo(@NonNull String info){
        kusLogMessage(KUSLogOptions.KUSLogOptionInfo,info);
    }

    public static void kusLogError(@NonNull String info){
        kusLogMessage(KUSLogOptions.KUSLogOptionErrors,info);
    }

    public static void kusLogRequest(@NonNull String info){
        kusLogMessage(KUSLogOptions.KUSLogOptionRequests,info);
    }

    public static void kusLogPusher(@NonNull String info){
        kusLogMessage(KUSLogOptions.KUSLogOptionPusher,info);
    }

    public static void kusLogPusherError(@NonNull String info){
        kusLogMessage(KUSLogOptions.KUSLogOptionPusher | KUSLogOptions.KUSLogOptionErrors,info);
    }
    //endregion

}
