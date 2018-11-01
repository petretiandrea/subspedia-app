package com.andreapetreti.subspedia.background;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.andreapetreti.subspedia.Constants;
import com.andreapetreti.subspedia.common.SubspediaService;
import com.andreapetreti.subspedia.database.SerieDao;
import com.andreapetreti.subspedia.database.SubsDatabase;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.Subtitle;
import com.andreapetreti.subspedia.model.SubtitleWithSerie;
import com.andreapetreti.subspedia.utils.SubspediaUtils;
import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Response;

public class NewSubsWorker extends Worker {

    private static final String KEY_LAST_CHECK = "com.andreapetreti.subspedia.last_check";

    private NotificationSubspedia mNotificationSubspedia;

    public NewSubsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mNotificationSubspedia = new NotificationSubspedia(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        SubspediaService mSubspediaService = SubspediaService.Provider.getInstance();
        SerieDao mSerieDao = SubsDatabase.getDatabase(getApplicationContext()).serieDao();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        long threshold = getInputData().getLong(Constants.KEY_PERIOD_SCHEDULE_NOTIFICATION, 0);
        long lastCheck = sharedPreferences.getLong(KEY_LAST_CHECK, 0);



        try {
            Response<List<Subtitle>> subs = mSubspediaService.getLastSubtitles().execute();
            List<Serie> favoriteSeries = mSerieDao.getFavoriteSeriesSync();

            if (subs.isSuccessful() && subs.body() != null) {
                List<SubtitleWithSerie> newSubs = SubspediaUtils.filterNewSubtitles(subs.body(),
                        favoriteSeries,
                        lastCheck,
                        threshold);
                sharedPreferences.edit().putLong(KEY_LAST_CHECK, Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()).apply();

                // set notifications
                mNotificationSubspedia.notifyNewSub(newSubs);
            }

            return Result.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.RETRY;
    }
}
