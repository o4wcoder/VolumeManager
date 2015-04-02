package com.fourthwardcoder.android.volumemanager;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;

/**
 * VolumeManagerFragment
 * 
 * Main fragment of the VolumeManagerActivity. Contains all the components to 
 * set start and end alarms of the specific alarm control.
 * 
 * @author Chris Hare
 * 3/13/2015
 *
 */
public class VolumeManagerFragment extends Fragment implements Constants{
	
	/************************************************************************/
	/*                           Constants                                  */
	/************************************************************************/
	private final static String TAG = "VolumeManagerFragment";
	//Tag for the time piker Dialog
	private static final String DIALOG_TIME = "time";
	
	//Constant for request code to Time Picker
	public static final int REQUEST_START_TIME = 0;
	public static final int REQUEST_END_TIME = 1;

	
	/************************************************************************/
	/*                          Local Data                                  */
	/************************************************************************/
	Profile profile;
	Switch volumeSwitch;
	TextView titleTextView, startTimeTextView, endTimeTextView;
	Button startTimeButton, endTimeButton, setControlButton;
	Date startDate,endDate;
	RadioGroup startVolumeRadioGroup;
	RadioGroup endVolumeRadioGroup;
	SeekBar startRingSeekBar, endRingSeekBar;
	boolean isControlEnabled = true;
	String profileTitle;
	int startVolumeType,endVolumeType;
	int startRingVolume,endRingVolume;
	TextView startRingVolumeTextView, endRingVolumeTextView;
	
	/*******************************************************/
	/*                  Override Methods                   */
	/*******************************************************/
	@Override
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
	
		Log.d(TAG,"onCreate");
		
		//Get Fragment arguments and pull out ID of crime
		Intent intent = getActivity().getIntent();
		UUID profileId = (UUID)intent.getSerializableExtra(EXTRA_PROFILE_ID);
		
        
		//UUID profileId = (UUID)getArguments().getSerializable(EXTRA_PROFILE_ID);
		//Fetch the Profile from the ProfileManager ArrayList
		profile = ProfileManager.get(getActivity()).getProfile(profileId);
		isControlEnabled = profile.isEnabled();
		profileTitle = profile.getTitle();
		startDate = profile.getStartDate();
	    endDate = profile.getEndDate();
	    startVolumeType = profile.getStartVolumeType();
        endVolumeType = profile.getEndVolumeType();
        startRingVolume = profile.getStartRingVolume();
        endRingVolume = profile.getEndRingVolume();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState){
		
		Log.d(TAG,"OnCreateView");
		
		View view = inflater.inflate(R.layout.fragment_volume_control, container, false);
		
	    /*
	     * Setup TextViews                        
	     */
		titleTextView = (TextView)view.findViewById(R.id.titleTextView);
		titleTextView.setText(profileTitle);
		
	    startTimeTextView = (TextView)view.findViewById(R.id.startTimeTextView);
        //Set up Ring Volume TextView
	    startRingVolumeTextView = (TextView)view.findViewById(R.id.startRingVolumeTextView);
	    //Set up End Time TextView
	    endTimeTextView = (TextView)view.findViewById(R.id.endTimeTextView);
	    //Set up Ring Volume TextView
	    endRingVolumeTextView = (TextView)view.findViewById(R.id.endRingVolumeTextView);
	    
	    /*
	     * Setup Buttons                     
	     */
	    //Setup start time of volume control
	    startTimeButton = (Button)view.findViewById(R.id.startTimeButton);
	    startTimeButton.setOnClickListener(new OnClickListener() {
	    	@Override
			public void onClick(View v) {
				//Start Time Picker Dialog on CrimeFragment after clicking Time Button
				FragmentManager fm = getActivity().getSupportFragmentManager();
				TimePickerFragment dialog = TimePickerFragment.newInstance(startDate,getString(R.string.start_time_dialog));
				//Make VolumeManagerFragment the target fragment of the TimePickerFragment instance
				dialog.setTargetFragment(VolumeManagerFragment.this, REQUEST_START_TIME);
				dialog.show(fm, DIALOG_TIME);
	    		
	    	}
	    });
	    //Setup end time of volume control
	    endTimeButton = (Button)view.findViewById(R.id.endTimeButton);
	    endTimeButton.setOnClickListener(new OnClickListener() {
	    	@Override
			public void onClick(View v) {
				//Start Time Picker Dialog on CrimeFragment after clicking Time Button
				FragmentManager fm = getActivity().getSupportFragmentManager();
				TimePickerFragment dialog = TimePickerFragment.newInstance(endDate,getString(R.string.end_time_dialog));
				//Make VolumeManagerFragment the target fragment of the TimePickerFragment instance
				dialog.setTargetFragment(VolumeManagerFragment.this, REQUEST_END_TIME);
				dialog.show(fm, DIALOG_TIME);
	    		
	    	}
	    });
	    //Set up Set Volume Control Button
	    setControlButton = (Button)view.findViewById(R.id.setControlButton);
	    setControlButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveSettings(); 
	            VolumeManagerService.setServiceAlarm(getActivity().getApplicationContext(), profile, true);
				Toast toast = Toast.makeText(getActivity().getApplicationContext(),
						R.string.toast_text, Toast.LENGTH_SHORT);
				toast.show();
				
				getActivity().finish();
			}
	    });
	    
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
					setSeekBarPosition(ID_START_ALARM,0);
					
				}
				else if(checkedId == R.id.startVibrateRadio) {
					startVolumeType = VOLUME_VIBRATE;
					setSeekBarPosition(ID_START_ALARM,0);
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
					setSeekBarPosition(ID_END_ALARM,0);

				}
				else if(checkedId == R.id.endVibrateRadio) {
					endVolumeType = VOLUME_VIBRATE;
					setSeekBarPosition(ID_END_ALARM,0);
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
				
				setRingVolumeText(startRingVolumeTextView,progress);
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
				
				setRingVolumeText(endRingVolumeTextView,progress);
				endRingVolume = progress;	
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar){}
	    });
	    	    
	    /*
	     * Set up volume Switch
	     */
	    volumeSwitch = (Switch)view.findViewById(R.id.volumeNotifySwitch);
	    volumeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//Turn components on/off and save settings
				isControlEnabled = isChecked;
				setWidgetVisibility(isChecked);
				
				
				//Shut down volume control alarm and save settings. Don't want to save settings unless the "Set"
				//button was pushed
				if(!isChecked) {
					saveSettings();
				   VolumeManagerService.setServiceAlarm(getActivity().getApplicationContext(), profile,false);
				}
			}
		});
	    	    
	    /*
	     * Set Default and Saved Settings
	     */
	    //Set default or saved radio button setting
	    ((RadioButton)startVolumeRadioGroup.getChildAt(startVolumeType)).setChecked(true);
	    //Set default or saved radio button setting
	    ((RadioButton)endVolumeRadioGroup.getChildAt(endVolumeType)).setChecked(true);
	    //Setup up wigits with saved settings
	    volumeSwitch.setChecked(isControlEnabled);
	    startTimeTextView.setText(ProfileListFragment.formatTime(startDate));
	    endTimeTextView.setText(ProfileListFragment.formatTime(endDate));
	    //Set Seekbar default
	    setSeekBarPosition(ID_START_ALARM,startRingVolume);
	    setSeekBarPosition(ID_END_ALARM,endRingVolume);
	    setWidgetVisibility(isControlEnabled);
	    
		return view;
	}
	
	//Get results from Dialog boxes and other Activities
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Log.e(TAG,"In onActivityResult with requestCode " + String.valueOf(requestCode));
		if(resultCode != Activity.RESULT_OK)
			return;
		
		if(requestCode == REQUEST_START_TIME) {
			startDate = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
			startTimeTextView.setText(ProfileListFragment.formatTime(startDate));
		}
		else if(requestCode == REQUEST_END_TIME) {
			endDate = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
			endTimeTextView.setText(ProfileListFragment.formatTime(endDate));
		}
		
		//saveSettings();
	}
	
	/*******************************************************************/
	/*                        Private Methods                          */
	/*******************************************************************/
	
	
	
	
	/**
	 * Sets each components visibility in the fragment. This is set
	 * by the on/off toggle switch
	 * @param set setting of toggle switch
	 */
	private void setWidgetVisibility(boolean set) {
		
		//Start Controls
		startTimeButton.setEnabled(set);
		startTimeTextView.setEnabled(set);
		setRadioGroupVisibility(startVolumeRadioGroup,set);
		startRingSeekBar.setEnabled(set);
		//End Controls
		endTimeButton.setEnabled(set);
		endTimeTextView.setEnabled(set);
		setRadioGroupVisibility(endVolumeRadioGroup,set);
		endRingSeekBar.setEnabled(set);
		
		setControlButton.setEnabled(set);
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
	
	/**
	 * Save all settings of the alarms to SharedPreferences 
	 */
	private void saveSettings() {
		
		Log.d(TAG,"Saving enabled " + isControlEnabled);
		//Save Settings to PreferenceManager		
		/*
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putBoolean(PREF_CONTROL_ENABLED,isControlEnabled);
		prefsEditor.putInt(PREF_START_HOUR, startDate.getHours());
		prefsEditor.putInt(PREF_START_MIN, startDate.getMinutes());
		prefsEditor.putInt(PREF_END_HOUR, endDate.getHours());
		prefsEditor.putInt(PREF_END_MIN, endDate.getMinutes());
		prefsEditor.putInt(PREF_START_VOLUME_TYPE, startVolumeType);
		prefsEditor.putInt(PREF_END_VOLUME_TYPE, endVolumeType);
	    prefsEditor.putInt(PREF_START_RING_VOLUME, startRingVolume);
	    prefsEditor.putInt(PREF_END_RING_VOLUME, endRingVolume);
		prefsEditor.commit();
		*/
		
		//Profile p = new Profile();
		profile.setTitle(titleTextView.getText().toString());
		profile.setEnabled(isControlEnabled);
		profile.setStartDate(startDate);
		profile.setEndDate(endDate);
		profile.setStartVolumeType(startVolumeType);
		profile.setEndVolumeType(endVolumeType);
		profile.setStartRingVolume(startRingVolume);
		profile.setEndRingVolume(endRingVolume);
		
		ProfileManager.get(getActivity()).saveProfiles();
		
	}
	
	/**
	 * Set the text on ring volume level
	 * @param textView TextView to set the ring volume level
	 * @param pos position of the seekbar
	 */
	private void setRingVolumeText(TextView textView, int pos) {
		
		textView.setText("(" + pos + "/7)");
	}
	
	/**
	 * Set the ring volume seek bar at a specific position
	 * @param alarmType which alarm (start or end) to set
	 * @param pos seekbar position
	 */
	private void setSeekBarPosition(int alarmType, int pos) {

		if(alarmType == ID_START_ALARM) {
			startRingSeekBar.setProgress(pos);
			startRingVolume = pos;
			setRingVolumeText(startRingVolumeTextView,pos);
			
		}
		else {
			endRingSeekBar.setProgress(pos);
			endRingVolume = pos;
			setRingVolumeText(endRingVolumeTextView,pos);
		}
	}
}
