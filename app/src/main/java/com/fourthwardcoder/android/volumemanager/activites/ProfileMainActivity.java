package com.fourthwardcoder.android.volumemanager.activites;



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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ProfileMainActivity extends AppCompatActivity implements ProfileMainFragment.Callback, Constants {
	
	/*********************************************************************/
	/*                          Constants                                */
	/*********************************************************************/
	private static final String TAG="ProfileMainActivity";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

	/*********************************************************************/
	/*                         Local Data                                */
	/*********************************************************************/
	ArrayList<Fragment> fragList = new ArrayList<Fragment>();
	TabLayout mTabLayout;
    FloatingActionButton mFloatingActionButton;
	boolean mTwoPane;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.e(TAG, "onCreate()");
		//Change status bar color
	    Util.setStatusBarColor(this);

	    //Set layout
	    setContentView(R.layout.activity_main);


		//Set toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//Create TabLayout for the Profiles (Basic and Location)d
		mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
		mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.time_tab)).setIcon(R.drawable.ic_action_alarm_light));
		mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.location_tab)).setIcon(R.drawable.ic_place_light));
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
				//mProfileType = tab.getPosition();
				Log.e(TAG,"Got tab " + tab.getPosition());
				setFABVisibility();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
		
        mFloatingActionButton = (FloatingActionButton)findViewById(R.id.fab_new_profile);
        //onListViewChange(mProfileType);
	
		setFABVisibility();

        Log.e(TAG,"Check if two pane");
        if((findViewById(R.id.profile_detail_container) != null)) {
            Log.e(TAG,"Got two pane!");
            mTwoPane = true;

                //In two-pane mode, show the detail view in this activity by
                //adding or replacing the detail fragment using a fragment transaction.
                if (savedInstanceState == null) {
                    Log.e(TAG,"Setting Detail Fragment");
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

    public void clickFAB(View view) {

        ProfileManager.newProfile(this,mTabLayout.getSelectedTabPosition());
    }

    private void setFABVisibility() {
		//Log.e(TAG,"!!!! The Listview Changed again!!!!, Selected tab: " + mTabLayout.getSelectedTabPosition());
		if(ProfileManager.isDatabaseEmpty(this,mTabLayout.getSelectedTabPosition())) {
            mFloatingActionButton.setVisibility(View.INVISIBLE);
            Log.e(TAG,"FAB invisible");
        }
		else {
            mFloatingActionButton.setVisibility(View.VISIBLE);
            Log.e(TAG, "FAB visible");
        }
	}

    @Override
    public void onListViewChange() {

           setFABVisibility();
    }

	@Override
	public void onItemSelected(Profile profile, int profileType, TextView textView) {

        if(mTwoPane) {
           Log.e(TAG,"onItemSelected() Two pane");
            Bundle args = new Bundle();
            args.putInt(EXTRA_PROFILE_TYPE, profileType);
            args.putParcelable(EXTRA_PROFILE, profile);

            ProfileDetailFragment fragment = new ProfileDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.profile_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Log.e(TAG,"onItemSelected() 1 pane");
            Intent intent = new Intent(this, ProfileDetailActivity.class);

            //Tell Volume Manager Fragment which Profile to display by making
            //giving id as Intent extra
            intent.putExtra(EXTRA_PROFILE, profile);
            intent.putExtra(EXTRA_PROFILE_TYPE, profileType);

            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                            new Pair<View, String>(textView, getString(R.string.trans_profile_title)));

            startActivity(intent, activityOptions.toBundle());
        }
	}

    @Override
    public void onLoadFinished(Profile profile) {

        if(mTwoPane) {

            //Pass profile of first list item
            Bundle args = new Bundle();
            args.putParcelable(EXTRA_PROFILE,profile);
            args.putInt(EXTRA_PROFILE_TYPE, mTabLayout.getSelectedTabPosition());

            ProfileDetailFragment fragment = new ProfileDetailFragment();
            fragment.setArguments(args);

            //Set first movie in detail pane. Needed to use "commitAllowingStateLoss"
            //instead of just "commit" because calling this directly when the loader
            //was done causes an "illegal state exception"
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.profile_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commitAllowingStateLoss();
        }
    }
}
