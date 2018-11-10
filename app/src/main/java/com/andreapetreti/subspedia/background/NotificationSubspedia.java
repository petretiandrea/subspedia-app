package com.andreapetreti.subspedia.background;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.andreapetreti.android_utils.notification.NotificationHelper;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.model.SubtitleWithSerie;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class NotificationSubspedia extends NotificationHelper {

    private static final String CHANNEL_NEW_SUB = "com.andreapetreti.subspedia.new_subs";
    private static final String CHANNEL_NEW_SUB_NAME = "Uscita nuova sottotitoli";

    private static final String GROUP_NEW_SUB = "com.andreapetreti.subspedia.group.new_sub";

    public NotificationSubspedia(Context base) {
        super(base);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @NonNull
    @Override
    protected List<NotificationChannel> onCreateChannels() {

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_NEW_SUB,
                CHANNEL_NEW_SUB_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.GREEN);
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        return Stream.of(notificationChannel).collect(Collectors.toList());
    }

    @TargetApi(Build.VERSION_CODES.O)
    @NonNull
    @Override
    protected List<NotificationChannelGroup> onCreateGroups() {
        return Collections.emptyList();
    }

    public void notifyNewSub(List<SubtitleWithSerie> newSubs) {
        if(newSubs.size() > 0) {
            /* Generic build of all notifications */
            NotificationCompat.Builder builder = getNotificationBuilder(CHANNEL_NEW_SUB);
            builder.setGroup(GROUP_NEW_SUB);
            builder.setSmallIcon(R.mipmap.ic_small_notification);
            builder.setOnlyAlertOnce(true);
            builder.setOngoing(false);
            builder.setAutoCancel(false);
            builder.setColor(ContextCompat.getColor(this, R.color.primaryColor));

            // style for summary
            NotificationCompat.InboxStyle styleSummary = new NotificationCompat.InboxStyle();

            // create single notification and summary content for all subtitles
            Stream.of(newSubs).forEach(subSerie -> {
                // try to set a bitmap
                try {
                    builder.setLargeIcon(Picasso.get().load(subSerie.getSubtitle().getSubtitleImage()).get());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                builder.setContentTitle(subSerie.getSerie().getName());
                builder.setContentText(String.format(Locale.getDefault(), "%dx%d - %s",
                        subSerie.getSubtitle().getSeasonNumber(),
                        subSerie.getSubtitle().getEpisodeNumber(),
                        subSerie.getSubtitle().getEpisodeTitle()));
                // adding line to summary
                styleSummary.addLine(String.format(Locale.getDefault(), "%s %dx%d - %s",
                        subSerie.getSerie().getName(),
                        subSerie.getSubtitle().getSeasonNumber(),
                        subSerie.getSubtitle().getEpisodeNumber(),
                        subSerie.getSubtitle().getEpisodeTitle()));

                getManager().notify(subSerie.getSubtitle().getIdSerie(), builder.build());
            });

            // create summary and notify it
            String contentText = String.format(Locale.getDefault(),
                    getResources().getQuantityString(R.plurals.notification_content_new_subs, newSubs.size()),
                    newSubs.size());

            styleSummary.setBigContentTitle(contentText);

            builder.setContentTitle(getString(R.string.notification_title_new_subtitles))
                    .setContentText(contentText)
                    .setSmallIcon(R.mipmap.ic_small_notification)
                    .setLargeIcon(null)
                    .setStyle(styleSummary)
                    .setGroupSummary(true);

            getManager().notify(1, builder.build());
        }
    }
}
