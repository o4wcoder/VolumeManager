package com.fourthwardcoder.android.volumemanager.activites;

import java.io.IOException;

//import com.fourthwardcoder.android.volumemanager.models.LocationProfile;
//import com.fourthwardcoder.android.volumemanager.json.ProfileJSONManager;
import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.location.GeofenceManager;
import com.fourthwardcoder.android.volumemanager.models.Profile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import  com.google.android.gms.location.LocationListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Class LocationMapActivity
 * Author: Chris Hare
 * Created: 1/17/2016
 *
 * Activity that holds a Google map that the user can select a location.
 */
public class LocationMapActivity extends AppCompatActivity
implements OnMapReadyCallback, OnMapLongClickListener, GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener, LocationListener, PlaceSelectionListener, Constants {

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

	private Profile mProfile;
	private LatLng currentLocation;

    private Address currentAddress;
	private String currentCity;
	private float currentRadius;

	/******************************************************************/
	/*                  Activity Override Methods                     */
	/******************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_map);

        Util.setStatusBarColor(this);

		Intent intent = getIntent();

		//First check if we have don't have anything in saveInstanceState from a rotation
		if(savedInstanceState == null) {

            mProfile = intent.getParcelableExtra(EXTRA_PROFILE);
		}
		else {
			//Restore Profile from rotation.
			mProfile = savedInstanceState.getParcelable(EXTRA_PROFILE);
		}

		if(mProfile.getLocation() != null) {
                currentLocation = mProfile.getLocation().getLatLng();
                currentRadius = mProfile.getLocation().getFenceRadius();
		}

		//Get map fragment
		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		// Retrieve the PlaceAutocompleteFragment.
		PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
				getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

		// Register a listener to receive callbacks when a place has been selected or an error has
		// occurred.
		autocompleteFragment.setOnPlaceSelectedListener(this);

        //Get Address and city textviews
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

		//geofenceManager = new GeofenceManager(this,mGoogleApiClient);

        //Set up zoom to current position button
        FloatingActionButton zoomPhonePosition = (FloatingActionButton) findViewById(R.id.zoomPhonePositionButton);
        zoomPhonePosition.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(map != null) {
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                                16));
                    }
                }
            }
        });

        //Set up increase geofence radius button
		FloatingActionButton increaseRadiusButton = (FloatingActionButton)findViewById(R.id.increaseRadiusButton);
		increaseRadiusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//increase the geofence size
				currentRadius += Constants.GEOFENCE_RADIUS_INC;
			//	radiusTextView.setText(String.valueOf(currentRadius));
				drawGeofenceCircle();


			}

		});

        //Set up decrease geofence radius button
		FloatingActionButton decreaseRadiusButton = (FloatingActionButton)findViewById(R.id.decreaseRadiusButton);
		decreaseRadiusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//decrease the geofence size
				currentRadius -= Constants.GEOFENCE_RADIUS_INC;
			//	radiusTextView.setText(String.valueOf(currentRadius));
				drawGeofenceCircle();
			}

		});

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putParcelable(EXTRA_PROFILE,mProfile);
        super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}


	@Override
	protected void onStop() {
		super.onStop();

		if (mGoogleApiClient.isConnected()) {
			LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
			mGoogleApiClient.disconnect();
		}
	}

    @Override
    public void onBackPressed() {

        Log.e(TAG,"onBackPressed()");
        saveLocation();
        super.onBackPressed();
    }

	/*****************************************************************/
	/*                 GoogleMap Listener Methods                     */
	/*****************************************************************/
	@Override
	public void onMapReady(GoogleMap map) {
		// TODO Auto-generated method stub

		this.map = map;
		map.setMyLocationEnabled(true);
        View mapView = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getView();
        // Get the button view
        View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
        locationButton.setVisibility(View.GONE);
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

        updateLocation(latLng);
	}


	@Override
	public void onLocationChanged(Location location) {

		currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
		handleNewLocation();

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.e(TAG,"onConnected()");
		Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (location == null) {
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
		}
		else {

			handleNewLocation();
		}

	}

	@Override
	public void onConnectionSuspended(int arg0) {}


    @Override
    public void onPlaceSelected(Place place) {

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),
                16));

        updateLocation(place.getLatLng());
    }

    @Override
    public void onError(Status status) {

        Toast.makeText(this, getString(R.string.place_error) + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    /**************************************************************************************/
    /*                                Private Methods                                     */
    /**************************************************************************************/

    /**
     * Make changes to the data when the location has changed
     * @param latLng new location to set
     */
    private void updateLocation(LatLng latLng) {
        currentLocation = latLng;

        addMarker(); //Add marker to new location
        setStreetAddress(); //Update the street address
    }

    /**
     * Add a marker on the map at selected location
     */
    private void addMarker() {

        if(currentMarker != null)
            currentMarker.remove();

        MarkerOptions options = new MarkerOptions()
                .position(currentLocation)
                .title(mProfile.getTitle());
        currentMarker = map.addMarker(options);

        drawGeofenceCircle();
    }

    /**
     * Draw a geofence circle around the lacation base on the radius set
     */
    private void drawGeofenceCircle() {

        if(currentCircle != null)
            currentCircle.remove();

        if(currentLocation == null)
            Log.i(TAG, " current location is null!!!!");


        Log.i(TAG, " current radius is " + currentRadius);

        currentCircle = map.addCircle(new CircleOptions()
                .center(currentLocation)
                .radius(currentRadius)
                .strokeColor(GEOFENCE_STROKE_COLOR)
                .strokeWidth(5)
                .fillColor(GEOFENCE_FILL_COLOR));


    }

    /**
     * Make changes to data and map when a new location has been set
     */
    private void handleNewLocation() {

        setStreetAddress();

        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        addMarker();
        //map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        zoomToCurrentLocation();
    }

    /**
     * Set the street address of the current location
     */
    private void setStreetAddress() {

        try {
            currentAddress = Util.getStreetAddress(this, currentLocation);
            addressTextView.setText(currentAddress.getAddressLine(0));
            Log.e(TAG,"address: " + currentAddress.toString());

            currentCity = currentAddress.getLocality() + ", " + currentAddress.getAdminArea();
            cityTextView.setText(currentCity);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            addressTextView.setText(getString(R.string.unknown_street_address));
        }
    }

    /**
     * Zoom map camera to current location
     */
    private void zoomToCurrentLocation() {

        Log.d(TAG, "Zooming to current location " + currentLocation.toString());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,
                16));
    }

    /**
     * save any changes to the location or geofence radius
     */
    private void saveLocation() {

        //Update Location data
        mProfile.getLocation().setLatLng(currentLocation);
        mProfile.getLocation().setAddress(currentAddress.getAddressLine(0));
        mProfile.getLocation().setCity(currentCity);
        mProfile.getLocation().setFenceRadius(currentRadius);

        Intent i = getIntent();
        i.putExtra(EXTRA_PROFILE,mProfile);
        setResult(RESULT_OK, i);

       // finish();
    }
}
