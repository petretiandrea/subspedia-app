package com.andreapetreti.subspedia;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutor {

    private Executor mDiskExecutor;

    private Executor mNetworkExecutor;

    private Executor mMainThread;

    private static AppExecutor instance;

    public static AppExecutor getInstance() {
        if(instance == null)
            instance = new AppExecutor();
        return instance;
    }

    private AppExecutor() {
        mDiskExecutor = Executors.newFixedThreadPool(2);
        mNetworkExecutor = Executors.newFixedThreadPool(3);
        mMainThread = new MainThreadExecutor();
    }

    public Executor getDiskExecutor() {
        return mDiskExecutor;
    }

    public Executor getNetworkExecutor() {
        return mNetworkExecutor;
    }

    public Executor getMainThread() {
        return mMainThread;
    }

    private static class MainThreadExecutor implements Executor {

        private Handler mHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mHandler.post(command);
        }
    }

}
