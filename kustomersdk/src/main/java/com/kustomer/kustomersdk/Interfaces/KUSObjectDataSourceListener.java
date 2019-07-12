package com.kustomer.kustomersdk.Interfaces;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.DataSources.KUSObjectDataSource;

/**
 * Created by Junaid on 1/29/2018.
 */

public interface KUSObjectDataSourceListener {
    void objectDataSourceOnLoad(@NonNull KUSObjectDataSource dataSource);
    void objectDataSourceOnError(@NonNull KUSObjectDataSource dataSource,@Nullable Error error);
}
