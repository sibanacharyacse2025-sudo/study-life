package com.stdili.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.models.User;
import java.util.List;

public class MentorAdapter extends RecyclerView.Adapter<MentorAdapter.ViewHolder> {

    private List<User> mentors;
    private OnMentorClickListener listener;

    public interface OnMentorClickListener {
        void onMentorClick(User mentor);
        void onRequestClick(User mentor);
    }

    public MentorAdapter(List<User> mentors, OnMentorClickListener listener) {
        this.mentors = mentors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mentor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User mentor = mentors.get(position);
        holder.tvName.setText(mentor.getName());
        holder.tvClass.setText(mentor.getClassGrade());
        
        StringBuilder subjects = new StringBuilder("Subjects: ");
        if (mentor.getSubjects() != null) {
            for (int i = 0; i < mentor.getSubjects().size(); i++) {
                subjects.append(mentor.getSubjects().get(i));
                if (i < mentor.getSubjects().size() - 1) subjects.append(", ");
            }
        }
        holder.tvSubjects.setText(subjects.toString());
        String status = (mentor.isOnline() || "online".equalsIgnoreCase(mentor.getAvailability())) ? "Online" : "Offline";
        holder.tvStatus.setText(status + " • Rating " + mentor.getRating() + " • Score " + (int) mentor.getMatchScore());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onMentorClick(mentor);
        });

        holder.btnRequest.setOnClickListener(v -> {
            if (listener != null) listener.onRequestClick(mentor);
        });
    }

    @Override
    public int getItemCount() {
        return mentors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvClass, tvSubjects, tvStatus;
        Button btnRequest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvClass = itemView.findViewById(R.id.tvClass);
            tvSubjects = itemView.findViewById(R.id.tvSubjects);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnRequest = itemView.findViewById(R.id.btnRequest);
        }
    }
}