package com.andreapetreti.subspedia.utils;

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.andreapetreti.android_utils.downloadmanager.DownloadManager;
import com.andreapetreti.subspedia.R;
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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SubspediaUtils {

    public static String generateSubspediaFilename(SubtitleWithSerie subtitle) {
        /* Manifest.s01.e03.subspedia */
        return String.format(Locale.getDefault(), "%s_s%d_e%d_subspedia",
                subtitle.getSerie().getName(),
                subtitle.getSubtitle().getSeasonNumber(),
                subtitle.getSubtitle().getEpisodeNumber());
    }

    public static String getMimeType(String url) {
        return MimeTypeMap.getFileExtensionFromUrl(url);
    }

    /**
     * Use the android download manager, for download the subtitle.
     * @param context
     * @param subtitle
     */
    public static void downloadSubtitle(Context context, SubtitleWithSerie subtitle) {

        DownloadManager downloadManager = new DownloadManager.Builder(context)
                .setSmallIcon(R.mipmap.ic_small_notification)
                .setLargeIcon(R.mipmap.ic_launcher_round)
                .build();

        DownloadManager.Request request = new DownloadManager.Request();
        request.setTitle(subtitle.getSerie().getName())
                .setDescription(String.format(Locale.getDefault(), "%dx%d - %s",
                    subtitle.getSubtitle().getSeasonNumber(),
                    subtitle.getSubtitle().getEpisodeNumber(),
                    subtitle.getSubtitle().getEpisodeNumber()))
                .setUri(Uri.parse(subtitle.getSubtitle().getLinkFile()))
                .setDestinationInExternalPublicDir("Subspedia", "");

        downloadManager.enqueue(request);
    }

    public static List<SubtitleWithSerie> filterNewSubtitles(List<Subtitle> subtitles, List<Serie> series,
                                                             long lastCheck, long threshold) {

        String dateFormat = "yyyy-MM-dd HH:mm:ss";

        return Stream.of(subtitles)
                .filter(value -> parseDateToUTC(dateFormat, value.getDate()).mapToBoolean(date -> date.getTime() >= (lastCheck - threshold)).orElse(false))
                .flatMap(subtitle -> Stream.of(series).filter(value -> value.getIdSerie() == subtitle.getIdSerie()).map(serie -> new SubtitleWithSerie(subtitle, serie)))
                .distinct()
                .collect(Collectors.toList());
    }

    public static Optional<Date> parseDateToUTC(String format, String source) {
        try {
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(format, Locale.getDefault());
            System.out.println(simpleDateFormat2.parse(source));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            System.out.println(simpleDateFormat.parse(source));

            return Optional.of(simpleDateFormat.parse(source));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }
}
