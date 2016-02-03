package com.fourthwardcoder.android.volumemanager.fragments;

import java.util.ArrayList;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.fourthwardcoder.android.volumemanager.data.ProfileContract;
import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.activites.SettingsActivity;
import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.services.VolumeManagerService;
import com.fourthwardcoder.android.volumemanager.activites.ProfileDetailActivity;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.Profile;


public class ProfileListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
Constants{
	
	/***************************************************/
	/*                  Constants                      */
	/***************************************************/
	private static final String TAG = "ProfileListFragment";

    //ID for Profile Loader
    private static final int PROFILE_LOADER = 0;
	
	
	/***************************************************/
	/*                 Local Data                      */
	/***************************************************/
	private ArrayList<Profile> mProfileList;
	ProfileListAdapter profileAdapter;
	ListView listview;
	TabName tab;
	/***************************************************/
	/*                Override Methods                 */
	/***************************************************/
	
	public static ProfileListFragment newInstance() {

		Bundle args = new Bundle();
		ProfileListFragment fragment = new ProfileListFragment();

		fragment.setArguments(args);
		return fragment;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Log.e(TAG,"Oncreate");
		//Tell the Fragment Manager that ProfileListFragment needs
		//to receive options menu callbacks
		setHasOptionsMenu(true);
		
		//retain the instance on rotation
		setRetainInstance(true);
				   
		//profileList = ProfileJSONManager.get(getActivity()).getProfiles();

        //Init the Profile Loader. Callbacks received in this fragment
        getLoaderManager().initLoader(PROFILE_LOADER,null,this);

        Util.setStatusBarColor(getActivity());
        
	}
	@SuppressLint("NewApi")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_profile_list, container, false);


		//Empty ListView view
		Button newProfileButton = (Button)view.findViewById(R.id.emptyButtonAddProfile);
		newProfileButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ProfileManager.newProfile(getActivity());
				
			}
			
		});

		
       // Log.e(TAG,"onCreateView with prifile list size " + profileList.size());
		
		listview = (ListView)view.findViewById(android.R.id.list);


		listview.setEmptyView(view.findViewById(android.R.id.empty));

		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub

				Profile p = (Profile)profileAdapter.getItem(position);
				Log.d(TAG,"Got profile " + p.getTitle());

				//Start CrimePagerActivity with this Crime
				Intent i = new Intent(getActivity(),ProfileDetailActivity.class);

				//Tell Volume Manager Fragment which Profile to display by making
				//giving id as Intent extra
				i.putExtra(ProfileDetailFragment.EXTRA_PROFILE, p);
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
						
						//ProfileJSONManager profileManager = ProfileJSONManager.get(getActivity());
 
						Log.d(TAG,"in onActionItemClicked with adapter count "+ profileAdapter.getCount());
						//Delete selected crimes
						for(int i = profileAdapter.getCount() - 1; i >= 0; i--) {
							if(listview.isItemChecked(i)) {
								//Kill alarms for volume control
								VolumeManagerService.setServiceAlarm(getActivity().getApplicationContext(), profileAdapter.getItem(i), false);
                                ProfileManager.deleteProfile(getActivity(), profileAdapter.getItem(i));
							//	profileManager.deleteProfile(profileAdapter.getItem(i));
							}
						}

						//Destroy Action mode context menu
						mode.finish();
                       // profileManager.saveProfiles();
						//profileAdapter.notifyDataSetChanged();
                        notifyListViewChanged();
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
		//Update the List of Profiles when you come back to the ProfileListFragment
		//After we've made changes to a Crime on another Activity
		//if(profileAdapter != null)
		//{
			Log.d(TAG,"Notify adapter that data has changed");
			
			//getListAdapter()
        if(profileAdapter != null)
            notifyListViewChanged();
		  //profileAdapter.notifyDataSetChanged();
		//}
		   
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
		
		Profile profile = profileAdapter.getItem(position);
		
		switch (item.getItemId()) {
		   case R.id.menu_item_delete_profile:
			 //  ProfileJSONManager.get(getActivity()).deleteProfile(profile);

               ProfileManager.deleteProfile(getActivity(), profile);
			   //profileAdapter.notifyDataSetChanged();
               notifyListViewChanged();
			   //Kill alarms for volume control
			   VolumeManagerService.setServiceAlarm(getActivity().getApplicationContext(), profile,false);
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
			ProfileManager.newProfile(getActivity());
			//Return true, no further processing is necessary
			return true; 	
		case R.id.menu_item_settings:
			Intent settingsIntent = new Intent(getActivity(),SettingsActivity.class);
			startActivity(settingsIntent);
			return true;
		case R.id.menu_item_about:
			FragmentManager fm = getActivity().getSupportFragmentManager();
			AboutFragment dialog = AboutFragment.newInstance();
			//Make ProfileListFragment the target fragment of the TimePickerFragment instance
			//dialog.setTargetFragment(VolumeManagerFragment.this, REQUEST_START_TIME);
			//dialog.show(fm, "about");
			
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e(TAG, "Inside onCreateLoader");

        Uri profileUri = ProfileContract.ProfileEntry.buildProfileUri();

        return new CursorLoader(getActivity(),
                profileUri,
                null,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        ArrayList<Profile> profileList = new ArrayList<>(cursor.getCount());

        while(cursor.moveToNext()) {
            Profile profile = new Profile(cursor);
            profileList.add(profile);
        }

        if(getActivity() != null && listview != null && profileList != null) {

            //Store global copy
            mProfileList = profileList;
            profileAdapter = new ProfileListAdapter(profileList, listview);
            listview.setAdapter(profileAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /*******************************************************************/
	/*                        Public Methods                           */
	/**
	 ******************************************************************/


	/************************************************************/
	/*                      Inner Classes                       */
	/************************************************************/
	//Class to hold different views of the listview. This helps
	//it run smoothly when scrolling
	private static class ViewHolder {

		public TextView titleTextView;
		public TextView timeTextView;
		public TextView daysTextView;
	}
	
	private class ProfileListAdapter extends ArrayAdapter<Profile> {

		//private ListView listview;
		private ArrayList<Profile> profiles;
				

		
		public ProfileListAdapter(ArrayList<Profile> profiles, ListView listview) {
			super(getActivity(), 0, profiles);
			// TODO Auto-generated constructor stub
			
			//this.listview = listview;
			this.profiles = profiles;
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
            		convertView = getActivity().getLayoutInflater().inflate(R.layout.profile_list_item,null);
            	           		        	    
            	}
            	else {
                   //Set up disabled profile
            		convertView = getActivity().getLayoutInflater().inflate(R.layout.profile_list_item_off,null);
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
    					
    				    //Turn off alarms for this profile
    					getItem(listPosition).setEnabled(false);
    					VolumeManagerService.setServiceAlarm(getActivity().getApplicationContext(), getItem(listPosition),false);
    				}
    				else {
    					//Turn on alarms for this profile
    					getItem(listPosition).setEnabled(true);
    					VolumeManagerService.setServiceAlarm(getActivity().getApplicationContext(), getItem(listPosition), true);
    				}
    				//refresh listview
    				//profileAdapter.notifyDataSetChanged();
                    notifyListViewChanged();
    			}
    			
    		});
    		
    		volumeImage.setTag(new Integer(position));
    		
        	holder.titleTextView = (TextView)convertView.findViewById(R.id.profileTitleTextView);
            holder.titleTextView.setText(getItem(position).getTitle());
            //holder.titleTextView.setAlpha(PRIMARY_TEXT_DARK);
            
            holder.timeTextView = (TextView)convertView.findViewById(R.id.timeTextView);
            holder.timeTextView.setText(getItem(position).getFullTimeForListItem());
            //holder.timeTextView.setAlpha(SECONDARY_TEXT_DARK);
            holder.daysTextView = (TextView)convertView.findViewById(R.id.profileDaysTextView);
            holder.daysTextView.setText(getItem(position).getDaysOfWeekString());
            //holder.daysTextView.setAlpha(SECONDARY_TEXT_DARK);
              	
            return convertView;
		}
		
	}

    private void notifyListViewChanged() {
        profileAdapter.notifyDataSetChanged();
        ((Callback)getActivity()).onListViewChange();
    }
    public interface Callback {

        void onListViewChange();
    }
}
