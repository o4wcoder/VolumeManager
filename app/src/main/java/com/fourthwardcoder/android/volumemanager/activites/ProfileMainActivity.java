package com.fourthwardcoder.android.volumemanager.activites;


import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import java.util.ArrayList;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.adapters.ProfilePagerAdapter;
import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
import com.fourthwardcoder.android.volumemanager.fragments.ProfileDetailFragment;
import com.fourthwardcoder.android.volumemanager.fragments.ProfileMainFragment;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.Profile;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Main App Activity
 * <p>
 * Launcher Activity for the app. Holds a page viewer for the different types of volume control
 * profiles
 * <p>
 * Created: 1/17/16
 *
 * @author Chris Hare
 */
public class ProfileMainActivity extends AppCompatActivity implements ProfileMainFragment.Callback,
        Constants {

    /*********************************************************************/
    /*                          Constants                                */
    /*********************************************************************/
    private static final String TAG = "ProfileMainActivity";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    /*********************************************************************/
	/*                         Local Data                                */
    /*********************************************************************/
    TabLayout mTabLayout;
    FloatingActionButton mFloatingActionButton;
    boolean mTwoPane;
    Profile mFirstTimeProfile = null;
    Profile mFirstLocationProfile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        Log.e(TAG, "onCreate()");
        //Change status bar color
        Util.setStatusBarColor(this);

        //Set layout
        setContentView(R.layout.activity_main);

        //Set toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set Defaults for Shared Preferences
        PreferenceManager.setDefaultValues(this,R.xml.settings_pref,false);

        //Create TabLayout for the Profiles (Basic and Location)d
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.addTab(mTabLayout.newTab()
                .setText(getString(R.string.time_tab))
                .setIcon(R.drawable.ic_action_alarm_light));
              //  .setContentDescription(getString(R.string.cont_desc_time_profile_tab)));
        mTabLayout.addTab(mTabLayout.newTab()
                .setText(getString(R.string.location_tab))
                .setIcon(R.drawable.ic_place_light));
               // .setContentDescription(getString(R.string.cont_desc_location_profile_tab)));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Create ViewPager to swipe between the tabs
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new ProfilePagerAdapter(getSupportFragmentManager(),
                mTabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                //Check if Floating Action Button should be shown/hidden based on if the tab's
                //ListView is empty or not
                setFABVisibility();

                //Change detail fragment to first in list when changing tabs
                if (tab.getPosition() == TIME_PROFILE_LIST)
                    setTwoFrameDetailFragment(mFirstTimeProfile, TIME_PROFILE_LIST);
                else
                    setTwoFrameDetailFragment(mFirstLocationProfile, LOCATION_PROFILE_LIST);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab_new_profile);

        //Set New Profile Floating Action Button Visibility
        setFABVisibility();
        //See if we are in two pane tablet mode.
        if ((findViewById(R.id.profile_detail_container) != null)) {

            mTwoPane = true;
            //In two-pane mode, show the detail view in this activity by
            //adding or replacing the detail fragment using a fragment transaction.
            if (savedInstanceState == null) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.profile_detail_container, new ProfileDetailFragment(),
                                DETAILFRAGMENT_TAG)
                        .commit();
            }

        } else {
            mTwoPane = false;
        }

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflate menu for main activity toolbar
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Get menu option by it's ID
        switch (item.getItemId()) {

            case R.id.menu_item_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /******************************************************************************/
    /*                             Private Methods                                */
    /******************************************************************************/

    /**
     * Sets the visibility of the New Profile Floating Action button. It is visible if there
     * are other profiles in the list. If not, the emtpy view is displayed and used to
     * create a new profile.
     */
    private void setFABVisibility() {
        if (ProfileManager.isDatabaseEmpty(this, mTabLayout.getSelectedTabPosition())) {
            mFloatingActionButton.setVisibility(View.INVISIBLE);
        } else {
            mFloatingActionButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sets up the detail profile fragment if in two pane tablet mode
     *
     * @param profile     Profile object to send to the detail fragment
     * @param profileType profile type (Time/Location)
     */
    private void setTwoFrameDetailFragment(Profile profile, int profileType) {

        if (mTwoPane) {
            if (profileType == mTabLayout.getSelectedTabPosition()) {

                if (profile != null) {

                    //Pass profile of first list item
                    Bundle args = new Bundle();
                    args.putParcelable(EXTRA_PROFILE, profile);
                    //  Log.e(TAG, "onLoadFinished() setting profile " + profile.getTitle() + " at tab pos " + mTabLayout.getSelectedTabPosition());
                    args.putInt(EXTRA_PROFILE_TYPE, mTabLayout.getSelectedTabPosition());

                    ProfileDetailFragment fragment = new ProfileDetailFragment();
                    fragment.setArguments(args);

                    //Set first profile in detail pane. Needed to use "commitAllowingStateLoss"
                    //instead of just "commit" because calling this directly when the loader
                    //was done causes an "illegal state exception"
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.profile_detail_container, fragment, DETAILFRAGMENT_TAG)
                            .commitAllowingStateLoss();
                }
            }
        }
    }

    /*****************************************************************************/
    /*                          Public Methods                                   */
    /*****************************************************************************/

    /**
     * Callback Listener for New Profile Floating Action Button. Creates a new profile
     * when selected.
     *
     * @param view view that was clicked
     */
    public void clickFAB(View view) {

        newProfile(mTabLayout.getSelectedTabPosition());
    }

    /**
     * Callback from ProfileMainFragment used when there is a data set change in the list of profiles.
     * Need to check if the New Profile Floating Action button should be hidden or not.
     */
    @Override
    public void onListViewChange() {

        setFABVisibility();
    }

    /**
     * Callback from ProfileMainFragment used when a profile was selected from the list. If we are
     * in two pane mode, then set the detail fragment of the profile selected.
     *
     * @param profile     Profile selected in list
     * @param profileType Type of profile (Time/Location)
     * @param textView    textView of the title of the profile. This is used for transitions in
     *                    portrait mode.
     */
    @Override
    public void onItemSelected(Profile profile, int profileType, TextView textView) {

        if (mTwoPane) {

            Bundle args = new Bundle();
            args.putInt(EXTRA_PROFILE_TYPE, profileType);
            args.putParcelable(EXTRA_PROFILE, profile);

            ProfileDetailFragment fragment = new ProfileDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.profile_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {

            Intent intent = new Intent(this, ProfileDetailActivity.class);

            //Tell Volume Manager Fragment which Profile to display by making
            //giving id as Intent extra
            intent.putExtra(EXTRA_PROFILE, profile);
            intent.putExtra(EXTRA_PROFILE_TYPE, profileType);

            //Set transition for the profile title between the list and the detail.
            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                            new Pair<View, String>(textView, getString(R.string.trans_profile_title)));

            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
        }
    }

    /**
     * Callback from ProfileMainFragment used to let the activity know that the list of profiles
     * has been loaded from the database.
     *
     * @param profile     profile to set in the detail fragment when first going into two pane mode. Most
     *                    likely the first profile in the list.
     * @param profileType type of profile (Time/Location)
     */
    @Override
    public void onLoadFinished(Profile profile, int profileType) {

        //Store the first profile in the list so it can be set when changing tabs
        if (profileType == TIME_PROFILE_LIST)
            mFirstTimeProfile = profile;
        else
            mFirstLocationProfile = profile;

        setTwoFrameDetailFragment(profile, profileType);
    }

    /**
     * Callback from ProfileMainFragment used to create a new Profile
     *
     * @param profileType
     */
    @Override
    public void newProfile(int profileType) {

        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putInt(EXTRA_PROFILE_TYPE, profileType);

            ProfileDetailFragment fragment = new ProfileDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.profile_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent i = new Intent(this, ProfileDetailActivity.class);
            i.putExtra(EXTRA_PROFILE_TYPE, profileType);
            //startActivityForResult(i,0);

            //Start activity with transition, but no shared Title since it's new
            startActivity(i, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());

        }
    }

}
