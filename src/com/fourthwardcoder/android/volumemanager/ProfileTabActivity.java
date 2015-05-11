package com.fourthwardcoder.android.volumemanager;



import java.util.ArrayList;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Bundle;

public class ProfileTabActivity extends Activity implements Constants{
	
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
	    ProfileListFragment.setStatusBarColor(this);
		
	    setContentView(R.layout.activity_tab);
		
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

}
