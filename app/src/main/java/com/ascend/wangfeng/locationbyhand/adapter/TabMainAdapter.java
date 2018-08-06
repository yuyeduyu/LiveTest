package com.ascend.wangfeng.locationbyhand.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;

import com.ascend.wangfeng.locationbyhand.view.myview.draglayout.DragLayout;

/**
 * Created by fengye on 2016/9/21.
 * email 1040441325@qq.com
 * 主界面加载fragment的适配器
 */
public class TabMainAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener{
    private int count;
    private String[] titles;
    private Fragment[] mFragments;
    private  DragLayout dl;
    public TabMainAdapter(FragmentManager fm, int count, String[] titles, Fragment[] fragments, DragLayout dl) {
        super(fm);
        this.count=count;
        this.titles=titles;
        this.mFragments=fragments;
        this.dl = dl;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getTitle(titles[position]);
    }

    /**
     * @param title
     * @return 绘制的标题
     */
    private CharSequence getTitle(String title){
        SpannableString sb=new SpannableString(title);
        return sb;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (0 == position & null!=dl) {
            dl.setDrag(true);
//                alphaIndicator.getTabView(0).showNumber(alphaIndicator.getTabView(0).getBadgeNumber() - 1);
        } else if (1 == position & null!=dl) {
            dl.setDrag(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

