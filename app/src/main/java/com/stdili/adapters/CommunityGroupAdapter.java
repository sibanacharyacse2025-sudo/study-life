package com.stdili.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.models.CommunityGroup;
import java.util.ArrayList;
import java.util.List;

public class CommunityGroupAdapter extends RecyclerView.Adapter<CommunityGroupAdapter.ViewHolder> {
    private List<CommunityGroup> groups;
    private OnGroupClickListener listener;

    public interface OnGroupClickListener {
        void onGroupClick(CommunityGroup group);
    }

    public CommunityGroupAdapter(List<CommunityGroup> groups, OnGroupClickListener listener) {
        this.groups = groups;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView groupName;
        public TextView groupDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.tvGroupName);
            groupDescription = itemView.findViewById(R.id.tvGroupDescription);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_group, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CommunityGroup group = groups.get(position);
        holder.groupName.setText(group.getGroupName());
        holder.groupDescription.setText(group.getGroupDescription());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGroupClick(group);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void setGroups(List<CommunityGroup> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

    public void setListener(OnGroupClickListener listener) {
        this.listener = listener;
    }
}
