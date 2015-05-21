package com.fourthwardcoder.android.volumemanager;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.location.Address;
import android.location.Location;
import android.util.Log;

public class LocationData {

	/*******************************************************/
	/*                     Local Data                      */
	/*******************************************************/
	private LatLng latLng;
	private Address address;
	private int fenceRadius;

	/*******************************************************/
	/*                     Constructors                    */
	/*******************************************************/
	public LocationData(LatLng latLng) {
		
		this.latLng = latLng;
	}
	/*
	
	
	public LocationProfile() {
		super.setId(UUID.randomUUID());
		
		//Setup defaults
		super.setEnabled(true);
		super.setStartVolumeType(VOLUME_OFF);
		super.setEndVolumeType(VOLUME_VIBRATE);
		super.setStartRingVolume(1);
		super.setEndRingVolume (1);
		
	}
	public LocationProfile(JSONObject json) throws JSONException {
		super.setId(UUID.fromString(json.getString(JSON_ID)));
		
		if(json.has(JSON_TITLE)) {
			super.setTitle(json.getString(JSON_TITLE));
		}
		
		super.setEnabled(json.getBoolean(JSON_ENABLED));
		super.setStartVolumeType(json.getInt(JSON_START_VOLUME_TYPE));
		super.setEndVolumeType(json.getInt(JSON_END_VOLUME_TYPE));
		super.setStartRingVolume(json.getInt(JSON_START_RING_VOLUME));
		super.setEndRingVolume(json.getInt(JSON_END_RING_VOLUME));
		
	}
	*/
	/*******************************************************/
	/*                    Public Methods                   */
	/*******************************************************/
	public Object toJSON() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public LatLng getLatLng() {
		return latLng;
	}
	public void setLocation(LatLng latLng) {
		this.latLng = latLng;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}

	public int getFenceRadius() {
		return fenceRadius;
	}

	public void setFenceRadius(int fenceRadius) {
		this.fenceRadius = fenceRadius;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}
	
}
