package com.andreapetreti.subspedia.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.andreapetreti.subspedia.AppExecutor;
import com.andreapetreti.subspedia.common.ApiResponse;
import com.andreapetreti.subspedia.cache.NetworkBoundResource;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.common.SubspediaService;
import com.andreapetreti.subspedia.database.SubsDatabase;
import com.andreapetreti.subspedia.database.SubtitlesDao;
import com.andreapetreti.subspedia.model.Subtitle;
import com.andreapetreti.subspedia.model.SubtitleWithSerie;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.Collections;
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

    public LiveData<Resource<List<SubtitleWithSerie>>> getSubtitlesOf(final int idSerie) {
        return new NetworkBoundResource<List<SubtitleWithSerie>, List<Subtitle>>() {
            @Override
            protected void saveCallResult(@NonNull List<Subtitle> item) {
                // policy for limit the cache of subtitles
                SubtitlesDao dao = db.subtitlesDao();
                // remove all subtitles of tv series too old, respecting the limit.
                dao.removeLRUSerieSubtitles();
                // save other subtitles.
                Stream.of(item).forEach(dao::save);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<SubtitleWithSerie> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<SubtitleWithSerie>> loadFromDb() {
                return db.subtitlesDao().getSubtitlesOf(idSerie);
            }
            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Subtitle>>> createCall() {
                MutableLiveData<ApiResponse<List<Subtitle>>> liveData = new MutableLiveData<>();
                mSubspediaService.getSubtitlesOf(idSerie).enqueue(new Callback<List<Subtitle>>() {
                    @Override
                    public void onResponse(Call<List<Subtitle>> call, Response<List<Subtitle>> response) {
                        liveData.postValue(new ApiResponse<>(response));
                    }

                    @Override
                    public void onFailure(Call<List<Subtitle>> call, Throwable t) {
                        liveData.postValue(new ApiResponse<>(t));
                    }
                });
                return liveData;
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<SubtitleWithSerie>>> getLastSubtitles() {
        MutableLiveData<Resource<List<SubtitleWithSerie>>> data = new MutableLiveData<>();
        data.setValue(Resource.loading(null));

        AppExecutor.getInstance().getNetworkExecutor().execute(() -> {
            try {
                Response<List<Subtitle>> subs = mSubspediaService.getLastSubtitles().execute();
                if(subs.isSuccessful() && subs.body() != null) {
                    List<SubtitleWithSerie> subtitleWithSeries = Stream.of(subs.body())
                            .map(subtitle -> new SubtitleWithSerie(subtitle, db.serieDao().getSerieSync(subtitle.getIdSerie())))
                            .collect(com.annimon.stream.Collectors.toCollection(ArrayList::new));

                    AppExecutor.getInstance().getMainThread().execute(() -> data.postValue(Resource.success(subtitleWithSeries)));
                } else
                    AppExecutor.getInstance().getMainThread().execute(() -> data.postValue(Resource.error(subs.message(), Collections.emptyList())));
            } catch (Throwable t) {
                AppExecutor.getInstance().getMainThread().execute(() -> data.postValue(Resource.error(t.getMessage(), null)));
            }
        });

        return data;
    }
}
