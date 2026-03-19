package com.stdili.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.stdili.fragments.MentorsFragment;
import com.stdili.fragments.PeersFragment;
import com.stdili.fragments.GroupsFragment;
import com.stdili.fragments.AIChatFragment;

public class NetworkPagerAdapter extends FragmentStateAdapter {

    public NetworkPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MentorsFragment();
            case 1:
                return new PeersFragment();
            case 2:
                return new GroupsFragment();
            case 3:
                return new AIChatFragment();
            default:
                return new MentorsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}