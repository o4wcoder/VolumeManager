package com.fourthwardcoder.android.volumemanager.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

//import com.fourthwardcoder.android.volumemanager.fragments.LocationProfileListFragment;
import com.fourthwardcoder.android.volumemanager.fragments.ProfileMainFragment;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;

/**
 * Pager Adapter
 * <p>
 * Pager Adapter for the Profile Tabs (Time and Location)
 * <p>
 * Created: 1/23/2016
 *
 * @author Chris Hare
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
                ProfileMainFragment tab1 = ProfileMainFragment.newInstance(TIME_PROFILE_LIST);
                return tab1;
            case 1:
            ProfileMainFragment tab2 = ProfileMainFragment.newInstance(LOCATION_PROFILE_LIST);
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
