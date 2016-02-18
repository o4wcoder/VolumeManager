package com.fourthwardcoder.android.volumemanager.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.fourthwardcoder.android.volumemanager.R;
//import com.fourthwardcoder.android.volumemanager.activites.SettingsActivity;
import com.fourthwardcoder.android.volumemanager.activites.LocationMapActivity;
import com.fourthwardcoder.android.volumemanager.activites.SettingsActivity;
import com.fourthwardcoder.android.volumemanager.adapters.LocationProfileListAdapter;
import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.GeoFenceLocation;
import com.fourthwardcoder.android.volumemanager.models.Profile;
import com.fourthwardcoder.android.volumemanager.services.VolumeManagerService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

/**
 * VolumeManagerFragment
 * 
 * Main fragment of the VolumeManagerActivity. Contains all the components to 
 * set start and end alarms of the specific alarm control.
 * 
 * @author Chris Hare
 * 3/13/2015
 *
 */
public class ProfileDetailFragment extends Fragment implements  LocationProfileListAdapter.LocationAdapterCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, Constants {
	
	/************************************************************************/
	/*                           Constants                                  */
	/************************************************************************/
	private final static String TAG = ProfileDetailFragment.class.getSimpleName();
	//Tag for the time piker Dialog
	private static final String DIALOG_TIME = "time";
	
	//Constant for request code to Time Picker
	public static final int REQUEST_START_TIME = 0;
	public static final int REQUEST_END_TIME = 1;

    //Google Static Maps paramters
    private static final String GOOGLE_STAIC_MAPS_URL = "http://maps.google.com/maps/api/staticmap";
	private static final String MAPS_PARAM_CENTER = "center";
    private static final String MAPS_PARAM_SIZE = "size";
    private static final String MAPS_PARAM_ZOOM = "zoom";
    private static final String MAPS_PARAM_SENSOR = "sensor";
    private static final String MAPS_PARAM_MARKERS = "markers";

    private static final String MAPS_SIZE_SMALL = "400x200";
    private static final String MAPS_ZOOM_VAL = "15";
    private static final String MAPS_SENSOR_VAL = "false";
	/************************************************************************/
	/*                          Local Data                                  */
	/************************************************************************/
	Profile mProfile;
	TextView mTitleTextView, startTimeTextView, endTimeTextView;
	RadioGroup startVolumeRadioGroup;
	RadioGroup endVolumeRadioGroup;
	SeekBar startRingSeekBar, endRingSeekBar;
	TextView startRingVolumeTextView, endRingVolumeTextView;
	ImageView startVolumeImageView, endVolumeImageView;

	boolean mIsNewProfile = false;

    int mProfileType;

    Menu mToolbarMenu;

    //Location profile only members
    ImageView mThumbnailImageView;
    TextView mAddressTextView;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
	
	/*******************************************************/
	/*                  Override Methods                   */
	/*******************************************************/
	@Override
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		Log.e(TAG,"onCreate");
		setHasOptionsMenu(true);
		
		//Change status bar color
		Util.setStatusBarColor(getActivity());
		
		//Get Fragment arguments and pull out ID of profile
		Intent intent = getActivity().getIntent();



        //First check if we have don't have anything in saveInstanceState from a rotation
        if(saveInstanceState == null) {
           Log.e(TAG,"No saved vars");
            mProfileType = intent.getIntExtra(EXTRA_PROFILE_TYPE,TIME_PROFILE_LIST);
            //See if we have a Profile object. If so we are editing the Profile.
            //If not, it's a new profile
            if (intent.hasExtra(EXTRA_PROFILE)) {
                mProfile = intent.getParcelableExtra(EXTRA_PROFILE);
				mIsNewProfile = false;
            } else {
                mProfile = new Profile();
				mIsNewProfile = true;
            }
        }
        else {
            Log.e(TAG,"Saved vars");
            //Restore Profile from rotation.
            mProfile = saveInstanceState.getParcelable(EXTRA_PROFILE);
            mProfileType = saveInstanceState.getInt(EXTRA_PROFILE_TYPE);
			mIsNewProfile = saveInstanceState.getBoolean(EXTRA_IS_NEW_PROFILE);
            Log.e(TAG,"Saved mProfile type " + mProfileType);
        }

        //Get Location if this is a location profile
        if(mProfileType == LOCATION_PROFILE_LIST) {

            //Get current location if there is not location set
                Log.e(TAG,"Creating Google API and Location requests");
                //Build Google API Client
                mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();

                // Create the LocationRequest object
                mLocationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                        .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        }

	}
	
	@SuppressLint("ResourceAsColor")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState){
				
		View view = inflater.inflate(R.layout.fragment_profile_detail, container, false);

		//Enable app icon to work as button and display caret
//		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			if(NavUtils.getParentActivityName(getActivity()) != null) {
//				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
//			}
//		}

        //Setup Toolbar
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if(toolbar != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
		//Get volume icons
		startVolumeImageView = (ImageView)view.findViewById(R.id.volumeStartImageView);
		endVolumeImageView = (ImageView)view.findViewById(R.id.volumeEndImageView);
	    /*
	     * Setup TextViews                        
	     */
		mTitleTextView = (TextView)view.findViewById(R.id.profileTitleTextView);
		mTitleTextView.setText(mProfile.getTitle());
        mTitleTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                 setSaveMenu();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

		
	    startTimeTextView = (TextView)view.findViewById(R.id.startTimeTextView);
        //Set up Ring Volume TextView
	    startRingVolumeTextView = (TextView)view.findViewById(R.id.startRingVolumeTextView);
	    //Set up End Time TextView
	    endTimeTextView = (TextView)view.findViewById(R.id.endTimeTextView);
	    //Set up Ring Volume TextView
	    endRingVolumeTextView = (TextView)view.findViewById(R.id.endRingVolumeTextView);
	    
	    /*
	     * Setup Buttons                     
	     */
		View.OnClickListener dayButtonListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
                ArrayList<Boolean> daysOfTheWeek = mProfile.getDaysOfTheWeek();

                 Log.d(TAG,"Button tag: " + v.getTag());
                 TextView textView = (TextView)v;
                 boolean setting = daysOfTheWeek.get((int)v.getTag());

                 if(setting) {
                	 //Turn Day off
                	daysOfTheWeek.add((int) v.getTag(), false);

     	        	textView.setTextColor(Color.parseColor("#000000"));
                     v.setBackground(getResources().getDrawable(R.drawable.round_button_off));
                 }
                 else {
                	 //Turn Day on
                	 daysOfTheWeek.add((int) v.getTag(), true);
     	        	 textView.setTextColor(Color.parseColor("#ffffff"));
                     v.setBackground(getResources().getDrawable(R.drawable.round_button));
                 }

                //Update days of the week in Profile
                mProfile.setDaysOfTheWeek(daysOfTheWeek);
                 
                 
			}
		};
		
		TableRow daysRow = (TableRow)view.findViewById(R.id.daysTableRow);
		for(int i = 0; i < daysRow.getChildCount(); i ++) {
			Button button = (Button)daysRow.getChildAt(i);
	        button.setText(Constants.daysButtonNames[i]);
	        button.setOnClickListener(dayButtonListener);
	        button.setTag(new Integer(i));
	        
	        boolean setting = mProfile.getDaysOfTheWeek().get(i);
	        if(setting) {
	        	//Turn Day on
	        	button.setTextColor(Color.parseColor("#ffffff"));
              //  button.setBackgroundColor(getResources().getColor(R.color.buttonColor));
	        }
	        else {
	        	//Turn Day off
	        	//button.setTextColor(R
				//		.color.primary_material_dark);
                button.setTextColor(Color.parseColor("#000000"));
                Log.e(TAG,"turn off day");
                button.setBackgroundColor(Color.TRANSPARENT);
	        }
	        	
	        
		}
	    //Setup start time of volume control
	  //  startTimeButton = (Button)view.findViewById(R.id.startTimeButton);
	    startTimeTextView.setOnClickListener(new OnClickListener() {
	    	@Override
			public void onClick(View v) {
				//Start Time Picker Dialog on CrimeFragment after clicking Time Button
				android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
				TimePickerFragment dialog = TimePickerFragment.newInstance(mProfile.getStartDate(),getString(R.string.start_time_dialog));
				//Make VolumeManagerFragment the target fragment of the TimePickerFragment instance
				dialog.setTargetFragment(ProfileDetailFragment.this, REQUEST_START_TIME);
				dialog.show(fm, DIALOG_TIME);
	    		
	    	}
	    });
	    //Setup end time of volume control
	   // endTimeButton = (Button)view.findViewById(R.id.endTimeButton);
	    endTimeTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start Time Picker Dialog on CrimeFragment after clicking Time Button
                android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mProfile.getEndDate(), getString(R.string.end_time_dialog));
                //Make VolumeManagerFragment the target fragment of the TimePickerFragment instance
                dialog.setTargetFragment(ProfileDetailFragment.this, REQUEST_END_TIME);
                dialog.show(fm, DIALOG_TIME);

            }
        });
	    
	    /*
	     * Setup Radio Buttons                  
	     */
	    //Set up Ringer type Radio Buttons
	    startVolumeRadioGroup = (RadioGroup)view.findViewById(R.id.startVolumeRadioGroup);
	    startVolumeRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	    
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
			       
				if(checkedId == R.id.startOffRadio) {
					mProfile.setStartVolumeType(VOLUME_OFF);
					mProfile.setStartRingVolume(0);
					Util.setSeekBarPosition(startRingSeekBar,startRingVolumeTextView,mProfile.getStartRingVolume(),Util.getMaxRingVolume(getActivity().getApplicationContext()));
					
				}
				else if(checkedId == R.id.startVibrateRadio) {
					mProfile.setStartVolumeType(VOLUME_VIBRATE);
					mProfile.setStartRingVolume(0);
					Util.setSeekBarPosition(startRingSeekBar,startRingVolumeTextView,mProfile.getStartRingVolume(),Util.getMaxRingVolume(getActivity().getApplicationContext()));
				}
				else {
					mProfile.setStartVolumeType(VOLUME_RING);
				}

                setVolumeIcon();
			    
			}
	    	
	    });
	    //Set up Ringer type Radio Buttons
	    endVolumeRadioGroup = (RadioGroup)view.findViewById(R.id.endVolumeRadioGroup);
	    endVolumeRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.endOffRadio) {
                    mProfile.setEndVolumeType(VOLUME_OFF);
                    mProfile.setEndRingVolume(0);
                    Util.setSeekBarPosition(endRingSeekBar, endRingVolumeTextView, mProfile.getEndRingVolume(), Util.getMaxRingVolume(getActivity().getApplicationContext()));

                } else if (checkedId == R.id.endVibrateRadio) {
                    mProfile.setEndVolumeType(VOLUME_VIBRATE);
                    mProfile.setEndRingVolume(0);
                    Util.setSeekBarPosition(endRingSeekBar, endRingVolumeTextView, mProfile.getEndRingVolume(), Util.getMaxRingVolume(getActivity().getApplicationContext()));
                } else {
                    mProfile.setEndVolumeType(VOLUME_RING);
                }

                setVolumeIcon();
            }
        });
	    
	    /*
	     * Setup SeekBars                    
	     */
	    //Set up Ringer SeekBar
	    startRingSeekBar = (SeekBar)view.findViewById(R.id.startRingSeekBar);
	    startRingSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                Log.d(TAG,"Start Seek Bar progress " + progress);
				
				if(progress == 0)
					 ((RadioButton)startVolumeRadioGroup.getChildAt(VOLUME_OFF)).setChecked(true);
				else 
					((RadioButton)startVolumeRadioGroup.getChildAt(VOLUME_RING)).setChecked(true);
				
				Util.setRingVolumeText(startRingVolumeTextView,progress,Util.getMaxRingVolume(getActivity().getApplicationContext()));
				mProfile.setStartRingVolume(progress);

                setVolumeIcon();
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
	    });
	    
	    //Set up Ringer SeekBar
	    endRingSeekBar = (SeekBar)view.findViewById(R.id.endRingSeekBar);
	    endRingSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (progress == 0)
                    ((RadioButton) endVolumeRadioGroup.getChildAt(VOLUME_OFF)).setChecked(true);
                else
                    ((RadioButton) endVolumeRadioGroup.getChildAt(VOLUME_RING)).setChecked(true);

                Util.setRingVolumeText(endRingVolumeTextView, progress, Util.getMaxRingVolume(getActivity().getApplicationContext()));
                mProfile.setEndRingVolume(progress);

                setVolumeIcon();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //Location views
        if(mProfileType == LOCATION_PROFILE_LIST) {

            mThumbnailImageView = (ImageView)view.findViewById(R.id.location_thumbnail);

            mThumbnailImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getActivity(), LocationMapActivity.class);
                    startActivity(i);
                }
            });

            mAddressTextView = (TextView)view.findViewById(R.id.address_textview);
            mAddressTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getActivity(), LocationMapActivity.class);
                    startActivity(i);
                }
            });
        }

	    /*
	     * Set Default and Saved Settings
	     */
	    //Set default or saved radio button setting
	    ((RadioButton)startVolumeRadioGroup.getChildAt(mProfile.getStartVolumeType())).setChecked(true);
	    //Set default or saved radio button setting
	    ((RadioButton)endVolumeRadioGroup.getChildAt(mProfile.getEndVolumeType())).setChecked(true);


	    Util.setTimeForLargeTextView(getActivity(),mProfile.getStartDate(), startTimeTextView);
	    Util.setTimeForLargeTextView(getActivity(),mProfile.getEndDate(), endTimeTextView);
	    //Set Seekbar default
	    Util.setSeekBarPosition(startRingSeekBar,startRingVolumeTextView,mProfile.getStartRingVolume(),Util.getMaxRingVolume(getActivity().getApplicationContext()));
	    Util.setSeekBarPosition(endRingSeekBar,endRingVolumeTextView,mProfile.getEndRingVolume(),Util.getMaxRingVolume(getActivity().getApplicationContext()));
	    startRingSeekBar.setMax(Util.getMaxRingVolume(getActivity().getApplicationContext()));
	    endRingSeekBar.setMax(Util.getMaxRingVolume(getActivity().getApplicationContext()));

		//Set Volume Icon
        setVolumeIcon();

        setLocationLayout(view);

		return view;
	}

    @Override
    public void onStart() {
        super.onStart();

        //If the Google Client is null we are either a Time Profile or we already have the location
        //for a location profile
        if(mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }


    @Override
    public void onStop() {
        super.onStop();

        if(mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putParcelable(EXTRA_PROFILE, mProfile);
		savedInstanceState.putBoolean(EXTRA_IS_NEW_PROFILE,mIsNewProfile);
        savedInstanceState.putInt(EXTRA_PROFILE_TYPE,mProfileType);
        super.onSaveInstanceState(savedInstanceState);
    }

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//super.onCreateOptionsMenu(menu, inflater);

		//Pass the resource ID of the menu and populate the Menu 
		//instance with the items defined in the xml file
		inflater.inflate(R.menu.toolbar_profile_detail_menu, menu);

        mToolbarMenu = menu;
        setSaveMenu();
		
	}
	
	//Get results from Dialog boxes and other Activities
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Log.e(TAG,"In onActivityResult with requestCode " + String.valueOf(requestCode));
		if(resultCode != Activity.RESULT_OK)
			return;
		
		if(requestCode == REQUEST_START_TIME) {
            mProfile.setStartDate((Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME));
			Util.setTimeForLargeTextView(getActivity(),mProfile.getStartDate(), startTimeTextView);
		}
		else if(requestCode == REQUEST_END_TIME) {
            mProfile.setEndDate((Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME));
            Util.setTimeForLargeTextView(getActivity(),mProfile.getEndDate(),endTimeTextView);
		}

	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            case R.id.menu_item_save_profile:
                Log.e(TAG, "Save location menu select");

                saveSettings();
                Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                        R.string.toast_text, Toast.LENGTH_SHORT);
                toast.show();

                getActivity().finish();
                return true;
            case R.id.menu_item_delete_profile:

                confirmDeleteDialog();

                return true;
            case R.id.menu_item_settings:
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /*******************************************************************/
	/*                        Private Methods                          */
	/*******************************************************************/

    private Uri getThumbnailUri(LatLng latLng) {

        String strLocation = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
        Log.e(TAG, "strLocation: " + strLocation);

       // String strMarkers = "color:red|" + strLocation
        Uri mapsUri = Uri.parse(GOOGLE_STAIC_MAPS_URL).buildUpon()
                .appendQueryParameter(MAPS_PARAM_CENTER,strLocation)
                .appendQueryParameter(MAPS_PARAM_ZOOM,MAPS_ZOOM_VAL)
                .appendQueryParameter(MAPS_PARAM_SIZE,MAPS_SIZE_SMALL)
                .appendQueryParameter(MAPS_PARAM_SENSOR, MAPS_SENSOR_VAL)
                .appendQueryParameter(MAPS_PARAM_MARKERS,strLocation)
                .build();

        Log.e(TAG,"The mapsUri: " + mapsUri);

        return mapsUri;

    }
    private void setLocationLayout(View view) {

        if(mProfileType == LOCATION_PROFILE_LIST) {
           (view.findViewById(R.id.location_layout)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.startTimeTextView)).setVisibility(View.GONE);
            (view.findViewById(R.id.endTimeTextView)).setVisibility(View.GONE);
        }
        else {
            (view.findViewById(R.id.location_layout)).setVisibility(View.GONE);
            (view.findViewById(R.id.startTimeTextView)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.startTimeTextView)).setVisibility(View.VISIBLE);
        }
    }
    private void confirmDeleteDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(getString(R.string.dialog_delete_profile,mProfile.getTitle()))
                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProfileManager.deleteProfile(getActivity(),mProfile);
                getActivity().finish();
            }
        })
        .setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();

    }

    private int getVolumeIconResource(int volumeType) {

        switch(volumeType) {

            case VOLUME_OFF:
                 return R.drawable.ic_volume_off;
            case VOLUME_VIBRATE:
                 return R.drawable.ic_vibration;
            case VOLUME_RING:
                return R.drawable.ic_volume_up;
            default:
            //Something went wrong if we get here.
            return R.drawable.ic_volume_off;

        }
    }
	private void setVolumeIcon() {

        int volumeIconRes = 0;
        //Set Start Volume Icon
        volumeIconRes = getVolumeIconResource(mProfile.getStartVolumeType());
        startVolumeImageView.setImageResource(volumeIconRes);

        //Set End Volume Icon
        volumeIconRes = getVolumeIconResource(mProfile.getEndVolumeType());
        endVolumeImageView.setImageResource(volumeIconRes);


	}


	private void setSaveMenu() {

        if(mToolbarMenu != null) {

            MenuItem saveMenuItem = mToolbarMenu.findItem(R.id.menu_item_save_profile);

            if (mTitleTextView.getText().length() > 0)
                saveMenuItem.setEnabled(true);
            else
                saveMenuItem.setEnabled(false);
        }
    }

	/**
	 * Save all settings of the alarms to SharedPreferences 
     */
    private void saveSettings() {

        Log.e(TAG, "Inside saveSettings()");

        mProfile.setTitle(mTitleTextView.getText().toString());

        //Insert into DB if it's a new profile, otherwise update existing record.
		if(mIsNewProfile)
            ProfileManager.insertProfile(getActivity(), mProfile);
		else
		    ProfileManager.updateProfile(getActivity(),mProfile);

        //Set Volume Control Alarms
        VolumeManagerService.setServiceAlarm(getActivity(),mProfile,true);





    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG,"onConnected()");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
          //  Log.e(TAG,"Location was null");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            LatLng latLng;
            if(mProfile.getLocation() == null) {
                //Store location into profile
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mProfile.setLocation(new GeoFenceLocation(latLng));
            }
            else {
                latLng = mProfile.getLocation().getLatLng();
            }
           // Log.e(TAG,"LatLng: " + latLng.toString());

            if(mThumbnailImageView != null) {

             //   Log.e(TAG, "Loading image....");
                Picasso.with(getActivity()).load(getThumbnailUri(latLng)).into(mThumbnailImageView);
            }

            //Finally set street address of location
            if(latLng != null) {

                try {
                    Address address = Util.getStreetAddress(getActivity(), latLng);
                    String strFullAddress = address.getAddressLine(0) + " " + address.getLocality() +
                            ", " + address.getAdminArea();
                    mAddressTextView.setText(strFullAddress);
                } catch(IOException e) {
                    mAddressTextView.setText("Unknown Street Address");
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onToggleLocationIcon() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
          Log.e(TAG,"onConnectionFailed(): " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
          Log.e(TAG,"onLocationChanged(): " + location.toString());
    }
}
