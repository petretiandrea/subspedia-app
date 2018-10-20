package com.andreapetreti.subspedia.cache;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.andreapetreti.subspedia.AppExecutor;
import com.andreapetreti.subspedia.common.ApiResponse;
import com.andreapetreti.subspedia.common.Resource;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 * <p>
 * You can read more about it in the <a href="https://developer.android.com/arch">Architecture
 * Guide</a>.
 * @param <ResultType>
 * @param <RequestType>
 */
public abstract class NetworkBoundResource<ResultType, RequestType> {

    private AppExecutor mAppExecutor;

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    public NetworkBoundResource() {
        mAppExecutor = AppExecutor.getInstance();

        // set null resource
        result.setValue(Resource.loading(null));

        mAppExecutor.getDiskExecutor().execute(() -> {
            // load data from database
            LiveData<ResultType> dbSource = loadFromDb();

            mAppExecutor.getMainThread().execute(() -> result.addSource(dbSource, data -> {
                result.removeSource(dbSource);
                if (shouldFetch(data)) {
                    fetchFromNetwork(dbSource);
                } else {
                    result.addSource(dbSource, newData -> result.setValue(Resource.success(newData)));
                }
            }));
        });
    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource, newData -> result.setValue(Resource.loading(newData)));
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            //noinspection ConstantConditions
            if (response.isSuccessful()) {
                mAppExecutor.getDiskExecutor().execute(() -> {
                    saveCallResult(processResponse(response));
                    mAppExecutor.getMainThread().execute(() ->
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromDb(),
                                    newData -> result.setValue(Resource.success(newData)))
                    );
                });
            } else {
                onFetchFailed();
                result.addSource(dbSource,
                        newData -> result.setValue(Resource.error(response.errorMessage, newData)));
            }
        });
    }

    protected  void onFetchFailed() {
    }

    public  LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    @WorkerThread
    protected  RequestType processResponse(ApiResponse<RequestType> response) {
        return response.body;
    }

    @WorkerThread
    protected abstract  void saveCallResult(@NonNull RequestType item);

    @MainThread
    protected abstract  boolean shouldFetch(@Nullable ResultType data);

    @NonNull
    @WorkerThread
    protected abstract  LiveData<ResultType> loadFromDb();

    @NonNull
    @MainThread
    protected abstract  LiveData<ApiResponse<RequestType>> createCall();
}
