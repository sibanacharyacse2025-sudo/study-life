package com.stdili.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.models.Message;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        
        if (message.isUser()) {
            // Show user message (right-aligned, yellow background)
            holder.llUserMessage.setVisibility(View.VISIBLE);
            holder.llAiMessage.setVisibility(View.GONE);
            holder.tvUserMessage.setText(message.getText());
        } else {
            // Show AI message (left-aligned, green background)
            holder.llAiMessage.setVisibility(View.VISIBLE);
            holder.llUserMessage.setVisibility(View.GONE);
            holder.tvAiMessage.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llUserMessage, llAiMessage;
        TextView tvUserMessage, tvAiMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llUserMessage = itemView.findViewById(R.id.llUserMessage);
            llAiMessage = itemView.findViewById(R.id.llAiMessage);
            tvUserMessage = itemView.findViewById(R.id.tvUserMessage);
            tvAiMessage = itemView.findViewById(R.id.tvAiMessage);
        }
    }
}