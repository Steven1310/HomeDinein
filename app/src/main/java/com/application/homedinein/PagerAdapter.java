package com.application.homedinein;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;


/**
 * Fragment to return the clicked tab.
 */
public class PagerAdapter extends FragmentStateAdapter {
    ViewPager2 viewPager;


    public PagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle,ViewPager2 viewPager) {
        super(fragmentManager, lifecycle);

        this.viewPager=viewPager;
    }


    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new TabFragment1();
            case 1: return new TabFragment2();
            default: return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}




