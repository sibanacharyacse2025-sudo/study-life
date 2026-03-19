package com.stdili.services;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stdili.models.StudyNote;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Service for AI-generated and manual study notes management
 */
public class NotesService {
    private static final String TAG = "NotesService";
    private FirebaseFirestore db;

    public NotesService() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Save a newly created or AI-generated note
     */
    public void saveNote(StudyNote note, OnNoteSavedListener listener) {
        try {
            db.collection("notes")
                    .add(note)
                    .addOnSuccessListener(documentReference -> {
                        note.setNoteId(documentReference.getId());
                        // Also update with the generated ID
                        documentReference.update("noteId", documentReference.getId());
                        Log.d(TAG, "Note saved: " + documentReference.getId());
                        listener.onNoteSaved(note);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error saving note", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while saving note", e);
            listener.onError(e.getMessage());
        }
    }

    /**
     * Generate AI notes from text
     */
    public void generateAINotes(String userId, String topic, String content, 
                                String subject, OnAINotesGeneratedListener listener) {
        try {
            // Create a note with AI-generated flag
            StudyNote note = new StudyNote(userId, "AI Notes: " + topic, content);
            note.setSubject(subject);
            note.setTopic(topic);
            note.setGeneratedBy("ai");
            
            // Call Gemini API to enhance/summarize notes
            String enhancementPrompt = "Create well-organized study notes from the following text. "
                    + "Format with clear sections, bullet points, and key concepts. "
                    + "Subject: " + subject + ", Topic: " + topic + "\n\nContent:\n" + content;

            // This would call your AI service (Gemini)
            // For now, we'll just save as-is
            listener.onNotesGenerated(note);

        } catch (Exception e) {
            Log.e(TAG, "Exception while generating AI notes", e);
            listener.onError(e.getMessage());
        }
    }

    /**
     * Get all notes for a user
     */
    public void getUserNotes(String userId, OnNotesLoadedListener listener) {
        try {
            db.collection("notes")
                    .whereEqualTo("userId", userId)
                    .orderBy("updatedAt")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<StudyNote> notes = new ArrayList<>();
                        queryDocumentSnapshots.forEach(doc -> {
                            StudyNote note = doc.toObject(StudyNote.class);
                            note.setNoteId(doc.getId());
                            notes.add(note);
                        });
                        listener.onNotesLoaded(notes);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading notes", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while loading notes", e);
            listener.onError(e.getMessage());
        }
    }

    /**
     * Get AI-generated notes for a user
     */
    public void getAIGeneratedNotes(String userId, OnNotesLoadedListener listener) {
        try {
            db.collection("notes")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("generatedBy", "ai")
                    .orderBy("createdAt")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<StudyNote> notes = new ArrayList<>();
                        queryDocumentSnapshots.forEach(doc -> {
                            StudyNote note = doc.toObject(StudyNote.class);
                            note.setNoteId(doc.getId());
                            notes.add(note);
                        });
                        listener.onNotesLoaded(notes);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading AI notes", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while loading AI notes", e);
            listener.onError(e.getMessage());
        }
    }

    /**
     * Update a note
     */
    public void updateNote(StudyNote note, OnUpdateListener listener) {
        try {
            note.setUpdatedAt(System.currentTimeMillis());
            
            db.collection("notes")
                    .document(note.getNoteId())
                    .set(note)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Note updated: " + note.getNoteId());
                        listener.onUpdateSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error updating note", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while updating note", e);
            listener.onError(e.getMessage());
        }
    }

    /**
     * Mark a note as favorite
     */
    public void toggleFavorite(String noteId, boolean isFavorite, OnUpdateListener listener) {
        try {
            db.collection("notes")
                    .document(noteId)
                    .update("isFavorite", isFavorite)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Note favorite status updated: " + noteId);
                        listener.onUpdateSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error updating favorite status", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while toggling favorite", e);
            listener.onError(e.getMessage());
        }
    }

    /**
     * Delete a note
     */
    public void deleteNote(String noteId, OnUpdateListener listener) {
        try {
            db.collection("notes")
                    .document(noteId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Note deleted: " + noteId);
                        listener.onUpdateSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error deleting note", e);
                        listener.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while deleting note", e);
            listener.onError(e.getMessage());
        }
    }

    // Listener Interfaces
    public interface OnNoteSavedListener {
        void onNoteSaved(StudyNote note);
        void onError(String errorMessage);
    }

    public interface OnAINotesGeneratedListener {
        void onNotesGenerated(StudyNote note);
        void onError(String errorMessage);
    }

    public interface OnNotesLoadedListener {
        void onNotesLoaded(List<StudyNote> notes);
        void onError(String errorMessage);
    }

    public interface OnUpdateListener {
        void onUpdateSuccess();
        void onError(String errorMessage);
    }
}
