package com.ispc.gymapp.views.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ispc.gymapp.views.fragments.*;


public class OnboardingStateAdapter extends FragmentStateAdapter {

    public OnboardingStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return GenderFragment.newInstance();
            case 1:
                return WeightFragment.newInstance();
            case 2:
                return HeightFragment.newInstance();
            case 3:
                return GoalFragment.newInstance();
            default:
                return null;
        }

    }

}