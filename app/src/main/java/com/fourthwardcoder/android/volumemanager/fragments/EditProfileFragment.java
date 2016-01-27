package com.fourthwardcoder.android.volumemanager.fragments;

import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.activites.SettingsActivity;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.data.ProfileContract;
import com.fourthwardcoder.android.volumemanager.models.Profile;

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
	private final static String TAG = EditProfileFragment.class.getSimpleName();
	//Tag for the time piker Dialog
	private static final String DIALOG_TIME = "time";
	
	//Constant for request code to Time Picker
	public static final int REQUEST_START_TIME = 0;
	public static final int REQUEST_END_TIME = 1;

	
	/************************************************************************/
	/*                          Local Data                                  */
	/************************************************************************/
	Profile mProfile;
	TextView titleTextView, startTimeTextView, endTimeTextView;
	Button startTimeButton, endTimeButton;
//	Date startDate,endDate;
	RadioGroup startVolumeRadioGroup;
	RadioGroup endVolumeRadioGroup;
	SeekBar startRingSeekBar, endRingSeekBar;
	//String profileTitle;
	//int startVolumeType,endVolumeType;
	//int startRingVolume,endRingVolume;
	TextView startRingVolumeTextView, endRingVolumeTextView;
	ArrayList<Boolean> daysOfTheWeek;
	
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

        //See if we have a Profile object. If so we are editing the Profile.
        //If not, it's a new profile
        if(intent.hasExtra(EXTRA_PROFILE)) {
            mProfile = intent.getParcelableExtra(EXTRA_PROFILE);
        }
        else {
            mProfile = new Profile();
        }
        
		//UUID profileId = (UUID)getArguments().getSerializable(EXTRA_PROFILE_ID);
		//Fetch the Profile from the ProfileJSONManager ArrayList
	//	mProfile = ProfileJSONManager.get(getActivity()).getProfile(profileId);
		//profileTitle = mProfile.getTitle();
		//startDate = mProfile.getStartDate();
	   // endDate = mProfile.getEndDate();
	  //  startVolumeType = mProfile.getStartVolumeType();
       // endVolumeType = mProfile.getEndVolumeType();
        //startRingVolume = mProfile.getStartRingVolume();
        //endRingVolume = mProfile.getEndRingVolume();
        //daysOfTheWeek = mProfile.getDaysOfTheWeek();
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
		titleTextView.setText(mProfile.getTitle());
		
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
                ArrayList<Boolean> daysOfTheWeek = mProfile.getDaysOfTheWeek();

                 Log.d(TAG,"Button tag: " + v.getTag());
                 TextView textView = (TextView)v;
                 boolean setting = daysOfTheWeek.get((int)v.getTag());

                 if(setting) {
                	 //Turn Day off
                	daysOfTheWeek.add((int)v.getTag(),false);

     	        	textView.setTextColor(Color.parseColor("#000000"));
                 }
                 else {
                	 //Turn Day on
                	 daysOfTheWeek.add((int)v.getTag(), true);
     	        	textView.setTextColor(Color.parseColor("#ffffff"));
                 }

                //Update days of the week in Profile
                mProfile.setDaysOfTheWeek(daysOfTheWeek);
                 
                 
			}
		};
		
		TableRow daysRow = (TableRow)view.findViewById(R.id.daysTableRow);
		for(int i = 0; i < daysRow.getChildCount(); i ++) {
			Button button = (Button)daysRow.getChildAt(i);
	        button.setText(Constants.daysButtonNames[i]);
	        button.setOnClickListener(dayButtonListener);
	        button.setTag(new Integer(i));
	        
	        boolean setting = daysOfTheWeek.get(i);
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
				TimePickerFragment dialog = TimePickerFragment.newInstance(mProfile.getStartDate(),getString(R.string.start_time_dialog));
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
				TimePickerFragment dialog = TimePickerFragment.newInstance(mProfile.getEndDate(),getString(R.string.end_time_dialog));
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
					mProfile.setStartVolumeType(VOLUME_OFF);
					mProfile.setStartRingVolume(0);
					Util.setSeekBarPosition(startRingSeekBar,startRingVolumeTextView,mProfile.getStartRingVolume(),Util.getMaxRingVolume(getActivity().getApplicationContext()));
					
				}
				else if(checkedId == R.id.startVibrateRadio) {
					mProfile.setStartVolumeType(VOLUME_VIBRATE);
					mProfile.setStartRingVolume(0);
					Util.setSeekBarPosition(startRingSeekBar,startRingVolumeTextView,mProfile.getStartRingVolume(),Util.getMaxRingVolume(getActivity().getApplicationContext()));
				}
				else {
					mProfile.setStartVolumeType(VOLUME_RING);
				}
			    
			}
	    	
	    });
	    //Set up Ringer type Radio Buttons
	    endVolumeRadioGroup = (RadioGroup)view.findViewById(R.id.endVolumeRadioGroup);
	    endVolumeRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
			       
				if(checkedId == R.id.endOffRadio) {
					mProfile.setEndVolumeType(VOLUME_OFF);
					mProfile.setEndRingVolume(0);
					Util.setSeekBarPosition(endRingSeekBar,endRingVolumeTextView,mProfile.getEndRingVolume(),Util.getMaxRingVolume(getActivity().getApplicationContext()));

				}
				else if(checkedId == R.id.endVibrateRadio) {
					mProfile.setEndVolumeType(VOLUME_VIBRATE);
					mProfile.setEndRingVolume(0);
					Util.setSeekBarPosition(endRingSeekBar,endRingVolumeTextView,mProfile.getEndRingVolume(),Util.getMaxRingVolume(getActivity().getApplicationContext()));
				}
				else {
					mProfile.setEndVolumeType(VOLUME_RING);
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
				mProfile.setStartRingVolume(progress);
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
				mProfile.setEndRingVolume(progress);
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
	    ((RadioButton)startVolumeRadioGroup.getChildAt(mProfile.getStartVolumeType())).setChecked(true);
	    //Set default or saved radio button setting
	    ((RadioButton)endVolumeRadioGroup.getChildAt(mProfile.getEndVolumeType())).setChecked(true);
	    startTimeTextView.setText(Util.formatTime(mProfile.getStartDate()));
	    endTimeTextView.setText(Util.formatTime(mProfile.getEndDate()));
	    //Set Seekbar default
	    Util.setSeekBarPosition(startRingSeekBar,startRingVolumeTextView,mProfile.getStartRingVolume(),Util.getMaxRingVolume(getActivity().getApplicationContext()));
	    Util.setSeekBarPosition(endRingSeekBar,endRingVolumeTextView,mProfile.getEndRingVolume(),Util.getMaxRingVolume(getActivity().getApplicationContext()));
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
            mProfile.setStartDate((Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME));
			startTimeTextView.setText(Util.formatTime(mProfile.getStartDate()));
		}
		else if(requestCode == REQUEST_END_TIME) {
            mProfile.setEndDate((Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME));
			endTimeTextView.setText(Util.formatTime(mProfile.getEndDate()));
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

        Log.e(TAG, "Inside saveSettings()");

        mProfile.setTitle(titleTextView.getText().toString());
		//mProfile.setStartDate(startDate);
		//mProfile.setEndDate(endDate);
		//mProfile.setStartVolumeType(startVolumeType);
		//mProfile.setEndVolumeType(endVolumeType);
		//mProfile.setStartRingVolume(startRingVolume);
		//mProfile.setEndRingVolume(endRingVolume);
		//mProfile.setDaysOfTheWeek(daysOfTheWeek);
		
		//ProfileJSONManager.get(getActivity()).saveProfiles();

		ContentValues profileValues = mProfile.getContentValues();

         Uri insertedRow = getActivity().getContentResolver()
                .insert(ProfileContract.ProfileEntry.CONTENT_URI, profileValues);

        Log.e(TAG,"Insert Profile row with result " + insertedRow);
    }
	

	

}
