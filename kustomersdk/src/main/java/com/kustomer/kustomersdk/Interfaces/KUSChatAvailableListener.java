package com.kustomer.kustomersdk.Interfaces;

/**
 * Created by Junaid on 2/27/2018.
 */

public interface KUSChatAvailableListener {
    void onSuccess(boolean enabled);
    void onFailure();
}
