package com.fourthwardcoder.android.volumemanager.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.fourthwardcoder.android.volumemanager.fragments.LocationProfileListFragment;
import com.fourthwardcoder.android.volumemanager.fragments.ProfileListFragment;

/**
 * Created by Chris Hare on 1/23/2016.
 */
public class ProfilePagerAdapter extends FragmentStatePagerAdapter {

    /*************************************************************************/
    /*                             Constants                                 */
    /*************************************************************************/
    final static String TAG = ProfilePagerAdapter.class.getSimpleName();

    /*************************************************************************/
    /*                             Local Data                                */
    /*************************************************************************/
    int mNumOfTabs;

    public ProfilePagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);

        this.mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {


        switch (position) {
            case 0:
                ProfileListFragment tab1 = ProfileListFragment.newInstance();
                return tab1;
            case 1:
                LocationProfileListFragment tab2 = LocationProfileListFragment.newInstance();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
