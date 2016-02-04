package com.fourthwardcoder.android.volumemanager.activites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.helpers.Util;

/**
 * VolumeManagerActivity
 * 
 * Activity for setting up a volume control
 * 
 * @author Chris Hare
 * 3/13/2015
 *
 */
public class ProfileDetailActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Change status bar color
		Util.setStatusBarColor(this);

		setContentView(R.layout.activity_profile_detail);
	}
}
