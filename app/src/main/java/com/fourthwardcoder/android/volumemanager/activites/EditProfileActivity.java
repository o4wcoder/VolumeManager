package com.fourthwardcoder.android.volumemanager.activites;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.fragments.EditProfileFragment;
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
public class EditProfileActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Change status bar color
		Util.setStatusBarColor(this);

		setContentView(R.layout.activity_profile);
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
}
