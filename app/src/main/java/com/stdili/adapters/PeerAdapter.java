package com.stdili.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.models.Peer;
import java.util.List;

public class PeerAdapter extends RecyclerView.Adapter<PeerAdapter.ViewHolder> {

    private List<Peer> peers;
    private OnPeerClickListener listener;

    public interface OnPeerClickListener {
        void onPeerClick(Peer peer);
        void onConnectClick(Peer peer);
    }

    public PeerAdapter(List<Peer> peers, OnPeerClickListener listener) {
        this.peers = peers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Peer peer = peers.get(position);
        holder.tvName.setText(peer.getName());
        holder.tvDetails.setText(peer.getDetails());
        holder.tvStatus.setText(peer.getStatus());
        holder.tvOnlineStatus.setText(peer.isOnline() ? "Online" : "Offline");
        holder.tvOnlineStatus.setTextColor(holder.itemView.getContext().getColor(
            peer.isOnline() ? R.color.teal_200 : R.color.text_secondary));

        holder.btnConnect.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConnectClick(peer);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPeerClick(peer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return peers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvStatus, tvOnlineStatus;
        Button btnConnect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvOnlineStatus = itemView.findViewById(R.id.tvOnlineStatus);
            btnConnect = itemView.findViewById(R.id.btnConnect);
        }
    }
}