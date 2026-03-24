package com.stdili.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.models.MentorRequest;
import java.util.List;

public class MentorRequestAdapter extends RecyclerView.Adapter<MentorRequestAdapter.ViewHolder> {

    private List<MentorRequest> requests;
    private final OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onAccept(MentorRequest request, int position);
        void onReject(MentorRequest request, int position);
    }

    public MentorRequestAdapter(List<MentorRequest> requests, OnRequestActionListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mentor_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MentorRequest request = requests.get(position);
        holder.tvJuniorName.setText(request.getJuniorName() + " wants you as a mentor!");
        holder.tvStatus.setText("Status: " + request.getStatus());
        
        if (request.getStatus().equals("pending")) {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
        } else {
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }

        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) listener.onAccept(request, position);
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onReject(request, position);
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJuniorName, tvStatus;
        Button btnAccept, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJuniorName = itemView.findViewById(R.id.tvJuniorName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}