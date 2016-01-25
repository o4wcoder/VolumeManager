package com.fourthwardcoder.android.volumemanager.activites;



import java.util.ArrayList;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.adapters.ProfilePagerAdapter;
import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
import com.fourthwardcoder.android.volumemanager.fragments.EditProfileFragment;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.fragments.LocationProfileListFragment;
import com.fourthwardcoder.android.volumemanager.fragments.ProfileListFragment;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.Profile;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;

public class ProfileTabActivity extends AppCompatActivity implements Constants {
	
	/*********************************************************************/
	/*                          Constants                                */
	/*********************************************************************/
	private static final String TAG="ProfileTabActivity";
	
	/*********************************************************************/
	/*                         Local Data                                */
	/*********************************************************************/
	ArrayList<Fragment> fragList = new ArrayList<Fragment>();
	Fragment fragment = null;
    Fragment tabFragment = null;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		//Change status bar color
	    Util.setStatusBarColor(this);

	    //Set layout
	    setContentView(R.layout.activity_tab);

		//Set toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//Create TabLayout for the Profiles (Basic and Location)d
		TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.basic_tab)));
		tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.location_tab)));
		tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

		//Create ViewPager to swipe between the tabs
		final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		final PagerAdapter adapter = new ProfilePagerAdapter(getSupportFragmentManager(),
				tabLayout.getTabCount());
		viewPager.setAdapter(adapter);
		viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				viewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
		

	
		

	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	    // Always call the superclass so it can restore the view hierarchy
	    super.onRestoreInstanceState(savedInstanceState);

	}

    public void clickFAB(View view) {
        Log.e(TAG, "Inside click FAB");
        newProfile();
    }

    private void newProfile()
    {
        //Add profile to the static List Array of Crimes
        Profile profile = new Profile();
        ProfileManager.get(this).addProfile(profile);

        //Create intent to start up CrimePagerActivity after selecting "New Crime" menu
    	/*
    	 * !!!! TODO Hook up to Pager Activity when created
    	 */
        Intent i = new Intent(this,EditProfileActivity.class);

        //Send the profile ID in the intent to CrimePagerActivity
        i.putExtra(EditProfileFragment.EXTRA_PROFILE_ID, profile.getId());

        //Start CrimePagerActivity
        startActivityForResult(i, 0);

    }
}
