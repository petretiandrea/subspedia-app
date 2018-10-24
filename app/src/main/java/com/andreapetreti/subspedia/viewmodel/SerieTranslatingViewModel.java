package com.andreapetreti.subspedia.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.SerieTranslating;
import com.andreapetreti.subspedia.repo.SerieRepository;
import com.andreapetreti.subspedia.repo.SerieTranslatingRepo;

import java.util.List;

public class SerieTranslatingViewModel extends AndroidViewModel {

    private SerieTranslatingRepo mSerieTranslatingRepo;

    private MediatorLiveData<Resource<List<SerieTranslating>>> mTranslatingSeries;
    private LiveData<Resource<List<SerieTranslating>>> mSourceTranslatingSeries;

    public SerieTranslatingViewModel(@NonNull Application application) {
        super(application);
        mSerieTranslatingRepo = new SerieTranslatingRepo(application);
        mTranslatingSeries = new MediatorLiveData<>();
    }

    public LiveData<Resource<List<SerieTranslating>>> getAllTranslatingSeries() {
        mSourceTranslatingSeries = mSerieTranslatingRepo.getAllTranslatingSeries(false);
        mTranslatingSeries.addSource(mSourceTranslatingSeries, mTranslatingSeries::setValue);
        return mTranslatingSeries;
    }

    public void refreshTranslatingSeries() {
        mTranslatingSeries.removeSource(mSourceTranslatingSeries);
        mSourceTranslatingSeries = mSerieTranslatingRepo.getAllTranslatingSeries(true);
        mTranslatingSeries.addSource(mSourceTranslatingSeries, mTranslatingSeries::setValue);
    }
}
