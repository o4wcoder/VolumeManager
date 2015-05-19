package com.fourthwardcoder.android.volumemanager;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class EditLocationProfileFragment extends Fragment {
	
	/*******************************************************/
	/*                    Constants                        */
	/*******************************************************/
	private static final String TAG = "EditLocationProfileFragment";
	/*******************************************************/
	/*                  Override Methods                   */
	/*******************************************************/
	@Override
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		
		setHasOptionsMenu(true);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState){
		
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
		case R.id.menu_item_save_location_profile:
			Log.e(TAG,"Save location menu select");
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}
