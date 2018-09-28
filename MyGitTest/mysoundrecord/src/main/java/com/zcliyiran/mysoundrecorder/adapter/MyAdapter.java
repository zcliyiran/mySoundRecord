package com.zcliyiran.mysoundrecorder.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author 甘罗
 * @date 2018/9/10.
 */
public class MyAdapter extends FragmentPagerAdapter {

    private String[] titles;

    private List<Fragment> list;

    public MyAdapter(FragmentManager fm, String[] titles, List<Fragment> list) {
        super(fm);
        this.list = list;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
