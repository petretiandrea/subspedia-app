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

    private MutableLiveData<Resource<List<SerieTranslating>>> mTranslatingSeries;

    public SerieTranslatingViewModel(@NonNull Application application) {
        super(application);
        mSerieTranslatingRepo = new SerieTranslatingRepo(application);
        mTranslatingSeries = new MutableLiveData<>();
        Transformations.switchMap(mTranslatingSeries, new Function<Resource<List<SerieTranslating>>, LiveData<? extends Object>>() {
            @Override
            public LiveData<? extends Object> apply(Resource<List<SerieTranslating>> input) {
                if(input != null) {
                    return mSerieTranslatingRepo.getAllTranslatingSeries(false);
                }
                return
            }
        })
    }

    public LiveData<Resource<List<SerieTranslating>>> getAllTranslatingSeries() {

        return mTranslatingSeries;
    }


    public void refreshTranslatingSeries() {

    }

}
