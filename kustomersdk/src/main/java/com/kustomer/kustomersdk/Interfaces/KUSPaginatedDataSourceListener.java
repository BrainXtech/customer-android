package com.kustomer.kustomersdk.Interfaces;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.DataSources.KUSPaginatedDataSource;

/**
 * Created by Junaid on 1/23/2018.
 */

public interface KUSPaginatedDataSourceListener {
    void onLoad(@NonNull KUSPaginatedDataSource dataSource);
    void onError(@NonNull KUSPaginatedDataSource dataSource,@Nullable Error error);
    void onContentChange(@NonNull KUSPaginatedDataSource dataSource);
}
