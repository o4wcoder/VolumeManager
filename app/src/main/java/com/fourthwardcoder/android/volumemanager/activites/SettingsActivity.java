package com.fourthwardcoder.android.volumemanager.activites;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.helpers.SeekbarPreference;

/**
 * Preference Settings Activity
 * <p/>
 * All preference settings for the app set in the Settings Menu
 * <p/>
 * Created: 1/17/16
 *
 * @author Chris Hare
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    /***************************************************************************************/
    /*                                   Constants                                         */
    /***************************************************************************************/
    private static final String TAG = SettingsActivity.class.getSimpleName();

    /***************************************************************************************/
    /*                                  Local Data                                         */
    /***************************************************************************************/
    SeekbarPreference mStartSeekBarPreference;
    SeekbarPreference mEndSeekBarPreference;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings_pref);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_default_start_volume_type_setting_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_default_end_volume_type_setting_key)));

        mStartSeekBarPreference = (SeekbarPreference) findPreference(getString(R.string.pref_default_start_ring_volume_setting_key));
        mEndSeekBarPreference = (SeekbarPreference) findPreference(getString(R.string.pref_default_end_ring_volume_setting_key));



    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Set the preference summaries
        setPreferenceSummary(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * Set the summary to be displayed on the preference field
     *
     * @param preference preference to set
     * @param value      object that contains the summary
     */
    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();

        Log.e(TAG, "setPreferenceSummary with pref = " + preference.getTitle());
        if (preference instanceof ListPreference) {
            Log.e(TAG, "got listpref with value = " + stringValue);
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                Log.e(TAG, "set summary for list");
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }

    }

     //Registers a shared preference change listener that gets notified when preferences change
    @Override
    protected void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    // Unregisters a shared preference change listener
    @Override
    protected void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        setPreferenceSummary(preference, newValue);
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


       if (key.equals(getString(R.string.pref_default_start_volume_type_setting_key))) {
           Log.e(TAG,"onSharedPreferenceChanged() Start volume type changed");
           String strVolumeType = sharedPreferences.getString(getString(R.string.pref_default_start_volume_type_setting_key),
                   getString(R.string.pref_default_start_volume_type_setting_key));
           mStartSeekBarPreference.updateVolumeIcon(strVolumeType);
       }
       else if(key.equals(getString(R.string.pref_default_end_volume_type_setting_key))) {
           Log.e(TAG,"onSharedPreferenceChanged() End volume type changed");
       }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
