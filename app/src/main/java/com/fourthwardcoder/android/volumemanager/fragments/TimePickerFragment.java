package com.fourthwardcoder.android.volumemanager.fragments;


import java.util.Calendar;
import java.util.Date;


import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import android.widget.TimePicker;
import android.text.format.DateFormat;

import com.fourthwardcoder.android.volumemanager.interfaces.Constants;

/**
 * TimePickerFragment
 * <p/>
 * DialogFragment that contains a TimePicker user to select the time to fire
 * the volume control alarms
 *
 * @author Chris Hare
 *         3/13/15
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener, Constants {

    /********************************************************************/
    /*                         Constants                                */
    /********************************************************************/
    public static final String EXTRA_TIME = "com.fourthwardcoder.android.volumemanager.time";
    public static final String EXTRA_TITLE = "com.fourthwardcoder.android.volumemanager.title";
    //public static final String EXTRA_TYPE = "com.fourthwardcoder.android.volumemanager.type";
    public static final String TAG = TimePickerFragment.class.getSimpleName();

    /********************************************************************/
	/*                          Local Data                              */
    /********************************************************************/
    private Date mTime;
    private String mDialogTitle;
    private int mDateType;

    public static TimePickerFragment newInstance(Date date, String dialogTitle) {

        Log.d(TAG, "Inside new Instance");
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TIME, date);
        args.putString(EXTRA_TITLE, dialogTitle);
        //args.putInt(EXTRA_TYPE, dateType);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    /************************************************************/
	/*                  Override Methods                        */

    /************************************************************/

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mTime = (Date) getArguments().getSerializable(EXTRA_TIME);
        mDialogTitle = (String) getArguments().getString(EXTRA_TITLE);

        //Create a Calendar to get the time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mTime);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, min,
               DateFormat.is24HourFormat(getActivity()));

    }

    /***************************************************************/
	/*                   Private Methods                           */
    /***************************************************************/
    /**
     * Sends the result of the dialog back to calling fragment
     *
     * @param resultCode code of result of dialog. In this case "OK"
     */
    private void sendResult(int resultCode) {
        if (getTargetFragment() == null)
            return;

        //Create intent and put extra on it
        Intent i = new Intent();
        i.putExtra(EXTRA_TIME, mTime);

        //Send result to VolumeManager Fragment
        //Request code to tell the target who is returning the result
        //result code to determine what action to take
        //An intent that can have extra data
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }


    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {

        Log.e(TAG, "Time set with hour " + hour);
        //retrieving the original date from the mTime value with a calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mTime);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        //Translating hourOfDay & minute into a Date object using a calendar, date keeps
        //the same
        mTime = calendar.getTime();
        Log.e(TAG, "Hour in data object " + mTime.getHours());

        //Update arguments to preserve selected value on rotation
        getArguments().putSerializable(EXTRA_TIME, mTime);
        sendResult(Activity.RESULT_OK);
    }
}
