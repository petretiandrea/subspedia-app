package com.andreapetreti.subspedia.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.SerieTranslating;
import com.andreapetreti.subspedia.repo.SerieRepository;
import com.andreapetreti.subspedia.repo.SerieTranslatingRepo;

import java.util.List;

public class SerieTranslatingViewModel extends AndroidViewModel {

    private SerieTranslatingRepo mSerieTranslatingRepo;

    public SerieTranslatingViewModel(@NonNull Application application) {
        super(application);
        mSerieTranslatingRepo = new SerieTranslatingRepo(application);
    }

    public LiveData<Resource<List<SerieTranslating>>> getAllTranslatingSeries() {
        return mSerieTranslatingRepo.getAllTranslatingSeries();
    }
}
