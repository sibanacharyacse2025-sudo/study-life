package com.stdili.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.stdili.R;
import com.stdili.adapters.MentorRequestAdapter;
import com.stdili.models.MentorRequest;
import com.stdili.network.ApiClient;
import com.stdili.utils.SecureSessionManager;
import com.stdili.utils.FirebaseHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardActivity extends AppCompatActivity implements MentorRequestAdapter.OnRequestActionListener {

    private RecyclerView rvRequests;
    private TextView tvTitle;
    private List<MentorRequest> requests = new ArrayList<>();
    private MentorRequestAdapter adapter;
    private ApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        rvRequests = findViewById(R.id.rvStudents); // reusing original ID for simplicity
        tvTitle = findViewById(R.id.tvTitle);
        
        if (tvTitle != null) tvTitle.setText("Senior (Mentor) Dashboard");

        apiClient = ApiClient.getInstance(new SecureSessionManager(this));
        adapter = new MentorRequestAdapter(requests, this);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        rvRequests.setAdapter(adapter);

        loadRequests();
    }

    private void loadRequests() {
        apiClient.get("/api/notifications", true, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject data) {
                runOnUiThread(() -> {
                    requests.clear();
                    JSONArray arr = data.optJSONArray("notifications");
                    if (arr != null) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject n = arr.optJSONObject(i);
                            if (n == null || !"mentor_request".equals(n.optString("type"))) continue;
                            MentorRequest req = new MentorRequest();
                            req.setId(n.optString("_id"));
                            req.setJuniorName(n.optString("body"));
                            req.setStatus("pending");
                            requests.add(req);
                        }
                    }
                    if (requests.isEmpty()) {
                        tvTitle.setText("No mentor requests yet");
                    }
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> FirebaseHelper.getMentorRequestsForSenior(FirebaseAuth.getInstance().getUid(), reqList -> {
                    requests.clear();
                    requests.addAll(reqList);
                    if (requests.isEmpty()) {
                        tvTitle.setText("No mentor requests yet");
                    }
                    adapter.notifyDataSetChanged();
                }));
            }
        });
    }

    @Override
    public void onAccept(MentorRequest request, int position) {
        updateRequestStatus(request, position, "accepted");
    }

    @Override
    public void onReject(MentorRequest request, int position) {
        updateRequestStatus(request, position, "rejected");
    }

    private void updateRequestStatus(MentorRequest request, int position, String status) {
        JSONObject body = new JSONObject();
        try {
            body.put("status", status);
        } catch (Exception ignored) {
        }
        apiClient.patch("/api/matching/request/" + request.getId(), body, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject data) {
                runOnUiThread(() -> {
                    request.setStatus(status);
                    adapter.notifyItemChanged(position);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    FirebaseHelper.updateMentorRequestStatus(request.getId(), status);
                    request.setStatus(status);
                    adapter.notifyItemChanged(position);
                    Toast.makeText(TeacherDashboardActivity.this, "Updated with fallback", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}