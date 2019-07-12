package com.kustomer.kustomersdk.Interfaces;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.API.KUSSessionQueuePollingManager;
import com.kustomer.kustomersdk.Models.KUSSessionQueue;

public interface KUSSessionQueuePollingListener {
    void onPollingStarted(@NonNull KUSSessionQueuePollingManager manager);
    void onSessionQueueUpdated(@NonNull KUSSessionQueuePollingManager manager,
                               @NonNull KUSSessionQueue sessionQueue);
    void onPollingEnd(@NonNull KUSSessionQueuePollingManager manager);
    void onPollingCanceled(@NonNull KUSSessionQueuePollingManager manager);
    void onFailure(@Nullable Error error, @NonNull KUSSessionQueuePollingManager manager);
}
