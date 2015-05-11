package com.fourthwardcoder.android.volumemanager;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class BasicProfile extends Profile implements Constants{
	
	/*****************************************************/
	/*                    Constants                      */
	/*****************************************************/
	private final static String TAG = "Profile";

	
	private static final int DAYS_OF_THE_WEEK = 7;
	
	/*****************************************************/
	/*                  Local Data                       */
	/*****************************************************/
	//private UUID id;
	//private String title;
	//private boolean enabled;
	private Date startDate, endDate;
	private int startVolumeType, endVolumeType;
	private int startRingVolume, endRingVolume;
	private int alarmId;
	private boolean daysOfTheWeek[] = new boolean[DAYS_OF_THE_WEEK];
	private boolean inAlarm;

	/****************************************************/
	/*                 Constructors                    */
	/****************************************************/
	public BasicProfile() {
		super.setId(UUID.randomUUID());
		
		//Setup defaults
		super.setEnabled(true);
		startDate = new Date();
		endDate = new Date();
		startVolumeType = VOLUME_OFF;
		endVolumeType = VOLUME_VIBRATE;
		startRingVolume = 1;
		endRingVolume = 1;
		inAlarm = false;
		
		calculateAlarmId();
		initDaysOfWeek();
		
	}

	public BasicProfile(JSONObject json) throws JSONException {
		super.setId(UUID.fromString(json.getString(JSON_ID)));
	
		Log.d(TAG,"Inside Profile Constructor");
		if(json.has(JSON_TITLE)) {
			super.setTitle(json.getString(JSON_TITLE));
		}
		
		super.setEnabled(json.getBoolean(JSON_ENABLED));
		startDate = new Date(json.getLong(JSON_START_DATE));
		endDate = new Date(json.getLong(JSON_END_DATE));
		startVolumeType = json.getInt(JSON_START_VOLUME_TYPE);
		endVolumeType = json.getInt(JSON_END_VOLUME_TYPE);
		startRingVolume = json.getInt(JSON_START_RING_VOLUME);
		endRingVolume = json.getInt(JSON_END_RING_VOLUME);
		
		JSONArray daysArray = json.getJSONArray(JSON_DAYS_OF_THE_WEEK);
		
		inAlarm = json.getBoolean(JSON_IN_ALARM);
		
		for(int i = 0; i < DAYS_OF_THE_WEEK; i++)
			daysOfTheWeek[i] = (boolean) daysArray.get(i);
	
		calculateAlarmId();
	}

	

	/*****************************************************/
	/*                 Override Methods                  */
	/*****************************************************/
	@Override
	public String toString() {
		return super.getTitle();
	}
	
	/*****************************************************/
	/*                   Private Methods                 */
	/*****************************************************/
	private void initDaysOfWeek() {
		
		for(int i = 0; i < 7; i++)
			daysOfTheWeek[i] = true;
	}
	private void calculateAlarmId() {
		
		String str=""+super.getId();        
        int uid=str.hashCode();
        String filterStr=""+uid;
        str=filterStr.replaceAll("-", "");
        alarmId = Integer.parseInt(str);
		
	}
	/*****************************************************/
	/*                   Public Methods                  */
	/*****************************************************/
	
	public JSONObject toJSON() throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put(JSON_ID, super.getId().toString());
		json.put(JSON_TITLE, super.getTitle());
		json.put(JSON_ENABLED, super.isEnabled());
		json.put(JSON_START_DATE, startDate.getTime());
		json.put(JSON_END_DATE, endDate.getTime());
		json.put(JSON_START_VOLUME_TYPE, startVolumeType);
		json.put(JSON_END_VOLUME_TYPE, endVolumeType);
		json.put(JSON_START_RING_VOLUME, startRingVolume);
		json.put(JSON_END_RING_VOLUME, endRingVolume);
		json.put(JSON_IN_ALARM, inAlarm);
		
		JSONArray jArray = new JSONArray();
		for(int i = 0; i < DAYS_OF_THE_WEEK; i++)
			jArray.put(daysOfTheWeek[i]);
		
		json.put(JSON_DAYS_OF_THE_WEEK, jArray);
		
		Log.d(TAG,"Put json object: " + json.toString());
		return json;
	}
	
	public boolean[] getDaysOfTheWeek() {
		return daysOfTheWeek;
	}

	public void setDaysOfTheWeek(boolean[] daysOfTheWeek) {
		this.daysOfTheWeek = daysOfTheWeek;
	}
	
	/*
	public UUID getId() {
		return id;
	}
*/
	public int getStartAlarmId() {
		
		return alarmId;
	}
	
	public int getEndAlarmId() {
		
		return alarmId + 1;
	}
	/*
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
*/
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getStartVolumeType() {
		return startVolumeType;
	}

	public void setStartVolumeType(int startVolumeType) {
		this.startVolumeType = startVolumeType;
	}

	public int getEndVolumeType() {
		return endVolumeType;
	}

	public void setEndVolumeType(int endVolumeType) {
		this.endVolumeType = endVolumeType;
	}

	public int getStartRingVolume() {
		return startRingVolume;
	}

	public void setStartRingVolume(int startRingVolume) {
		this.startRingVolume = startRingVolume;
	}

	public int getEndRingVolume() {
		return endRingVolume;
	}

	public void setEndRingVolume(int endRingVolume) {
		this.endRingVolume = endRingVolume;
	}
	
	public String getFullTimeForListItem() {
		
		return ProfileListFragment.formatTime(startDate) + " - " + ProfileListFragment.formatTime(endDate);
	}
	
	public String getDaysOfWeekString() {

		String strDays = "";
		for(int i=0; i< DAYS_OF_THE_WEEK; i++) {
	        
			if(daysOfTheWeek[i]) {
				strDays += daysButtonNames[i];
				if(i != DAYS_OF_THE_WEEK - 1)
					strDays += ",";
			}
		}
		
		return strDays;
	}
	
	public boolean isInAlarm() {
		return inAlarm;
	}

	public void setInAlarm(boolean inAlarm) {
		this.inAlarm = inAlarm;
	}
}
