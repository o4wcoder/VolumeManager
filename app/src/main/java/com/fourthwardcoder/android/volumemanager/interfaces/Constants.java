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


	int DAYS_OF_THE_WEEK = 7;

	//Volume Constants
    int VOLUME_OFF = 0;
	int VOLUME_VIBRATE = 1;
	int VOLUME_RING = 2;
	
	//IDs to make the intents unique for the start and end alarms. Also used for setting widgets
    int ID_START_ALARM = 0;
	int ID_END_ALARM = 1;
	
	//Extras
	String EXTRA_START_ALARM = "com.fourthwardcoder.android.volumemanager.extra_startalarm";
	String EXTRA_PROFILE_ID = "com.fourthwardcoder.android.volumemanager.extra_profile_id";
	String EXTRA_PROFILE = "com.fourthwardcoder.android.volumemanager.extra_profile";
    String EXTRA_PROFILE_TYPE = "com.fourthwardcoder.android.volumemanager.extra_profile_type";
	String EXTRA_IS_NEW_PROFILE = "com.fourthwardcoder.android.volumemanager.extra_is_new_profile";
    String EXTRA_LOCATION = "com.fourthwardcoder.android.volumemanager.extra_location";
    String EXTRA_PROFILE_TITLE = "com.fourthwardcoder.android.volumemanager.extra_profile_title";
	
	float PRIMARY_TEXT_DARK = (float) .87;
	float SECONDARY_TEXT_DARK = (float) .54;
	
	String daysButtonNames[] ={"S","M","T","W","T","F","S"};
	String daysAbbreviation[]={"Su","Mo","Tu","We","Th","Fr","Sa"};
	
	int NORMAL_PROFILE = 0;
	int DISABLED_PROFILE = 1;
	
	float GEOFENCE_RADIUS_DEFAULT = 50;
	float GEOFENCE_RADIUS_INC = 10;

	int TIME_PROFILE_LIST = 0;
	int LOCATION_PROFILE_LIST = 1;
	
    //Notification Group
    String GROUP_NOTIFICATIONS = "group_notifications";
}
