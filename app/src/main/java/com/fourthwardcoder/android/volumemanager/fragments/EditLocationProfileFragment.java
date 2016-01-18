package com.fourthwardcoder.android.volumemanager.fragments;

import java.util.UUID;

import com.fourthwardcoder.android.volumemanager.models.LocationProfile;
import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.activites.SettingsActivity;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class EditLocationProfileFragment extends Fragment implements Constants {
	
	/*******************************************************/
	/*                    Constants                        */
	/*******************************************************/
	private static final String TAG = "EditLocationProfileFragment";
	
	/*******************************************************/
	/*                   Local Data                        */
	/*******************************************************/
	TextView titleTextView,startRingVolumeTextView,endRingVolumeTextView;
	RadioGroup startVolumeRadioGroup;
	RadioGroup endVolumeRadioGroup;
	SeekBar startRingSeekBar, endRingSeekBar;
	int startVolumeType,endVolumeType;
	int startRingVolume,endRingVolume;
	
	LocationProfile profile;
	String profileTitle;
	 
	/*******************************************************/
	/*                  Override Methods                   */
	/*******************************************************/
	@Override
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		
		setHasOptionsMenu(true);
		
		UUID profileId = (UUID) getActivity().getIntent().getSerializableExtra(Constants.EXTRA_PROFILE_ID);
		profile = ProfileManager.get(getActivity()).getLocationProfile(profileId);
		Log.e(TAG,"I've been passed profile with id " + profile.getId());
		
		profileTitle = profile.getTitle();
	  //  currentLocation = profile.getLocation();	
	    startVolumeType = profile.getStartVolumeType();
        endVolumeType = profile.getEndVolumeType();
        startRingVolume = profile.getStartRingVolume();
        endRingVolume = profile.getEndRingVolume();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState){
		
		View view = inflater.inflate(R.layout.fragment_location, container, false);
		
	    /*
	     * Setup TextViews                        
	     */
		titleTextView = (TextView)view.findViewById(R.id.titleTextView);
		titleTextView.setText(profileTitle);
		
        //Set up Ring Volume TextView
	    startRingVolumeTextView = (TextView)view.findViewById(R.id.startRingVolumeTextView);
	    //Set up Ring Volume TextView
	    endRingVolumeTextView = (TextView)view.findViewById(R.id.endRingVolumeTextView);
	    
	    /*
	     * Setup Radio Buttons                  
	     */
	    //Set up Ringer type Radio Buttons
	    startVolumeRadioGroup = (RadioGroup)view.findViewById(R.id.startVolumeRadioGroup);
	    startVolumeRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	    
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
			       
				if(checkedId == R.id.startOffRadio) {
					startVolumeType = VOLUME_OFF;
					startRingVolume = 0;
					Util.setSeekBarPosition(startRingSeekBar, startRingVolumeTextView, startRingVolume, Util.getMaxRingVolume(getActivity().getApplicationContext()));
					
				}
				else if(checkedId == R.id.startVibrateRadio) {
					startVolumeType = VOLUME_VIBRATE;
					startRingVolume = 0;
					Util.setSeekBarPosition(startRingSeekBar,startRingVolumeTextView,startRingVolume,Util.getMaxRingVolume(getActivity().getApplicationContext()));
				}
				else {
					startVolumeType = VOLUME_RING;
				}
			    
			}
	    	
	    });
	    //Set up Ringer type Radio Buttons
	    endVolumeRadioGroup = (RadioGroup)view.findViewById(R.id.endVolumeRadioGroup);
	    endVolumeRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
			       
				if(checkedId == R.id.endOffRadio) {
					endVolumeType = VOLUME_OFF;
					endRingVolume = 0;
					Util.setSeekBarPosition(endRingSeekBar,endRingVolumeTextView,endRingVolume,Util.getMaxRingVolume(getActivity().getApplicationContext()));

				}
				else if(checkedId == R.id.endVibrateRadio) {
					endVolumeType = VOLUME_VIBRATE;
					Util.setSeekBarPosition(endRingSeekBar,endRingVolumeTextView,endRingVolume,Util.getMaxRingVolume(getActivity().getApplicationContext()));
				}
				else {
					endVolumeType = VOLUME_RING;
				}
			}
	    });
	    
	    /*
	     * Setup SeekBars                    
	     */
	    //Set up Ringer SeekBar
	    startRingSeekBar = (SeekBar)view.findViewById(R.id.startRingSeekBar);
	    startRingSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				Log.d(TAG,"Start Seek Bar progress " + progress);
				
				if(progress == 0)
					 ((RadioButton)startVolumeRadioGroup.getChildAt(VOLUME_OFF)).setChecked(true);
				else 
					((RadioButton)startVolumeRadioGroup.getChildAt(VOLUME_RING)).setChecked(true);
				
				Util.setRingVolumeText(startRingVolumeTextView,progress,Util.getMaxRingVolume(getActivity().getApplicationContext()));
				startRingVolume = progress;
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
	    });
	    
	    //Set up Ringer SeekBar
	    endRingSeekBar = (SeekBar)view.findViewById(R.id.endRingSeekBar);
	    endRingSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(progress == 0)
					 ((RadioButton)endVolumeRadioGroup.getChildAt(VOLUME_OFF)).setChecked(true);
				else 
					((RadioButton)endVolumeRadioGroup.getChildAt(VOLUME_RING)).setChecked(true);
				
				Util.setRingVolumeText(endRingVolumeTextView,progress,Util.getMaxRingVolume(getActivity().getApplicationContext()));
				endRingVolume = progress;	
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar){}
	    });
	    	    
	    	    
	    /*
	     * Set Default and Saved Settings
	     */
	    //Set default or saved radio button setting
	    ((RadioButton)startVolumeRadioGroup.getChildAt(startVolumeType)).setChecked(true);
	    //Set default or saved radio button setting
	    ((RadioButton)endVolumeRadioGroup.getChildAt(endVolumeType)).setChecked(true);

	    //Set Seekbar default
	    Util.setSeekBarPosition(startRingSeekBar,startRingVolumeTextView,startRingVolume,Util.getMaxRingVolume(getActivity().getApplicationContext()));
	    Util.setSeekBarPosition(endRingSeekBar,endRingVolumeTextView,endRingVolume,Util.getMaxRingVolume(getActivity().getApplicationContext()));
	    startRingSeekBar.setMax(Util.getMaxRingVolume(getActivity().getApplicationContext()));
	    endRingSeekBar.setMax(Util.getMaxRingVolume(getActivity().getApplicationContext()));
	    
		//Enable app icon to work as button and display caret
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if(NavUtils.getParentActivityName(getActivity()) != null) {
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//super.onCreateOptionsMenu(menu, inflater);
		
		//Pass the resource ID of the menu and populate the Menu 
		//instance with the items defined in the xml file
		inflater.inflate(R.menu.action_bar_profile_menu, menu);
		
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		case android.R.id.home:
			if(NavUtils.getParentActivityName(getActivity()) != null) {
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;
		case R.id.menu_item_save_profile:
			Log.e(TAG,"Save location menu select");
			
			saveSettings(); 
			Toast toast = Toast.makeText(getActivity().getApplicationContext(),
					R.string.toast_text, Toast.LENGTH_SHORT);
			toast.show();
			
			getActivity().finish();
			return true;
		case R.id.menu_item_settings:
			Intent settingsIntent = new Intent(getActivity(),SettingsActivity.class);
			startActivity(settingsIntent);
			return true;
		case R.id.menu_item_about:
			FragmentManager fm = getActivity().getFragmentManager();
			AboutFragment dialog = AboutFragment.newInstance();
			//Make ProfileListFragment the target fragment of the TimePickerFragment instance
			//dialog.setTargetFragment(VolumeManagerFragment.this, REQUEST_START_TIME);
			dialog.show(fm, "about");
			return true;
        default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	/**
	 * Set the visibility of the RadioGroups. This is set by the on/off toggle switch
	 * @param radioGroup RadioGroup to set the visibility on
	 * @param set setting of the toggle switch
	 */
	private void setRadioGroupVisibility(RadioGroup radioGroup, boolean set) {
		
		//Set visibility on each RadioButton in the Group
		for(int i = 0; i <radioGroup.getChildCount(); i++)
			((RadioButton)radioGroup.getChildAt(i)).setEnabled(set);
	}
	private void saveSettings() {
	
		profile.setTitle(titleTextView.getText().toString());
		profile.setStartVolumeType(startVolumeType);
		profile.setEndVolumeType(endVolumeType);
		profile.setStartRingVolume(startRingVolume);
		profile.setEndRingVolume(endRingVolume);
		
		ProfileManager.get(getActivity()).saveProfiles();
	}
}
