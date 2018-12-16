package com.example.hauntarl.beproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by hauntarl on 15/12/18.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    int NoOfTabs;

    public PagerAdapter(FragmentManager fm, int NumberOfTabs)
    {
       super(fm);
       this.NoOfTabs=NumberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                Extract extract = new Extract();
                return extract;
            case 1:
                Map map = new Map();
                return map;
            case 2:
                Upload upload = new Upload();
                return upload;
            case 3:
                Driver driver = new Driver();
                return driver;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NoOfTabs;
    }
}
