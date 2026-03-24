package com.stdili.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.stdili.R;
import com.stdili.adapters.PeerAdapter;
import com.stdili.models.Peer;
import com.stdili.models.User;
import com.stdili.utils.FirebaseHelper;
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
        String myUid = FirebaseAuth.getInstance().getUid();
        FirebaseHelper.getJuniors(users -> {
            peers.clear();
            for (User user : users) {
                if (myUid != null && myUid.equals(user.getUid())) {
                    continue;
                }
                String subtitle = (user.getClassGrade() == null ? "" : user.getClassGrade()) + " | " +
                        (user.getSubjects() == null ? "No subjects" : String.join(", ", user.getSubjects()));
                peers.add(new Peer(user.getName(), subtitle, user.getGoals(), true));
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });
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