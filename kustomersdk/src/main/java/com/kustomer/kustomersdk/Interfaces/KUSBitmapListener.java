package com.kustomer.kustomersdk.Interfaces;

import android.support.annotation.NonNull;

public interface KUSBitmapListener {
    void onBitmapCreated();

    void onMemoryError(@NonNull OutOfMemoryError memoryError);
}