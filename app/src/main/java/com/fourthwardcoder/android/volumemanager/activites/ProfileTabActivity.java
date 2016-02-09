package com.fourthwardcoder.android.volumemanager.activites;



import java.util.ArrayList;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.adapters.ProfilePagerAdapter;
import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
import com.fourthwardcoder.android.volumemanager.fragments.ProfileListFragment;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;


import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;

public class ProfileTabActivity extends AppCompatActivity implements ProfileListFragment.Callback, Constants {
	
	/*********************************************************************/
	/*                          Constants                                */
	/*********************************************************************/
	private static final String TAG="ProfileTabActivity";
	
	/*********************************************************************/
	/*                         Local Data                                */
	/*********************************************************************/
	ArrayList<Fragment> fragList = new ArrayList<Fragment>();
	TabLayout mTabLayout;
    FloatingActionButton mFloatingActionButton;
	//int mProfileType;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		//Change status bar color
	    Util.setStatusBarColor(this);

		//mProfileType = TIME_PROFILE_LIST;
	    //Set layout
	    setContentView(R.layout.activity_tab);

		//Set toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//Create TabLayout for the Profiles (Basic and Location)d
		mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
		mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.basic_tab)));
		mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.location_tab)));
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
	
		

	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	    // Always call the superclass so it can restore the view hierarchy
	    super.onRestoreInstanceState(savedInstanceState);

	}

    public void clickFAB(View view) {
        Log.e(TAG, "Inside click FAB");
		if(mTabLayout.getSelectedTabPosition() == LOCATION_PROFILE_LIST)
			ProfileManager.newLocationProfile(this);
		else
            ProfileManager.newProfile(this);
    }

    private void setFABVisibility() {
		Log.e(TAG,"!!!! Listview Changed !!!!, Selected tab: " + mTabLayout.getSelectedTabPosition());
		if(ProfileManager.isDatabaseEmpty(this,mTabLayout.getSelectedTabPosition()))
			mFloatingActionButton.setVisibility(View.INVISIBLE);
		else
			mFloatingActionButton.setVisibility(View.VISIBLE);
	}

    @Override
    public void onListViewChange() {

           setFABVisibility();

    }
}
