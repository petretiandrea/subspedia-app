package com.andreapetreti.subspedia.utils;

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.andreapetreti.android_utils.downloadmanager.DownloadManager;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.model.Subtitle;
import com.andreapetreti.subspedia.model.SubtitleWithSerie;

import java.util.Locale;

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
}
