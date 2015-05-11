package com.fourthwardcoder.android.volumemanager;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class Profile implements Constants{
	
	private UUID id;
	private String title;
	private boolean enabled;
	
	//public Profile(JSONObject json) throws JSONException {
	//}

	/*
	public JSONObject toJSON() throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put(JSON_ID, id.toString());
		json.put(JSON_TITLE, title);
		json.put(JSON_ENABLED, enabled);
		
		return json;
	}
	*/
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
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
	
	

}
