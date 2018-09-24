package com.andreapetreti.subspedia.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.Subtitle;
import com.andreapetreti.subspedia.repo.SerieRepository;
import com.andreapetreti.subspedia.repo.SubtitlesRepo;

import java.util.List;

public class SubtitleViewModel extends AndroidViewModel {

    private LiveData<Resource<List<Subtitle>>> mSubtitles;
    private SubtitlesRepo mSubtitlesRepo;

    public SubtitleViewModel(@NonNull Application application) {
        super(application);
        mSubtitlesRepo = new SubtitlesRepo(application);
        mSubtitles = null;
    }

    public LiveData<Resource<List<Subtitle>>> getSubtitlesOf(int idSerie) {
        if(mSubtitles == null)
            mSubtitles = mSubtitlesRepo.getSubtitlesOf(idSerie);
        return mSubtitles;
    }

    public LiveData<Resource<List<Subtitle>>> getLastSubtitles() {
        return mSubtitlesRepo.getLastSubtitles();
    }


}
