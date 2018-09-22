package com.andreapetreti.subspedia.repo;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andreapetreti.subspedia.common.ApiResponse;
import com.andreapetreti.subspedia.common.NetworkBoundResource;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.common.SubspediaService;
import com.andreapetreti.subspedia.database.SerieTranslatingDao;
import com.andreapetreti.subspedia.database.SubsDatabase;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.SerieTranslating;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SerieTranslatingRepo {

    /**
     * Dao for manager Series Translating inside db.
     */
    private SerieTranslatingDao mSerieTranslatingDao;

    /**
     * Web service for access to network resource
     */
    private SubspediaService mSubspediaService;

    public SerieTranslatingRepo(Context context) {
        SubsDatabase db = SubsDatabase.getDatabase(context);
        mSerieTranslatingDao = db.serieTranslatingDao();
        mSubspediaService = SubspediaService.Provider.getInstance();
    }

    public LiveData<Resource<List<SerieTranslating>>> getAllTranslatingSeries() {
        return new NetworkBoundResource<List<SerieTranslating>, List<SerieTranslating>>() {

            @Override
            protected void saveCallResult(@NonNull List<SerieTranslating> item) {
                for(SerieTranslating s : item)
                    mSerieTranslatingDao.save(s);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<SerieTranslating> data) {
                return true; // always check update, because translate serie change frequently.
            }

            @NonNull
            @Override
            protected LiveData<List<SerieTranslating>> loadFromDb() {
                return mSerieTranslatingDao.getAllTranslatingSeries();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<SerieTranslating>>> createCall() {
                return new LiveData<ApiResponse<List<SerieTranslating>>>() {
                    boolean alreadyCalled = false;
                    @Override
                    protected void onActive() {
                        super.onActive();
                        synchronized (this) {
                            if (!alreadyCalled) {
                                alreadyCalled = true;
                                mSubspediaService.getAllTranslatingSeries().enqueue(new Callback<List<SerieTranslating>>() {
                                    @Override
                                    public void onResponse(Call<List<SerieTranslating>> call, Response<List<SerieTranslating>> response) {
                                        postValue(new ApiResponse<>(response));
                                    }

                                    @Override
                                    public void onFailure(Call<List<SerieTranslating>> call, Throwable t) {
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
}
