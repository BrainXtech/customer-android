package com.kustomer.kustomersdk.Models;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by Junaid on 3/22/2018.
 */

public class KUSMessageRetry extends KUSRetry {

    //region Properties
    @Nullable
    private List<KUSModel> temporaryMessages;
    @Nullable
    private List<Bitmap> attachments;
    @Nullable
    private String text;
    @Nullable
    private List<String> cachedImages;
    //endregion

    //region LifeCycle
    public KUSMessageRetry(@Nullable List<KUSModel> temporaryMessages, @Nullable List<Bitmap> attachments,
                           @Nullable String text, @Nullable List<String> cachedImages) {

        this.temporaryMessages = temporaryMessages;
        this.attachments = attachments;
        this.text = text;
        this.cachedImages = cachedImages;

    }
    //endregion

    //region Getters

    @Nullable
    public List<KUSModel> getTemporaryMessages() {
        return temporaryMessages;
    }

    @Nullable
    public List<Bitmap> getAttachments() {
        return attachments;
    }

    @Nullable
    public String getText() {
        return text;
    }

    @Nullable
    public List<String> getCachedImages() {
        return cachedImages;
    }

    //endregion
}
