package com.stdili.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.models.Mentor;
import java.util.List;

public class MentorAdapter extends RecyclerView.Adapter<MentorAdapter.ViewHolder> {

    private List<Mentor> mentors;
    private OnMentorClickListener listener;

    public interface OnMentorClickListener {
        void onMentorClick(Mentor mentor);
    }

    public MentorAdapter(List<Mentor> mentors, OnMentorClickListener listener) {
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
        Mentor mentor = mentors.get(position);
        holder.tvName.setText(mentor.getName());
        holder.tvSubject.setText(mentor.getSubject());
        holder.tvStatus.setText(mentor.isOnline() ? "Online" : "Offline");
        holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(
            mentor.isOnline() ? R.color.teal_200 : R.color.text_secondary));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMentorClick(mentor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mentors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSubject, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}