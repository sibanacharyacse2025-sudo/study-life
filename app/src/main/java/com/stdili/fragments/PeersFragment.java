package com.stdili.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.stdili.R;
import com.stdili.adapters.PeerAdapter;
import com.stdili.models.Peer;
import java.util.ArrayList;
import java.util.List;

public class PeersFragment extends Fragment implements PeerAdapter.OnPeerClickListener {

    private RecyclerView rvPeers;
    private PeerAdapter adapter;
    private List<Peer> peers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_peers, container, false);

        rvPeers = view.findViewById(R.id.rvPeers);

        peers = new ArrayList<>();
        loadPeers();

        adapter = new PeerAdapter(peers, this);
        rvPeers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPeers.setAdapter(adapter);

        return view;
    }

    private void loadPeers() {
        // Load from Firebase
        peers.add(new Peer("Alice Johnson", "Grade 12, Math Major", "Studying Calculus", true));
        peers.add(new Peer("Bob Smith", "Grade 11, Science", "Physics project", false));
        peers.add(new Peer("Charlie Brown", "Grade 10, Literature", "Essay writing", true));
        peers.add(new Peer("Diana Prince", "Grade 12, History", "World War II research", true));
        peers.add(new Peer("Eve Wilson", "Grade 11, Art", "Digital painting", false));
    }

    @Override
    public void onPeerClick(Peer peer) {
        // Open chat or profile
    }

    @Override
    public void onConnectClick(Peer peer) {
        // Send connection request
    }
}