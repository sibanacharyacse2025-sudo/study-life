package com.stdili.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.stdili.R;

public class NotificationHandler {
    public static final String CHANNEL_ID_GENERAL = "stdili_general";

    private final Context appContext;

    public NotificationHandler(Context context) {
        this.appContext = context.getApplicationContext();
        ensureChannel();
    }

    private void ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationManager nm = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;

        NotificationChannel existing = nm.getNotificationChannel(CHANNEL_ID_GENERAL);
        if (existing != null) return;

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID_GENERAL,
                "Stdili notifications",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("General alerts and reminders");
        nm.createNotificationChannel(channel);
    }

    // Local notification (client-side). Remote push to a specific user requires a server/Cloud Function.
    public void notifyUser(String userId, String message) {
        int notificationId = (userId == null ? 0 : Math.abs(userId.hashCode())) ^ (int) System.currentTimeMillis();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext, CHANNEL_ID_GENERAL)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Stdili")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat.from(appContext).notify(notificationId, builder.build());
    }
}

