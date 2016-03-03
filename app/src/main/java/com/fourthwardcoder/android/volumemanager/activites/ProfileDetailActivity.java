package com.fourthwardcoder.android.volumemanager.activites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.fragments.ProfileDetailFragment;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.GeoFenceLocation;
import com.fourthwardcoder.android.volumemanager.models.Profile;

/**
 * Class VolumeManagerActivity
 * Author: Chris Hare
 * Created: 3/13/2015
 *
 * Activity for holding the Fragment of the Profile's details
 *
 */
public class ProfileDetailActivity extends AppCompatActivity implements Constants {

    private final String TAG = ProfileDetailActivity.class.getSimpleName();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Change status bar color
		Util.setStatusBarColor(this);
        Log.e(TAG, "onCreate()");
		setContentView(R.layout.activity_profile_detail);

        //If sent data here means we are in portrait mode. Pass along to fragment
        if(savedInstanceState == null) {

             Log.e(TAG,"Check for extras");
            //Get profile type
            if(getIntent().getExtras().containsKey(EXTRA_PROFILE_TYPE)) {
                Log.e(TAG, "Got extras. Put into arguments");

                Bundle arguments = new Bundle();
                int profileType = getIntent().getIntExtra(EXTRA_PROFILE_TYPE, 0);
                arguments.putInt(EXTRA_PROFILE_TYPE,profileType);

                if(getIntent().getExtras().containsKey(EXTRA_PROFILE)) {
                    Profile profile = getIntent().getParcelableExtra(EXTRA_PROFILE);
                    arguments.putParcelable(EXTRA_PROFILE, profile);
                }

                ProfileDetailFragment fragment = new ProfileDetailFragment();
                fragment.setArguments(arguments);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.profile_detail_container, fragment)
                        .commit();

                // Being here means we are in animation mode
                //supportPostponeEnterTransition();
            }


        }
	}

}
