package com.fourthwardcoder.android.volumemanager;

import java.util.UUID;

import org.json.JSONObject;

import android.location.Address;
import android.location.Location;

public class LocationProfile extends Profile {

	/*******************************************************/
	/*                     Local Data                      */
	/*******************************************************/
	private Location location;
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
	
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	

}
