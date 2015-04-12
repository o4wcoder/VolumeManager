package com.fourthwardcoder.android.volumemanager;

import android.support.v4.app.Fragment;

public class LocationActivity extends SingleFragmentActivity implements
		Constants {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return new LocationFragment();
	}

}
