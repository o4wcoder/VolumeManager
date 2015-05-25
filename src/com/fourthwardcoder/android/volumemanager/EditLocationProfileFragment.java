package com.fourthwardcoder.android.volumemanager;

import java.util.UUID;

import com.google.android.gms.maps.model.LatLng;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;

public class EditLocationProfileFragment extends Fragment {
	
	/*******************************************************/
	/*                    Constants                        */
	/*******************************************************/
	private static final String TAG = "EditLocationProfileFragment";
	
	/*******************************************************/
	/*                   Local Data                        */
	/*******************************************************/
	LocationProfile profile;
	TextView titleTextView;
	String profileTitle;
	LatLng currentLocation;
	
	/*******************************************************/
	/*                  Override Methods                   */
	/*******************************************************/
	@Override
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		
		setHasOptionsMenu(true);
		
		UUID profileId = (UUID) getActivity().getIntent().getSerializableExtra(Constants.EXTRA_PROFILE_ID);
		profile = ProfileManager.get(getActivity()).getLocationProfile(profileId);
		Log.e(TAG,"I've been passed profile with id " + profile.getId());
		
		profileTitle = profile.getTitle();
	    currentLocation = profile.getLocation();	
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState){
		
		View view = inflater.inflate(R.layout.fragment_location, container, false);
		
	    /*
	     * Setup TextViews                        
	     */
		titleTextView = (TextView)view.findViewById(R.id.titleTextView);
		titleTextView.setText(profileTitle);
		
		//Enable app icon to work as button and display caret
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if(NavUtils.getParentActivityName(getActivity()) != null) {
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//super.onCreateOptionsMenu(menu, inflater);
		
		//Pass the resource ID of the menu and populate the Menu 
		//instance with the items defined in the xml file
		inflater.inflate(R.menu.action_bar_location_menu, menu);
		
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		case android.R.id.home:
			if(NavUtils.getParentActivityName(getActivity()) != null) {
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;
		case R.id.menu_item_save_location_profile:
			Log.e(TAG,"Save location menu select");
			
			saveSettings(); 
			Toast toast = Toast.makeText(getActivity().getApplicationContext(),
					R.string.toast_text, Toast.LENGTH_SHORT);
			toast.show();
			
			getActivity().finish();
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void saveSettings() {
	
		profile.setTitle(titleTextView.getText().toString());
		
		ProfileManager.get(getActivity()).saveProfiles();
	}
}
