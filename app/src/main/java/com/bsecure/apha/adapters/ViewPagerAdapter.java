package com.bsecure.apha.adapters;

import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bsecure.apha.fragments.ParentFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<ParentFragment> mFragmentList = new ArrayList<>();
    private List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public ParentFragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void clear() {
        try {

            mFragmentList.clear();
            mFragmentList = null;

            mFragmentTitleList.clear();
            mFragmentTitleList = null;

        } catch (Exception e) {
            e.printStackTrace();
            //TraceUtils.logException(e);
        }
    }

    public void addFrag(ParentFragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public Parcelable saveState()
    {
        return null;
    }
}