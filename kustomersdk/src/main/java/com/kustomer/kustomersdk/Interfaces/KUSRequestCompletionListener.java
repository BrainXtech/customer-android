package com.kustomer.kustomersdk.Interfaces;

import android.support.annotation.Nullable;

import org.json.JSONObject;

/**
 * Created by Junaid on 1/29/2018.
 */

public interface KUSRequestCompletionListener{
    void onCompletion(Error error,@Nullable JSONObject response);
}
