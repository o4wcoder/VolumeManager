package com.fourthwardcoder.android.volumemanager.activites;

import android.support.v4.app.Fragment;

import com.fourthwardcoder.android.volumemanager.fragments.EditProfileFragment;

/**
 * VolumeManagerActivity
 * 
 * Activity for setting up a volume control
 * 
 * @author Chris Hare
 * 3/13/2015
 *
 */
public class EditProfileActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return new EditProfileFragment();
	}
}
