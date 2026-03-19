package com.stdili.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.RemoteMessage;
import com.stdili.utils.NotificationHandler;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "StdiliFCM";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = null;
        String body = null;

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        if (body == null && remoteMessage.getData() != null) {
            body = remoteMessage.getData().get("message");
            if (title == null) title = remoteMessage.getData().get("title");
        }

        if (body == null) return;

        String uid = FirebaseAuth.getInstance().getUid();
        new NotificationHandler(getApplicationContext()).notifyUser(uid, body);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "New FCM token received");
        saveTokenToFirestore(token);
    }

    private void saveTokenToFirestore(String token) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .update("fcmToken", token, "fcmTokenUpdatedAt", System.currentTimeMillis())
                .addOnFailureListener(e -> Log.e(TAG, "Failed to save token", e));
    }
}