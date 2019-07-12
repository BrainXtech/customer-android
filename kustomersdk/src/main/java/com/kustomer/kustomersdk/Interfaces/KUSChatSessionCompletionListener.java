package com.kustomer.kustomersdk.Interfaces;

import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.Models.KUSChatSession;

/**
 * Created by Junaid on 2/12/2018.
 */

public interface KUSChatSessionCompletionListener {
    void onComplete(@Nullable Error error,@Nullable KUSChatSession session);
}
