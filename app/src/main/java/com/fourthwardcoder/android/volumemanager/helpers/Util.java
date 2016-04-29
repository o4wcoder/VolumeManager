package com.fourthwardcoder.android.volumemanager.helpers;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.Profile;
import com.google.android.gms.maps.model.LatLng;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Utility Helper
 * <p>
 * Class that defines static helper methods that are used throughout the app
 * <p>
 * Created 2/15/2016
 *
 * @author Chris Hare
 */
public class Util implements Constants {

    /***********************************************************************/
    /*                           Constants                                 */
    /***********************************************************************/
    private final static String TAG = "Util";

    /**
     * Modify the format of the time of the alarms. Changes hour from
     * military time to standard. Also make sure the minute is two digits.
     *
     * @param date Stores the time of the alarm
     */
    public static String formatTime(Context context, Date date) {

        String time;
        int hour;

        //Create a Calendar to get the time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int min = calendar.get(Calendar.MINUTE);

        String strMin = String.valueOf(min);

        //Make sure minute is 2 digits
        if (min < 10)
            strMin = "0" + strMin;

        if (!DateFormat.is24HourFormat(context)) {
            // Log.e(TAG, "Hours in date: " + date.getHours());
            String am_or_pm = (date.getHours() < 12) ? "AM" : "PM";

            hour = calendar.get(Calendar.HOUR);
            String strHour = String.valueOf(hour);

            if (hour == 0)
                strHour = "12";

            time = strHour + ":" + strMin + " " + am_or_pm;
        } else {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            time = String.valueOf(hour) + ":" + strMin;
        }

        return time;
    }

    /**
     * Get Time String used as list item
     *
     * @param context context of calling activity
     * @param profile data to set
     * @return time string
     */
    public static String getFullTimeForListItem(Context context, Profile profile) {

        return Util.formatTime(context, profile.getStartDate()) + " - " + Util.formatTime(context, profile.getEndDate());
    }

    /**
     * Modify the format of the time of the alarms. Changes hour from
     * military time to standard. Also make sure the minute is two digits.
     *
     * @param date Stores the time of the alarm
     */
    public static void setTimeForLargeTextView(Context context, Date date, TextView textView) {

        //Make the AM or PM half the size of the time
        String time = formatTime(context, date);

        if (!DateFormat.is24HourFormat(context)) {
            SpannableString ss1 = new SpannableString(time);
            ss1.setSpan(new RelativeSizeSpan(.5f), time.length() - 3, time.length(), 0);

            textView.setText(ss1);

        } else {
            textView.setText(time);
        }
    }

    /**
     * Converts shared perference string into integer
     * @param str shared pref string
     * @return integer of the volume type
     */
    public static int getIntVolumeType(Context context, String str) {

        if (str.equals(context.getString(R.string.pref_default_ring_type_key_off)))
            return VOLUME_OFF;
        else if (str.equals(context.getString(R.string.pref_default_ring_type_key_vibrate)))
            return VOLUME_VIBRATE;
        else if (str.equals(context.getString(R.string.pref_default_ring_type_key_ring)))
            return VOLUME_RING;
        else
            return -1;
    }

    /**
     * Get the Default start volume type from Shared Preferences
     *
     * @param context context of calling activity
     * @return integer volume type
     */
    public static int getDefaultStartVolumeType(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String strRingType = prefs.getString(context.getString(R.string.pref_default_start_volume_type_setting_key),
                context.getString(R.string.pref_default_ring_type_key_vibrate));

        int ringType = getIntVolumeType(context, strRingType);

        return ringType;
    }

    /**
     * Get the Default end volume type from Shared Preference
     *
     * @param context context of calling activity
     * @return integer volume type
     */
    public static int getDefaultEndVolumeType(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String strRingType = prefs.getString(context.getString(R.string.pref_default_end_volume_type_setting_key),
                context.getString(R.string.pref_default_ring_type_key_vibrate));

        int ringType = getIntVolumeType(context, strRingType);

        return ringType;
    }

    /**
     * Get the Default start ring volume from Shared Preference
     *
     * @param context of calling activity
     * @return integer start ring volume
     */
    public static int getDefaultStartRingVolume(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(context.getString(R.string.pref_default_start_ring_volume_setting_key),
                Integer.parseInt(context.getString(R.string.pref_default_ring_volume_default)));
    }

    /**
     * Get the Default End ring volume from Shared Preference
     * @param context context of calling activity
     * @return integer end ring volume
     */
    public static int getDefaultEndRingVolume(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(context.getString(R.string.pref_default_end_ring_volume_setting_key),
                Integer.parseInt(context.getString(R.string.pref_default_ring_volume_default)));
    }
    /**
     * Set the text on ring volume level
     *
     * @param textView TextView to set the ring volume level
     * @param pos      position of the seekbar
     */
    public static void setRingVolumeText(TextView textView, int pos, int maxVolume) {

        textView.setText("(" + pos + "/" + maxVolume + ")");
    }

    /**
     * Set the visibility of the RadioGroups. This is set by the on/off toggle switch
     *
     * @param radioGroup RadioGroup to set the visibility on
     * @param set        setting of the toggle switch
     */
//    public static void setRadioGroupVisibility(RadioGroup radioGroup, boolean set) {
//
//        //Set visibility on each RadioButton in the Group
//        for (int i = 0; i < radioGroup.getChildCount(); i++)
//            ((RadioButton) radioGroup.getChildAt(i)).setEnabled(set);
//    }

    /**
     * Set the ring volume seek bar at a specific position
     *
     * @param alarmType which alarm (start or end) to set
     * @param pos       seekbar position
     */
    public static void setSeekBarPosition(SeekBar seekBar, TextView textView, int pos, int maxVolume) {

        seekBar.setProgress(pos);
        setRingVolumeText(textView, pos, maxVolume);

    }

    @SuppressLint("NewApi")
    public static void setStatusBarColor(Activity activity) {


//	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//			Window window = activity.getWindow();
//			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			window.setStatusBarColor(activity.getResources().getColor(R.color.app_primary_dark_color));
//
//	    }
    }

    /**
     * Returns an instance of a Value Animator. This is used to animat the icon on the lists item
     *
     * @param context context of calling activity
     * @param image   image to animate
     * @return instance of value animator
     */
    public static ValueAnimator getIconAnimator(Context context, ImageView image) {

        final int startColor = context.getResources().getColor(R.color.app_primary_color);
        final int endColor = context.getResources().getColor(R.color.green_accent_color);

        ValueAnimator valueAnimator = ObjectAnimator.ofInt(image, "colorFilter", startColor, endColor);
        valueAnimator.setDuration(1000);
        valueAnimator.setEvaluator(new ArgbEvaluator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);

        return valueAnimator;
    }

    /**
     * Set the color of the icon on the profile's list item. If a profile's volume control is active,
     * it will display an pulsating animation. If not, it is set to the primary color.
     *
     * @param context context of calling activity
     * @param va      value animator used to animate icon
     * @param image   image to set the color
     * @param inAlarm status of the profile
     */
    public static void setListIconColor(Context context, ValueAnimator va, ImageView image, boolean inAlarm) {

        //if profile is currently active, set the animation
        if (inAlarm) {
            // image.setColorFilter(context.getResources().getColor(R.color.app_in_alarm_color));
            Log.e(TAG, "Got in alarm, starting animation");
            va.start();
        } else {
            //If the profile is not currently active, kill aimation if running and set primary color
            if (va.isRunning())
                va.cancel();

            image.setColorFilter(context.getResources().getColor(R.color.app_primary_color));
        }

    }

    /**
     * Update the widget that data has changed
     *
     * @param context     context of calling app
     * @param profileId   profile id
     * @param profileType type of profile (Time/Location)
     */
    public static void updateWidget(Context context, UUID profileId, int profileType) {

        Intent alarmUpdateIntent = new Intent(ACTION_ALARM_UPDATED)
                .setPackage(context.getPackageName());
        alarmUpdateIntent.putExtra(EXTRA_PROFILE_ID, profileId);
        alarmUpdateIntent.putExtra(EXTRA_PROFILE_TYPE, profileType);
        context.sendBroadcast(alarmUpdateIntent);
    }

    /**
     * Use the Audio Manager to set the ring volume
     *
     * @param context    context of calling app
     * @param ringType   ring type to set
     * @param ringVolume ring volume to set
     */
    public static void setAudioManager(Context context, int ringType, int ringVolume) {


        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        //Log.e(TAG, "Max stream audio is " + audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));

        if (ringType == VOLUME_OFF)
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        else if (ringType == VOLUME_VIBRATE)
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        else {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

            if (ringVolume > 0 && ringVolume <= audioManager.getStreamMaxVolume(AudioManager.STREAM_RING))
                audioManager.setStreamVolume(AudioManager.STREAM_RING, ringVolume, AudioManager.FLAG_PLAY_SOUND);
        }
    }

    /**
     * Get the max ring volume for this device. This can be different depending on the device
     *
     * @param context context of calling app
     * @return return max ring volume
     */
    public static int getMaxRingVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        return audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
    }

    /**
     * Gets the current ring volume of the phone
     * @param context of calling app
     * @return current ring volume
     */
    public static int getRingVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        return audioManager.getStreamVolume(AudioManager.STREAM_RING);


    }

    /**
     * Set the ring volume. It does not change the mode
     * @param context context of calling app
     * @param ringVolume ring volume to set
     */
    public static void setRingVolume(Context context,int ringVolume, boolean fromUser) {

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (ringVolume > 0 && ringVolume <= audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)) {

            int ringerMode = audioManager.getRingerMode();
            if(fromUser)
                audioManager.setStreamVolume(AudioManager.STREAM_RING, ringVolume, AudioManager.FLAG_PLAY_SOUND);
            else
                audioManager.setStreamVolume(AudioManager.STREAM_RING, ringVolume,0);
                    //Reset audio mode. We just want to change the volume settings. Don't change the mode.
                    audioManager.setRingerMode(ringerMode);

        }
    }

    /*****************************************************************************/
    /*                       Location/Maps Helpers                               */
    /*****************************************************************************/

    /**
     * Get the street address of latitude/longitude
     *
     * @param context context of calling app
     * @param latLng  latitude/longitude
     * @return street address of latitude/longitude
     * @throws IOException
     */
    public static Address getStreetAddress(Context context, LatLng latLng) throws IOException {

        //Get Address of current location
        Address currentAddress = null;

        if (Geocoder.isPresent()) {
            Geocoder gcd = new Geocoder(context);

            List<Address> addresses = gcd.getFromLocation(latLng.latitude,
                    latLng.longitude, 1);

            if (addresses.size() > 0)
                currentAddress = addresses.get(0);
        }

        return currentAddress;
    }


}
