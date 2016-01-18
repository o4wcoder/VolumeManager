package com.fourthwardcoder.android.volumemanager.activites;

import android.support.v4.app.Fragment;

import com.fourthwardcoder.android.volumemanager.fragments.LocationFragment;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;

public class LocationActivity extends SingleFragmentActivity implements
		Constants {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return new LocationFragment();
	}

}
