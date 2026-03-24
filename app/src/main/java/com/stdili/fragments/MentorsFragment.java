package com.stdili.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.stdili.R;
import com.stdili.activities.ChatActivity;
import com.stdili.adapters.MentorAdapter;
import com.stdili.models.MentorRequest;
import com.stdili.models.User;
import com.stdili.network.ApiClient;
import com.stdili.utils.SecureSessionManager;
import com.stdili.utils.FirebaseHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MentorsFragment extends Fragment implements MentorAdapter.OnMentorClickListener {

    private RecyclerView rvMentors;
    private EditText etSearchSubject, etSearchClass, etSearchLanguage, etSearchAvailability;
    private TextView tvMentorEmpty;
    private ProgressBar progressMentorLoading;
    private MentorAdapter adapter;
    private List<User> allSeniors = new ArrayList<>();
    private List<User> filteredSeniors = new ArrayList<>();
    private User currentUser;
    private ApiClient apiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mentors, container, false);

        rvMentors = view.findViewById(R.id.rvMentors);
        etSearchSubject = view.findViewById(R.id.etSearchSubject);
        etSearchClass = view.findViewById(R.id.etSearchClass);
        etSearchLanguage = view.findViewById(R.id.etSearchLanguage);
        etSearchAvailability = view.findViewById(R.id.etSearchAvailability);
        tvMentorEmpty = view.findViewById(R.id.tvMentorEmpty);
        progressMentorLoading = view.findViewById(R.id.progressMentorLoading);
        apiClient = ApiClient.getInstance(new SecureSessionManager(requireContext()));

        rvMentors.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MentorAdapter(filteredSeniors, this);
        rvMentors.setAdapter(adapter);

        FirebaseHelper.getUser(FirebaseAuth.getInstance().getUid(), user -> {
            currentUser = user;
        });

        loadMentors();

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int count, int after) { filterMentors(); }
            @Override public void afterTextChanged(Editable s) {}
        };

        etSearchSubject.addTextChangedListener(watcher);
        etSearchClass.addTextChangedListener(watcher);
        etSearchLanguage.addTextChangedListener(watcher);
        etSearchAvailability.addTextChangedListener(watcher);

        return view;
    }

    private void loadMentors() {
        String subject = etSearchSubject.getText().toString().trim();
        String className = etSearchClass.getText().toString().trim();
        String language = etSearchLanguage.getText().toString().trim();
        String availability = etSearchAvailability.getText().toString().trim();

        setLoading(true);
        String path = "/api/matching/find-mentor?subject=" + subject + "&classGrade=" + className +
                "&language=" + language + "&availability=" + availability;

        apiClient.get(path, true, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject data) {
                requireActivity().runOnUiThread(() -> {
                    allSeniors.clear();
                    JSONArray mentors = data.optJSONArray("mentors");
                    if (mentors != null) {
                        for (int i = 0; i < mentors.length(); i++) {
                            JSONObject m = mentors.optJSONObject(i);
                            if (m == null) continue;
                            User u = new User();
                            u.setUid(m.optString("_id", ""));
                            u.setName(m.optString("name", "Mentor"));
                            u.setClassGrade(m.optString("classGrade", ""));
                            u.setPreferredLanguage(m.optString("preferredLanguage", ""));
                            u.setAvailability(m.optString("availability", "offline"));
                            u.setOnline(m.optBoolean("isOnline", false));
                            u.setRating(m.optDouble("rating", 0));
                            u.setExperienceYears(m.optInt("experienceYears", 0));
                            u.setStudentsHelped(m.optInt("studentsHelped", 0));
                            u.setMatchScore(m.optDouble("matchScore", 0));

                            JSONArray subjectsJson = m.optJSONArray("subjects");
                            List<String> subjects = new ArrayList<>();
                            if (subjectsJson != null) {
                                for (int s = 0; s < subjectsJson.length(); s++) {
                                    subjects.add(subjectsJson.optString(s));
                                }
                            }
                            u.setSubjects(subjects);
                            allSeniors.add(u);
                        }
                    }
                    filterMentors();
                    setLoading(false);
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    setLoading(false);
                    // Fallback for firebase-based sessions.
                    FirebaseHelper.getSeniors(users -> {
                        allSeniors.clear();
                        allSeniors.addAll(users);
                        filterMentors();
                    });
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void filterMentors() {
        String subject = etSearchSubject.getText().toString().toLowerCase().trim();
        String className = etSearchClass.getText().toString().toLowerCase().trim();
        String language = etSearchLanguage.getText().toString().toLowerCase().trim();
        String availability = etSearchAvailability.getText().toString().toLowerCase().trim();

        filteredSeniors.clear();
        for (User mentor : allSeniors) {
            boolean matchesSubject = subject.isEmpty();
            if (!subject.isEmpty() && mentor.getSubjects() != null) {
                for (String s : mentor.getSubjects()) {
                    if (s.toLowerCase().contains(subject)) {
                        matchesSubject = true;
                        break;
                    }
                }
            }
            
            boolean matchesClass = className.isEmpty() || (mentor.getClassGrade() != null && mentor.getClassGrade().toLowerCase().contains(className));
            boolean matchesLanguage = language.isEmpty() || (mentor.getPreferredLanguage() != null && mentor.getPreferredLanguage().toLowerCase().contains(language));
            boolean matchesAvailability = availability.isEmpty() || (mentor.getAvailability() != null && mentor.getAvailability().toLowerCase().contains(availability));
            
            if (matchesSubject && matchesClass && matchesLanguage && matchesAvailability) {
                filteredSeniors.add(mentor);
            }
        }
        adapter.notifyDataSetChanged();
        tvMentorEmpty.setVisibility(filteredSeniors.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onMentorClick(User mentor) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("mentor_name", mentor.getName());
        intent.putExtra("mentor_id", mentor.getUid());
        startActivity(intent);
    }

    @Override
    public void onRequestClick(User mentor) {
        if (currentUser == null) return;
        setLoading(true);
        
        MentorRequest request = new MentorRequest();
        request.setJuniorId(currentUser.getUid());
        request.setJuniorName(currentUser.getName());
        request.setSeniorId(mentor.getUid());
        request.setSeniorName(mentor.getName());
        request.setStatus("pending");
        request.setTimestamp(System.currentTimeMillis());
        
        JSONObject body = new JSONObject();
        try {
            body.put("seniorId", mentor.getUid());
            body.put("subject", etSearchSubject.getText().toString().trim());
            body.put("note", "Interested in mentorship");
        } catch (Exception ignored) {
        }
        apiClient.post("/api/matching/request", body, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject data) {
                requireActivity().runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(getContext(), "Request sent to " + mentor.getName(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    setLoading(false);
                    FirebaseHelper.sendMentorRequest(request);
                    Toast.makeText(getContext(), "Request saved (fallback): " + mentor.getName(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setLoading(boolean loading) {
        progressMentorLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        etSearchSubject.setEnabled(!loading);
        etSearchClass.setEnabled(!loading);
        etSearchLanguage.setEnabled(!loading);
        etSearchAvailability.setEnabled(!loading);
    }
}