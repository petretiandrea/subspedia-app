package com.andreapetreti.subspedia.background;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.andreapetreti.subspedia.common.SubspediaService;
import com.andreapetreti.subspedia.database.SerieDao;
import com.andreapetreti.subspedia.database.SubsDatabase;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.Subtitle;
import com.andreapetreti.subspedia.model.SubtitleWithSerie;
import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.annimon.stream.function.ToBooleanFunction;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Response;

public class NewSubsWorker extends Worker {

    private static final String KEY_LAST_CHECK = "com.andreapetreti.subspedia.last_check";

    private SimpleDateFormat mSimpleDateFormat;
    private NotificationSubspedia mNotificationSubspedia;

    public NewSubsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mNotificationSubspedia = new NotificationSubspedia(context);
        mSimpleDateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.getDefault());
    }

    @NonNull
    @Override
    public Result doWork() {

        SubspediaService mSubspediaService = SubspediaService.Provider.getInstance();
        SerieDao mSerieDao = SubsDatabase.getDatabase(getApplicationContext()).serieDao();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        long lastCheck = sharedPreferences.getLong(KEY_LAST_CHECK, 0);

        try {
            Response<List<Subtitle>> subs = mSubspediaService.getLastSubtitles().execute();
            List<Serie> favoriteSeries = mSerieDao.getFavoriteSeriesSync();

            if (subs.isSuccessful() && subs.body() != null) {

                List<SubtitleWithSerie> newSubs = Stream.of(subs.body())
                        .filter(value -> parseDate(value.getDate()).mapToBoolean(date -> date.getTime() >= lastCheck).orElse(false))
                        .flatMap(subtitle -> Stream.of(favoriteSeries).filter(value -> value.getIdSerie() == subtitle.getIdSerie()).map(serie -> new SubtitleWithSerie(subtitle, serie)))
                        .distinct()
                        .collect(Collectors.toList());

                sharedPreferences.edit().putLong(KEY_LAST_CHECK, System.currentTimeMillis()).apply();

                // set notifications
                mNotificationSubspedia.notifyNewSub(newSubs);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return Result.FAILURE;
        }

        return Result.SUCCESS;
    }

    private Optional<Date> parseDate(String source) {
        try {
            return Optional.of(mSimpleDateFormat.parse(source));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }
}
