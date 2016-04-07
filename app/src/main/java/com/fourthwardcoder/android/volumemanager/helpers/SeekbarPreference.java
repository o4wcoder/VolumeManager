package com.fourthwardcoder.android.volumemanager.helpers;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fourthwardcoder.android.volumemanager.R;

import org.w3c.dom.Text;

/**
 * Created by Chris Hare on 4/7/2016.
 */
public class SeekbarPreference extends Preference implements SeekBar.OnSeekBarChangeListener{

    private static final String TAG = SeekbarPreference.class.getSimpleName();

    private SeekBar mSeekBar;
    private int mProgress;
    private TextView mRingVolumeTextView;

    public SeekbarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setLayoutResource(R.layout.default_ring_volume_setting);
    }

    @Override
    protected void onBindView(View view) {

        //Get textview that displays the current ring volume
        mRingVolumeTextView = (TextView)view.findViewById(R.id.ring_volume_textview);

        mSeekBar = (SeekBar)view.findViewById(R.id.seekbar);
        mSeekBar.setProgress(mProgress);
        mSeekBar.setOnSeekBarChangeListener(this);

        //Set the max of the seekbar based on phones top volume
        mSeekBar.setMax(Util.getMaxRingVolume(getContext()));

    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

        Log.e(TAG, "onSetInitialValue with restore value = " + restoreValue + ". Default value = " + defaultValue);

        if(restoreValue) {
            setValue(getPersistedInt(mProgress));
        }
        else {
            Log.e(TAG,"No value, set from current ring volume");
            //Not currently set. Get current phone ring volume
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

        Log.e(TAG,"onProgressChanged with fromUser=" + fromUser);
        Util.setRingVolumeText(mRingVolumeTextView, progress, Util.getMaxRingVolume(getContext()));

        if(shouldPersist()) {
            persistInt(progress);
            Util.setRingVolume(getContext(), progress, fromUser);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
