package com.fourthwardcoder.android.volumemanager.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

//import com.fourthwardcoder.android.volumemanager.fragments.LocationProfileListFragment;
import com.fourthwardcoder.android.volumemanager.fragments.ProfileListFragment;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;

/**
 * Created by Chris Hare on 1/23/2016.
 */
public class ProfilePagerAdapter extends FragmentStatePagerAdapter implements Constants {

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
                ProfileListFragment tab1 = ProfileListFragment.newInstance(TIME_PROFILE_LIST);
                return tab1;
            case 1:
             //   LocationProfileListFragment tab2 = LocationProfileListFragment.newInstance();
            ProfileListFragment tab2 = ProfileListFragment.newInstance(LOCATION_PROFILE_LIST);
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
