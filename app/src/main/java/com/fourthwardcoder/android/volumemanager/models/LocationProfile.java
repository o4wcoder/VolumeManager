package com.fourthwardcoder.android.volumemanager.models;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.google.android.gms.maps.model.LatLng;

public class LocationProfile extends Profile implements Constants {

	/*******************************************************/
	/*                     Constants                       */
	/*******************************************************/
	private static final String JSON_LOCATION_DATA = "locationData";
	private static final String JSON_LATITUDE = "latitude";
	private static final String JSON_LONGITUDE = "longitude";
    private static final String JSON_ADDRESS = "address";
	private static final String JSON_CITY = "city";
	private static final String JSON_RADIUS = "radius";
	/*******************************************************/
	/*                     Local Data                      */
	/*******************************************************/
	private LatLng latLng;
	private String address;
	private String city;
	private float fenceRadius;

	/*******************************************************/
	/*                     Constructors                    */
	/*******************************************************/
	/*
	public LocationData(LatLng latLng) {

		this.latLng = latLng;
	}
	*/



	public LocationProfile() {

		latLng = null;
		fenceRadius = GEOFENCE_RADIUS_DEFAULT;

	}
	public LocationProfile(JSONObject json) throws JSONException {
		super.setId(UUID.fromString(json.getString(JSON_ID)));

		if(json.has(JSON_TITLE)) {
			super.setTitle(json.getString(JSON_TITLE));
		}

		this.address = json.getString(JSON_ADDRESS);
		this.city = json.getString(JSON_CITY);

		//Get LatLng data
		double latitude = json.getDouble(JSON_LATITUDE);
		double longitude = json.getDouble(JSON_LONGITUDE);
		latLng = new LatLng(latitude,longitude);

		super.setEnabled(json.getBoolean(JSON_ENABLED));
		super.setStartVolumeType(json.getInt(JSON_START_VOLUME_TYPE));
		super.setEndVolumeType(json.getInt(JSON_END_VOLUME_TYPE));
		super.setStartRingVolume(json.getInt(JSON_START_RING_VOLUME));
		super.setEndRingVolume(json.getInt(JSON_END_RING_VOLUME));

		fenceRadius = (float)json.getDouble(JSON_RADIUS);



	}

	/*******************************************************/
	/*                    Public Methods                   */
	/*******************************************************/
	public JSONObject toJSON() throws JSONException {

		JSONObject json = new JSONObject();
		json.put(JSON_ID, super.getId().toString());
		json.put(JSON_TITLE, super.getTitle());
		json.put(JSON_ENABLED, super.isEnabled());
		json.put(JSON_START_VOLUME_TYPE, super.getStartVolumeType());
		json.put(JSON_END_VOLUME_TYPE, super.getEndVolumeType());
		json.put(JSON_START_RING_VOLUME, super.getStartRingVolume());
		json.put(JSON_END_RING_VOLUME, super.getEndRingVolume());
		json.put(JSON_ADDRESS, address);
		json.put(JSON_CITY, city);
		json.put(JSON_LATITUDE, latLng.latitude);
		json.put(JSON_LONGITUDE, latLng.longitude);
		json.put(JSON_RADIUS, fenceRadius);



		return json;
	}

//	public LatLng getLocation() {
//		return this.latLng;
//	}
//	public void setLocation(LatLng latLng) {
//		this.latLng = latLng;
//	}
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public float getFenceRadius() {
		return fenceRadius;
	}

	public void setFenceRadius(float fenceRadius) {
		this.fenceRadius = fenceRadius;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}


}
