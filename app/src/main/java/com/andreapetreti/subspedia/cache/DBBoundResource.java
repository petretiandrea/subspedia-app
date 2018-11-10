package com.andreapetreti.subspedia.cache;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.andreapetreti.subspedia.AppExecutor;
import com.andreapetreti.subspedia.common.Resource;

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
