package com.stdili.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.stdili.R;
import com.stdili.adapters.NetworkPagerAdapter;

public class NetworkFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        NetworkPagerAdapter adapter = new NetworkPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.mentors_tab);
                    break;
                case 1:
                    tab.setText(R.string.peers_tab);
                    break;
                case 2:
                    tab.setText(R.string.groups_tab);
                    break;
                case 3:
                    tab.setText(R.string.ai_chat_tab);
                    break;
            }
        }).attach();

        return view;
    }
}