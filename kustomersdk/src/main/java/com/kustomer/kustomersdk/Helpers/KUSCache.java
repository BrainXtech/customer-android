package com.kustomer.kustomersdk.Helpers;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LruCache;

/**
 * Created by Junaid on 2/16/2018.
 */

public class KUSCache {

    private static LruCache<String,Bitmap> mMemoryCache;

    public KUSCache(){

        if(mMemoryCache == null) {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;

            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getByteCount() / 1024;
                }
            };
        }

    }

    public void addBitmapToMemoryCache(@NonNull String key,@NonNull Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(@Nullable String key) {
        if(key == null)
            return null;
        return mMemoryCache.get(key);
    }

    public void removeBitmapFromMemCache(@Nullable String key){
        if(key != null)
            mMemoryCache.remove(key);
    }

}
