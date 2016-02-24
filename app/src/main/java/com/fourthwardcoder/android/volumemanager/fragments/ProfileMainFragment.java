package com.fourthwardcoder.android.volumemanager.fragments;

import java.util.ArrayList;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.Profile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;


public class ProfileMainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        LocationProfileListAdapter.LocationAdapterCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>, Constants {

    /***************************************************/
    /*                  Constants                      */
    /***************************************************/
    private static final String TAG = "ProfileMainFragment";

    //ID for Profile Loader
    private static final int PROFILE_LOADER = 0;


    /***************************************************/
    /*                 Local Data                      */
    /***************************************************/
    private ArrayList<Profile> mProfileList;
    private boolean mFirstRun = true;
    //ProfileListAdapter mProfileAdapter;
    BaseAdapter mProfileAdapter;
    ListView listview;
    int mProfileType;
    GoogleApiClient mGoogleApiClient;
    GeofenceManager mGeofenceManager;
    /***************************************************/
	/*                Override Methods                 */

    /***************************************************/

    public static ProfileMainFragment newInstance(int profileType) {

        Bundle args = new Bundle();
        args.putInt(EXTRA_PROFILE_TYPE, profileType);
        ProfileMainFragment fragment = new ProfileMainFragment();

        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate()");
        setHasOptionsMenu(true);

        //retain the instance on rotation
        setRetainInstance(true);

        if (savedInstanceState != null) {
            mProfileType = savedInstanceState.getInt(EXTRA_PROFILE_TYPE);
        } else {
            Bundle bundle = getArguments();
            mProfileType = bundle.getInt(EXTRA_PROFILE_TYPE);
        }

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG,"onActivityCreated() with mFristRun = " + mFirstRun);

        //Init the Profile Loader. Callbacks received in this fragment
        //If this is first time running, initialize the loader. Otherwise
        //just restart it. This is necessary because the loader won't run
        //again on rotation.
        if(mFirstRun)
           getLoaderManager().initLoader(PROFILE_LOADER, null, this);
        else
            getLoaderManager().restartLoader(PROFILE_LOADER,null,this);

        //Need to set the details view to the first movie when in 2 pane mode. Hit this
        //situation when we first come up on a tablet in portrait and rotate to landscape
//        if(mProfileList != null) {
//            if (mProfileList.size() > 0) {
//                Log.e(TAG, "onActivityCreated(): Calling on loadfinished");
//                ((Callback) getActivity()).onLoadFinished(mProfileList.get(0));
//            }
//        }
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
        TextView headerTextView = (TextView) headerView.findViewById(R.id.profileHeaderTextView);

        if (mProfileType == TIME_PROFILE_LIST)
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

                            ArrayList<String> requestIdList = new ArrayList<>();
                            //Delete selected profiles
                            for (int i = listview.getCount() - 1; i > 0; i--) {
                                if (listview.isItemChecked(i)) {

                                    if(mProfileType == LOCATION_PROFILE_LIST) {
                                        requestIdList.add(((Profile)mProfileAdapter.getItem(i - 1)).getId().toString());
                                    }

                                    ProfileManager.deleteProfile(getActivity(), (Profile) mProfileAdapter.getItem(i - 1));
                                }
                            }

                            //Delete geofences in list
                            if(mProfileType == LOCATION_PROFILE_LIST) {
                                if(requestIdList.size() > 0) {
                                    deleteGeofenceList(requestIdList);
                                }

                            }
                            //Destroy Action mode context menu
                            mode.finish();

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
                    Log.d(TAG, "Selected list item at position " + position);

                }


            });
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(EXTRA_PROFILE_TYPE, mProfileType);
        //savedInstanceState.putSerializable(EXTRA_PROFILE_LIST,mProfileList);
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

        Profile profile = (Profile) listview.getItemAtPosition(position);
        Log.e(TAG, "Deleting profile " + profile.getTitle());

        switch (item.getItemId()) {
            case R.id.menu_item_delete_profile:

                if(mProfileType == LOCATION_PROFILE_LIST) {
                   deleteGeofence(profile.getId().toString());
                }
                //Delete the profile from the DB
                ProfileManager.deleteProfile(getActivity(), profile);
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

        mFirstRun = false;
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

            if (mProfileType == TIME_PROFILE_LIST) {

                if (profile.getLocationKey() == 0)
                    profileList.add(profile);

            } else {
                //Get location profiles
                if (profile.getLocationKey() > 0) {

                    //Add location table to profile object
                    GeoFenceLocation location = ProfileManager.getLocation(getActivity(), profile.getLocationKey());

                    if (location != null) {

                        profile.setLocation(location);
                        profileList.add(profile);
                    }
                }
            }


        }

        if(mProfileType == TIME_PROFILE_LIST)
            Log.e(TAG, "onLoadFinished: Number of profiles = " + profileList.size());

        if (getActivity() != null && listview != null && profileList != null) {
            Log.e(TAG,"onLoadFinished: Set adapter");
            //Store global copy
            mProfileList = profileList;
            if (mProfileType == LOCATION_PROFILE_LIST)
                mProfileAdapter = new LocationProfileListAdapter(getActivity(), profileList, this);
            else
                mProfileAdapter = new ProfileListAdapter(getActivity(), profileList);

            listview.setAdapter(mProfileAdapter);

            //If in 2 pane mode, set first profile in list to detail pane
            if(profileList.size() > 0 )
                ((Callback)getActivity()).onLoadFinished(profileList.get(0));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void notifyListViewChanged() {
        mProfileAdapter.notifyDataSetChanged();
        ((Callback) getActivity()).onListViewChange();
    }

    private void deleteGeofence(String requestId) {
        if(mGoogleApiClient.isConnected()) {

            if(mGeofenceManager != null) {

                mGeofenceManager.removeGeofence(this,requestId);
            }
        }
    }

    private void deleteGeofenceList(ArrayList<String> list) {
        if(mGoogleApiClient.isConnected()) {

            if(mGeofenceManager != null) {

                mGeofenceManager.removeGeofenceList(this, list);
            }
        }
    }

    private void updateGeofences() {
            if (mGoogleApiClient.isConnected()) {
                //Restart geofences now that the state has changed.
                mGeofenceManager.startGeofences(this);
            } else
                Log.e(TAG, "Connection to Google API is disconnected! Not good!");
    }

    @Override
    public void onToggleLocationIcon() {
        updateGeofences();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mGeofenceManager = new GeofenceManager(getActivity().getApplicationContext(), mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(Status status) {

        GeofenceManager.setGeofenceResult(getActivity(),status,getString(R.string.geofence_deleted));
    }

    /*******************************************************************/
	/*                        Public Methods                           */

    /*******************************************************************/
    public interface Callback {

        void onListViewChange();

        void onItemSelected(Profile profile, int profileType, TextView textView);

        void onLoadFinished(Profile profile);
    }
}
