package com.fourthwardcoder.android.volumemanager.activites;

import android.support.v4.app.Fragment;

import com.fourthwardcoder.android.volumemanager.fragments.LocationProfileDetailFragment;

public class EditLocationProfileActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return new LocationProfileDetailFragment();
	}

}
