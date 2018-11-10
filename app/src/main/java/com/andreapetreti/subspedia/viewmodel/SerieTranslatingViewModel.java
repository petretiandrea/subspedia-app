package com.andreapetreti.subspedia.viewmodel;

import android.app.Application;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.model.SerieTranslating;
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
