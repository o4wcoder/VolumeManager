package com.fourthwardcoder.android.volumemanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		prefsEditor = prefs.edit();
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		
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

}
