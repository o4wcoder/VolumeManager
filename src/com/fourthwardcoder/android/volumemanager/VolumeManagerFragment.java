package com.fourthwardcoder.android.volumemanager;


import java.util.Calendar;
import java.util.Date;

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
	Switch volumeSwitch;
	TextView startTimeTextView, endTimeTextView;
	Button startTimeButton, endTimeButton, setControlButton;
	Date startDate,endDate;
	RadioGroup startVolumeRadioGroup;
	RadioGroup endVolumeRadioGroup;
	SeekBar startRingSeekBar, endRingSeekBar;
	boolean isControlEnabled = true;
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
		
		startDate = new Date();
		endDate = new Date();

		//Get any stored settings
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
				isControlEnabled = prefs.getBoolean(PREF_CONTROL_ENABLED,true);
				startDate.setHours(prefs.getInt(PREF_START_HOUR, 11));
				startDate.setMinutes(prefs.getInt(PREF_START_MIN, 0));
			    endDate.setHours(prefs.getInt(PREF_END_HOUR, 8));
			    endDate.setMinutes(prefs.getInt(PREF_END_MIN, 0));
			    startVolumeType = prefs.getInt(PREF_START_VOLUME_TYPE, VOLUME_OFF);
		        endVolumeType = prefs.getInt(PREF_END_VOLUME_TYPE,VOLUME_VIBRATE);
		        startRingVolume = prefs.getInt(PREF_START_RING_VOLUME, 1);
		        endRingVolume = prefs.getInt(PREF_END_RING_VOLUME, 1);
		        
		Log.d(TAG,"on create switch enabled? " + isControlEnabled);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState){
		
		Log.d(TAG,"OnCreateView");
		
		View view = inflater.inflate(R.layout.fragment_volume_control, container, false);
		
	    /*
	     * Setup TextViews                        
	     */
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
	            VolumeManagerService.setServiceAlarm(getActivity().getApplicationContext(), true);
				Toast toast = Toast.makeText(getActivity().getApplicationContext(),
						R.string.toast_text, Toast.LENGTH_SHORT);
				toast.show();
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
			    
				saveSettings();
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
			    
				saveSettings();
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
	    volumeSwitch = (Switch)view.findViewById(R.id.volumeSwitch);
	    volumeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//Turn components on/off and save settings
				isControlEnabled = isChecked;
				setWidgetVisibility(isControlEnabled);
				
				
				//Shut down volume control alarm and save settings. Don't want to save settings unless the "Set"
				//button was pushed
				if(!isChecked) {
					saveSettings();
				   VolumeManagerService.setServiceAlarm(getActivity().getApplicationContext(), false);
				}
			}
		});
	    	    
	    /*
	     * Set Default and Saved Settings
	     */
	    //Set default or saved radio button setting
	    ((RadioButton)startVolumeRadioGroup.getChildAt(startVolumeType)).setChecked(true);
	    //Set default or saved radio buttonn setting
	    ((RadioButton)endVolumeRadioGroup.getChildAt(endVolumeType)).setChecked(true);
	    //Setup up wigits with saved settings
	    volumeSwitch.setChecked(isControlEnabled);
	    updateTime(startDate,startTimeTextView);
	    updateTime(endDate,endTimeTextView);
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
	        updateTime(startDate, startTimeTextView);
		}
		else if(requestCode == REQUEST_END_TIME) {
			endDate = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
			updateTime(endDate,endTimeTextView);
		}
		
		saveSettings();
	}
	
	/*******************************************************************/
	/*                        Private Methods                          */
	/*******************************************************************/
	private void updateTime(Date date, TextView textView) {
		
		
		String am_or_pm = (date.getHours() < 12) ? "AM" : "PM";
		
		Log.d(TAG,"In update time with hour " + date.getHours());
		
		//Create a Calendar to get the time
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		int hour = calendar.get(Calendar.HOUR);
		int min = calendar.get(Calendar.MINUTE);
		
		String strHour = String.valueOf(hour);
		
		if(hour == 0)
			strHour = "12";
		
		String strMin = String.valueOf(min);
		
		if(min < 10)
			strMin = "0" + strMin;
		
		String time = strHour + ":" + strMin + " " + am_or_pm;
		textView.setText(time);
	}
	
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
	
	private void setRadioGroupVisibility(RadioGroup radioGroup, boolean set) {
		
		for(int i = 0; i <radioGroup.getChildCount(); i++)
			((RadioButton)radioGroup.getChildAt(i)).setEnabled(set);
	}
	
	private void saveSettings() {
		
		Log.d(TAG,"Saving enabled " +isControlEnabled);
		//Save Settings to PreferenceManager		
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
		
	}
	
	private void setRingVolumeText(TextView textView, int pos) {
		
		textView.setText("(" + pos + "/7)");
		
	}
	
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
