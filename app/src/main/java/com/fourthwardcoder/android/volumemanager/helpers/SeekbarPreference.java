package com.fourthwardcoder.android.volumemanager.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;

import org.w3c.dom.Text;

/**
 * Created by Chris Hare on 4/7/2016.
 */
public class SeekbarPreference extends Preference implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = SeekbarPreference.class.getSimpleName();

    private SeekBar mSeekBar;
    private int mProgress;
    private TextView mRingVolumeTextView;
    private ImageView mImageView;

    public SeekbarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setLayoutResource(R.layout.default_ring_volume_setting);

    }

    @Override
    protected void onBindView(View view) {

        //Get textview that displays the current ring volume
        mRingVolumeTextView = (TextView) view.findViewById(R.id.ring_volume_textview);
        mImageView = (ImageView)view.findViewById(R.id.volumeIcon);

        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        mSeekBar.setProgress(mProgress);
        mSeekBar.setOnSeekBarChangeListener(this);

        //Set the max of the seekbar based on phones top volume
        mSeekBar.setMax(Util.getMaxRingVolume(getContext()));

    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

        Log.e(TAG, "onSetInitialValue with restore value = " + restoreValue + ". Default value = " + defaultValue);

        if (restoreValue) {
            setValue(getPersistedInt(mProgress));
        } else {
            Log.e(TAG, "No value, set from current ring volume");
            //Not currently set. Get current phone ring volume if this is the end/exit control
            if (getKey().equals(R.string.pref_default_end_volume_type_setting_key))
                setValue(Util.getRingVolume(getContext()));
        }
    }

    private void setValue(int value) {
        if (shouldPersist()) {
            //  int currentPhoneVolume = Util.getRingVolume(getContext());

            //  if(value == currentPhoneVolume)
            persistInt(value);
            //  else {
            //       persistInt(currentPhoneVolume);
            //        value = currentPhoneVolume;
            //    }
        }

        if (value != mProgress) {
            mProgress = value;
            notifyChanged();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

      //  Log.e(TAG, "onProgressChanged with fromUser=" + fromUser);
        Util.setRingVolumeText(mRingVolumeTextView, progress, Util.getMaxRingVolume(getContext()));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = prefs.edit();

        if (shouldPersist()) {

            if(getKey().equals(getContext().getString(R.string.pref_default_start_ring_volume_setting_key))) {
                Log.e(TAG,"onProgressChanged(): Got start volume seekbar");
                if(progress > 0) {
                    Log.e(TAG,"onProgressChanged(): Have progress > 0 updating type to Ring");
                    editor.putString(getContext().getString(R.string.pref_default_start_volume_type_setting_key),
                            getContext().getString(R.string.pref_default_ring_type_key_ring)).apply();
                    ListPreference listPreference = (ListPreference)findPreferenceInHierarchy(getContext().getString(R.string.pref_default_start_volume_type_setting_key));
                   // Log.e(TAG,"Setting list pref to " + listPreference.getEntries()[Constants.VOLUME_RING]);
                    listPreference.setSummary(listPreference.getEntries()[Constants.VOLUME_RING]);
                    listPreference.setValue(listPreference.getEntryValues()[Constants.VOLUME_RING].toString());


                }
            }
            if (getKey().equals(getContext().getString(R.string.pref_default_end_ring_volume_setting_key))) {
                Log.e(TAG,"Got end volume seekbar");
                Util.setRingVolume(getContext(), progress, fromUser);
            }

            setValue(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /**************************************************************************************/
    /*                               Public Methods                                       */
    /**************************************************************************************/
    public void updateVolumeIcon(String value) {

        Log.e(TAG,"updateVolumeIcon() with ring type = " + value);


        if(value.equals(getContext().getString(R.string.pref_default_ring_type_key_off))) {
            Log.e(TAG,"change to OFF icon");
            mImageView.setImageResource(R.drawable.ic_volume_off);
            updateSeekBarPosition(0);
        }
        else if(value.equals(getContext().getString(R.string.pref_default_ring_type_key_vibrate))) {
            Log.e(TAG,"change to VIBRATE icon");
            mImageView.setImageResource(R.drawable.ic_vibration);
            updateSeekBarPosition(0);
        }
        else if(value.equals(getContext().getString(R.string.pref_default_ring_type_key_ring))) {
            Log.e(TAG,"change to RING icon");
            mImageView.setImageResource(R.drawable.ic_volume_up);
            updateSeekBarPosition(Integer.parseInt(getContext().getString(R.string.pref_default_ring_volume_default)));
        }

    }

    public void updateSeekBarPosition(int position) {
        Util.setSeekBarPosition(mSeekBar,mRingVolumeTextView,position,Util.getMaxRingVolume(getContext()));
    }
}
