package com.andreapetreti.subspedia.utils;

import android.content.Context;

import com.andreapetreti.androidcommonutils.picasso.PicassoBuilderExtended;
import com.annimon.stream.Objects;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

public final class PicassoSingleton {

    private static Picasso mInstance;

    public static Picasso getInstance(Context context) {
        if(Objects.nonNull(mInstance)) {
            if(Objects.nonNull(context)) {
                mInstance = new PicassoBuilderExtended(context)
                        .setDiskCacheSize("picasso_disk_cache", 10 * 1024 * 1024) // 10 mb of disk cache.
                        .memoryCache(new LruCache(context))
                        .build();
            }
        }
        return mInstance;
    }

}
