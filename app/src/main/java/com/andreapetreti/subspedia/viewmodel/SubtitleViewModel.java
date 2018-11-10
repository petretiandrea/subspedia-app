package com.andreapetreti.subspedia.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.model.SubtitleWithSerie;
import com.andreapetreti.subspedia.repo.SubtitlesRepo;

import java.util.List;

public class SubtitleViewModel extends AndroidViewModel {

    private LiveData<Resource<List<SubtitleWithSerie>>> mSubtitles;
    private SubtitlesRepo mSubtitlesRepo;

    public SubtitleViewModel(@NonNull Application application) {
        super(application);
        mSubtitlesRepo = new SubtitlesRepo(application);
        mSubtitles = null;
    }

    public LiveData<Resource<List<SubtitleWithSerie>>> getSubtitlesOf(int idSerie) {
        if(mSubtitles == null)
            mSubtitles = mSubtitlesRepo.getSubtitlesOf(idSerie);
        return mSubtitles;
    }

    public LiveData<Resource<List<SubtitleWithSerie>>> getLastSubtitles() {
        return mSubtitlesRepo.getLastSubtitles();
    }


}
