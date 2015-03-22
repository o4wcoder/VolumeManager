package com.fourthwardcoder.android.volumemanager;
import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

/******************************************************************/
/* Class: PorfileManager.java                                     */
/*                                                                */
/* Singleton Class to store one instance of ArrayList of Profiles */
/******************************************************************/
public class ProfileManager {
	
	/***************************************************/
	/*                  Local Data                     */
	/***************************************************/
	//Store array of profiles
	private ArrayList<Profile> profileList;
	//s prefix for static variable
	private static ProfileManager sProfileManager;
	
	private VolumeManagerJSONSerializer mSerializer;
	
	//Context parameter allows singleton to start activities,
	//access project resources, find application's private storage, etc
	private Context mAppContext;
	
	private static final String TAG = "ProfileManager";
	private static final String FILENAME = "profiles.json";
	
	/***************************************************/
	/*                  Constructors                   */
	/***************************************************/
	private ProfileManager(Context appContext) {
		mAppContext = appContext;
		
		mSerializer = new VolumeManagerJSONSerializer(mAppContext,FILENAME);
		
		//Load Crimes from JSON file
		try {
			profileList = this.mSerializer.loadProfiles();
		} catch (Exception e) {
			//No Crimes stored. Create empty list
			profileList = new ArrayList<Profile>();
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
	public void addProfile(Profile p) {
		profileList.add(p);
	}
	
	public void deleteProfile(Profile p) {
		Log.d(TAG,"Delte profile " + p.getTitle());
		profileList.remove(p);
	}
	
	public ArrayList<Profile> getProfiles() {
		return profileList;
	}
	
	public Profile getProfile(UUID id) {
		for (Profile c : profileList) {
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
