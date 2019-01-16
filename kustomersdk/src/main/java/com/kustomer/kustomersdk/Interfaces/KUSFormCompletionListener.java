package com.kustomer.kustomersdk.Interfaces;

import com.kustomer.kustomersdk.Models.KUSChatSession;
import com.kustomer.kustomersdk.Models.KUSModel;

import java.util.List;

/**
 * Created by Junaid on 2/21/2018.
 */

public interface KUSFormCompletionListener {
    void onComplete(Error error, KUSChatSession chatSession, List<KUSModel> chatMessages);
}
