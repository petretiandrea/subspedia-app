package com.andreapetreti.subspedia.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.andreapetreti.subspedia.AppExecutor;
import com.andreapetreti.subspedia.Constants;
import com.andreapetreti.subspedia.model.Subtitle;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import static android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;

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
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(subtitle.getLinkFile());
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(subtitle.getSerie().getName());
        request.setDescription(String.format(Locale.getDefault(), "%dx%d - %s",
                subtitle.getSeasonNumber(),
                subtitle.getEpisodeNumber(),
                subtitle.getEpisodeNumber()));
        request.setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);

        AppExecutor.getInstance().getNetworkExecutor().execute(() -> {
            try {
                URL url = new URL(subtitle.getLinkFile());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                connection.connect();
                // Extract filename
                String contentSplit[] = connection.getHeaderField("Content-Disposition").split("filename=");
                String filename = contentSplit[1].replace("filename=", "").replace("\"", "").trim();
                String extension = filename.substring(filename.lastIndexOf(".") + 1);
                String invalid = "|\\?*<\":>+[]./'";
                for(char c : invalid.toCharArray())
                    filename = filename.replace(c, '_');
                filename = filename.substring(0, filename.lastIndexOf(extension) - 1) + "." + extension;
                connection.disconnect();

                request.setDestinationInExternalPublicDir("/" + Constants.DOWNLOAD_FOLDER, filename);
                downloadManager.enqueue(request);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }
}
