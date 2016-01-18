package com.fourthwardcoder.android.volumemanager.activites;



import java.util.ArrayList;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.fragments.LocationProfileListFragment;
import com.fourthwardcoder.android.volumemanager.fragments.ProfileListFragment;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Bundle;

public class ProfileTabActivity extends Activity implements Constants {
	
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
	    
	    /*
	    //Get the max ringer volume for this device
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    	int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
	    //Store max volume in shared prefs to be used in the app
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    SharedPreferences.Editor prefsEditor = prefs.edit();
	    prefsEditor.putInt(PREF_MAX_VOLUME, maxVolume).commit();
		*/
	    
	    //Set layout
	    setContentView(R.layout.activity_tab);
		
	    //Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				//Already created the fragment, just grab it
				if(fragList.size() > tab.getPosition())
					fragList.get(tab.getPosition());

				if(fragment == null) {
					
					if(TabName.values()[tab.getPosition()] == TabName.BASIC)
					   tabFragment = new ProfileListFragment();
					else
						tabFragment = new LocationProfileListFragment();
					
					Bundle data = new Bundle();
					data.putInt(TAB_ID, tab.getPosition());
					tabFragment.setArguments(data);
					fragList.add(tabFragment);

				}
				else {
					
					if(TabName.values()[tab.getPosition()] == TabName.BASIC)
					   tabFragment  = (ProfileListFragment)fragment;
					else
					   tabFragment  = (LocationProfileListFragment)fragment;
				}
				ft.replace(R.id.fragment_container, tabFragment);
				
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				if (fragList.size() > tab.getPosition()) {
					ft.remove(fragList.get(tab.getPosition()));
				}
				
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		
		actionBar.addTab(actionBar.newTab().setText(R.string.basic_tab).setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(R.string.location_tab).setTabListener(tabListener));
		

	
		

	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	    // Always call the superclass so it can restore the view hierarchy
	    super.onRestoreInstanceState(savedInstanceState);

	}
	

}
