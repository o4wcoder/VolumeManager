package com.fourthwardcoder.android.volumemanager;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Profile implements Constants{
	
	/*****************************************************/
	/*                    Constants                      */
	/*****************************************************/
	private static final String JSON_ID = "id";
	private static final String JSON_ALARM_ID = "alarmId";
	private static final String JSON_TITLE = "title";
	private static final String JSON_ENABLED = "enabled";
	private static final String JSON_START_DATE = "startDate";
	private static final String JSON_END_DATE = "endDate";
	private static final String JSON_START_VOLUME_TYPE = "startVolumeType";
	private static final String JSON_END_VOLUME_TYPE = "endVolumeType";
	private static final String JSON_START_RING_VOLUME = "startRingVolume";
	private static final String JSON_END_RING_VOLUME = "endRingVolume";
	
	/*****************************************************/
	/*                  Local Data                       */
	/*****************************************************/
	private UUID id;
	private String title;
	private boolean enabled;
	private Date startDate, endDate;
	private int startVolumeType, endVolumeType;
	private int startRingVolume, endRingVolume;
	private int alarmId;
	private HashMap<String,Boolean> daysOfWeek = new HashMap<String,Boolean>();
	
	/****************************************************/
	/*                 Constructors                    */
	/****************************************************/
	public Profile() {
		this.id = UUID.randomUUID();
		
		//Setup defaults
		this.enabled = true;
		startDate = new Date();
		endDate = new Date();
		startVolumeType = VOLUME_OFF;
		endVolumeType = VOLUME_VIBRATE;
		startRingVolume = 1;
		endRingVolume = 1;
		
		calculateAlarmId();
		initDaysOfWeek();
		
	}

	public Profile(JSONObject json) throws JSONException {
		id = UUID.fromString(json.getString(JSON_ID));
	
		if(json.has(JSON_TITLE)) {
			title = json.getString(JSON_TITLE);
		}
		
		enabled = json.getBoolean(JSON_ENABLED);
		startDate = new Date(json.getLong(JSON_START_DATE));
		endDate = new Date(json.getLong(JSON_END_DATE));
		startVolumeType = json.getInt(JSON_START_VOLUME_TYPE);
		endVolumeType = json.getInt(JSON_END_VOLUME_TYPE);
		startRingVolume = json.getInt(JSON_START_RING_VOLUME);
		endRingVolume = json.getInt(JSON_END_RING_VOLUME);
		
		calculateAlarmId();
	}
	/*****************************************************/
	/*                 Override Methods                  */
	/*****************************************************/
	@Override
	public String toString() {
		return title;
	}
	
	/*****************************************************/
	/*                   Private Methods                 */
	/*****************************************************/
	private void initDaysOfWeek() {
		
		//daysOfWeek.put(MONDAY,true);
		//daysOfWeek.put(key, value)
	}
	private void calculateAlarmId() {
		
		String str=""+id;        
        int uid=str.hashCode();
        String filterStr=""+uid;
        str=filterStr.replaceAll("-", "");
        alarmId = Integer.parseInt(str);
		
	}
	/*****************************************************/
	/*****************************************************/
	/*                   Public Methods                  */
	/*****************************************************/
	
	public JSONObject toJSON() throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put(JSON_ID, id.toString());
		json.put(JSON_TITLE, title);
		json.put(JSON_ENABLED, enabled);
		json.put(JSON_START_DATE, startDate.getTime());
		json.put(JSON_END_DATE, endDate.getTime());
		json.put(JSON_START_VOLUME_TYPE, startVolumeType);
		json.put(JSON_END_VOLUME_TYPE, endVolumeType);
		json.put(JSON_START_RING_VOLUME, startRingVolume);
		json.put(JSON_END_RING_VOLUME, endRingVolume);
		return json;
	}
	
	public UUID getId() {
		return id;
	}

	public int getStartAlarmId() {
		
		return alarmId;
	}
	
	public int getEndAlarmId() {
		
		return alarmId + 1;
	}
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

}
