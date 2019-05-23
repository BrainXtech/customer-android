package com.kustomer.kustomersdk.Helpers;

import android.support.annotation.NonNull;

/**
 * Created by Junaid on 1/26/2018.
 */

public class KUSInvalidJsonException extends Exception {
    public KUSInvalidJsonException(@NonNull String message) {
        super(message);
    }
}
