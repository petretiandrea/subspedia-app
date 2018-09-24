package com.andreapetreti.subspedia.service;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;

import com.andreapetreti.subspedia.Constants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class DownloadService extends IntentService {

    public static final int UPDATE_PROGRESS = 4000;
    public static final int ERROR_DOWNLOAD = 5000;

    private static final String KEY_RECEIVER = "com.andreapetreti.subspedia.receiver";
    private static final String KEY_URL = "com.andreapetreti.subspedia.url";
    private static final String KEY_FILENAME = "com.andreapetreti.subspedia.filename";

    private static final String ACTION_DOWNLOAD = "com.andreapetreti.subspedia.service.action.DOWNLOAD";
    private static final String ACTION_DOWNLOAD_STOP = "com.andreapetreti.subspedia.service.action.DOWNLOAD_STOP";

    public DownloadService() {
        super("DownloadService");
        mURLConnectionMap = new HashMap<>();
    }

    private Map<String, HttpURLConnection> mURLConnectionMap;

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startDownload(Context context, String downloadUrl, /*String filename, */ResultReceiver receiver) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(KEY_RECEIVER, receiver);
        //intent.putExtra(KEY_FILENAME, filename);
        intent.putExtra(KEY_URL, downloadUrl);
        context.startService(intent);
    }

    public static void stopDownload(Context context, String downloadUrl) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_DOWNLOAD_STOP);
        intent.putExtra(KEY_URL, downloadUrl);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                final ResultReceiver param1 = intent.getParcelableExtra(KEY_RECEIVER);
                final String downloadUrl = intent.getStringExtra(KEY_URL);
                handleActionDownload(downloadUrl, param1);
            } else if (ACTION_DOWNLOAD_STOP.equals(action)) {
                final String downloadUrl = intent.getStringExtra(KEY_URL);
                handleActionStop(downloadUrl);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDownload(String downloadUrl, ResultReceiver receiver) {


        /* Do download operation */
        try {
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            mURLConnectionMap.put(downloadUrl, connection);

            // Extract filename
            String content = connection.getHeaderField("Content-Disposition");
            String contentSplit[] = content.split("filename=");
            String filename = contentSplit[1].replace("filename=", "").replace("\"", "").trim();

            String invalid = "|\\?*<\":>+[]/'";
            for(char c : invalid.toCharArray())
                filename = filename.replace(c, '_');

            File file = new File(makeDirs(), filename);

            int length = connection.getContentLength();
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            OutputStream outputStream = new FileOutputStream(file);

            byte data[] = new byte[1024];
            long total = 0;
            int count;

            while ((count = inputStream.read(data)) != -1) {
                // write on output stream
                outputStream.write(data, 0 , count);
                total += count;
                // publish progress
                Bundle bundle = new Bundle();
                bundle.putInt("progress", (int) (total * 100 / length));
                receiver.send(UPDATE_PROGRESS, bundle);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            receiver.send(ERROR_DOWNLOAD, null);
        } finally {
            mURLConnectionMap.remove(downloadUrl);
        }

        Bundle resultData = new Bundle();
        resultData.putInt("progress" ,100);
        receiver.send(UPDATE_PROGRESS, resultData);
    }

    private void handleActionStop(String downloadUrl) {
        if(mURLConnectionMap.containsKey(downloadUrl))
            mURLConnectionMap.get(downloadUrl).disconnect();
    }

    private File makeDirs() {
        File f;
        if (isExternalStorageWritable()) {
            f = new File(Environment.getExternalStorageDirectory(), Constants.DOWNLOAD_FOLDER);
        } else {
            f = getFileStreamPath(Constants.DOWNLOAD_FOLDER);
        }
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    private boolean isExternalStorageWritable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }
}
