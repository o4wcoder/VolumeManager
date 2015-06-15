package com.fourthwardcoder.android.volumemanager;

import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.location.GeofenceStatusCodes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class Util implements Constants{
	
	/***********************************************************************/
	/*                           Constants                                 */
	/***********************************************************************/
	private final static String TAG = "Util";
	/**
	 * Modify the format of the time of the alarms. Changes hour from 
	 * military time to standard. Also make sure the minute is two digits.
	 * 
	 * @param date Stores the time of the alarm
	 */
	public static String formatTime(Date date) {
		
		String am_or_pm = (date.getHours() < 12) ? "AM" : "PM";
		
		//Log.d(TAG,"In update time with hour " + date.getHours());
		
		//Create a Calendar to get the time
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		int hour = calendar.get(Calendar.HOUR);
		int min = calendar.get(Calendar.MINUTE);
		
		String strHour = String.valueOf(hour);
		
		if(hour == 0)
			strHour = "12";
		
		String strMin = String.valueOf(min);
		
		//Make sure minute is 2 digits
		if(min < 10)
			strMin = "0" + strMin;
		
		String time = strHour + ":" + strMin + " " + am_or_pm;
		
		return time;
	}
	
	/**
	 * Set the text on ring volume level
	 * @param textView TextView to set the ring volume level
	 * @param pos position of the seekbar
	 */
	public static void setRingVolumeText(TextView textView, int pos, int maxVolume) {
		
		textView.setText("(" + pos + "/" + maxVolume + ")");
	}
	
	/**
	 * Set the visibility of the RadioGroups. This is set by the on/off toggle switch
	 * @param radioGroup RadioGroup to set the visibility on
	 * @param set setting of the toggle switch
	 */
	public static void setRadioGroupVisibility(RadioGroup radioGroup, boolean set) {
		
		//Set visibility on each RadioButton in the Group
		for(int i = 0; i <radioGroup.getChildCount(); i++)
			((RadioButton)radioGroup.getChildAt(i)).setEnabled(set);
	}
	
	/**
	 * Set the ring volume seek bar at a specific position
	 * @param alarmType which alarm (start or end) to set
	 * @param pos seekbar position
	 */
	public static void setSeekBarPosition(SeekBar seekBar, TextView textView, int pos,int maxVolume) {

		seekBar.setProgress(pos);
		setRingVolumeText(textView,pos,maxVolume);

	}
	
	@SuppressLint("NewApi")
	public static void setStatusBarColor(Activity activity) {
		
	    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
			Window window = activity.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(activity.getResources().getColor(R.color.indigoPrimaryDark700));
	
	    }
	}
	

    
    public static void setAudioManager(Context context, int ringType, int ringVolume) {
    	
    	
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		Log.e(TAG,"Max stream audio is " + audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
		
		if(ringType == VOLUME_OFF)
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		else if(ringType == VOLUME_VIBRATE)
			audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		else {
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

			if(ringVolume > 0 && ringVolume <= audioManager.getStreamMaxVolume(AudioManager.STREAM_RING))
				audioManager.setStreamVolume(AudioManager.STREAM_RING, ringVolume, AudioManager.FLAG_PLAY_SOUND);
		}
    }
    
    public static int getMaxRingVolume(Context context) {
    	AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    	
    	return audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
    	
    }

}
