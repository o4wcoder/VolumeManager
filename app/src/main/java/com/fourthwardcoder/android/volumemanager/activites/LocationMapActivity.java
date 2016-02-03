package com.fourthwardcoder.android.volumemanager.activites;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.fourthwardcoder.android.volumemanager.fragments.ProfileDetailFragment;
import com.fourthwardcoder.android.volumemanager.models.LocationProfile;
import com.fourthwardcoder.android.volumemanager.data.ProfileJSONManager;
import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.fragments.AboutFragment;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.location.GeofenceManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.FragmentManager;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import  com.google.android.gms.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LocationMapActivity extends FragmentActivity
implements OnMapReadyCallback, OnMapLongClickListener, GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener, LocationListener, Constants, ResultCallback<Status>{

	/******************************************************************/
	/*                         Constants                              */
	/******************************************************************/
	private final static String TAG = "LocationMapActivity";

	private final static int GEOFENCE_FILL_COLOR = 0x40c62828; //Red, %25 transparent
	private final static int GEOFENCE_STROKE_COLOR = 0x80c62828; //Red, %25 transparent

	//Keys for storing data when we have a rotation
	private final static String KEY_ID = "profileId";

	/******************************************************************/
	/*                         Local Data                             */
	/******************************************************************/
	private GoogleMap map;
	//FusedLocationService fusedLocationService;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private TextView addressTextView;
	private TextView cityTextView;
	private Marker currentMarker;
	private Circle currentCircle;
	TextView radiusTextView;

	//Geofence data
	//private ArrayList<Geofence> geofenceList;
	//private PendingIntent geofencePendingIntent;
	GeofenceManager geofenceManager;

	//Current Locations data
	private UUID profileId;
	private LocationProfile currentProfile;
	private LatLng currentLocation;
	private Address currentAddress;
	private String currentCity;
	private float currentRadius;

	//private LocationData currentLocationData;

	/******************************************************************/
	/*                  Activity Override Methods                     */
	/******************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_map);

		View mapView = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getView();

		// Get the button view 
		View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);

		// place button on bottom right
		RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
		// position on right bottom
		rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
		rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		rlp.setMargins(0, 0, 30, 30);


		getActionBar().setDisplayHomeAsUpEnabled(true);

		Util.setStatusBarColor(this);

		//pull out ID of profile
		if(savedInstanceState != null) {
			profileId = UUID.fromString(savedInstanceState.getString(KEY_ID));
		}
		else {
			Intent intent = getIntent();
			profileId = (UUID)intent.getSerializableExtra(EXTRA_PROFILE_ID);
		}
		Log.d(TAG,"Profile id: " + profileId);

		//Fetch the Profile from the ProfileJSONManager ArrayList
		currentProfile = ProfileJSONManager.get(this).getLocationProfile(profileId);


		if(currentProfile != null) {
			currentLocation = currentProfile.getLocation();
			currentRadius = currentProfile.getFenceRadius();
		}

		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		addressTextView = (TextView)findViewById(R.id.mapAddressTextView);

		cityTextView = (TextView)findViewById(R.id.mapCityTextView);

		//Build Google API Client
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

		//create empty list for storing all other location geofence's
		//geofenceList = new ArrayList<Geofence>();

		//init pending intent for add/removing geofences.
		//geofencePendingIntent = null;

		geofenceManager = new GeofenceManager(this,mGoogleApiClient);

		//Make linear layout that holds address and city textviews clickable.
		LinearLayout lDetailsLayout = (LinearLayout)findViewById(R.id.locationDetailsLayout);

		lDetailsLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(LocationMapActivity.this,EditLocationProfileActivity.class);
				i.putExtra(ProfileDetailFragment.EXTRA_PROFILE_ID,currentProfile.getId());
				startActivity(i);

			}

		});

		radiusTextView = (TextView)findViewById(R.id.radiusTextView);
		radiusTextView.setText(String.valueOf(currentRadius));


		radiusTextView.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				currentRadius = Float.valueOf(s.toString());
				drawGeofenceCircle();

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

		});

		Button increaseRadiusButton = (Button)findViewById(R.id.increaseRadiusButton);

		increaseRadiusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//increase the geofence size
				currentRadius += Constants.GEOFENCE_RADIUS_INC;
				radiusTextView.setText(String.valueOf(currentRadius));
				drawGeofenceCircle();


			}

		});

		Button decreaseRadiusButton = (Button)findViewById(R.id.decreaseRadiusButton);

		decreaseRadiusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//decrease the geofence size
				currentRadius -= Constants.GEOFENCE_RADIUS_INC;
				radiusTextView.setText(String.valueOf(currentRadius));
				drawGeofenceCircle();
			}

		});


	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putString(KEY_ID,profileId.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		//Pass the resource ID of the menu and populate the Menu 
		//instance with the items defined in the xml file
		inflater.inflate(R.menu.action_bar_profile_menu, menu);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case android.R.id.home:
			if(NavUtils.getParentActivityName(this) != null) {
				NavUtils.navigateUpFromSameTask(this);
			}
			return true;
		case R.id.menu_item_save_profile:
			saveLocation();
			return true;
		case R.id.menu_item_settings:
			Intent settingsIntent = new Intent(this,SettingsActivity.class);
			startActivity(settingsIntent);
			return true;
		case R.id.menu_item_about:
			FragmentManager fm = this.getFragmentManager();
			AboutFragment dialog = AboutFragment.newInstance();
			//Make ProfileListFragment the target fragment of the TimePickerFragment instance
			//dialog.setTargetFragment(VolumeManagerFragment.this, REQUEST_START_TIME);
			dialog.show(fm, "about");
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
		currentLocation = latLng;

		addMarker();
		setStreetAddress();
		//zoomToCurrentLocation();

	}


	/******************************************************************/
	/*                        Geofence Methods                        */
	/******************************************************************/
	/*
	private Geofence createGeofence(LocationProfile profile) {

		return new Geofence.Builder()
		.setRequestId(profile.getId().toString())
		.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
		.setCircularRegion(profile.getLocation().latitude, profile.getLocation().longitude,profile.getFenceRadius())
		.setExpirationDuration(Geofence.NEVER_EXPIRE)
		.build();

	}
	 */
	/*
	private void populateGeofenceList() {

		//Get all location profiles
		ArrayList<LocationProfile> locationProfileList = ProfileJSONManager.get(this).getLocationProfiles();

		for(int i = 0; i < locationProfileList.size(); i++ ) {

			LocationProfile profile = locationProfileList.get(i);
			Log.e(TAG,"Create geofence with profile " +profile.toString());
			Geofence fence = createGeofence(profile);

			geofenceList.add(fence);


		}

	}
	 */
	/**
	 * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
	 * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
	 * current list of geofences.
	 *
	 * @return A PendingIntent for the IntentService that handles geofence transitions.
	 */
	/*
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
	 */
	/**
	 * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
	 * Also specifies how the geofence notifications are initially triggered.
	 */
	/*
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(geofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }
	 */

	/**
	 * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
	 * specified geofences. Handles the success or failure results returned by addGeofences().
	 */
	/*
    public void addGeofences() {

    	Log.i(TAG,"adding georfences");

        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
        	Log.e(TAG,"Security exception when trying to add geofences.");
            //logSecurityException(securityException);
        }
    }
	 */
	/*
    public void removeGeofence() {

    	 try {
             // Remove geofences.
             LocationServices.GeofencingApi.removeGeofences(
                     mGoogleApiClient,
                     // This is the same pending intent that was used in addGeofences().
                     getGeofencePendingIntent()
             ).setResultCallback(this); // Result processed in onResult().
         } catch (SecurityException securityException) {
             // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
             //logSecurityException(securityException);
         }
    }
	 */

	/*

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
		Log.e(TAG,"Inside add Marker with title " + currentProfile.getTitle());
		String markerTitle;

		//if(profile.getTitle() == "")
		//	markerTitle = "New Location";
		//	else
		markerTitle = currentProfile.getTitle();
		MarkerOptions options = new MarkerOptions()
		.position(currentLocation)
		.title(markerTitle);
		currentMarker = map.addMarker(options);

		drawGeofenceCircle();
	}

	private void drawGeofenceCircle() {

		if(currentCircle != null)
			currentCircle.remove();

		if(currentLocation == null)
			Log.i(TAG," current location is null!!!!");


		Log.i(TAG," current radius is "+ currentRadius);

		currentCircle = map.addCircle(new CircleOptions()
		.center(currentLocation)
		.radius(currentRadius)
		.strokeColor(GEOFENCE_STROKE_COLOR)
		.strokeWidth(5)
		.fillColor(GEOFENCE_FILL_COLOR));


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
		//Address currentAddress = null;
		if(Geocoder.isPresent()) {
			Geocoder gcd = new Geocoder(getBaseContext());

			List<Address> addresses = gcd.getFromLocation(currentLocation.latitude,
					currentLocation.longitude,1);

			if(addresses.size() > 0)
				currentAddress = addresses.get(0);
		}

		return currentAddress;
	}

	private void setStreetAddress() {

		Address address;
		try {
			currentAddress = getStreetAddress();
			addressTextView.setText(currentAddress.getAddressLine(0));
			Log.e(TAG,"address: " + currentAddress.toString());

			currentCity = currentAddress.getLocality() + ", " + currentAddress.getAdminArea();
			cityTextView.setText(currentCity);
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
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,
				16));
		//}
		//else
		//Log.d(TAG,"Location was null!!!");
	}

	private void saveLocation() {

		//Log.e(TAG,"In save location with currentLocation " + currentLocationData.toString());


		if(currentLocation != null)
			currentProfile.setLocation(currentLocation);

		if(currentAddress != null) {
			currentProfile.setAddress(currentAddress.getAddressLine(0));
			currentProfile.setCity(currentCity);
		}

		currentProfile.setFenceRadius(currentRadius);

		ProfileJSONManager.get(this).saveLocationProfiles();

		//Create geofence from new location profile
		//Geofence fence = createGeofence(currentProfile);
		Geofence fence = geofenceManager.createGeofence(currentProfile);
		//store geofence's from all other locations
		//populateGeofenceList();

		//Add new geofence to list
		//geofenceList.add(fence);
		geofenceManager.addGeofence(fence);
		//Set up geofences with pending intent
		//addGeofences();
		geofenceManager.startGeofences(this);

	}

	@Override
	public void onLocationChanged(Location location) {

		currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
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
			if(currentLocation == null)
				currentLocation = new LatLng(location.getLatitude(),location.getLongitude());


			handleNewLocation();
		}

	}

	@Override
	public void onConnectionSuspended(int arg0) {}

	@Override
	public void onResult(Status status) {
		if(status.isSuccess()) {
			Log.i(TAG,"Success creating intents for geofences");

			String msgStr = getString(R.string.geofence_added) + " " + currentAddress.getAddressLine(0);

			Toast.makeText(
					this,
					msgStr,
					Toast.LENGTH_SHORT
					).show();
			finish();

		}
		else {
			// Get the status code for the error and log it using a user-friendly message.
			String errorMessage = GeofenceManager.getGeofenceErrorString(this,
					status.getStatusCode());
			Log.e(TAG, errorMessage);
			Toast toast = Toast.makeText(getApplicationContext(),
					errorMessage, Toast.LENGTH_SHORT);
			toast.show();

		}

	}







}
