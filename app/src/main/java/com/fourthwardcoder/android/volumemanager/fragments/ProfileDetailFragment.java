package com.fourthwardcoder.android.volumemanager.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.fourthwardcoder.android.volumemanager.Manifest;
import com.fourthwardcoder.android.volumemanager.R;
//import com.fourthwardcoder.android.volumemanager.activites.SettingsActivity;
import com.fourthwardcoder.android.volumemanager.activites.LocationMapActivity;
import com.fourthwardcoder.android.volumemanager.activites.ProfileDetailActivity;
import com.fourthwardcoder.android.volumemanager.activites.SettingsActivity;
import com.fourthwardcoder.android.volumemanager.adapters.LocationProfileListAdapter;
import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
import com.fourthwardcoder.android.volumemanager.helpers.ImageTransitionListener;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.location.GeofenceManager;
import com.fourthwardcoder.android.volumemanager.models.GeoFenceLocation;
import com.fourthwardcoder.android.volumemanager.models.Profile;
import com.fourthwardcoder.android.volumemanager.services.VolumeManagerService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

/**
 * Profile Detail Fragment
 * <p>
 * Fragment of the Profile Detail's. Here is where all settings of a profile are made
 * <p>
 * Created: 3/13/2015
 *
 * @author Chris Hare
 */
public class ProfileDetailFragment extends Fragment implements LocationProfileListAdapter.LocationAdapterCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<Status>, CheckBox.OnCheckedChangeListener, Constants {

    /************************************************************************/
    /*                           Constants                                  */
    /************************************************************************/
    private final static String TAG = ProfileDetailFragment.class.getSimpleName();
    //Tag for the time piker Dialog
    private static final String DIALOG_TIME = "time";

    //Constant for request code to Time Picker
    public static final int REQUEST_START_TIME = 0;
    public static final int REQUEST_END_TIME = 1;
    //Request code for MAP Activity Callback
    public static final int REQUEST_LOCATION_UPDATE = 2;
    //Duration of day button fade in
    public static int BUTTON_FADE_DURATION = 500;

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

    //Identifier for the permission request
    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST = 1;

    /************************************************************************/
	/*                          Local Data                                  */
    /************************************************************************/
    Profile mProfile;
    TextView mTitleTextView, startTimeTextView, endTimeTextView;
    RadioGroup mStartVolumeRadioGroup;
    RadioGroup mEndVolumeRadioGroup;
    SeekBar mStartRingSeekBar, mEndRingSeekBar;
    TextView mStartRingVolumeTextView, mEndRingVolumeTextView;
    ImageView mStartVolumeImageView, mEndVolumeImageView;
    CheckBox mUseStartVolumeDefaultCheckBox;
    CheckBox mUseEndVolumeDefaultCheckBox;

    boolean mIsNewProfile = false;
    boolean mIsSaveGeofence = true;

    int mProfileType;

    Menu mToolbarMenu;

    //Location profile only members
    ImageView mThumbnailImageView;
    TextView mAddressTextView;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    GeofenceManager mGeofenceManager;

    String[] mDaysList;

    View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }
    /*******************************************************/
	/*                  Override Methods                   */

    /*******************************************************/
    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile_detail, container, false);

        //First check if we have don't have anything in saveInstanceState from a rotation
        if (savedInstanceState == null) {

            Bundle arguments = getArguments();
            if (arguments != null) {
                // mProfileType = intent.getIntExtra(EXTRA_PROFILE_TYPE,TIME_PROFILE_LIST);
                mProfileType = arguments.getInt(EXTRA_PROFILE_TYPE);
                //See if we have a Profile object. If so we are editing the Profile.
                //If not, it's a new profile
                if (arguments.containsKey(EXTRA_PROFILE)) {
                    //Get profile object sent in as argument
                    mProfile = arguments.getParcelable(EXTRA_PROFILE);

                    mIsNewProfile = false;
                } else {
                    mProfile = new Profile();
                    mIsNewProfile = true;
                }
            } else {
                Log.e(TAG, "Arguments are null!");
            }
        } else {
            Log.e(TAG, "Saved vars");
            //Restore Profile from rotation.
            mProfile = savedInstanceState.getParcelable(EXTRA_PROFILE);
            mProfileType = savedInstanceState.getInt(EXTRA_PROFILE_TYPE);
            mIsNewProfile = savedInstanceState.getBoolean(EXTRA_IS_NEW_PROFILE);
            Log.e(TAG, "Saved mProfile type " + mProfileType);
        }

        //Setup Toolbar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        if (toolbar != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle(null);
        }

        //Get days of the week
         mDaysList = getContext().getResources().getStringArray(R.array.days_of_the_week);

        //Get volume icons
        mStartVolumeImageView = (ImageView) view.findViewById(R.id.volumeStartImageView);
        mEndVolumeImageView = (ImageView) view.findViewById(R.id.volumeEndImageView);
        /*
	     * Setup TextViews                        
	     */
        mTitleTextView = (TextView) view.findViewById(R.id.profileTitleTextView);
        mTitleTextView.setContentDescription(getString(R.string.cont_desc_detail_title));
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


        startTimeTextView = (TextView) view.findViewById(R.id.startTimeTextView);
        //Set up Ring Volume TextView
        mStartRingVolumeTextView = (TextView) view.findViewById(R.id.startRingVolumeTextView);
        //Set up End Time TextView
        endTimeTextView = (TextView) view.findViewById(R.id.endTimeTextView);
        //Set up Ring Volume TextView
        mEndRingVolumeTextView = (TextView) view.findViewById(R.id.endRingVolumeTextView);
	    
	    /*
	     * Setup Buttons                     
	     */
        View.OnClickListener dayButtonListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<Boolean> daysOfTheWeek = mProfile.getDaysOfTheWeek();

                Log.e(TAG, "Button tag: " + v.getTag());
                TextView textView = (TextView) v;
                boolean setting = daysOfTheWeek.get((int) v.getTag());
                int index = (int) v.getTag();

                Log.e(TAG, "Days of the week before change");
                Log.e(TAG, mProfile.getDaysOfWeekDebugString());
                if (setting) {
                    //Turn Day off
                  //  Log.e(TAG, "Turn Day " + daysButtonNames[index] + " off");

                    daysOfTheWeek.set(index, Boolean.FALSE);
                    textView.setTextColor(getResources().getColor(R.color.app_primary_text_dark));
                    textView.setBackground(getResources().getDrawable(R.drawable.round_button_off));
                    textView.setContentDescription(mDaysList[index] + " " + getString(R.string.cont_desc_disabled));

                    //  v.animate().setDuration(BUTTON_FADE_DURATION).alpha(0f);

                } else {
                    //Turn Day on
                   // Log.e(TAG, "Turn day " + daysButtonNames[index] + " on");
                    daysOfTheWeek.set(index, Boolean.TRUE);
                    textView.setTextColor(Color.parseColor("#ffffff"));
                    textView.setBackground(getResources().getDrawable(R.drawable.round_button));
                    //  v.animate().setDuration(BUTTON_FADE_DURATION).alpha(1f);
                    textView.setContentDescription(mDaysList[index] + " " + getString(R.string.cont_desc_enabled));

                }

                //Update days of the week in Profile
                mProfile.setDaysOfTheWeek(daysOfTheWeek);

                Log.e(TAG, "Days of the week after change");
                Log.e(TAG, mProfile.getDaysOfWeekDebugString());


            }
        };

        mStartVolumeRadioGroup = (RadioGroup) view.findViewById(R.id.startVolumeRadioGroup);
        mEndVolumeRadioGroup = (RadioGroup) view.findViewById(R.id.endVolumeRadioGroup);
        mStartRingSeekBar = (SeekBar) view.findViewById(R.id.startRingSeekBar);
        mEndRingSeekBar = (SeekBar) view.findViewById(R.id.endRingSeekBar);
        mUseStartVolumeDefaultCheckBox = (CheckBox)view.findViewById(R.id.start_volume_use_default_checkbox);
        mUseStartVolumeDefaultCheckBox.setOnCheckedChangeListener(this);
        mUseEndVolumeDefaultCheckBox = (CheckBox)view.findViewById(R.id.end_volume_use_default_checkbox);
        mUseEndVolumeDefaultCheckBox.setOnCheckedChangeListener(this);



        //Set data if we have a profile object
        if (mProfile != null) {
            //Set Profile title
            mTitleTextView.setText(mProfile.getTitle());

            Log.e(TAG, mProfile.getDaysOfWeekDebugString());
            final TableRow daysRow = (TableRow) view.findViewById(R.id.days_table_row);

            for (int i = 0; i < daysRow.getChildCount(); i++) {
                Button button = (Button) daysRow.getChildAt(i);
                button.setText(Constants.daysButtonNames[i]);
                button.setOnClickListener(dayButtonListener);
                button.setTag(new Integer(i));

                boolean setting = mProfile.getDaysOfTheWeek().get(i);
                if (setting) {
                    //Turn Day on
                    button.setTextColor(Color.parseColor("#ffffff"));
                    button.setBackground(getResources().getDrawable(R.drawable.round_button));
                    button.setContentDescription(mDaysList[i] + " " + getString(R.string.cont_desc_enabled));
                } else {
                    //Turn Day off
                    button.setTextColor(getResources().getColor(R.color.app_primary_text_dark));
                    button.setBackground(getResources().getDrawable(R.drawable.round_button_off));
                    button.setContentDescription(mDaysList[i] + " " + getString(R.string.cont_desc_disabled));
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                getActivity().getWindow().getSharedElementEnterTransition().addListener(new ImageTransitionListener() {
                    @Override
                    public void onTransitionEnd(Transition transition) {

                        // view.setBackgroundColor(getResources().getColor(R.color.sound_control_panel_background));
                        //Fade in days of week row
                        daysRow.animate().setDuration(BUTTON_FADE_DURATION).alpha(1f);
                    }

                    @Override
                    public void onTransitionStart(Transition transition) {
                        //Start of transition, make days of week row invisible
                        daysRow.setAlpha(0f);
                    }
                });

            }
            //Setup start time of volume control
            //  startTimeButton = (Button)view.findViewById(R.id.startTimeButton);
            startTimeTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Start Time Picker Dialog on CrimeFragment after clicking Time Button
                    android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                    TimePickerFragment dialog = TimePickerFragment.newInstance(mProfile.getStartDate(), getString(R.string.start_time_dialog));
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
            //Set volume controls initial settings
            setVolumeControls();

            //Set up Ringer type Radio Buttons
            mStartVolumeRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    if (checkedId == R.id.startOffRadio) {
                        mProfile.setStartVolumeType(VOLUME_OFF);
                        mProfile.setStartRingVolume(0);
                        Util.setSeekBarPosition(mStartRingSeekBar, mStartRingVolumeTextView,
                                mProfile.getStartRingVolume(), Util.getMaxRingVolume(getActivity().getApplicationContext()));

                    } else if (checkedId == R.id.startVibrateRadio) {
                        mProfile.setStartVolumeType(VOLUME_VIBRATE);
                        mProfile.setStartRingVolume(0);
                        Util.setSeekBarPosition(mStartRingSeekBar, mStartRingVolumeTextView,
                                mProfile.getStartRingVolume(), Util.getMaxRingVolume(getActivity().getApplicationContext()));
                    } else {
                        Log.e(TAG, "Setting Ring volume seekbar to 3!!");
                        mProfile.setStartVolumeType(VOLUME_RING);
                        Util.setSeekBarPosition(mStartRingSeekBar, mStartRingVolumeTextView,
                                Integer.parseInt(getContext().getString(R.string.pref_default_ring_volume_default)),
                                Util.getMaxRingVolume(getActivity().getApplicationContext()));
                    }

                    setVolumeIcon();
                }

            });

            //Set up Ringer type Radio Buttons
            mEndVolumeRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    if (checkedId == R.id.endOffRadio) {
                        mProfile.setEndVolumeType(VOLUME_OFF);
                        mProfile.setEndRingVolume(0);
                        Util.setSeekBarPosition(mEndRingSeekBar, mEndRingVolumeTextView, mProfile.getEndRingVolume(), Util.getMaxRingVolume(getActivity().getApplicationContext()));

                    } else if (checkedId == R.id.endVibrateRadio) {
                        mProfile.setEndVolumeType(VOLUME_VIBRATE);
                        mProfile.setEndRingVolume(0);
                        Util.setSeekBarPosition(mEndRingSeekBar, mEndRingVolumeTextView, mProfile.getEndRingVolume(), Util.getMaxRingVolume(getActivity().getApplicationContext()));
                    } else {
                        mProfile.setEndVolumeType(VOLUME_RING);
                        Util.setSeekBarPosition(mEndRingSeekBar, mEndRingVolumeTextView,
                                Integer.parseInt(getContext().getString(R.string.pref_default_ring_volume_default)),
                                Util.getMaxRingVolume(getActivity().getApplicationContext()));
                    }

                    setVolumeIcon();
                }
            });
	    
	    /*
	     * Setup SeekBars                    
	     */
            //Set up Ringer SeekBar
            mStartRingSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    Log.e(TAG, "!!!!!Start Seek Bar progress " + progress + " fromUser=" + fromUser);

                        if (progress == 0)
                            //If this was changed from the radio buttons, we may have it set to vibrate
                            if (!fromUser) {

                                int volumeType;

                                //See if we need to get volume type from defaults or profile
                                if(mProfile.isUseStartDefault())
                                    volumeType = Util.getDefaultStartVolumeType(getContext()) ;
                                else
                                    volumeType = mProfile.getStartVolumeType();

                                //Set radio button
                                if (volumeType == VOLUME_OFF)
                                    ((RadioButton) mStartVolumeRadioGroup.getChildAt(VOLUME_OFF)).setChecked(true);
                                else if (volumeType == VOLUME_VIBRATE)
                                    ((RadioButton) mStartVolumeRadioGroup.getChildAt(VOLUME_VIBRATE)).setChecked(true);
                            } else {
                                ((RadioButton) mStartVolumeRadioGroup.getChildAt(VOLUME_OFF)).setChecked(true);
                            }
                        else
                            ((RadioButton) mStartVolumeRadioGroup.getChildAt(VOLUME_RING)).setChecked(true);

                        Util.setRingVolumeText(mStartRingVolumeTextView, progress, Util.getMaxRingVolume(getActivity().getApplicationContext()));
                        mProfile.setStartRingVolume(progress);

                        setVolumeIcon();

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            //Set up Ringer SeekBar
            mEndRingSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {

                        if (progress == 0)
                            //If this was changed from the radio buttons, we may have it set to vibrate
                            if (!fromUser) {

                                int volumeType;

                                //See if we need to get volume type from defaults or profile
                                if(mProfile.isUseEndDefault())
                                    volumeType = Util.getDefaultStartVolumeType(getContext()) ;
                                else
                                    volumeType = mProfile.getEndVolumeType();

                                if (volumeType == VOLUME_OFF)
                                    ((RadioButton) mEndVolumeRadioGroup.getChildAt(VOLUME_OFF)).setChecked(true);
                                else if (volumeType == VOLUME_VIBRATE)
                                    ((RadioButton) mEndVolumeRadioGroup.getChildAt(VOLUME_VIBRATE)).setChecked(true);
                            } else {
                                ((RadioButton) mEndVolumeRadioGroup.getChildAt(VOLUME_OFF)).setChecked(true);
                            }
                        else
                            ((RadioButton) mEndVolumeRadioGroup.getChildAt(VOLUME_RING)).setChecked(true);

                        Util.setRingVolumeText(mEndRingVolumeTextView, progress, Util.getMaxRingVolume(getActivity().getApplicationContext()));
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
            if (mProfileType == LOCATION_PROFILE_LIST) {

                OnClickListener locationListener = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), LocationMapActivity.class);
                        i.putExtra(EXTRA_PROFILE, mProfile);
                        startActivityForResult(i, REQUEST_LOCATION_UPDATE);
                    }
                };

                mThumbnailImageView = (ImageView) view.findViewById(R.id.location_thumbnail);
                mThumbnailImageView.setOnClickListener(locationListener);

                mAddressTextView = (TextView) view.findViewById(R.id.address_textview);
                mAddressTextView.setOnClickListener(locationListener);

                //Modify text if control headings
                TextView startHeadingTextView = (TextView)view.findViewById(R.id.start_volume_control_heading);
                startHeadingTextView.setText(getString(R.string.start_volume_control_settings_location));
                TextView endHeadingTextView = (TextView)view.findViewById(R.id.end_volume_control_heading);
                endHeadingTextView.setText(getString(R.string.end_volume_control_settings_location));
            }

	    /*
	     * Set Default and Saved Settings
	     */
            Util.setTimeForLargeTextView(getActivity(), mProfile.getStartDate(), startTimeTextView);
            startTimeTextView.setContentDescription(getString(R.string.cont_desc_detail_start_time) + " " + Util.formatTime(getContext(), mProfile.getStartDate()));
            Util.setTimeForLargeTextView(getActivity(), mProfile.getEndDate(), endTimeTextView);
            endTimeTextView.setContentDescription(getString(R.string.cont_desc_detail_end_time) + " " + Util.formatTime(getContext(), mProfile.getEndDate()));

            //Set default checkbox
            mUseStartVolumeDefaultCheckBox.setChecked(mProfile.isUseStartDefault());
            mUseEndVolumeDefaultCheckBox.setChecked(mProfile.isUseEndDefault());

            mStartRingSeekBar.setMax(Util.getMaxRingVolume(getActivity().getApplicationContext()));
            mEndRingSeekBar.setMax(Util.getMaxRingVolume(getActivity().getApplicationContext()));

            //Set access to volume controls
            setVolumeControlAccess();
            //Set Volume Icon
            setVolumeIcon();

            //Display Location portion if this is a location profile
            setLocationLayout(view);

        }


        getActivity().supportStartPostponedEnterTransition();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");

        //Get Location if this is a location profile
        if (mProfileType == LOCATION_PROFILE_LIST) {

            //Check for permissions for API 23 (Marshmallow)
            getPermissionAccessFineLocation();

            //Get current location if there is not location set
            Log.e(TAG, "Creating Google API and Location requests");
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

            //For Marshmallow and above, get Location Permissions
         //   getPermissionAccessFineLocation();
        }
        //If the Google Client is null we are either a Time Profile or we already have the location
        //for a location profile
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }


    @Override
    public void onStop() {
        super.onStop();

        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        Log.e(TAG, "onResume()");
        //Check that the controls have changed from the Settings menu
        setVolumeControls();
        setVolumeIcon();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putParcelable(EXTRA_PROFILE, mProfile);
        savedInstanceState.putBoolean(EXTRA_IS_NEW_PROFILE, mIsNewProfile);
        savedInstanceState.putInt(EXTRA_PROFILE_TYPE, mProfileType);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        //Inflate menu Items
        if (getActivity() instanceof ProfileDetailActivity) {
            Log.e(TAG, "onCreateOptionsMenu(): portrait mode");
            inflater.inflate(R.menu.fragment_profile_detail_menu, menu);

            mToolbarMenu = menu;
        } else {
            Log.e(TAG, "onCreateOptionsMenu(): In two pane, clean and rebuild fragment menu");

            if(getView() != null) {
                Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
                if (toolbar != null) {
                    AppCompatActivity activity = (AppCompatActivity) getActivity();
                    activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                    //Clear menus
                    Log.e(TAG, "onCreateOptionsMenu(): Clear old menu, rebuild");
                    Menu toolbarMenu = toolbar.getMenu();
                    if (toolbarMenu != null)
                        toolbarMenu.clear();

                    toolbar.inflateMenu(R.menu.fragment_profile_detail_menu);


                }
            }
        }

        setSaveMenu();

    }

    //Get results from Dialog boxes and other Activities
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e(TAG, "In onActivityResult with requestCode " + String.valueOf(requestCode));
        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == REQUEST_START_TIME) {
            mProfile.setStartDate((Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME));
            Util.setTimeForLargeTextView(getActivity(), mProfile.getStartDate(), startTimeTextView);
            startTimeTextView.setContentDescription(getString(R.string.cont_desc_detail_start_time) + " " + Util.formatTime(getContext(), mProfile.getStartDate()));

        } else if (requestCode == REQUEST_END_TIME) {
            mProfile.setEndDate((Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME));
            Util.setTimeForLargeTextView(getActivity(), mProfile.getEndDate(), endTimeTextView);
            endTimeTextView.setContentDescription(getString(R.string.cont_desc_detail_end_time) + " " + Util.formatTime(getContext(), mProfile.getEndDate()));
        } else if (requestCode == REQUEST_LOCATION_UPDATE) {

            mProfile = data.getParcelableExtra(EXTRA_PROFILE);
            Log.e(TAG, "Got request back from Map Activity!!");
            Log.e(TAG, "Title of location = " + mProfile.getTitle());

            //Make the title the name of the place that was searched in the location activity
            if (mTitleTextView != null) {
                if (mProfile.getTitle() != null) {
                    mTitleTextView.setText(mProfile.getTitle());
                }
            }
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

                //Only kill the activity if we are in portrait mode
                if (getActivity() instanceof ProfileDetailActivity) {

                    //Do renter transition if we are Lollipop or greater.
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                        getActivity().finish();
                    else
                        getActivity().finishAfterTransition();
                }
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

    private void setVolumeControlAccess() {
        
        if(mUseStartVolumeDefaultCheckBox != null) {

            if(mUseStartVolumeDefaultCheckBox.isChecked()) {
                setRadioGroupAccess(mStartVolumeRadioGroup,false);
                mStartRingSeekBar.setEnabled(false);
            }
            else {
                setRadioGroupAccess(mStartVolumeRadioGroup,true);
                mStartRingSeekBar.setEnabled(true);
            }
        }

        if(mUseEndVolumeDefaultCheckBox != null) {

            if(mUseEndVolumeDefaultCheckBox.isChecked()) {
                setRadioGroupAccess(mEndVolumeRadioGroup,false);
                mEndRingSeekBar.setEnabled(false);
            }
            else {
                setRadioGroupAccess(mEndVolumeRadioGroup,true);
                mEndRingSeekBar.setEnabled(true);
            }

        }
    }

    private void setRadioGroupAccess(RadioGroup rg, boolean access) {

        for (int i = 0; i< rg.getChildCount(); i++ )
            rg.getChildAt(i).setEnabled(access);
    }
    /**
     * Pull the static Google map image for the location in the profile
     * @param latLng latitude and longitude of the loaction
     * @return path to the map image
     */
    private Uri getThumbnailUri(LatLng latLng) {

        String strLocation = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
        Log.e(TAG, "strLocation: " + strLocation);

        // String strMarkers = "color:red|" + strLocation
        Uri mapsUri = Uri.parse(GOOGLE_STAIC_MAPS_URL).buildUpon()
                .appendQueryParameter(MAPS_PARAM_CENTER, strLocation)
                .appendQueryParameter(MAPS_PARAM_ZOOM, MAPS_ZOOM_VAL)
                .appendQueryParameter(MAPS_PARAM_SIZE, MAPS_SIZE_SMALL)
                .appendQueryParameter(MAPS_PARAM_SENSOR, MAPS_SENSOR_VAL)
                .appendQueryParameter(MAPS_PARAM_MARKERS, strLocation)
                .build();

        Log.e(TAG, "The mapsUri: " + mapsUri);

        return mapsUri;

    }

    /**
     * Set the location part of the layout if this is a location profile. Otherwise hide it.
     * @param view root view of the fragment
     */
    private void setLocationLayout(View view) {

        if (mProfileType == LOCATION_PROFILE_LIST) {
            (view.findViewById(R.id.location_layout)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.startTimeTextView)).setVisibility(View.GONE);
            (view.findViewById(R.id.endTimeTextView)).setVisibility(View.GONE);
        } else {
            (view.findViewById(R.id.location_layout)).setVisibility(View.GONE);
            (view.findViewById(R.id.startTimeTextView)).setVisibility(View.VISIBLE);
            (view.findViewById(R.id.startTimeTextView)).setVisibility(View.VISIBLE);
        }
    }

    /**
     * After delete of profile, show a confirmation dialog
     */
    private void confirmDeleteDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(getString(R.string.dialog_delete_profile, mProfile.getTitle()))
                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mProfileType == LOCATION_PROFILE_LIST) {
                            if (mGeofenceManager != null)
                                deleteGeofence(mProfile.getId().toString());
                        }
                        //delete the profile
                        ProfileManager.deleteProfile(getActivity(), mProfile);

                        //close the activity
                        if (getActivity() instanceof ProfileDetailActivity)
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

    /**
     * delete a geofence
     * @param requestId id of geofence to delete
     */
    private void deleteGeofence(String requestId) {
        if (mGoogleApiClient.isConnected()) {

            if (mGeofenceManager != null) {

                //Set geofence flag for result message
                mIsSaveGeofence = false;
                mGeofenceManager.removeGeofence(this, requestId);
            }
        }
    }

    /**
     * Get the icon resource number based on volume type
     * @param volumeType volume type of the control
     * @return icon resource
     */
    private int getVolumeIconResource(int volumeType) {

        switch (volumeType) {

            case VOLUME_OFF:
                return R.drawable.ic_volume_off_white;
            case VOLUME_VIBRATE:
                return R.drawable.ic_vibration_white;
            case VOLUME_RING:
                return R.drawable.ic_volume_up_white;
            default:
                //Something went wrong if we get here.
                return R.drawable.ic_volume_off_white;

        }
    }

    /**
     * Set the type of icon on the ring seek bar based on the type of the volume control
     */
    private void setVolumeIcon() {

        int volumeIconRes = 0;
        int volumeType;
        //Set Start Volume Icon
        if(mProfile.isUseStartDefault())
            volumeIconRes = getVolumeIconResource(Util.getDefaultStartVolumeType(getContext()));
        else
            volumeIconRes = getVolumeIconResource(mProfile.getStartVolumeType());
        mStartVolumeImageView.setImageResource(volumeIconRes);

        //Set End Volume Icon
        if(mProfile.isUseEndDefault())
            volumeIconRes = getVolumeIconResource(Util.getDefaultEndVolumeType(getContext()));
        else
            volumeIconRes = getVolumeIconResource(mProfile.getEndVolumeType());
        mEndVolumeImageView.setImageResource(volumeIconRes);

    }


    /**
     * Sets visibility of the SAVE menu. Only show the menu if there has been a title entered.
     */
    private void setSaveMenu() {

        if (mToolbarMenu != null) {

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

        if (mProfileType == LOCATION_PROFILE_LIST) {
            //Add new location and store key if this is a new location
            if (mProfile.getLocationKey() == 0) {
                long locationId = ProfileManager.addLocation(getActivity(), mProfile.getLocation());
                mProfile.setLocationKey(locationId);
            } else {
                //Updating existing location
                ProfileManager.updateLocation(getActivity(), mProfile.getLocation(), mProfile.getLocationKey());
            }
            //Set flag for geofence result
            mIsSaveGeofence = true;
            //Create/Update the Geofence
            mGeofenceManager.saveGeofence(this, mProfile);

        }

        //Insert into DB if it's a new profile, otherwise update existing record.
        if (mIsNewProfile)
            ProfileManager.insertProfile(getActivity(), mProfile);
        else
            ProfileManager.updateProfile(getActivity(), mProfile);

        if (mProfileType == TIME_PROFILE_LIST) {
            //Set Volume Control Alarms
            VolumeManagerService.setServiceAlarm(getActivity(), mProfile, true);
        }
    }

    private void getPermissionAccessFineLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Check if we have permission to access Location Services using FINE LOCATION
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION_PERMISSION_REQUEST);

            }
        }
    }

    private boolean haveLocationPermission() {

        //Check if we have permission to access Location Services using FINE LOCATION
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Have Location permission
            return true;
        }
        else {
            return false;
        }
    }

    private void getCurrentLocation() {

        Location location = null;

        if(haveLocationPermission()) {
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (location == null) {
                Log.e(TAG, "Location was null. Can't get location");
                Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                        R.string.network_error, Toast.LENGTH_LONG);
                toast.show();


            } else {
                LatLng latLng;
                if (mProfile.getLocation() == null) {
                    //We have a new profile. Set it up
                    //Store location into profile
                    Log.e(TAG, "onConnected() Location in Profile is null. Create new Location");
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mProfile.setLocation(new GeoFenceLocation(latLng));

                    //Finally set street address of location
                    if (latLng != null) {

                        String strFullAddress;

                        try {
                            Address address = Util.getStreetAddress(getActivity(), latLng);

                            //Get and store street address
                            String strStreetAddress = address.getAddressLine(0);
                            mProfile.getLocation().setAddress(strStreetAddress);

                            //Get and store city
                            String strCity = address.getLocality() + ", " + address.getAdminArea();
                            mProfile.getLocation().setCity(strCity);

                            strFullAddress = strStreetAddress + " " + strCity;

                        } catch (IOException e) {
                            //Could not get address
                            strFullAddress = getString(R.string.unknown_street_address);

                            mProfile.getLocation().setAddress(strFullAddress);
                            mProfile.getLocation().setCity("");
                        }

                        //Set Street TextView
                        mAddressTextView.setText(strFullAddress);
                    }
                } else {
                    //Have existing profile

                    //Poll DB incase location has changed
                    // mProfile.setLocation(ProfileManager.getLocation(getActivity(), mProfile.getLocationKey()));
                    Log.e(TAG, "Have existing profile");
                    Log.e(TAG, mProfile.getLocation().toString());

                    latLng = mProfile.getLocation().getLatLng();

                    mAddressTextView.setText(mProfile.getLocation().getFullAddress());
                }
                // Log.e(TAG,"LatLng: " + latLng.toString());

                if (mThumbnailImageView != null) {

                    //   Log.e(TAG, "Loading image....");
                    Picasso.with(getActivity()).load(getThumbnailUri(latLng)).into(mThumbnailImageView);
                }
            }

        }
    }



    /**
     * Set the volume controls from stored in the profile or the default settings
     */
    private void setVolumeControls() {


        if(mProfile.isUseStartDefault()) {

            int ringType = Util.getDefaultStartVolumeType(getContext());
            int ringVolume = Util.getDefaultStartRingVolume(getContext());

            //Set start controls from defaults
            ((RadioButton) mStartVolumeRadioGroup.getChildAt(ringType)).setChecked(true);
            Util.setSeekBarPosition(mStartRingSeekBar, mStartRingVolumeTextView, ringVolume, Util.getMaxRingVolume(getActivity().getApplicationContext()));

        }
        else {
            //Used start/enter settings stored in profile
            ((RadioButton) mStartVolumeRadioGroup.getChildAt(mProfile.getStartVolumeType())).setChecked(true);
            Util.setSeekBarPosition(mStartRingSeekBar, mStartRingVolumeTextView, mProfile.getStartRingVolume(), Util.getMaxRingVolume(getActivity().getApplicationContext()));

        }

        if(mProfile.isUseEndDefault()) {

            int ringType = Util.getDefaultEndVolumeType(getContext());
            int ringVolume = Util.getDefaultEndRingVolume(getContext());

            //Set start controls from defaults
            ((RadioButton) mEndVolumeRadioGroup.getChildAt(ringType)).setChecked(true);
            Util.setSeekBarPosition(mEndRingSeekBar, mEndRingVolumeTextView, ringVolume, Util.getMaxRingVolume(getActivity().getApplicationContext()));

        }
        else {
            //Used end/exit settings stored in profile
            ((RadioButton) mEndVolumeRadioGroup.getChildAt(mProfile.getEndVolumeType())).setChecked(true);
            Util.setSeekBarPosition(mEndRingSeekBar, mEndRingVolumeTextView, mProfile.getEndRingVolume(), Util.getMaxRingVolume(getActivity().getApplicationContext()));

        }

    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG, "onConnected()");
        //Get GeofenceManger object
        mGeofenceManager = new GeofenceManager(getActivity().getApplicationContext(), mGoogleApiClient);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Check if we have permission to access Location Services using FINE LOCATION
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION_PERMISSION_REQUEST);

            }
            else {
                getCurrentLocation();
            }
        }
        else {
            //Pre-Marshmellow. Will already have permission
            getCurrentLocation();
        }

    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed(): " + connectionResult.toString());


        Log.e(TAG,"status code = " + connectionResult.getErrorCode());


        if(connectionResult.getErrorCode() == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            Toast.makeText(getActivity(), getString(R.string.google_play_service_out_of_date_error),Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity(),getString(R.string.google_play_service_connection_failed),Toast.LENGTH_LONG).show();
        }

        //Disable the title TextView so it can not be changed and the profile can not be saved
        mTitleTextView.setEnabled(false);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "onLocationChanged(): " + location.toString());
    }


    @Override
    public void onResult(Status status) {

        String msg;

        if (mIsSaveGeofence)
            msg = getString(R.string.geofence_saved);
        else
            msg = getString(R.string.geofence_deleted);

        GeofenceManager.setGeofenceResult(getActivity(), status, msg);
    }

    @Override
    public void onToggleLocationIcon(boolean enable, String requestId) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(buttonView.getId() == R.id.start_volume_use_default_checkbox) {
            Log.e(TAG,"onCheckChanged() start default check box");
            mProfile.setUseStartDefault(isChecked);
        }
        else {
            Log.e(TAG,"onCheckChanged() end default check box");
            mProfile.setUseEndDefault(isChecked);
        }

        setVolumeControlAccess();
        setVolumeControls();

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        Log.e(TAG,"onRequestPermissionResult() with requestCoode="+requestCode);
        if(requestCode == ACCESS_FINE_LOCATION_PERMISSION_REQUEST) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(),getString(R.string.permission_fine_location_granted),Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            }
            else {
                Toast.makeText(getActivity(),getString(R.string.permission_fine_location_denied),Toast.LENGTH_SHORT).show();
            }

        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
