package com.fourthwardcoder.android.volumemanager.fragments;

import java.util.ArrayList;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.telecom.Call;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fourthwardcoder.android.volumemanager.activites.LocationMapActivity;
import com.fourthwardcoder.android.volumemanager.activites.SettingsActivity;
import com.fourthwardcoder.android.volumemanager.adapters.LocationProfileListAdapter;
import com.fourthwardcoder.android.volumemanager.adapters.ProfileListAdapter;
import com.fourthwardcoder.android.volumemanager.data.ProfileContract;
import com.fourthwardcoder.android.volumemanager.R;
//import com.fourthwardcoder.android.volumemanager.activites.SettingsActivity;
import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.location.GeofenceManager;
import com.fourthwardcoder.android.volumemanager.models.GeoFenceLocation;
import com.fourthwardcoder.android.volumemanager.services.VolumeManagerService;
import com.fourthwardcoder.android.volumemanager.activites.ProfileDetailActivity;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.Profile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;


public class ProfileListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        LocationProfileListAdapter.LocationAdapterCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>, Constants {

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
    //ProfileListAdapter mProfileAdapter;
    BaseAdapter mProfileAdapter;
    ListView listview;
    int mProfileType;
    GoogleApiClient mGoogleApiClient;
    /***************************************************/
	/*                Override Methods                 */

    /***************************************************/

    public static ProfileListFragment newInstance(int profileType) {

        Bundle args = new Bundle();
        args.putInt(EXTRA_PROFILE_TYPE, profileType);
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

        if (savedInstanceState != null) {
            mProfileType = savedInstanceState.getInt(EXTRA_PROFILE_TYPE);
        } else {
            Bundle bundle = getArguments();
            mProfileType = bundle.getInt(EXTRA_PROFILE_TYPE);
        }

        //profileList = ProfileJSONManager.get(getActivity()).getProfiles();

        //Init the Profile Loader. Callbacks received in this fragment
        getLoaderManager().initLoader(PROFILE_LOADER, null, this);

        Util.setStatusBarColor(getActivity());

        //Get Google Api Client if this is the list of Location Profiles
        if (mProfileType == LOCATION_PROFILE_LIST) {
            //Build Google API Client
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    @SuppressLint("NewApi")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;

        view = inflater.inflate(R.layout.fragment_profile_list, container, false);

        //Empty ListView view
        Button newProfileButton = (Button) view.findViewById(R.id.emptyButtonAddProfile);
        newProfileButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                ProfileManager.newProfile(getActivity(), mProfileType);
            }

        });

        listview = (ListView) view.findViewById(android.R.id.list);
        ViewGroup headerView = (ViewGroup) inflater.inflate(R.layout.listview_header, listview, false);
        TextView headerTextView = (TextView)headerView.findViewById(R.id.profileHeaderTextView);

        if(mProfileType == TIME_PROFILE_LIST)
            headerTextView.setText(getString(R.string.profile_header));
        else
            headerTextView.setText(getString(R.string.location_profile_header));

        listview.addHeaderView(headerView);

        listview.setEmptyView(view.findViewById(android.R.id.empty));

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                // TODO Auto-generated method stub

                Profile p = (Profile) parent.getItemAtPosition(position);
                Log.d(TAG, "Got profile " + p.getTitle());

                TextView textView = (TextView) view.findViewById(R.id.profileTitleTextView);

                ((Callback) getActivity()).onItemSelected(p, mProfileType, textView);
            }

        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            //Use floating context menues for Froyo and Gingerbread
            registerForContextMenu(listview);
        } else {
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

                            Log.e(TAG, "in onActionItemClicked with adapter count " + mProfileAdapter.getCount());
                            Log.e(TAG, "listivew count " + listview.getCount());

                            //Kill alarms for Time Profiles
                            if (mProfileType == TIME_PROFILE_LIST) {
                                //Delete selected profiles
                                for (int i = listview.getCount() - 1; i > 0; i--) {
                                    if (listview.isItemChecked(i)) {
                                        ProfileManager.deleteProfile(getActivity(), (Profile) mProfileAdapter.getItem(i - 1));
                                    }
                                }
                            }

                            //Destroy Action mode context menu
                            mode.finish();

                            notifyListViewChanged();

                            if (mProfileType == LOCATION_PROFILE_LIST)
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
                    Log.d(TAG, "Selected list item at position " + position);

                }


            });
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(EXTRA_PROFILE_TYPE, mProfileType);
        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume()");
        //Re-connect to Google service
        if (mProfileType == LOCATION_PROFILE_LIST)
            mGoogleApiClient.connect();

        if (mProfileAdapter != null)
            notifyListViewChanged();
    }

    @Override
    public void onPause() {
        super.onPause();

        //Disconnect to Google service
        if (mProfileType == LOCATION_PROFILE_LIST)
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);

        //Pass the resource ID of the menu and populate the Menu
        //instance with the items defined in the xml file
        inflater.inflate(R.menu.toolbar_profile_list_menu, menu);

    }

    //Context Menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {


        //Get menu item in context menu. ListView is a subclass of AdapterView
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        //  Log.d(TAG,"in onContextItemSelected with position: " + );

        Profile profile = (Profile) listview.getItemAtPosition(position);
        Log.e(TAG, "Deleting profile " + profile.getTitle());

        switch (item.getItemId()) {
            case R.id.menu_item_delete_profile:

                if (mProfileType == LOCATION_PROFILE_LIST)
                    updateGeofences();
                else {
                    ProfileManager.deleteProfile(getActivity(), profile);
                }
                notifyListViewChanged();

                return true;

        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Get menu option by it's ID
        switch (item.getItemId()) {

            case R.id.menu_item_settings:
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e(TAG, "Inside onCreateLoader with profile type " + mProfileType);

        Uri profileUri = ProfileContract.ProfileEntry.buildProfileUri();
        String selection;

        if (mProfileType == LOCATION_PROFILE_LIST)
            selection = ProfileManager.getLocationDbProfileSelection();
        else
            selection = ProfileManager.getProfileDbSelection();

        return new CursorLoader(getActivity(),
                profileUri,
                null,
                selection,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        ArrayList<Profile> profileList = new ArrayList<>(cursor.getCount());

        while (cursor.moveToNext()) {
            Profile profile = new Profile(cursor);

            if(mProfileType == TIME_PROFILE_LIST) {

                if(profile.getLocationKey() == 0)
                    profileList.add(profile);

            }
            else {
                    Log.e(TAG,"Profile with location id" + profile.getLocationKey());

                    if(profile.getLocationKey() > 0) {

                        Log.e(TAG,"Got a location, query it");

                        Cursor locCursor = ProfileManager.getLocation(getActivity(),profile.getLocationKey());

                        if(locCursor != null) {
                            Log.e(TAG,"Location was not null!!");
                            Log.e(TAG,"size of cursor = " + cursor.getCount());

                            String strAddress = locCursor.getString(ProfileContract.COL_LOCATION_ADDRESS);
                            double lat = locCursor.getLong(ProfileContract.COL_LOCATION_LATITUDE);
                            double lng = locCursor.getLong(ProfileContract.COL_LOCATION_LONGITUDE);
                            String city = locCursor.getString(ProfileContract.COL_LOCATION_CITY);
                            Float radius = locCursor.getFloat(ProfileContract.COL_LOCATION_RADIUS);
                            Log.e(TAG,"Address = " + strAddress);
                            Log.e(TAG,"Lat, Lng = " + lat + ","+lng);
                            Log.e(TAG,"City: " + city);
                            Log.e(TAG, "Radius: " + radius);

                                    profile.setLocation(new GeoFenceLocation(locCursor));
                            Log.e(TAG, "Double check address ");
                            Log.e(TAG,"Address = " + profile.getLocation().getAddress());
                            profileList.add(profile);
                        }
                    }
            }


        }

        if (getActivity() != null && listview != null && profileList != null) {

            //Store global copy
            mProfileList = profileList;
            if (mProfileType == LOCATION_PROFILE_LIST)
                mProfileAdapter = new LocationProfileListAdapter(getActivity(), profileList, this);
            else
                mProfileAdapter = new ProfileListAdapter(getActivity(), profileList);

            listview.setAdapter(mProfileAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void notifyListViewChanged() {
        mProfileAdapter.notifyDataSetChanged();
        ((Callback) getActivity()).onListViewChange();
    }

    private void updateGeofences() {
        if (mGoogleApiClient.isConnected()) {

            GeofenceManager geofenceManager = new GeofenceManager(getActivity().getApplicationContext(), mGoogleApiClient);

            //Restart geofences now that the state has changed.
            geofenceManager.startGeofences(this);
        } else
            Log.e(TAG, "Connection to Google API is disconnected! Not good!");
    }

    @Override
    public void onToggleLocationIcon() {
        updateGeofences();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(Status status) {

    }

    /*******************************************************************/
	/*                        Public Methods                           */

    /*******************************************************************/
    public interface Callback {

        void onListViewChange();

        void onItemSelected(Profile profile, int profileType, TextView textView);
    }
}
