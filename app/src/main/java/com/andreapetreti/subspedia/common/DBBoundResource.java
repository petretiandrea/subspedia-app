package com.andreapetreti.subspedia.common;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.andreapetreti.subspedia.AppExecutor;

public abstract class DBBoundResource<T> {

    private AppExecutor mAppExecutor;
    private MediatorLiveData<Resource<T>> mResult;

    @MainThread
    public DBBoundResource() {
        mAppExecutor = AppExecutor.getInstance();
        mResult = new MediatorLiveData<>();
        init();
    }

    private void init() {
        mResult.setValue(Resource.loading(null));

        mAppExecutor.getDiskExecutor().execute(() -> {
            // load from database
            LiveData<T> dbSource = loadFromDb();
            mAppExecutor.getMainThread().execute(() -> {
                mResult.removeSource(dbSource);
                mResult.addSource(dbSource, newData -> mResult.setValue(Resource.success(newData)));
            });
        });
    }

    @NonNull
    @WorkerThread
    protected abstract LiveData<T> loadFromDb();

    public LiveData<Resource<T>> asLiveData() {return mResult;}

    public void forceRefresh() {
        init();
    }

}
