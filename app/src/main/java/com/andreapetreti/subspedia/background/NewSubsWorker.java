package com.andreapetreti.subspedia.background;

import android.content.Context;

import com.andreapetreti.subspedia.common.SubspediaService;
import com.andreapetreti.subspedia.database.SerieDao;
import com.andreapetreti.subspedia.database.SubsDatabase;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.Subtitle;
import com.andreapetreti.subspedia.model.SubtitleWithSerie;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Response;

public class NewSubsWorker extends Worker {

    private NotificationSubspedia mNotificationSubspedia;
    private NewSubsChecker mNewSubsChecker;

    public NewSubsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mNotificationSubspedia = new NotificationSubspedia(context);
        mNewSubsChecker = new NewSubsChecker(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        SubspediaService mSubspediaService = SubspediaService.Provider.getInstance();
        SerieDao mSerieDao = SubsDatabase.getDatabase(getApplicationContext()).serieDao();

        try {
            /* Retrieve the new subtitles from subspedia.tv, and the favorite tv series from db */
            Response<List<Subtitle>> subs = mSubspediaService.getLastSubtitles().execute();
            List<Serie> favoriteSeries = mSerieDao.getFavoriteSeriesSync();

            if (subs.isSuccessful() && subs.body() != null) {
                List<SubtitleWithSerie> newSubs = mNewSubsChecker.retrieveOnlyNewSubs(subs.body(), favoriteSeries);

                // send notifications
                mNotificationSubspedia.notifyNewSub(newSubs);
            }

            return Result.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.RETRY;
    }
}
