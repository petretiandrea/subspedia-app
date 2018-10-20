package com.andreapetreti.android_utils.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.andreapetreti.android_utils.R;

import java.util.List;

/**
 * Project Android Utils
 * Package it.petretiandrea.sensors.utils
 * Created by Petreti Andrea petretiandrea@gmail.com.
 * Created at 20/11/17.
 */

public abstract class NotificationHelper extends ContextWrapper {

    private NotificationManagerCompat mNotificationManagerCompat;

    public NotificationHelper(Context base) {
        super(base);
        createChannelsAndGroups();
    }

    @TargetApi(android.os.Build.VERSION_CODES.O)
    @NonNull
    protected abstract List<NotificationChannel> onCreateChannels();

    @NonNull
    protected abstract List<NotificationChannelGroup> onCreateGroups();

    private void createChannelsAndGroups()
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && notificationManager != null) {
            notificationManager.createNotificationChannels(onCreateChannels());
            notificationManager.createNotificationChannelGroups(onCreateGroups());
        }
    }

    protected NotificationCompat.Builder getNotificationBuilder(String channelId) {
        return new NotificationCompat.Builder(this, channelId);
    }

    public void notify(int id, NotificationCompat.Builder notificationBuilder)
    {
        getManager().notify(id, notificationBuilder.build());

    }

    protected NotificationManagerCompat getManager() {
        if (mNotificationManagerCompat == null)
            mNotificationManagerCompat = NotificationManagerCompat.from(this);
        return mNotificationManagerCompat;
    }

}
