package com.fourthwardcoder.android.volumemanager.fragments;

import java.util.Date;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.activites.SettingsActivity;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.BasicProfile;

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
public class EditProfileFragment extends Fragment implements Constants {
	
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
	BasicProfile profile;
	TextView titleTextView, startTimeTextView, endTimeTextView;
	Button startTimeButton, endTimeButton;
	Date startDate,endDate;
	RadioGroup startVolumeRadioGroup;
	RadioGroup endVolumeRadioGroup;
	SeekBar startRingSeekBar, endRingSeekBar;
	String profileTitle;
	int startVolumeType,endVolumeType;
	int startRingVolume,endRingVolume;
	TextView startRingVolumeTextView, endRingVolumeTextView;
	boolean daysOfTheWeek[];
	
	/*******************************************************/
	/*                  Override Methods                   */
	/*******************************************************/
	@Override
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		
		setHasOptionsMenu(true);
		
		//Change status bar color
		Util.setStatusBarColor(getActivity());
		
		//Get Fragment arguments and pull out ID of profile
		Intent intent = getActivity().getIntent();
		UUID profileId = (UUID)intent.getSerializableExtra(EXTRA_PROFILE_ID);
		
        
		//UUID profileId = (UUID)getArguments().getSerializable(EXTRA_PROFILE_ID);
		//Fetch the Profile from the ProfileManager ArrayList
		profile = ProfileManager.get(getActivity()).getProfile(profileId);
		profileTitle = profile.getTitle();
		startDate = profile.getStartDate();
	    endDate = profile.getEndDate();
	    startVolumeType = profile.getStartVolumeType();
        endVolumeType = profile.getEndVolumeType();
        startRingVolume = profile.getStartRingVolume();
        endRingVolume = profile.getEndRingVolume();
        daysOfTheWeek = profile.getDaysOfTheWeek();
	}
	
	@SuppressLint("ResourceAsColor")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState){
				
		View view = inflater.inflate(R.layout.fragment_profile, container, false);

		//Enable app icon to work as button and display caret
//		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			if(NavUtils.getParentActivityName(getActivity()) != null) {
//				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
//			}
//		}
		
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
		View.OnClickListener dayButtonListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
                 Log.d(TAG,"Button tag: " + v.getTag());
                 TextView textView = (TextView)v;
                 boolean setting = daysOfTheWeek[(int)v.getTag()];
                 
                 if(setting) {
                	 //Turn Day off
                	 daysOfTheWeek[(int)v.getTag()] = false;
     	        	textView.setTextColor(Color.parseColor("#000000"));
                 }
                 else {
                	 //Turn Day on
                	 daysOfTheWeek[(int)v.getTag()] = true;
     	        	textView.setTextColor(Color.parseColor("#ffffff"));
                 }
                 
                 
			}
		};
		
		TableRow daysRow = (TableRow)view.findViewById(R.id.daysTableRow);
		for(int i = 0; i < daysRow.getChildCount(); i ++) {
			Button button = (Button)daysRow.getChildAt(i);
	        button.setText(Constants.daysButtonNames[i]);
	        button.setOnClickListener(dayButtonListener);
	        button.setTag(new Integer(i));
	        
	        boolean setting = daysOfTheWeek[i];
	        if(setting) {
	        	//Turn Day on
	        	button.setTextColor(Color.parseColor("#ffffff"));
	        }
	        else {
	        	//Turn Day off
	        	//button.setTextColor(R
				//		.color.primary_material_dark);
	        }
	        	
	        
		}
	    //Setup start time of volume control
	    startTimeButton = (Button)view.findViewById(R.id.startTimeButton);
	    startTimeButton.setOnClickListener(new OnClickListener() {
	    	@Override
			public void onClick(View v) {
				//Start Time Picker Dialog on CrimeFragment after clicking Time Button
				android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
				TimePickerFragment dialog = TimePickerFragment.newInstance(startDate,getString(R.string.start_time_dialog));
				//Make VolumeManagerFragment the target fragment of the TimePickerFragment instance
				dialog.setTargetFragment(EditProfileFragment.this, REQUEST_START_TIME);
				dialog.show(fm, DIALOG_TIME);
	    		
	    	}
	    });
	    //Setup end time of volume control
	    endTimeButton = (Button)view.findViewById(R.id.endTimeButton);
	    endTimeButton.setOnClickListener(new OnClickListener() {
	    	@Override
			public void onClick(View v) {
				//Start Time Picker Dialog on CrimeFragment after clicking Time Button
				android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
				TimePickerFragment dialog = TimePickerFragment.newInstance(endDate,getString(R.string.end_time_dialog));
				//Make VolumeManagerFragment the target fragment of the TimePickerFragment instance
				dialog.setTargetFragment(EditProfileFragment.this, REQUEST_END_TIME);
				dialog.show(fm, DIALOG_TIME);
	    		
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
					startRingVolume = 0;
					Util.setSeekBarPosition(startRingSeekBar,startRingVolumeTextView,startRingVolume,Util.getMaxRingVolume(getActivity().getApplicationContext()));
					
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
					endRingVolume = 0;
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
	    startTimeTextView.setText(Util.formatTime(startDate));
	    endTimeTextView.setText(Util.formatTime(endDate));
	    //Set Seekbar default
	    Util.setSeekBarPosition(startRingSeekBar,startRingVolumeTextView,startRingVolume,Util.getMaxRingVolume(getActivity().getApplicationContext()));
	    Util.setSeekBarPosition(endRingSeekBar,endRingVolumeTextView,endRingVolume,Util.getMaxRingVolume(getActivity().getApplicationContext()));
	    startRingSeekBar.setMax(Util.getMaxRingVolume(getActivity().getApplicationContext()));
	    endRingSeekBar.setMax(Util.getMaxRingVolume(getActivity().getApplicationContext()));
	    
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//super.onCreateOptionsMenu(menu, inflater);
		
		//Pass the resource ID of the menu and populate the Menu 
		//instance with the items defined in the xml file
		inflater.inflate(R.menu.action_bar_profile_menu, menu);
		
	}
	
	//Get results from Dialog boxes and other Activities
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Log.e(TAG,"In onActivityResult with requestCode " + String.valueOf(requestCode));
		if(resultCode != Activity.RESULT_OK)
			return;
		
		if(requestCode == REQUEST_START_TIME) {
			startDate = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
			startTimeTextView.setText(Util.formatTime(startDate));
		}
		else if(requestCode == REQUEST_END_TIME) {
			endDate = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
			endTimeTextView.setText(Util.formatTime(endDate));
		}
		
		//saveSettings();
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
	/*******************************************************************/
	/*                        Private Methods                          */
	/*******************************************************************/
	
	
	
	
	/**
	 * Sets each components visibility in the fragment. This is set
	 * by the on/off toggle switch
	 * @param set setting of toggle switch
	 */
	/*
	private void setWidgetVisibility(boolean set) {
		
		//Start Controls
		startTimeButton.setEnabled(set);
		startTimeTextView.setEnabled(set);
		Util.setRadioGroupVisibility(startVolumeRadioGroup,set);
		startRingSeekBar.setEnabled(set);
		//End Controls
		endTimeButton.setEnabled(set);
		endTimeTextView.setEnabled(set);
		Util.setRadioGroupVisibility(endVolumeRadioGroup,set);
		endRingSeekBar.setEnabled(set);
		
		setControlButton.setEnabled(set);
	}
	*/
	

	
	/**
	 * Save all settings of the alarms to SharedPreferences 
	 */
	private void saveSettings() {

		profile.setTitle(titleTextView.getText().toString());
		profile.setStartDate(startDate);
		profile.setEndDate(endDate);
		profile.setStartVolumeType(startVolumeType);
		profile.setEndVolumeType(endVolumeType);
		profile.setStartRingVolume(startRingVolume);
		profile.setEndRingVolume(endRingVolume);
		profile.setDaysOfTheWeek(daysOfTheWeek);
		
		ProfileManager.get(getActivity()).saveProfiles();
		
	}
	

	

}
