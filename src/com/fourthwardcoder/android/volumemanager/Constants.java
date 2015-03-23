package com.fourthwardcoder.android.volumemanager;

/**
 * Constants
 * 
 * Contains the constants share between the files
 * 
 * @author Chris Hare
 *
 */
public interface Constants {
	
	//Shared Preferences
	public static final String PREF_CONTROL_ENABLED = "controlEnabled";
	public static final String PREF_START_HOUR = "startHour";
	public static final String PREF_START_MIN = "startMin";
	public static final String PREF_END_HOUR = "endHour";
	public static final String PREF_END_MIN = "endMib";
	public static final String PREF_START_VOLUME_TYPE = "startRingType";
	public static final String PREF_END_VOLUME_TYPE = "endRingType";
	public static final String PREF_START_RING_VOLUME = "startRingVolume";
	public static final String PREF_END_RING_VOLUME = "endRingVolume";
	public static final String PREF_IS_ALARM_ON = "isAlarmOn";
	
	//Volume Constants
	public static final int VOLUME_OFF = 0;
	public static final int VOLUME_VIBRATE = 1;
	public static final int VOLUME_RING = 2;
	
	//IDs to make the intents unique for the start and end alarms. Also used for setting widgets
	public static final int ID_START_ALARM = 0;
	public static final int ID_END_ALARM = 1;
	
	//Extras
	public static final String EXTRA_START_ALARM = "com.fourthwardcoder.android.volumemanager.startalarm";
	public static final String EXTRA_PROFILE_ID = "com.fourthwardcoder.android.volumemanager.profile_id";

}
