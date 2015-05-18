package com.fourthwardcoder.android.volumemanager;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import  com.google.android.gms.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LocationMapActivity extends FragmentActivity
implements OnMapReadyCallback, OnMapLongClickListener, GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener,
LocationListener{

	/******************************************************************/
	/*                         Constants                              */
	/******************************************************************/
	private final static String TAG = "LocationMapActivity";

	/******************************************************************/
	/*                         Local Data                             */
	/******************************************************************/
	GoogleMap map;
	//FusedLocationService fusedLocationService;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private TextView addressTextView;
	private TextView cityTextView;
	private Marker currentMarker;
	
	//Current Locations data
	private LatLng latLng;

	/******************************************************************/
	/*                  Activity Override Methods                     */
	/******************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_map);

		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		addressTextView = (TextView)findViewById(R.id.mapAddressTextView);

		cityTextView = (TextView)findViewById(R.id.mapCityTextView);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(LocationServices.API)
		.build();


		// Create the LocationRequest object
		mLocationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(10 * 1000)        // 10 seconds, in milliseconds
				.setFastestInterval(1 * 1000); // 1 second, in milliseconds
		
		LinearLayout lDetailsLayout = (LinearLayout)findViewById(R.id.locationDetailsLayout);
		
		lDetailsLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LocationMapActivity.this,EditLocationProfileActivity.class);
				//i.putExtra(EditProfileFragment.EXTRA_PROFILE_ID,p.getId());
				startActivity(i);
				
			}
			
		});


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		//Pass the resource ID of the menu and populate the Menu 
		//instance with the items defined in the xml file
		inflater.inflate(R.menu.action_bar_location_menu, menu);
		return super.onCreateOptionsMenu(menu);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.menu_item_save_location_profile:
	            saveLocation();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	protected void onResume() {
		super.onResume();
		//setUpMapIfNeeded();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mGoogleApiClient.isConnected()) {
			LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
			mGoogleApiClient.disconnect();
		}
	}



    /*****************************************************************/
	/*                 GoogleMap Listener Methods                     */
	/*****************************************************************/
	@Override
	public void onMapReady(GoogleMap map) {
		// TODO Auto-generated method stub

		this.map = map;
		map.setMyLocationEnabled(true);
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		map.setOnMapLongClickListener(this);

	}
	
	/**
	 * OnMapLongClickListener Override Method
	 * 
	 * Used to detect when the user does a long press on the map. Usually to put a marker down.
	 */
	@Override
	public void onMapLongClick(LatLng latLng) {
		//Store current LatLng
		this.latLng = latLng;
				
        addMarker();
		setStreetAddress();
		zoomToCurrentLocation();
	
	}
	

	/******************************************************************/
	/*                       Private Methods                          */
	/******************************************************************/
	/*
	
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (map == null) {
			// Try to obtain the map from the SupportMapFragment.
			map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
					.getMap();
			// Check if we were successful in obtaining the map.
			if (map != null) {
				setUpMap();
			}
		}
	}
*/
	/**
	 * This is where we can add markers or lines, add listeners or move the camera. In this case, we
	 * just add a marker near Africa.
	 * <p/>
	 * This should only be called once and when we are sure that {@link #mMap} is not null.
	 */
	/*
	private void setUpMap() {
		map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
	}
	*/
	
	private void addMarker() {
		
		if(currentMarker != null)
			currentMarker.remove();
		
		MarkerOptions options = new MarkerOptions()
		.position(latLng)
		.title("New Location");
		currentMarker = map.addMarker(options);
	}
	private void handleNewLocation() {

        setStreetAddress();
		
		//mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        addMarker();
		//map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		zoomToCurrentLocation();
	}
	

	
	private Address getStreetAddress() throws IOException {

		//Get Address of current location
		Address currentAddress = null;
		if(Geocoder.isPresent()) {
			Geocoder gcd = new Geocoder(getBaseContext());

			List<Address> addresses = gcd.getFromLocation(latLng.latitude,
					latLng.longitude,1);
			
			if(addresses.size() > 0)
				currentAddress = addresses.get(0);
		}

		return currentAddress;
	}
	
	private void setStreetAddress() {
		
        Address address;
		try {
			address = getStreetAddress();
			addressTextView.setText(address.getAddressLine(0));
			Log.e(TAG,"address: " + address.toString());
			String strCity = address.getLocality() + ", " + address.getAdminArea();
			cityTextView.setText(strCity);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			addressTextView.setText("Unknown Street Address");
		}
	}
	
	private void zoomToCurrentLocation() {


		//if (location != null) {
			//LatLng myLocation = new LatLng(location.getLatitude(),
				//	location.getLongitude());
			Log.d(TAG,"Zooming to current location");
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
					16));
		//}
		//else
			//Log.d(TAG,"Location was null!!!");
	}

	private void saveLocation() {
		
	}
	
	@Override
	public void onLocationChanged(Location location) {
		
		this.latLng = new LatLng(location.getLatitude(),location.getLongitude());
		handleNewLocation();

	}


    /*************************************************************/
	/*                GoogleApi Listener Methods                 */
	/*************************************************************/
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle bundle) {
		Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (location == null) {
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
		}
		else {
			//Store current latLng
			this.latLng = new LatLng(location.getLatitude(),location.getLongitude());
			handleNewLocation();
		}

	}

	@Override
	public void onConnectionSuspended(int arg0) {}
	
	



}
