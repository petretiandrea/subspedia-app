package com.andreapetreti.subspedia.viewmodel;

import android.app.Application;
import android.util.SparseArray;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.andreapetreti.subspedia.common.Resource;
import com.andreapetreti.subspedia.model.SubtitleWithSerie;
import com.andreapetreti.subspedia.repo.SubtitlesRepo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubtitleViewModel extends AndroidViewModel {

    private LiveData<Resource<List<SubtitleWithSerie>>> mSubtitles;
    private SubtitlesRepo mSubtitlesRepo;

    public SubtitleViewModel(@NonNull Application application) {
        super(application);
        mSubtitlesRepo = new SubtitlesRepo(application);
        mSubtitles = null;
    }

    public LiveData<Resource<List<SubtitleWithSerie>>> getSubtitlesOf(int idSerie) {
        if(mSubtitles == null) {
            mSubtitles = mSubtitlesRepo.getSubtitlesOf(idSerie);
        }
        return mSubtitles;
    }

    /* TODO: expose a direct method to obtain a map (sparse array), of season -> list subtitles.
    mSubtitles = new MediatorLiveData<>();
            mSubtitles.addSource(mSubtitlesRepo.getSubtitlesOf(idSerie), new Observer<Resource<List<SubtitleWithSerie>>>() {
                @Override
                public void onChanged(Resource<List<SubtitleWithSerie>> listResource) {
                    SparseArray<List<SubtitleWithSerie>> all = new SparseArray<>();


                }
            });
     */

    public LiveData<Resource<List<SubtitleWithSerie>>> getLastSubtitles() {
        return mSubtitlesRepo.getLastSubtitles();
    }


}
