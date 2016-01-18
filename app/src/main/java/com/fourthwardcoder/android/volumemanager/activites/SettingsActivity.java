package com.fourthwardcoder.android.volumemanager.activites;

import android.support.v4.app.Fragment;

import com.fourthwardcoder.android.volumemanager.fragments.SettingsFragment;

public class SettingsActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return new SettingsFragment();
	}

}
