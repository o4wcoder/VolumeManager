package com.fourthwardcoder.android.volumemanager.fragments;

import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class LocationMapFragment extends SupportMapFragment implements LoaderCallbacks<Cursor>, OnMapReadyCallback{
	
    /***************************************************/
	/*                 Constants                       */
	/***************************************************/
	private static final String TAG = "LocationMapFragment";
	
	/***************************************************/
	/*                Local Data                       */
	/***************************************************/
	GoogleMap map;
	
	/***************************************************/
	/*                Override Methods                 */
	/***************************************************/
	/*
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		//retain the instance on rotation
		setRetainInstance(true);
		
		//Set up Options menu for Up Caret navigation
		setHasOptionsMenu(true);
		
		//Change status bar color
		ProfileMainFragment.setStatusBarColor(getActivity());
		
		
	}
	*/
	public static LocationMapFragment newInstance() {
		
		LocationMapFragment lf = new LocationMapFragment();
		
		return lf;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		//Set up Options menu for Up Caret navigation
		setHasOptionsMenu(true);
		
		//Change status bar color
		Util.setStatusBarColor(getActivity());
		
		//View view = inflater.inflate(R.layout.fragment_location, container, false);
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		//Enable app icon to work as button and display caret
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if(NavUtils.getParentActivityName(getActivity()) != null) {
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
		 
		/*
		//Get a reference to the GoogleMap
		map = getMap();
		//Show the user's location
		map.setMyLocationEnabled(true);
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		Location location = map.getMyLocation();
		
		if(location != null) {
			
			Log.e(TAG,"Location: " + location.toString());
		}
		else
			Log.e(TAG,"Location is nul!!!!");
		*/
		getMapAsync(this);
		
		return view;
	}
	
	@Override
	public void onMapReady(final GoogleMap map) {
	    this.map = map;
	    map.setMyLocationEnabled(true);
	    
	    Log.e(TAG,"Google Map is ready");
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		Location location = map.getMyLocation();
		
		if(location != null) {
			
			Log.e(TAG,"Location: " + location.toString());
		}
		else
			Log.e(TAG,"Location is nul!!!!");
	    
	    
	}
	private void setMarker() {
		
		//static final LatLng TutorialsPoint = new LatLng(21 , 57);
		
		 /*
		 googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
         Marker TP = googleMap.addMarker(new MarkerOptions().
         position(TutorialsPoint).title("TutorialsPoint"));

      } catch (Exception e) {
         e.printStackTrace();
      }
      */
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		case android.R.id.home:
			if(NavUtils.getParentActivityName(getActivity()) != null) {
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	

}
