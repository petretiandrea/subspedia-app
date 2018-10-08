package com.andreapetreti.subspedia.repo;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andreapetreti.android_utils.Utils;
import com.andreapetreti.subspedia.common.ApiResponse;
import com.andreapetreti.subspedia.common.NetworkBoundResource;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.common.SubspediaService;
import com.andreapetreti.subspedia.database.SerieDao;
import com.andreapetreti.subspedia.database.SubsDatabase;
import com.andreapetreti.subspedia.model.Serie;
import com.annimon.stream.Collector;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Predicate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SerieRepository {

    private static final String TAG = SerieRepository.class.getName();
    private static final String SERIE_LAST_UPDATE = "com.andreapetreti.subspedia.serie_last_update";
    private static final long THRESHOLD_UPDATE = 20 * 60 * 60 * 60;

    /**
     * Dao for manage Series inside database.
     */
    private SerieDao mSerieDao;

    /**
     * Web service for access to network resources.
     */
    private SubspediaService mSubspediaService;
    private SharedPreferences mSharedPreferences;

    public SerieRepository(Context context) {
        SubsDatabase db = SubsDatabase.getDatabase(context);
        mSerieDao = db.serieDao();
        mSubspediaService = SubspediaService.Provider.getInstance();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public LiveData<Resource<List<Serie>>> getAllSeries() {
        return new NetworkBoundResource<List<Serie>, List<Serie>>() {

            @Override
            protected void saveCallResult(@NonNull List<Serie> item) {
                // preserve favorite to DB.
                Map<Integer, Serie> dbSerie = Stream.of(mSerieDao.getAllSeriesSync()).filter(Serie::isFavorite).collect(Collectors.toMap(Serie::getIdSerie));

                Stream.of(item)
                        .forEach(serie -> {
                            if(dbSerie.containsKey(serie.getIdSerie()))
                                serie.setFavorite(dbSerie.get(serie.getIdSerie()).isFavorite());
                            mSerieDao.save(serie);
                        });
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Serie> data) {
                // Get current time
                long last = mSharedPreferences.getLong(SERIE_LAST_UPDATE, 0);
                boolean needUpdate = Utils.checkTimeDifference(last, THRESHOLD_UPDATE);
                return data == null || needUpdate;
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
                                        // save last update
                                        if(response.isSuccessful() && response.body() != null)
                                            mSharedPreferences.edit().putLong(SERIE_LAST_UPDATE, Utils.currentTimeUTCMillis()).apply();
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
