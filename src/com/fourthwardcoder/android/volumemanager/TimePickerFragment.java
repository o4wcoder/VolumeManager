package com.fourthwardcoder.android.volumemanager;

import java.util.Calendar;
import java.util.Date;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements Constants {
	
	/********************************************************************/
	/*                         Constants                                */
	/********************************************************************/
	public static final String EXTRA_TIME = "com.fourthwardcoder.android.volumemanager.time";
	public static final String EXTRA_TITLE = "com.fourthwardcoder.android.volumemanager.title";
	//public static final String EXTRA_TYPE = "com.fourthwardcoder.android.volumemanager.type";
    public static final String TAG = "TimePickerFragment";
	
	/********************************************************************/
	/*                          Local Data                              */
	/********************************************************************/
	private Date mTime;
    private String mDialogTitle;
    private int mDateType;

	public static TimePickerFragment newInstance(Date date, String dialogTitle) {
		
		Log.d(TAG,"Inside new Instance");
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_TIME, date);
		args.putString(EXTRA_TITLE, dialogTitle);
		//args.putInt(EXTRA_TYPE, dateType);
		TimePickerFragment fragment = new TimePickerFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
    /************************************************************/
	/*                  Override Methods                        */
	/************************************************************/
	
	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		mTime = (Date)getArguments().getSerializable(EXTRA_TIME);
		mDialogTitle = (String)getArguments().getString(EXTRA_TITLE);

		//Create a Calendar to get the time
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mTime);
		
		int hour = calendar.get(Calendar.HOUR);
		int min = calendar.get(Calendar.MINUTE);
		
		View v = getActivity().getLayoutInflater()
				.inflate(R.layout.dialog_time, null);
		
		TimePicker timePicker = (TimePicker)v.findViewById(R.id.dialog_time_timePicker);
		timePicker.setCurrentHour(hour);
		timePicker.setCurrentMinute(min);
		timePicker.setIs24HourView(false);
		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hour, int min) {
				
				Log.d(TAG,"Got Hour " + hour);
				//retrieving the original date from the mTime value with a calendar
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(mTime);
				calendar.set(Calendar.HOUR_OF_DAY, hour);
				calendar.set(Calendar.MINUTE,min);
				
				//Translating hourOfDay & minute into a Date object using a calendar, date keeps
				//the same
				mTime = calendar.getTime();
				Log.d(TAG,"Hour in data object " + mTime.getHours());
			
			
				//Update arguments to preserve selected value on rotation
				getArguments().putSerializable(EXTRA_TIME, mTime);
			}
			
		});
		
	      return new AlertDialog.Builder(getActivity())
          .setView(v)
          .setTitle(mDialogTitle)
          .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
             
             @Override
             public void onClick(DialogInterface dialog, int which) {
                sendResult(Activity.RESULT_OK);
                
             }
          })
          .create();
		
	}
	
	/***************************************************************/
	/*                   Private Methods                           */
	/***************************************************************/
	
	private void sendResult(int resultCode) {
		if(getTargetFragment() == null)
			return;
		
		//Create intent and put extra on it
		Intent i = new Intent();
		i.putExtra(EXTRA_TIME,mTime);
		
		//Send result to Crime Fragment
		//Request code to tell the target who is returning hte result
		//result code to determine what action to take
		//An intent that can have extra data
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}
}
