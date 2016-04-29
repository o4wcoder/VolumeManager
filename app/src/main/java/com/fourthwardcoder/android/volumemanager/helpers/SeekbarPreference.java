package com.fourthwardcoder.android.volumemanager.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
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

    /*********************************************************************************/
    /*                               Constants                                       */
    /*********************************************************************************/
    private static final String TAG = SeekbarPreference.class.getSimpleName();

    /*********************************************************************************/
    /*                               Local Data                                      */
    /*********************************************************************************/
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

        super.onBindView(view);

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

        //Just restore the value if we've gotten initially and just need to update it with the
        //progress of the seekbar
        if (restoreValue) {
            setValue(getPersistedInt(mProgress));
        } else {

            //Start/Enter volume will be set to off or vibrate by default. Ring volume will be 0
            if(getKey().equals(getContext().getString(R.string.pref_default_start_ring_volume_setting_key)))  {
                setValue(0);
            }
            else if (getKey().equals(getContext().getString(R.string.pref_default_end_ring_volume_setting_key))) {

                //Get current phone ring volume if this is the end/exit control
                int currentRingVolume = Util.getRingVolume(getContext());

                //
                if (currentRingVolume > 0)
                    setValue(currentRingVolume);
                else
                    setValue((Integer) defaultValue);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {

        try {
            int defValue = Integer.parseInt(getContext().getString(R.string.pref_default_ring_volume_default));
            return a.getInteger(index, defValue);
        } catch (UnsupportedOperationException e) {
            //Something went wrong with the typedarray. Just return the default value defined in string.xml
            return Integer.parseInt(getContext().getString(R.string.pref_default_ring_volume_default));
        }
    }

    private void setValue(int value) {
        if (shouldPersist()) {
            persistInt(value);
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
        ListPreference listPreference = null;

        if (shouldPersist()) {

            //Set start volume seekbar
            if(getKey().equals(getContext().getString(R.string.pref_default_start_ring_volume_setting_key))) {

                if(progress > 0) {

                    editor.putString(getContext().getString(R.string.pref_default_start_volume_type_setting_key),
                            getContext().getString(R.string.pref_default_ring_type_key_ring)).apply();
                    listPreference = (ListPreference)findPreferenceInHierarchy(getContext().getString(R.string.pref_default_start_volume_type_setting_key));

                }
            }
            //Set end volume seekbar
            if (getKey().equals(getContext().getString(R.string.pref_default_end_ring_volume_setting_key))) {

                if(progress > 0) {

                    editor.putString(getContext().getString(R.string.pref_default_end_volume_type_setting_key),
                            getContext().getString(R.string.pref_default_ring_type_key_ring)).apply();
                    listPreference = (ListPreference)findPreferenceInHierarchy(getContext().getString(R.string.pref_default_end_volume_type_setting_key));
                }
            }

            //Update Volume type to ring if we've changed the ringer volume seekbar
            if(listPreference != null) {
                listPreference.setSummary(listPreference.getEntries()[Constants.VOLUME_RING]);
                listPreference.setValue(listPreference.getEntryValues()[Constants.VOLUME_RING].toString());
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

    /**
     * change the volume icon with regards to the volume type (off/vibrat/ring)
     * @param value volume type
     */
    public void updateVolumeIcon(String value) {

     //   Log.e(TAG, "updateVolumeIcon() with ring type = " + value);


        if(value.equals(getContext().getString(R.string.pref_default_ring_type_key_off))) {
         //   Log.e(TAG, "change to OFF icon");
            updateSeekBarPosition(0);
            mImageView.setImageResource(R.drawable.ic_volume_off);

        }
        else if(value.equals(getContext().getString(R.string.pref_default_ring_type_key_vibrate))) {
         //   Log.e(TAG, "change to VIBRATE icon");
            updateSeekBarPosition(0);
            mImageView.setImageResource(R.drawable.ic_vibration);

        }
        else if(value.equals(getContext().getString(R.string.pref_default_ring_type_key_ring))) {
          //  Log.e(TAG,"change to RING icon");
            updateSeekBarPosition(Integer.parseInt(getContext().getString(R.string.pref_default_ring_volume_default)));
            mImageView.setImageResource(R.drawable.ic_volume_up);

        }


    }

    /**
     * update this seekbars position
     * @param position position to change to
     */
    public void updateSeekBarPosition(int position) {
        Util.setSeekBarPosition(mSeekBar,mRingVolumeTextView,position,Util.getMaxRingVolume(getContext()));
    }
}
