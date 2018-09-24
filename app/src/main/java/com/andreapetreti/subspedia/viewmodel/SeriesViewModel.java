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

import java.util.List;

public class SeriesViewModel extends AndroidViewModel {

    private SerieRepository mSerieRepository;

    private LiveData<Resource<List<Serie>>> mAllSeries;

    public SeriesViewModel(@NonNull Application application) {
        super(application);
        mSerieRepository = new SerieRepository(application);
        mAllSeries = mSerieRepository.getAllSeries();
    }

    public LiveData<Resource<List<Serie>>> getAllSeries() {
        return mAllSeries;
    }

    public LiveData<Serie> getSerie(int idSerie) {
        return mSerieRepository.getSerie(idSerie);
    }
}
