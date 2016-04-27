package com.fourthwardcoder.android.volumemanager.activites;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.fragments.ProfileDetailFragment;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.GeoFenceLocation;
import com.fourthwardcoder.android.volumemanager.models.Profile;

/**
 * Profile Detail Activity
 * <p>
 * Activity for holding the Fragment of the Profile's details. Most changes to a Volume Control
 * profile are made here on the UI.
 * <p>
 * Created: 3/13/2015
 *
 * @author Chris Hare
 */
public class ProfileDetailActivity extends AppCompatActivity implements Constants {

    /*********************************************************************/
    /*                          Constants                                */
    /*********************************************************************/
    private final String TAG = ProfileDetailActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change status bar color
        Util.setStatusBarColor(this);
        Log.e(TAG, "onCreate()");
        setContentView(R.layout.activity_profile_detail);

        //If sent data here means we are in portrait mode. Pass along to fragment
        if (savedInstanceState == null) {

            //Get profile type
            if (getIntent().getExtras().containsKey(EXTRA_PROFILE_TYPE)) {

                Bundle arguments = new Bundle();
                int profileType = getIntent().getIntExtra(EXTRA_PROFILE_TYPE, 0);
                arguments.putInt(EXTRA_PROFILE_TYPE, profileType);

                if (getIntent().getExtras().containsKey(EXTRA_PROFILE)) {
                    Profile profile = getIntent().getParcelableExtra(EXTRA_PROFILE);
                    arguments.putParcelable(EXTRA_PROFILE, profile);
                }

                ProfileDetailFragment fragment = new ProfileDetailFragment();
                fragment.setArguments(arguments);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.profile_detail_container, fragment)
                        .commit();

                // Being here means we are in animation mode
                supportPostponeEnterTransition();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflate menu for main activity toolbar
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Get menu option by it's ID
        switch (item.getItemId()) {

            case R.id.menu_item_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
