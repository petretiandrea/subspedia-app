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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
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
                for(Subtitle subtitle : item)
                    db.subtitlesDao().save(subtitle);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Subtitle> data) {
                return data == null || data.size() == 0;
            }

            @NonNull
            @Override
            protected LiveData<List<Subtitle>> loadFromDb() {
                return db.subtitlesDao().getSubtitlesOf(idSerie);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Subtitle>>> createCall() {
                return new LiveData<ApiResponse<List<Subtitle>>>() {
                    boolean alreadyCalled = false;
                    @Override
                    protected void onActive() {
                        super.onActive();
                        synchronized (this) {
                            if (!alreadyCalled) {
                                alreadyCalled = true;
                                mSubspediaService.getSubtitlesOf(idSerie).enqueue(new Callback<List<Subtitle>>() {
                                    @Override
                                    public void onResponse(@NonNull Call<List<Subtitle>> call, Response<List<Subtitle>> response) {
                                        postValue(new ApiResponse<>(response));
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<List<Subtitle>> call, Throwable t) {
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


    public LiveData<Resource<List<Subtitle>>> getLastSubtitles() {
        MutableLiveData<Resource<List<Subtitle>>> data = new MutableLiveData<>();
        AppExecutor.getInstance().getNetworkExecutor().execute(() ->
            mSubspediaService.getLastSubtitles().enqueue(new Callback<List<Subtitle>>() {
                @Override
                public void onResponse(Call<List<Subtitle>> call, Response<List<Subtitle>> response) {
                    ApiResponse<List<Subtitle>> apiResp = new ApiResponse<>(response);
                    AppExecutor.getInstance().getMainThread().execute(() -> {
                        if(apiResp.isSuccessful())
                            data.setValue(Resource.success(apiResp.body));
                        else
                            data.setValue(Resource.error(apiResp.errorMessage, apiResp.body));
                    });
                }

                @Override
                public void onFailure(Call<List<Subtitle>> call, Throwable t) {
                    ApiResponse<List<Subtitle>> apiResp = new ApiResponse<>(t);
                    AppExecutor.getInstance().getMainThread().execute(() -> data.setValue(Resource.error(apiResp.errorMessage, apiResp.body)));
                }
        }));
        return data;
    }
}
