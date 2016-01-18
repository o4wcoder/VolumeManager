package com.fourthwardcoder.android.volumemanager.data;
import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

import com.fourthwardcoder.android.volumemanager.models.BasicProfile;
import com.fourthwardcoder.android.volumemanager.models.LocationProfile;

/******************************************************************/
/* Class: PorfileManager.java                                     */
/*                                                                */
/* Singleton Class to store one instance of ArrayList of Profiles */
/**************************************************************/
public class ProfileManager {
	
	/***************************************************/
	/*                  Local Data                     */
	/***************************************************/
	//Store array of profiles
	private ArrayList<BasicProfile> profileList;
	private ArrayList<LocationProfile> locationProfileList;

	//s prefix for static variable
	private static ProfileManager sProfileManager;
	
	private VolumeManagerJSONSerializer mSerializer;
	
	//Context parameter allows singleton to start activities,
	//access project resources, find application's private storage, etc
	private Context mAppContext;
	
	private static final String TAG = "ProfileManager";

	
	/***************************************************/
	/*                  Constructors                   */
	/***************************************************/
	private ProfileManager(Context appContext) {
		mAppContext = appContext;
		
		mSerializer = new VolumeManagerJSONSerializer(mAppContext);
		
		//Load Profiles from JSON file
		try {
			profileList =   this.mSerializer.loadProfiles();
		} catch (Exception e) {
			//No Profiles stored. Create empty list
			profileList =  new ArrayList<BasicProfile>();
			Log.e(TAG,"Error loading profiles: ", e);
		}
		
		
		//Load Location Profiles from JSON file
		try {
			locationProfileList =   this.mSerializer.loadLocationProfiles();
		} catch (Exception e) {
			//No Profiles stored. Create empty list
			Log.e(TAG,"No location files stored! Create empty list");
			locationProfileList =  new ArrayList<LocationProfile>();
			Log.e(TAG,"Error loading profiles: ", e);
		}

	}
	
	/**************************************************/
	/*                Public Methods                  */
	/**************************************************/
	//Serialize crimes and return if successful
	public boolean saveProfiles() {
		try {
			mSerializer.saveProfiles(profileList);
			Log.d(TAG,"profiles saved to file");
			return true;
		} catch (Exception e) {
			Log.e(TAG,"Error saving profiles: ",e);
			return false;
		}
	}
	
	
	public boolean saveLocationProfiles() {
		try {
			mSerializer.saveLocationProfiles(locationProfileList);
			Log.d(TAG,"location profiles saved to file");
			return true;
		} catch (Exception e) {
			Log.e(TAG,"Error saving profiles: ",e);
			return false;
		}
	}

	public void addProfile(BasicProfile p) {
		profileList.add(p);
	}
	
	
	public void addLocationProfile(LocationProfile p) {
		locationProfileList.add(p);
	}
	
	public void deleteProfile(BasicProfile p) {
		Log.d(TAG,"Delte profile " + p.getTitle());
		profileList.remove(p);
	}
	

	public void deleteLocationProfile(LocationProfile p) {
		Log.d(TAG,"Delte profile " + p.getTitle());
		locationProfileList.remove(p);
	}
	
	public ArrayList<BasicProfile> getProfiles() {
		return profileList;
	}
	
	
	public ArrayList<LocationProfile> getLocationProfiles() {
		return locationProfileList;
	}
	
	public BasicProfile getProfile(UUID id) {
		for (BasicProfile c : profileList) {
			if(c.getId().equals(id))
				return c;
		}
		
		return null;
	}

	
	public LocationProfile getLocationProfile(UUID id) {
		for (LocationProfile c : locationProfileList) {
			if(c.getId().equals(id))
				return c;
		}
		
		return null;
	}
	
	public static ProfileManager get(Context c) {
		if(sProfileManager == null) {
			//To ensure that the singleton has a long-term Context to work with,
			//call getApplicationContext() and trade the passed-in Context for the
			//application context
			sProfileManager = new ProfileManager(c.getApplicationContext());
		}
		return sProfileManager;
	}

}