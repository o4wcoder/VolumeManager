//package com.fourthwardcoder.android.volumemanager.models;
//
//import java.util.Date;
//import java.util.UUID;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.google.android.gms.maps.model.LatLng;
//
//import android.location.Address;
//import android.location.Location;
//import android.util.Log;
//
//public class LocationData {
//
//	/*******************************************************/
//	/*                     Constants                       */
//	/*******************************************************/
//	private static final String JSON_LATITUDE = "latitude";
//	private static final String JSON_LONGITUDE = "longitude";
//    private static final String JSON_ADDRESS = "address";
//	private static final String JSON_CITY = "city";
//	private static final String JSON_RADIUS = "radius";
//	/*******************************************************/
//	/*                     Local Data                      */
//	/*******************************************************/
//	private LatLng latLng;
//	private Address address;
//	private String city;
//	private int fenceRadius;
//
//	/*******************************************************/
//	/*                     Constructors                    */
//	/*******************************************************/
//	public LocationData(LatLng latLng) {
//
//		this.latLng = latLng;
//	}
//	/*
//
//
//	public LocationProfile() {
//		super.setId(UUID.randomUUID());
//
//		//Setup defaults
//		super.setEnabled(true);
//		super.setStartVolumeType(VOLUME_OFF);
//		super.setEndVolumeType(VOLUME_VIBRATE);
//		super.setStartRingVolume(1);
//		super.setEndRingVolume (1);
//
//	}
//	public LocationProfile(JSONObject json) throws JSONException {
//		super.setId(UUID.fromString(json.getString(JSON_ID)));
//
//		if(json.has(JSON_TITLE)) {
//			super.setTitle(json.getString(JSON_TITLE));
//		}
//
//		super.setEnabled(json.getBoolean(JSON_ENABLED));
//		super.setStartVolumeType(json.getInt(JSON_START_VOLUME_TYPE));
//		super.setEndVolumeType(json.getInt(JSON_END_VOLUME_TYPE));
//		super.setStartRingVolume(json.getInt(JSON_START_RING_VOLUME));
//		super.setEndRingVolume(json.getInt(JSON_END_RING_VOLUME));
//
//	}
//	*/
//	/*******************************************************/
//	/*                    Public Methods                   */
//	/*******************************************************/
//	public JSONObject toJSON() throws JSONException {
//
//		JSONObject json = new JSONObject();
//		json.put(JSON_ADDRESS, address.getAddressLine(0));
//		json.put(JSON_CITY, city);
//
//
//		return json;
//	}
//
//	public LatLng getLatLng() {
//		return latLng;
//	}
//	public void setLocation(LatLng latLng) {
//		this.latLng = latLng;
//	}
//	public Address getAddress() {
//		return address;
//	}
//	public void setAddress(Address address) {
//		this.address = address;
//		this.city = address.getLocality() + ", " + address.getAdminArea();
//	}
//
//	public int getFenceRadius() {
//		return fenceRadius;
//	}
//
//	public void setFenceRadius(int fenceRadius) {
//		this.fenceRadius = fenceRadius;
//	}
//
//	public void setLatLng(LatLng latLng) {
//		this.latLng = latLng;
//	}
//	public String getCity() {
//		return city;
//	}
//
//
//}
