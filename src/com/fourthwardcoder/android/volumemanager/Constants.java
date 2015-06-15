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
	/*
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
	*/
	
	public static final String JSON_ID = "id";
	public static final String JSON_TITLE = "title";
	public static final String JSON_ENABLED = "enabled";
	public static final String JSON_START_VOLUME_TYPE = "startVolumeType";
	public static final String JSON_END_VOLUME_TYPE = "endVolumeType";
	public static final String JSON_START_RING_VOLUME = "startRingVolume";
	public static final String JSON_END_RING_VOLUME = "endRingVolume";

	
	public static final String PREF_VOLUME_NOTIFY_ENABLED = "volumeNotifyEnabled";
	public static final String PREF_LOCATION_NOTIFY_ENABLED = "locationNotifyEnabled";
	public static final String PREF_MAX_VOLUME = "maxVolume";
	
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
	public static final String EXTRA_LOCATION_PROFILE = "com.fourthwardcoder.android.volumemanager.location_profile";
	
	public static final String MONDAY = "Monday";
	public static final String TUESDAY = "Tuesday";
	public static final String WEDNESDAY = "Wednesday";
	public static final String THURSDAY = "THURSDAY";
	public static final String FRIDAY = "Friday";
	public static final String SATURDAY = "Saturday";
	public static final String SUNDAY = "Sunday";
	
	public static final float PRIMARY_TEXT_DARK = (float) .87;
	public static final float SECONDARY_TEXT_DARK = (float) .54;
	
	public static final String daysButtonNames[] ={"S","M","T","W","T","F","S"};
	public static final String daysAbbreviation[]={"Su","Mo","Tu","We","Th","Fr","Sa"};
	
	public static enum TabName {
		BASIC,
		LOCATION
	}
	
	public static final String TAB_ID = "tabId";
	
	public static final int NORMAL_PROFILE = 0;
	public static final int DISABLED_PROFILE = 1;
	
	public static final float GEOFENCE_RADIUS_DEFAULT = 50;
	public static final float GEOFENCE_RADIUS_INC = 10;
	
	

}
