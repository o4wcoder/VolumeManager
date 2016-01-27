package com.fourthwardcoder.android.volumemanager.fragments;

import java.util.ArrayList;

import com.fourthwardcoder.android.volumemanager.data.ProfileJSONManager;
import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.activites.SettingsActivity;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.activites.LocationMapActivity;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.location.GeofenceManager;
import com.fourthwardcoder.android.volumemanager.models.LocationProfile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class LocationProfileListFragment extends Fragment implements Constants, ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status>{
	
	/***************************************************/
	/*                  Constants                      */
	/***************************************************/
	private static final String TAG = "LocationProfileListFragment";
	
	
	/***************************************************/
	/*                 Local Data                      */
	/***************************************************/
	private ArrayList<LocationProfile> profileList;
	ProfileListAdapter profileAdapter;
	ListView listview;
	TabName tab;
	 GoogleApiClient mGoogleApiClient;
	/***************************************************/
	/*                Override Methods                 */
	/***************************************************/
	

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Tell the Fragment Manager that ProfileListFragment needs
		//to receive options menu callbacks
		setHasOptionsMenu(true);
		
		//retain the instance on rotation
		setRetainInstance(true);
				   
		profileList = ProfileJSONManager.get(getActivity()).getLocationProfiles();

        Util.setStatusBarColor(getActivity());
        
		//Build Google API Client
		mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(LocationServices.API)
		.build();
	
		
	}
	@SuppressLint("NewApi")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_location_profile_list, container, false);

        Log.d(TAG,"Inside LocationProfileListFragment");
		
		Button newProfileButton = (Button)view.findViewById(R.id.emptyButtonAddProfile);
		newProfileButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				newProfile();
				
			}
			
		});
		

		
		listview = (ListView)view.findViewById(android.R.id.list);
		
		
		profileAdapter = new ProfileListAdapter(profileList,listview);
		listview.setAdapter(profileAdapter);
		listview.setEmptyView(view.findViewById(android.R.id.empty));

		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub

				LocationProfile p = (LocationProfile)profileAdapter.getItem(position);
				Log.d(TAG,"Got profile " + p.getTitle());

				//Start CrimePagerActivity with this Crime
				Intent i = new Intent(getActivity(),LocationMapActivity.class);

				//Tell Volume Manager Fragment which Profile to display by making
				//giving id as Intent extra
				i.putExtra(EditProfileFragment.EXTRA_PROFILE_ID,p.getId());
				startActivity(i);

			}

		});

		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			//Use floating context menues for Froyo and Gingerbread
			registerForContextMenu(listview);
		}
		else {
			//Use contextual action bar on Honeycomb and higher.
			//Setting choice to MULTIPLE_MODAL allows multiselect of items
			listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

			//Set listener for context menu in action mode
			listview.setMultiChoiceModeListener(new MultiChoiceModeListener() {

				@Override
				public boolean onActionItemClicked(android.view.ActionMode mode,
						MenuItem item) {
					switch (item.getItemId()) {
					case R.id.menu_item_delete_profile:
						
						ProfileJSONManager profileJSONManager = ProfileJSONManager.get(getActivity());
 
						Log.d(TAG,"in onActionItemClicked with adapter count "+ profileAdapter.getCount());
						//Delete selected crimes
						for(int i = profileAdapter.getCount() - 1; i >= 0; i--) {
							if(listview.isItemChecked(i)) {
								//Kill alarms for volume control
								//VolumeManagerService.setServiceAlarm(getActivity().getApplicationContext(), profileAdapter.getItem(i),false);
								profileJSONManager.deleteLocationProfile(profileAdapter.getItem(i));
							}
						}

						//Destroy Action mode context menu
						mode.finish();
                        profileJSONManager.saveLocationProfiles();
						profileAdapter.notifyDataSetChanged();
						
						updateGeofences();
						
						return true;
					default:
						return false;
					}
				}
				@Override
				public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {

					MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(R.menu.context_menu, menu);

					return true;
				}

				@Override
				public void onDestroyActionMode(android.view.ActionMode mode) {
					// TODO Auto-generated method stub

				}

				@Override
				public boolean onPrepareActionMode(android.view.ActionMode mode,
						Menu menu) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void onItemCheckedStateChanged(android.view.ActionMode mode,
						int position, long id, boolean checked) {
					// TODO Auto-generated method stub
					Log.d(TAG,"Selected list item at position " + position);

				}


			});
		}
		
		

		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		Log.d(TAG,"onResume");
		mGoogleApiClient.connect();

			//getListAdapter()
		  profileAdapter.notifyDataSetChanged();
		
		   
	}
	
	@Override
	public void onPause() {
		super.onPause();

	    mGoogleApiClient.disconnect();
		
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//super.onCreateOptionsMenu(menu, inflater);
		
		//Pass the resource ID of the menu and populate the Menu 
		//instance with the items defined in the xml file
		inflater.inflate(R.menu.action_bar_list_menu, menu);
		
	}
	
	//Context Menu
	@Override
	public void onCreateContextMenu(ContextMenu menu,View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		Log.d(TAG,"in onContextItemSelected");
		//Get menu item in context menu. ListView is a subclass of AdapterView
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int position = info.position;
		
		LocationProfile profile = profileAdapter.getItem(position);
		
		switch (item.getItemId()) {
		   case R.id.menu_item_delete_profile:
			   ProfileJSONManager.get(getActivity()).deleteLocationProfile(profile);
			   ProfileJSONManager.get(getActivity()).saveLocationProfiles();
			   profileAdapter.notifyDataSetChanged();
			   
			   updateGeofences();
			   
			   return true;
		  
		}
		
		return super.onContextItemSelected(item);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Get menu option by it's ID
		switch(item.getItemId()) {
		//New Crime menu item
		case R.id.menu_item_new_profile:
			newProfile();
			//Return true, no further processing is necessary
			return true;  	
		case R.id.menu_item_settings:
			Intent settingsIntent = new Intent(getActivity(),SettingsActivity.class);
			startActivity(settingsIntent);
			return true;
		case R.id.menu_item_about:
			FragmentManager fm = getActivity().getFragmentManager();
			AboutFragment dialog = AboutFragment.newInstance();
			//Make ProfileListFragment the target fragment of the TimePickerFragment instance
			//dialog.setTargetFragment(VolumeManagerFragment.this, REQUEST_START_TIME);
			dialog.show(fm, "about");
			
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/***********************************************************/
	/*                   Private Methods                       */
	/***********************************************************/
	private void newProfile()
	{
    	//Add profile to the static List Array of Locations
    	LocationProfile profile = new LocationProfile();
    	ProfileJSONManager.get(getActivity()).addLocationProfile(profile);
    	
    	Intent i = new Intent(getActivity(),LocationMapActivity.class);
    	
    	//Send the profile ID in the intent to 
    	i.putExtra(EditProfileFragment.EXTRA_PROFILE_ID, profile.getId());
    	
    	//Start CrimePagerActivity
    	startActivityForResult(i,0);
	
	}
	
	private void updateGeofences() {
		
		if(mGoogleApiClient.isConnected()) {
			
			GeofenceManager geofenceManager = new GeofenceManager(getActivity().getApplicationContext(),mGoogleApiClient);
			
			//Restart geofences now that the state has changed. 
			geofenceManager.startGeofences(this);
		}
		else
			Log.e(TAG,"Connection to Google API is disconnected! Not good!");
	}
	
	/*******************************************************************/
	/*                        Public Methods                           */
	/*******************************************************************/


	/************************************************************/
	/*                      Inner Classes                       */
	/************************************************************/
	//Class to hold different views of the listview. This helps
	//it run smoothly when scrolling
	private static class ViewHolder {

		public TextView titleTextView;
		//public TextView timeTextView;
		//public TextView daysTextView;
		public TextView addressTextView;
		public TextView cityTextView;
		public TextView radiusTextView;
	}
	
	private class ProfileListAdapter extends ArrayAdapter<LocationProfile> {

		private ListView listview;
		private ArrayList<LocationProfile> profiles;
		
		private static final int NORMAL_PROFILE = 0;
		private static final int MOVING_PROFILE = 1;
		
		public ProfileListAdapter(ArrayList<LocationProfile> profileList, ListView listview) {
			super(getActivity(), 0, profileList);
			// TODO Auto-generated constructor stub
			
			this.listview = listview;
			this.profiles = profileList;
		}
		
		//Override method needed from multiple layouts in listview
		//Determines the type of layout to display in the row
		@Override
		public int getItemViewType(int position) {
            
			if(getItem(position).isEnabled() == true)
				return NORMAL_PROFILE;
			else
				return DISABLED_PROFILE;

		}
		//Override method needed from multiple layouts in listview
		//Returns how many different layouts the listview can have
		@Override
		public int getViewTypeCount() {
			return 2;
		}
		//Override getView to return a view inflated from the custom
		//layout and inflated with Profile Data
		
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
            int rowType = getItemViewType(position);
            
            //Log.e(TAG,"VP: " + listView.getFirstVisiblePosition() +": " + getItem(listView.getFirstVisiblePosition()).getHour() + " hp: " + headerPosition);
       
        
			//If we weren't given a view, inflate one
            if (convertView == null) {
            	holder = new ViewHolder();

            	
         
            	if(rowType == NORMAL_PROFILE) {
            		convertView = getActivity().getLayoutInflater().inflate(R.layout.location_profile_list_item,null);	           		        	    
            	}
            	else {
            		//Set up disabled profile
            		convertView = getActivity().getLayoutInflater().inflate(R.layout.location_profile_list_item_off,null);
            	}
            	convertView.setTag(holder);
            }
			else {
				holder = (ViewHolder)convertView.getTag();
				//Log.e(TAG,"In getView(!null) with profile " +holder.titleTextView.getText() + " position: " + position);
			}
            
          //Set up click listner on volume image button to turn profile on/off
    		ImageView volumeImage = (ImageView)convertView.findViewById(R.id.volumeStartImageView);
    		
    		volumeImage.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				//get the position from the view's tag
    				
    				Integer listPosition = (Integer)v.getTag();
    				Log.e(TAG,"Click image at position " + v.getTag().toString());
    				
    				//Toggle Porfile's enabled state
    				if(getItem(listPosition).isEnabled()) {
    					
    				    //Turn off geofence for this profile
    					getItem(listPosition).setEnabled(false);
    				}
    				else {
    					//Turn on geofence for this profile
    					getItem(listPosition).setEnabled(true);
    				}
    				//refresh listview
    				profileAdapter.notifyDataSetChanged();
    				
    				//Modify geofences
    			    updateGeofences();
    				
    			}
    			
    		});
    		
    		volumeImage.setTag(new Integer(position));
            //Set title
        	holder.titleTextView = (TextView)convertView.findViewById(R.id.profileTitleTextView);
            holder.titleTextView.setText(getItem(position).getTitle());
            //Set address
            holder.addressTextView = (TextView)convertView.findViewById(R.id.addressTextView);
            holder.addressTextView.setText(getItem(position).getAddress());
            //Set city
            holder.cityTextView = (TextView)convertView.findViewById(R.id.cityTextView);
            holder.cityTextView.setText(getItem(position).getCity());

            
            
            return convertView;
		}
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		

		
	}
	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onResult(Status status) {
		
		if(status.isSuccess()) {
			Log.i(TAG,"Success starting Geofences on startup");
			
		}
		else {
			// Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceManager.getGeofenceErrorString(getActivity().getApplicationContext(),
                    status.getStatusCode());
            Log.e(TAG, errorMessage);		
		}
		
		
	}
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}
}
