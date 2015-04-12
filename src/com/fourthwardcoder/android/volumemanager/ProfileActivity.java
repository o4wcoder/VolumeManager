package com.fourthwardcoder.android.volumemanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

/**
 * VolumeManagerActivity
 * 
 * Activity for setting up a volume control
 * 
 * @author Chris Hare
 * 3/13/2015
 *
 */
public class ProfileActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return new ProfileFragment();
	}
}
