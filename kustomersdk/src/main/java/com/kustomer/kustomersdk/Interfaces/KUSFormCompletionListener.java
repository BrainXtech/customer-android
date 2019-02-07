package com.kustomer.kustomersdk.Interfaces;

import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.Models.KUSChatSession;
import com.kustomer.kustomersdk.Models.KUSModel;

import java.util.List;

/**
 * Created by Junaid on 2/21/2018.
 */

public interface KUSFormCompletionListener {
    void onComplete(@Nullable Error error, @Nullable KUSChatSession chatSession,
                    @Nullable List<KUSModel> chatMessages);
}
