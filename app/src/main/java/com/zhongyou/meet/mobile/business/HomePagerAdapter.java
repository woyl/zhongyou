package com.zhongyou.meet.mobile.business;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by wufan on 2017/7/26.
 */

public class HomePagerAdapter extends FragmentPagerAdapter {
    public static final String TAG = "HomePagerAdapter";
    private ArrayList<Fragment> mData;



    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    public void setData(ArrayList<Fragment> data) {
        this.mData = data;
    }

    public ArrayList<Fragment> getData() {
        return mData;
    }


}
