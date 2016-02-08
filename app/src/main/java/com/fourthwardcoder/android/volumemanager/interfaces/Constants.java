package com.fourthwardcoder.android.volumemanager.interfaces;

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
	public static final String PREF_VOLUME_NOTIFY_ENABLED = "volumeNotifyEnabled";
	public static final String PREF_LOCATION_NOTIFY_ENABLED = "locationNotifyEnabled";
	public static final String PREF_MAX_VOLUME = "maxVolume";

	public static final int DAYS_OF_THE_WEEK = 7;

	//Volume Constants
	public static final int VOLUME_OFF = 0;
	public static final int VOLUME_VIBRATE = 1;
	public static final int VOLUME_RING = 2;
	
	//IDs to make the intents unique for the start and end alarms. Also used for setting widgets
	public static final int ID_START_ALARM = 0;
	public static final int ID_END_ALARM = 1;
	
	//Extras
	public static final String EXTRA_START_ALARM = "com.fourthwardcoder.android.volumemanager.extra_startalarm";
	public static final String EXTRA_PROFILE_ID = "com.fourthwardcoder.android.volumemanager.extra_profile_id";
	public static final String EXTRA_PROFILE = "com.fourthwardcoder.android.volumemanager.extra_profile";
	public static final String EXTRA_LOCATION_PROFILE = "com.fourthwardcoder.android.volumemanager.extra_location_profile";
    public static final String EXTRA_PROFILE_TYPE = "com.fourthwardcoder.android.volumemanager.extra_profile_type";
	
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

	public static final int TIME_PROFILE_LIST = 0;
	public static final int LOCATION_PROFILE_LIST = 1;
	

}
