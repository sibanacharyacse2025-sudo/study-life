package com.stdili.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.stdili.models.User;
import java.util.List;

public class FirebaseHelper {

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void saveUser(User user) {
        db.collection("users").document(user.getUid()).set(user);
    }

    public static void syncFcmTokenIfLoggedIn() {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {
                    if (token == null || token.trim().isEmpty()) return;
                    db.collection("users").document(uid)
                            .update("fcmToken", token, "fcmTokenUpdatedAt", System.currentTimeMillis());
                });
    }

    public static void getUser(String uid, OnUserLoadedListener listener) {
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            listener.onUserLoaded(user);
        });
    }

    public static void updateUserXP(String uid, int xpGained) {
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null) {
                user.setXp(user.getXp() + xpGained);
                user.setLevel(LevelSystem.calculateLevel(user.getXp()));
                saveUser(user);
            }
        });
    }

    public static void updateUserCoins(String uid, int coinsGained) {
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null) {
                user.setCoins(user.getCoins() + coinsGained);
                saveUser(user);
            }
        });
    }

    public static void getAllUsers(OnUsersLoadedListener listener) {
        db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            listener.onUsersLoaded(queryDocumentSnapshots.toObjects(User.class));
        });
    }

    public static void getStudents(OnUsersLoadedListener listener) {
        db.collection("users").whereEqualTo("role", "student").get().addOnSuccessListener(queryDocumentSnapshots -> {
            listener.onUsersLoaded(queryDocumentSnapshots.toObjects(User.class));
        });
    }

    public static void getTeachers(OnUsersLoadedListener listener) {
        db.collection("users").whereEqualTo("role", "teacher").get().addOnSuccessListener(queryDocumentSnapshots -> {
            listener.onUsersLoaded(queryDocumentSnapshots.toObjects(User.class));
        });
    }

    public interface OnUserLoadedListener {
        void onUserLoaded(User user);
    }

    public interface OnUsersLoadedListener {
        void onUsersLoaded(List<User> users);
    }
}