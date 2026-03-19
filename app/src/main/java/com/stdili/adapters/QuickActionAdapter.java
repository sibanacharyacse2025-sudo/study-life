package com.stdili.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.models.QuickAction;
import java.util.List;

public class QuickActionAdapter extends RecyclerView.Adapter<QuickActionAdapter.ViewHolder> {

    private List<QuickAction> actions;
    private OnActionClickListener listener;

    public interface OnActionClickListener {
        void onActionClick(QuickAction action);
    }

    public QuickActionAdapter(List<QuickAction> actions, OnActionClickListener listener) {
        this.actions = actions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quick_action, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuickAction action = actions.get(position);
        holder.tvTitle.setText(action.getTitle());
        holder.ivIcon.setImageResource(action.getIcon());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onActionClick(action);
            }
        });
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }
    }
}