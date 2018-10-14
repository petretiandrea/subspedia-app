package com.andreapetreti.subspedia.utils;

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.andreapetreti.android_utils.downloadmanager.DownloadManager;
import com.andreapetreti.subspedia.model.Subtitle;

import java.util.Locale;

public class SubspediaUtils {

    public static String generateSubspediaFilename(Subtitle subtitle) {
        /* Manifest.s01.e03.subspedia */
        return String.format(Locale.getDefault(), "%s_s%d_e%d_subspedia",
                subtitle.getSerie().getName(),
                subtitle.getSeasonNumber(),
                subtitle.getEpisodeNumber());
    }

    public static String getMimeType(String url) {
        return MimeTypeMap.getFileExtensionFromUrl(url);
    }

    /**
     * Use the android download manager, for download the subtitle.
     * @param context
     * @param subtitle
     */
    public static void downloadSubtitle(Context context, Subtitle subtitle) {

        DownloadManager downloadManager = DownloadManager.newInstance(context);
        DownloadManager.Request request = new DownloadManager.Request();
        request.setTitle(subtitle.getSerie().getName());
        request.setDescription(String.format(Locale.getDefault(), "%dx%d - %s",
                subtitle.getSeasonNumber(),
                subtitle.getEpisodeNumber(),
                subtitle.getEpisodeNumber()));
        request.setUri(Uri.parse(subtitle.getLinkFile()));
        request.setDestinationInExternalPublicDir("Subspedia", "");

        downloadManager.enqueue(request);
    }
}
