package com.fourthwardcoder.android.volumemanager.helpers;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.Profile;
import com.google.android.gms.maps.model.LatLng;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class Util implements Constants {
	
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
	public static String formatTime(Context context, Date date) {

        String time;
        int hour;

        //Create a Calendar to get the time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int min = calendar.get(Calendar.MINUTE);

        String strMin = String.valueOf(min);

        //Make sure minute is 2 digits
        if (min < 10)
            strMin = "0" + strMin;

        if(!DateFormat.is24HourFormat(context)) {
           // Log.e(TAG, "Hours in date: " + date.getHours());
            String am_or_pm = (date.getHours() < 12) ? "AM" : "PM";

            hour = calendar.get(Calendar.HOUR);
            String strHour = String.valueOf(hour);

            if (hour == 0)
                strHour = "12";

            time = strHour + ":" + strMin + " " + am_or_pm;
        }
        else {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            time = String.valueOf(hour) + ":" + strMin;
        }

		return time;
	}

	public static String getFullTimeForListItem(Context context, Profile profile) {

		return Util.formatTime(context, profile.getStartDate()) + " - " + Util.formatTime(context, profile.getEndDate());
	}

	/**
	 * Modify the format of the time of the alarms. Changes hour from 
	 * military time to standard. Also make sure the minute is two digits.
	 * 
	 * @param date Stores the time of the alarm
	 */
	public static void setTimeForLargeTextView(Context context, Date date, TextView textView) {

        //Make the AM or PM half the size of the time
        String time = formatTime(context,date);

        if(!DateFormat.is24HourFormat(context)) {
            SpannableString ss1 = new SpannableString(time);
            ss1.setSpan(new RelativeSizeSpan(.5f), time.length() - 3, time.length(), 0);

            textView.setText(ss1);
        }
        else {
            textView.setText(time);
        }
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


//	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//			Window window = activity.getWindow();
//			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			window.setStatusBarColor(activity.getResources().getColor(R.color.app_primary_dark_color));
//
//	    }
	}

    public static ValueAnimator getIconAnimator(Context context, ImageView image) {

        final int startColor = context.getResources().getColor(R.color.app_primary_color);
        final int endColor = context.getResources().getColor(R.color.green_accent_color);

        ValueAnimator valueAnimator = ObjectAnimator.ofInt(image, "colorFilter", startColor, endColor);
        valueAnimator.setDuration(1000);
        valueAnimator.setEvaluator(new ArgbEvaluator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);

        return valueAnimator;
    }

    public static void setListIconColor(Context context, ValueAnimator va, ImageView image, boolean inAlarm) {

        if (inAlarm) {
            // image.setColorFilter(context.getResources().getColor(R.color.app_in_alarm_color));
            Log.e(TAG, "Got in alarm, starting animation");
            va.start();
        } else {
            if (va.isRunning())
                va.cancel();

            image.setColorFilter(context.getResources().getColor(R.color.app_primary_color));
        }

    }

    public static void updateWidget(Context context, UUID profileId, int profileType) {

        Intent alarmUpdateIntent = new Intent(ACTION_ALARM_UPDATED)
                .setPackage(context.getPackageName());
        alarmUpdateIntent.putExtra(EXTRA_PROFILE_ID,profileId);
        alarmUpdateIntent.putExtra(EXTRA_PROFILE_TYPE,profileType);
        context.sendBroadcast(alarmUpdateIntent);
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

    /*****************************************************************************/
    /*                       Location/Maps Helpers                               */
    /*****************************************************************************/
    public static Address getStreetAddress(Context context, LatLng latLng) throws IOException {

        //Get Address of current location
        Address currentAddress = null;

        if(Geocoder.isPresent()) {
            Geocoder gcd = new Geocoder(context);

            List<Address> addresses = gcd.getFromLocation(latLng.latitude,
                    latLng.longitude,1);

            if(addresses.size() > 0)
                currentAddress = addresses.get(0);
        }

        return currentAddress;
    }



}
