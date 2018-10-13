package com.andreapetreti.subspedia.repo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andreapetreti.subspedia.AppExecutor;
import com.andreapetreti.subspedia.common.ApiResponse;
import com.andreapetreti.subspedia.common.NetworkBoundResource;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.common.SubspediaService;
import com.andreapetreti.subspedia.database.SubsDatabase;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.Subtitle;
import com.annimon.stream.Stream;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Response;

public class SubtitlesRepo {


    private SubsDatabase db;

    /**
     * Web service for access to network resources.
     */
    private SubspediaService mSubspediaService;

    public SubtitlesRepo(Context context) {
        db = SubsDatabase.getDatabase(context);
        mSubspediaService = SubspediaService.Provider.getInstance();
    }

    public LiveData<Resource<List<Subtitle>>> getSubtitlesOf(final int idSerie) {
        return new NetworkBoundResource<List<Subtitle>, List<Subtitle>>() {
            @Override
            protected void saveCallResult(@NonNull List<Subtitle> item) {
                Stream.of(item).forEach(db.subtitlesDao()::save);
            }
            @Override
            protected boolean shouldFetch(@Nullable List<Subtitle> data) { return true; }
            @NonNull
            @Override
            protected LiveData<List<Subtitle>> loadFromDb() {
                return db.subtitlesDao().getSubtitlesOf(idSerie);
            }
            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Subtitle>>> createCall() {
                MutableLiveData<ApiResponse<List<Subtitle>>> liveData = new MutableLiveData<>();
                AppExecutor.getInstance().getNetworkExecutor().execute(() -> {
                    try {
                        // blocking method
                        Response<List<Subtitle>> subs = mSubspediaService.getSubtitlesOf(idSerie).execute();
                        if(subs.isSuccessful() && subs.body() != null) {
                            Serie serie = db.serieDao().getSerieSync(idSerie);
                            Stream.of(subs.body()).forEach(subtitle -> subtitle.setSerie(serie));
                        }
                        AppExecutor.getInstance().getMainThread().execute(() -> liveData.postValue(new ApiResponse<>(subs)));
                    } catch (Throwable t) {
                        AppExecutor.getInstance().getMainThread().execute(() -> liveData.postValue(new ApiResponse<>(t)));
                    }
                });
                return liveData;
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Subtitle>>> getLastSubtitles() {
        MutableLiveData<Resource<List<Subtitle>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading(null));

        AppExecutor.getInstance().getNetworkExecutor().execute(() -> {
            try {
                Response<List<Subtitle>> subs = mSubspediaService.getLastSubtitles().execute();
                if(subs.isSuccessful() && subs.body() != null) {
                    Stream.of(subs.body()).forEach(subtitle -> subtitle.setSerie(db.serieDao().getSerieSync(subtitle.getIdSerie())));
                }
                AppExecutor.getInstance().getMainThread().execute(() -> data.postValue(Resource.success(subs.body())));
            } catch (Throwable t) {
                AppExecutor.getInstance().getMainThread().execute(() -> data.postValue(Resource.error(t.getMessage(), null)));
            }
        });

        return data;
    }
}
