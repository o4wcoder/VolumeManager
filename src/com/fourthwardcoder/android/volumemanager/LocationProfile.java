package com.fourthwardcoder.android.volumemanager;

import java.util.UUID;

import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.location.Address;
import android.location.Location;

public class LocationProfile extends Profile {

	/*******************************************************/
	/*                     Local Data                      */
	/*******************************************************/
	private LatLng latLng;
	private Address address;

	/*******************************************************/
	/*                     Constructors                    */
	/*******************************************************/
	public LocationProfile() {
		super.setId(UUID.randomUUID());
		
		//Setup defaults
		super.setEnabled(true);
	}
	public LocationProfile(JSONObject jsonObject) {
		// TODO Auto-generated constructor stub
	}
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
	

}
