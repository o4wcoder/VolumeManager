package com.fourthwardcoder.android.volumemanager;

import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.location.GeofenceStatusCodes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

public class Util {
	
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
	
	@SuppressLint("NewApi")
	public static void setStatusBarColor(Activity activity) {
		
	    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
			Window window = activity.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(activity.getResources().getColor(R.color.statusBarColor));
	
	    }
	}
	
    /** 
     * Returns the error string for a geofencing error code. 
     */ 
    public static String getGeofenceErrorString(Context context, int errorCode) {
        Resources mResources = context.getResources();
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE: 
                return mResources.getString(R.string.geofence_not_available);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES: 
                return mResources.getString(R.string.geofence_too_many_geofences);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS: 
                return mResources.getString(R.string.geofence_too_many_pending_intents);
            default: 
                return mResources.getString(R.string.unknown_geofence_error);
        } 
    } 

}
