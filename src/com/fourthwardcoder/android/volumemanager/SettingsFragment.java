package com.fourthwardcoder.android.volumemanager;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class SettingsFragment extends Fragment implements Constants{
	
	SharedPreferences prefs;
	SharedPreferences.Editor prefsEditor;
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
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		prefsEditor = prefs.edit();
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		
		//Enable app icon to work as button and display caret
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if(NavUtils.getParentActivityName(getActivity()) != null) {
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
		
		Switch volumeNotifySwitch = (Switch)view.findViewById(R.id.volumeNotifySwitch);
		volumeNotifySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				prefsEditor.putBoolean(PREF_VOLUME_NOTIFY_ENABLED, isChecked).commit();
			}
			
		});
		Switch locationNotifySwitch = (Switch)view.findViewById(R.id.locationNotifySwitch);
		locationNotifySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				prefsEditor.putBoolean(PREF_LOCATION_NOTIFY_ENABLED, isChecked).commit();	
			}
			
		});
		
		volumeNotifySwitch.setChecked(prefs.getBoolean(PREF_VOLUME_NOTIFY_ENABLED, false));
		locationNotifySwitch.setChecked(prefs.getBoolean(PREF_LOCATION_NOTIFY_ENABLED, false));
		
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
