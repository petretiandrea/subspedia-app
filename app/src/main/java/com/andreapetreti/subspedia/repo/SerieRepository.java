package com.andreapetreti.subspedia.repo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.andreapetreti.subspedia.common.ApiResponse;
import com.andreapetreti.subspedia.common.NetworkBoundResource;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.common.SubspediaService;
import com.andreapetreti.subspedia.database.SerieDao;
import com.andreapetreti.subspedia.database.SubsDatabase;
import com.andreapetreti.subspedia.model.Serie;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class SerieRepository {

    private static final String TAG = SerieRepository.class.getName();

    /**
     * Dao for manage Series inside database.
     */
    private SerieDao mSerieDao;

    /**
     * Web service for access to network resources.
     */
    private SubspediaService mSubspediaService;

    public SerieRepository(Context context) {
        SubsDatabase db = SubsDatabase.getDatabase(context);
        mSerieDao = db.serieDao();
        mSubspediaService = SubspediaService.Provider.getInstance();
    }

    public LiveData<Resource<List<Serie>>> getAllSeries() {
        return new NetworkBoundResource<List<Serie>, List<Serie>>() {

            @Override
            protected void saveCallResult(@NonNull List<Serie> item) {
                for (Serie s : item)
                    mSerieDao.save(s);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Serie> data) {
                return data == null || data.size() == 0;
            }

            @NonNull
            @Override
            protected LiveData<List<Serie>> loadFromDb() {
                return mSerieDao.getAllSeries();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Serie>>> createCall() {
                return new LiveData<ApiResponse<List<Serie>>>() {
                    boolean alreadyCalled = false;
                    @Override
                    protected void onActive() {
                        super.onActive();
                        synchronized (this) {
                            if (!alreadyCalled) {
                                alreadyCalled = true;
                                mSubspediaService.getAllSeries().enqueue(new Callback<List<Serie>>() {
                                    @Override
                                    public void onResponse(Call<List<Serie>> call, Response<List<Serie>> response) {
                                        postValue(new ApiResponse<>(response));
                                    }

                                    @Override
                                    public void onFailure(Call<List<Serie>> call, Throwable t) {
                                        postValue(new ApiResponse<>(t));
                                    }
                                });
                            }
                        }
                    }
                };
            }
        }.asLiveData();
    }

    public LiveData<Serie> getSerie(int idSerie) {
        return mSerieDao.getSerie(idSerie);
    }
}
