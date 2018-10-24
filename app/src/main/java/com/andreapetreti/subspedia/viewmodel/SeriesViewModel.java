package com.andreapetreti.subspedia.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.repo.SerieRepository;
import com.google.gson.JsonObject;

import java.util.List;

public class SeriesViewModel extends AndroidViewModel {

    private SerieRepository mSerieRepository;

    private LiveData<Resource<List<Serie>>> mAllSeries;
    private MediatorLiveData<Resource<List<Serie>>> mRefreshableAllSeries;

    public SeriesViewModel(@NonNull Application application) {
        super(application);
        mSerieRepository = new SerieRepository(application);
        mRefreshableAllSeries = new MediatorLiveData<>();
        mAllSeries = mSerieRepository.getAllSeries(false);
        mRefreshableAllSeries.addSource(mAllSeries, this::postNewSeries);
    }

    public LiveData<Resource<List<Serie>>> getAllSeries() {
        return mRefreshableAllSeries;
    }

    public LiveData<Serie> getSerie(int idSerie) {
        return mSerieRepository.getSerie(idSerie);
    }

    public LiveData<List<Serie>> getFavoriteSeries() {
        return mSerieRepository.getFavoriteSeries();
    }

    public LiveData<Resource<JsonObject>> getDetails(int idSerie) {
        return mSerieRepository.getDetails(idSerie);
    }

    public void setFavoriteSerie(int idSerie, boolean addRemove) {
        mSerieRepository.addSerieToFavorite(idSerie, addRemove);
    }

    private void postNewSeries(Resource<List<Serie>> series) {
        mRefreshableAllSeries.setValue(series);
    }

    public void refreshAllSeries() {
        mRefreshableAllSeries.removeSource(mAllSeries);
        mAllSeries = mSerieRepository.getAllSeries(true);
        mRefreshableAllSeries.addSource(mAllSeries, this::postNewSeries);
    }
}
