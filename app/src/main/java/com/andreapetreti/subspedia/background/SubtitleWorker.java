package com.andreapetreti.subspedia.background;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.andreapetreti.androidcommonutils.common.TimeValue;
import com.andreapetreti.subspedia.R;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public final class SubtitleWorker {

    private static final String NEW_SUB_WORKER_ID = "periodic_new_sub";

    public static void enableSubtitleNotification(Context context) {
        changeIntervalSubtitleNotification(context, getWorkerInterval(context));
    }

    public static void changeIntervalSubtitleNotification(Context context, TimeValue timeValue) {
        if(!isNotificationSubsEnabled(context)) {
            // enable it
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            PeriodicWorkRequest workRequest = new PeriodicWorkRequest
                    .Builder(NewSubsWorker.class, timeValue.getValue(), timeValue.getTimeUnit())
                    .setConstraints(constraints)
                    .build();
            WorkManager.getInstance().enqueueUniquePeriodicWork(NEW_SUB_WORKER_ID, ExistingPeriodicWorkPolicy.REPLACE, workRequest);
        }
    }

    public static void disableSubtitleNotification(Context context) {
        if(isNotificationSubsEnabled(context)) {
            WorkManager.getInstance().cancelAllWorkByTag(NEW_SUB_WORKER_ID);
        }
    }

    private static boolean isNotificationSubsEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.key_settings_notification), true);
    }

    private static TimeValue getWorkerInterval(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return TimeValue.fromMinutes( // default 1 hour
                Long.parseLong(sharedPreferences.getString(context.getString(R.string.key_interval_settings), "60"))
        );
    }
}
