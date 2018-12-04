package com.andreapetreti.subspedia.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.preference.PreferenceManager;
import android.webkit.MimeTypeMap;

import com.andreapetreti.android_utils.downloadmanager.DownloadManager;
import com.andreapetreti.subspedia.Constants;
import com.andreapetreti.subspedia.DashboardActivity;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.background.NewSubsWorker;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.Subtitle;
import com.andreapetreti.subspedia.model.SubtitleWithSerie;
import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SubspediaUtils {

    /**
     * Use the android download manager, for download the subtitle.
     * @param context
     * @param subtitle
     */
    public static void downloadSubtitle(Context context, SubtitleWithSerie subtitle) {

        DownloadManager downloadManager = new DownloadManager.Builder(context)
                .setSmallIcon(R.mipmap.ic_small_notification)
                .setLargeIcon(R.mipmap.ic_launcher_round)
                .setNotificationColor(ContextCompat.getColor(context, R.color.primaryColor))
                .build();

        Intent thisApp = new Intent(context, DashboardActivity.class);
        thisApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        DownloadManager.Request request = new DownloadManager.Request();
        request.setTitle(subtitle.getSerie().getName())
                .setDescription(String.format(Locale.getDefault(), "%dx%d - %s",
                    subtitle.getSubtitle().getSeasonNumber(),
                    subtitle.getSubtitle().getEpisodeNumber(),
                    subtitle.getSubtitle().getEpisodeNumber()))
                .setUri(Uri.parse(subtitle.getSubtitle().getLinkFile()))
                .setDestinationInExternalPublicDir("Subspedia", "")
                .setPendingIntent(PendingIntent.getActivity(context, 0, thisApp, PendingIntent.FLAG_UPDATE_CURRENT));

        downloadManager.enqueue(request);
    }

    public static Optional<Date> parseDate(String format, String source) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        try {
            return Optional.of(dateFormat.parse(source));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }

    public static String formatToDefaultDate(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(date);
    }

    /**
     * Check if difference between current UTC time and last UTC time, is greater than threshold.
     * @param lastUTCMillis
     * @param thresholdMillis Threshold time in milliseconds.
     * @return True if difference is greater than {thresholdMillis}.
     */
    public static boolean checkTimeDifference(long lastUTCMillis, long thresholdMillis) {
        long current = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
        long difference = Math.abs(current - lastUTCMillis);

        return difference > thresholdMillis;
    }

    public static long currentTimeUTCMillis() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
    }
}
