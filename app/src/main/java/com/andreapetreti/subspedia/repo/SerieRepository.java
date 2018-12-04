package com.andreapetreti.subspedia.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.andreapetreti.subspedia.AppExecutor;
import com.andreapetreti.subspedia.common.ApiResponse;
import com.andreapetreti.subspedia.cache.NetworkBoundResource;
import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.common.SubspediaService;
import com.andreapetreti.subspedia.database.SerieDao;
import com.andreapetreti.subspedia.database.SubsDatabase;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.utils.SubspediaUtils;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

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

    public LiveData<List<Serie>> getFavoriteSeries() {
        return mSerieDao.getFavoriteSeries();
    }

    public LiveData<Resource<List<Serie>>> getAllSeries(boolean forceFetch) {
        return new NetworkBoundResource<List<Serie>, List<Serie>>() {

            @Override
            protected void saveCallResult(@NonNull List<Serie> item) {
                // preserve favorite to DB.
                Map<Integer, Serie> dbSerie = Stream.of(mSerieDao.getAllSeriesSync())
                        .filter(Serie::isFavorite)
                        .collect(Collectors.toMap(Serie::getIdSerie));

                // save all series that is not favorite
                Stream.of(item)
                        .filterNot(value -> dbSerie.containsKey(value.getIdSerie()))
                        .forEach(mSerieDao::save);

                // save series that is already favorite
                Stream.of(item)
                        .filter(value -> dbSerie.containsKey(value.getIdSerie()))
                        .peek(serie -> serie.setFavorite(true))
                        .forEach(mSerieDao::save);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Serie> data) {
                // Get current time
                long last = mSharedPreferences.getLong(SERIE_LAST_UPDATE, 0);
                boolean needUpdate = SubspediaUtils.checkTimeDifference(last, THRESHOLD_UPDATE);
                return data == null || data.isEmpty() || needUpdate || forceFetch;
            }

            @NonNull
            @Override
            protected LiveData<List<Serie>> loadFromDb() {
                return mSerieDao.getAllSeries();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Serie>>> createCall() {
                MutableLiveData<ApiResponse<List<Serie>>> liveData = new MutableLiveData<>();

                mSubspediaService.getAllSeries().enqueue(new Callback<List<Serie>>() {
                    @Override
                    public void onResponse(Call<List<Serie>> call, Response<List<Serie>> response) {
                        if(response.isSuccessful() && response.body() != null)
                            mSharedPreferences.edit().putLong(SERIE_LAST_UPDATE, SubspediaUtils.currentTimeUTCMillis()).apply();
                        liveData.postValue(new ApiResponse<>(response));
                    }

                    @Override
                    public void onFailure(Call<List<Serie>> call, Throwable t) {
                        liveData.postValue(new ApiResponse<>(t));
                    }
                });

                return liveData;
            }
        }.asLiveData();
    }

    public LiveData<Serie> getSerie(int idSerie) {
        return mSerieDao.getSerie(idSerie);
    }

    public LiveData<Resource<JsonObject>> getDetails(int idSerie) {
        MutableLiveData<Resource<JsonObject>> liveData = new MutableLiveData<>();
        liveData.postValue(Resource.loading(null));
        mSubspediaService.getSerieDetails(idSerie).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful() && response.body() != null)
                    liveData.postValue(Resource.success(response.body()));
                else
                    liveData.postValue(Resource.error(response.message(), response.body()));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                liveData.postValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }

    public void addSerieToFavorite(int idSerie, boolean add) {
        AppExecutor.getInstance().getDiskExecutor().execute(() -> mSerieDao.setFavoriteSerie(idSerie, add));
    }
}
