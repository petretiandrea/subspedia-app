package com.andreapetreti.subspedia.cache;

import androidx.lifecycle.LiveData;

public class SimpleLiveDataCache<T> {

    private static final int EXPIRING_TIME = 2 * 60 * 1000; // 2 minute

    private long mLastCacheUpdate;

    private LiveData<T> mCache;

    public SimpleLiveDataCache() {}

    public LiveData<T> get() {
        if(isExpired()) {
            mCache = null;
            return null;
        }

        return mCache;
    }

    public void put(LiveData<T> data) {
        mLastCacheUpdate = System.currentTimeMillis();
        mCache = data;
    }

    private boolean isExpired() {
        return System.currentTimeMillis() > (mLastCacheUpdate + EXPIRING_TIME) || mCache == null;
    }

    public void invalidate() {
        mCache = null;
        mLastCacheUpdate = 0;
    }
}
