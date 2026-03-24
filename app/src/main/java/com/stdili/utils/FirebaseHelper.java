package com.stdili.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.stdili.models.MentorRequest;
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
            if (listener != null) listener.onUserLoaded(user);
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

    public static void updateUserPoints(String uid, int pointsGained) {
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null) {
                user.setPoints(user.getPoints() + pointsGained);
                saveUser(user);
            }
        });
    }

    public static void getAllUsers(OnUsersLoadedListener listener) {
        db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            listener.onUsersLoaded(queryDocumentSnapshots.toObjects(User.class));
        });
    }

    public static void getJuniors(OnUsersLoadedListener listener) {
        db.collection("users").whereEqualTo("role", "junior").get().addOnSuccessListener(queryDocumentSnapshots -> {
            listener.onUsersLoaded(queryDocumentSnapshots.toObjects(User.class));
        });
    }

    public static void getSeniors(OnUsersLoadedListener listener) {
        db.collection("users").whereEqualTo("role", "senior").get().addOnSuccessListener(queryDocumentSnapshots -> {
            listener.onUsersLoaded(queryDocumentSnapshots.toObjects(User.class));
        });
    }

    public static void sendMentorRequest(MentorRequest request) {
        String id = db.collection("mentor_requests").document().getId();
        request.setId(id);
        db.collection("mentor_requests").document(id).set(request);
    }

    public static void getMentorRequestsForSenior(String seniorId, OnRequestsLoadedListener listener) {
        db.collection("mentor_requests").whereEqualTo("seniorId", seniorId).get().addOnSuccessListener(queryDocumentSnapshots -> {
            listener.onRequestsLoaded(queryDocumentSnapshots.toObjects(MentorRequest.class));
        });
    }

    public static void updateMentorRequestStatus(String requestId, String status) {
        db.collection("mentor_requests").document(requestId).update("status", status);
    }

    public interface OnUserLoadedListener {
        void onUserLoaded(User user);
    }

    public interface OnUsersLoadedListener {
        void onUsersLoaded(List<User> users);
    }

    public interface OnRequestsLoadedListener {
        void onRequestsLoaded(List<MentorRequest> requests);
    }
}