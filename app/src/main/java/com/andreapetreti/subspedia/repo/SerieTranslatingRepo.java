package com.andreapetreti.subspedia.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;

import com.andreapetreti.subspedia.cache.SimpleLiveDataCache;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.common.SubspediaService;
import com.andreapetreti.subspedia.database.SerieTranslatingDao;
import com.andreapetreti.subspedia.database.SubsDatabase;
import com.andreapetreti.subspedia.model.SerieTranslating;
import com.annimon.stream.Objects;

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

    private SimpleLiveDataCache<Resource<List<SerieTranslating>>> mTranslatingCache;

    public SerieTranslatingRepo(Context context) {
        SubsDatabase db = SubsDatabase.getDatabase(context);
        mSerieTranslatingDao = db.serieTranslatingDao();
        mSubspediaService = SubspediaService.Provider.getInstance();
        mTranslatingCache = new SimpleLiveDataCache<>();
    }

    public LiveData<Resource<List<SerieTranslating>>> getAllTranslatingSeries(boolean forceFetch) {
        // retrieve cached data
        LiveData<Resource<List<SerieTranslating>>> cached = mTranslatingCache.get();
        if(cached != null && !forceFetch) {
            return cached;
        }

        final MutableLiveData<Resource<List<SerieTranslating>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        mTranslatingCache.put(liveData);
        mSubspediaService.getAllTranslatingSeries().enqueue(new Callback<List<SerieTranslating>>() {
            @Override
            public void onResponse(Call<List<SerieTranslating>> call, Response<List<SerieTranslating>> response) {
                if(response.isSuccessful() && Objects.nonNull(response.body())) {
                    liveData.setValue(Resource.success(response.body()));
                }
            }

            @Override
            public void onFailure(Call<List<SerieTranslating>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }
}
