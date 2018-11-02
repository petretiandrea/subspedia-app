package com.andreapetreti.android_utils.downloadmanager;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;

import com.andreapetreti.android_utils.R;

public class DownloadIntentService extends IntentService implements Downloader.DownloadListener {
    /* Intent params */
    protected static String REQUEST = "request";
    protected static String SMALL_ICON = "small_icon";
    protected static String LARGE_ICON = "large_icon";
    protected static String NOTIFICATION_COLOR = "notification_color";
    protected static String COMPLETE_PENDING_INTENT = "complete_pending_intent";

    private static final String CHANNEL_ONE_ID = "download_intent_service";
    private static final CharSequence CHANNEL_ONE_NAME = "Download Channel";

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private Downloader mDownloader;

    private int mNotificationId;

    public static Intent obtainIntent(Context context, DownloadManager.Request request, int smallIcon,
                                      int largeIcon, int notificationColor) {
        Intent intent = new Intent(context, DownloadIntentService.class);
        intent.putExtra(REQUEST, request);
        intent.putExtra(SMALL_ICON, smallIcon);
        intent.putExtra(LARGE_ICON, largeIcon);
        intent.putExtra(NOTIFICATION_COLOR, notificationColor);
        return intent;
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public DownloadIntentService() {
        super("IntentDownload");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloader = new Downloader();
        mDownloader.setListener(this);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createChannels();
    }

    private void createChannels() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Parcelable parcelable = intent.getParcelableExtra(REQUEST);
        if(parcelable instanceof DownloadManager.Request) {
            DownloadManager.Request request = (DownloadManager.Request) parcelable;

            // resource for notification
            int smallIcon = intent.getIntExtra(SMALL_ICON, -1);
            int largeIcon = intent.getIntExtra(LARGE_ICON, -1);
            int color = intent.getIntExtra(NOTIFICATION_COLOR, -1);

            mNotificationId = request.getId();
            mBuilder = new NotificationCompat.Builder(this, CHANNEL_ONE_ID)
                    .setSmallIcon(smallIcon)
                    .setProgress(0, 0, true);

            if(color != -1)
                mBuilder.setColor(color);

            if(largeIcon != -1)
                mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), largeIcon));

            // locking method, wait for download
            mDownloader.download(request, this);
        }
    }

    @Override
    public void onStart(DownloadManager.Request request) {
        // show notification
        mBuilder.setContentTitle(request.getTitle())
                .setContentText(request.getDescription())
                .setProgress(0, 0, true)
                .setAutoCancel(true)
                .setOngoing(true);

        mNotificationManager.notify(request.getId(), mBuilder.build());
    }

    @Override
    public void onProgressChange(DownloadManager.Request request, int progress) {
        if(progress < 100) {
            if(progress >= 0)
                mBuilder.setProgress(100, progress, false);
            else
                mBuilder.setProgress(0, 0, true);

            mNotificationManager.notify(request.getId(), mBuilder.build());
        } else {
            // complete download
            mNotificationManager.cancel(request.getId());

            mBuilder.setContentTitle(request.getTitle())
                    .setContentText(getString(R.string.download_complete))
                    .setProgress(0, 0, false)
                    .setNumber(request.getId())
                    .setOngoing(false)
                    .setAutoCancel(false);

            mNotificationManager.notify(request.getId(), mBuilder.build());
        }
    }

    @Override
    public void onFail(DownloadManager.Request request, Throwable t) {
        mBuilder.setContentText(getString(R.string.download_fail)).setProgress(0, 0, false);
        mBuilder.setOngoing(false);
        mBuilder.setAutoCancel(false);
        mNotificationManager.notify(request.getId(), mBuilder.build());
    }
}
