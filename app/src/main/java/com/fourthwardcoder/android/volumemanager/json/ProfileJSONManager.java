//package com.fourthwardcoder.android.volumemanager.json;
//import java.util.ArrayList;
//import java.util.UUID;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.fourthwardcoder.android.volumemanager.models.LocationProfile;
//import com.fourthwardcoder.android.volumemanager.models.Profile;
//
///******************************************************************/
///* Class: PorfileManager.java                                     */
///*                                                                */
///* Singleton Class to store one instance of ArrayList of Profiles */
///**************************************************************/
//public class ProfileJSONManager {
//
//	/***************************************************/
//	/*                  Local Data                     */
//	/***************************************************/
//	//Store array of profiles
//	private ArrayList<Profile> profileList;
//	private ArrayList<LocationProfile> locationProfileList;
//
//	//s prefix for static variable
//	private static ProfileJSONManager sProfileJSONManager;
//
//	private VolumeManagerJSONSerializer mSerializer;
//
//	//Context parameter allows singleton to start activities,
//	//access project resources, find application's private storage, etc
//	private Context mAppContext;
//
//	private static final String TAG = "ProfileJSONManager";
//
//
//	/***************************************************/
//	/*                  Constructors                   */
//	/***************************************************/
//	private ProfileJSONManager(Context appContext) {
//		mAppContext = appContext;
//
//		mSerializer = new VolumeManagerJSONSerializer(mAppContext);
//
//		//Load Profiles from JSON file
////		try {
////			profileList =   this.mSerializer.loadProfiles();
////		} catch (Exception e) {
////			//No Profiles stored. Create empty list
////			profileList =  new ArrayList<Profile>();
////			Log.e(TAG,"Error loading profiles: ", e);
////		}
//
//
//		//Load Location Profiles from JSON file
//		try {
//			locationProfileList =   this.mSerializer.loadLocationProfiles();
//		} catch (Exception e) {
//			//No Profiles stored. Create empty list
//			Log.e(TAG,"No location files stored! Create empty list");
//			locationProfileList =  new ArrayList<LocationProfile>();
//			Log.e(TAG,"Error loading profiles: ", e);
//		}
//
//	}
//
//	/**************************************************/
//	/*                Public Methods                  */
//	/**************************************************/
//	//Serialize crimes and return if successful
////	public boolean saveProfiles() {
////		try {
////			mSerializer.saveProfiles(profileList);
////			Log.d(TAG,"profiles saved to file");
////			return true;
////		} catch (Exception e) {
////			Log.e(TAG,"Error saving profiles: ",e);
////			return false;
////		}
////	}
//
//
//	public boolean saveLocationProfiles() {
//		try {
//			mSerializer.saveLocationProfiles(locationProfileList);
//			Log.d(TAG,"location profiles saved to file");
//			return true;
//		} catch (Exception e) {
//			Log.e(TAG,"Error saving profiles: ",e);
//			return false;
//		}
//	}
//
////	public void addProfile(Profile p) {
////		profileList.add(p);
////	}
//
//
//	public void addLocationProfile(LocationProfile p) {
//		locationProfileList.add(p);
//	}
//
////	public void deleteProfile(Profile p) {
////		Log.d(TAG,"Delte profile " + p.getTitle());
////		profileList.remove(p);
////	}
//
//
//	public void deleteLocationProfile(LocationProfile p) {
//		Log.d(TAG,"Delte profile " + p.getTitle());
//		locationProfileList.remove(p);
//	}
//
//	public ArrayList<Profile> getProfiles() {
//		return profileList;
//	}
//
//
//	public ArrayList<LocationProfile> getLocationProfiles() {
//		return locationProfileList;
//	}
//
////	public Profile getProfile(UUID id) {
////		for (Profile c : profileList) {
////			if(c.getId().equals(id))
////				return c;
////		}
////
////		return null;
////	}
//
//
//	public LocationProfile getLocationProfile(UUID id) {
//		for (LocationProfile c : locationProfileList) {
//			if(c.getId().equals(id))
//				return c;
//		}
//
//		return null;
//	}
//
//	public static ProfileJSONManager get(Context c) {
//		if(sProfileJSONManager == null) {
//			//To ensure that the singleton has a long-term Context to work with,
//			//call getApplicationContext() and trade the passed-in Context for the
//			//application context
//			sProfileJSONManager = new ProfileJSONManager(c.getApplicationContext());
//		}
//		return sProfileJSONManager;
//	}
//
//}
