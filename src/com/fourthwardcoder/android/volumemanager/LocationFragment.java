package com.fourthwardcoder.android.volumemanager;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class LocationFragment extends Fragment {
	

	/***************************************************/
	/*                Override Methods                 */
	/***************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//retain the instance on rotation
		setRetainInstance(true);
		
		//Set up Options menu for Up Caret navigation
		setHasOptionsMenu(true);
		
		//Change status bar color
		ProfileListFragment.setStatusBarColor(getActivity());
		
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {


		View view = inflater.inflate(R.layout.fragment_location, container, false);
		
		//Enable app icon to work as button and display caret
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if(NavUtils.getParentActivityName(getActivity()) != null) {
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
		
		return view;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		case android.R.id.home:
			if(NavUtils.getParentActivityName(getActivity()) != null) {
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	

}
