package com.andreapetreti.android_utils.downloadmanager;

import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.annimon.stream.Optional;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

class Downloader {

    interface DownloadListener {
        void onStart(DownloadManager.Request request);
        void onProgressChange(DownloadManager.Request request, int progress);
        void onFail(DownloadManager.Request request, Throwable t);
    }

    private Optional<DownloadListener> mListener;

    public Downloader() {
        mListener = Optional.empty();
    }

    public void setListener(DownloadListener downloadListener) {
        mListener = Optional.of(downloadListener);
    }

    public void download(DownloadManager.Request request, DownloadListener callback) {
        executeRequest(request, callback);
    }

    private void notifyStart(DownloadManager.Request request) {
        mListener.executeIfPresent(downloadListener -> downloadListener.onStart(request));
    }

    private void notifyProgress(DownloadManager.Request request, final int progress) {
        mListener.executeIfPresent((downloadListener) -> downloadListener.onProgressChange(request, progress));
    }

    private void notifyFail(DownloadManager.Request request, Throwable t) {
        mListener.executeIfPresent(downloadListener -> downloadListener.onFail(request, t));
    }

    private void executeRequest(DownloadManager.Request request, DownloadListener callback) {
        notifyStart(request);

        try {
            URL url = new URL(request.getUri().toString());
            URLConnection connection = url.openConnection();
            connection.connect();

            int fileLength = connection.getContentLength();

            // retrieve filename and extension
            String filename;
            String mime = MimeTypeMap.getFileExtensionFromUrl(url.toString());
            if(mime == null || mime.isEmpty()) {
                // no filename or extension are in the URL, retrieve from headers
                String contentSplit[] = connection.getHeaderField("Content-Disposition").split("filename=");
                filename = contentSplit[1].replace("filename=", "").replace("\"", "").trim();
                String extension = filename.substring(filename.lastIndexOf("."));
                String invalid = "|\\?*<\":>+[]./'";
                for(char c : invalid.toCharArray())
                    filename = filename.replace(c, '_');
                filename = filename + extension;
            } else {
                filename = FilenameUtils.getName(url.getPath());
            }

            // open input and output stream
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            OutputStream outputStream = new FileOutputStream(Uri.withAppendedPath(request.getPath(), filename).getPath());

            // download file
            byte data[] = new byte[1024];
            int read = 0;
            long progress = 0;
            while((read = inputStream.read(data)) != -1) {
                progress += read;
                outputStream.write(data, 0, read);
                if(progress >= 0)
                    notifyProgress(request, (int) (progress * 100 / fileLength));
            }
            // notify complete
            notifyProgress(request, 100);

            outputStream.flush();
            outputStream.close();
            inputStream.close();
            // end download file

        } catch (Throwable t) {
            notifyFail(request, t);
        }
    }
}
