package com.ispc.gymapp.views.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ispc.gymapp.model.User;
import com.ispc.gymapp.views.fragments.PerformanceFragment;
import com.ispc.gymapp.views.fragments.ProfileFragment;

public class ProfileAdapter extends FragmentStateAdapter {

    private User user;
    public ProfileAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,User user) {
        super(fragmentManager, lifecycle);
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new PerformanceFragment();
        }
        return ProfileFragment.newInstance("param1", "param2", this.user);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public void setUser(User user){
        this.user = user;
    }
}
