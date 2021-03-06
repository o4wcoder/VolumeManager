package com.fourthwardcoder.android.volumemanager.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.fourthwardcoder.android.volumemanager.data.ProfileContract;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Profile Model
 * <p>
 * Model Object that stores all the data of a volume control profile. This data is stored in the
 * database.
 * <p>
 * Created: 1/17/16
 *
 * @author Chris Hare
 */
public class Profile implements Constants, Parcelable {

    /*****************************************************/
    /*                    Constants                      */
    /*****************************************************/
    private static final String TAG = Profile.class.getSimpleName();
    /*****************************************************/
	/*                   Local Data                      */
    /*****************************************************/
    private UUID id;
    private String title;
    private boolean enabled;
    private int startVolumeType;
    private int endVolumeType;
    private int startRingVolume;
    private int endRingVolume;
    private int previousVolumeType;
    private int previousRingVolume;
    private Date startDate;
    private Date endDate;
    private int alarmId;
    private ArrayList<Boolean> daysOfTheWeek;
    private boolean inAlarm;
    private GeoFenceLocation location;
    private long locationKey;     //key to location data
    private boolean useStartDefault;
    private boolean useEndDefault;

    public Profile() {

        id = UUID.randomUUID();
        enabled = true;
        startVolumeType = VOLUME_OFF;
        endVolumeType = VOLUME_VIBRATE;
        startRingVolume = 0;
        endRingVolume = 3;
        previousVolumeType = VOLUME_VIBRATE;
        previousRingVolume = 1;
        startDate = new Date();
        endDate = new Date();
        inAlarm = false;
        useStartDefault = true;
        useEndDefault = true;


        calculateAlarmId();
        daysOfTheWeek = new ArrayList<>(DAYS_OF_THE_WEEK);
        initDaysOfWeek();
        location = null;
        locationKey = 0;

    }

    public Profile(Cursor cursor) {

        this.id = UUID.fromString(cursor.getString(ProfileContract.COL_PROFILE_ID));
        this.title = cursor.getString(ProfileContract.COL_PROFILE_TITLE);
        this.enabled = (cursor.getInt(ProfileContract.COL_PROFILE_ENABLED)) > 0 ? true : false;
        this.startVolumeType = cursor.getInt(ProfileContract.COL_PROFILE_START_VOLUME_TYPE);
        this.endVolumeType = cursor.getInt(ProfileContract.COL_PROFILE_END_VOLUME_TYPE);
        this.startRingVolume = cursor.getInt(ProfileContract.COL_PROFILE_START_RING_VOLUME);
        this.endRingVolume = cursor.getInt(ProfileContract.COL_PROFILE_END_RING_VOLUME);
        this.previousVolumeType = cursor.getInt(ProfileContract.COL_PROFILE_PREVIOUS_VOLUME_TYPE);
        this.previousRingVolume = cursor.getInt(ProfileContract.COL_PROFILE_PREVIOUS_RING_VOLUME);
        this.alarmId = cursor.getInt(ProfileContract.COL_PROFILE_ALARM_ID);
        this.startDate = new Date(cursor.getLong(ProfileContract.COL_PROFILE_START_DATE));
        this.endDate = new Date(cursor.getLong(ProfileContract.COL_PROFILE_END_DATE));

        Type booleanType = new TypeToken<ArrayList<Boolean>>() {
        }.getType();
        this.daysOfTheWeek = new Gson().fromJson(cursor.getString(ProfileContract.COL_PROFILE_DAYS_OF_THE_WEEK), booleanType);
        this.inAlarm = (cursor.getInt(ProfileContract.COL_PROFILE_IN_ALARM)) > 0 ? true : false;
        this.locationKey = cursor.getLong(ProfileContract.COL_PROFILE_LOCATION_KEY);
        this.useStartDefault = (cursor.getInt(ProfileContract.COL_PROFILE_USE_START_DEFAULT)) > 0 ? true : false;
        this.useEndDefault = (cursor.getInt(ProfileContract.COL_PROFILE_USE_END_DEFAULT)) > 0 ? true : false;
    }

    public ContentValues getContentValues() {

        ContentValues profileValues = new ContentValues();
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_ID, this.id.toString());
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_TITLE, this.title);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_ENABLED, this.enabled);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_START_VOLUME_TYPE, this.startVolumeType);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_END_VOLUME_TYPE, this.endVolumeType);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_START_RING_VOLUME, this.startRingVolume);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_END_RING_VOLUME, this.endRingVolume);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_PREVIOUS_VOLUME_TYPE, this.previousVolumeType);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_PREVIOUS_RING_VOLUME, this.previousRingVolume);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_ALARM_ID, this.alarmId);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_START_DATE, this.startDate.getTime());
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_END_DATE, this.endDate.getTime());
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_DAYS_OF_THE_WEEK, new Gson().toJson(this.daysOfTheWeek));
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_IN_ALARM, this.inAlarm);

        //Put key of the location data
        if (location != null)
            profileValues.put(ProfileContract.ProfileEntry.COLUMN_LOC_KEY, this.locationKey);

        profileValues.put(ProfileContract.ProfileEntry.COLUMN_USE_START_DEFAULT,this.useStartDefault);
        profileValues.put(ProfileContract.ProfileEntry.COLUMN_USE_END_DEFAULT,this.useEndDefault);

        return profileValues;
    }

    @Override
    public String toString() {
        return title;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getStartVolumeType() {
        return startVolumeType;
    }

    public void setStartVolumeType(int startVolumeType) {
        this.startVolumeType = startVolumeType;
    }

    public int getEndVolumeType() {
        return endVolumeType;
    }

    public void setEndVolumeType(int endVolumeType) {
        this.endVolumeType = endVolumeType;
    }

    public int getStartRingVolume() {
        return startRingVolume;
    }

    public void setStartRingVolume(int startRingVolume) {
        this.startRingVolume = startRingVolume;
    }

    public int getEndRingVolume() {
        return endRingVolume;
    }

    public void setEndRingVolume(int endRingVolume) {
        this.endRingVolume = endRingVolume;
    }

    public int getPreviousVolumeType() {
        return previousVolumeType;
    }

    public void setPreviousVolumeType(int previousVolumeType) {
        this.previousVolumeType = previousVolumeType;
    }

    public int getPreviousRingVolume() {
        return previousRingVolume;
    }

    public void setPreviousRingVolume(int previousRingVolume) {
        this.previousRingVolume = previousRingVolume;
    }

    public ArrayList<Boolean> getDaysOfTheWeek() {
        return daysOfTheWeek;
    }

    public void setDaysOfTheWeek(ArrayList<Boolean> daysOfTheWeek) {
        this.daysOfTheWeek = daysOfTheWeek;
    }

    public int getStartAlarmId() {

        return alarmId;
    }

    public int getEndAlarmId() {

        return alarmId + 1;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    public String getDaysOfWeekString() {

        String strDays = "";

        for (int i = 0; i < DAYS_OF_THE_WEEK; i++) {

            if (daysOfTheWeek.get(i)) {
                strDays += daysAbbreviation[i];
                strDays += ",";
            }
        }

        //remove last comma from string
        if (strDays.length() > 0)
            strDays = strDays.substring(0, strDays.length() - 1);

        return strDays;
    }

    public String getDaysOfWeekDebugString() {

        String str;

        str = "Sun = " + daysOfTheWeek.get(0) + "\n" +
                "Mon = " + daysOfTheWeek.get(1) + "\n" +
                "Tue = " + daysOfTheWeek.get(2) + "\n" +
                "Wed = " + daysOfTheWeek.get(3) + "\n" +
                "Thu = " + daysOfTheWeek.get(4) + "\n" +
                "Fri = " + daysOfTheWeek.get(5) + "\n" +
                "Sat = " + daysOfTheWeek.get(6) + "\n";

        return str;
    }

    public boolean isInAlarm() {
        return inAlarm;
    }

    public void setInAlarm(boolean inAlarm) {
        this.inAlarm = inAlarm;
    }

    public GeoFenceLocation getLocation() {
        return location;
    }

    public void setLocation(GeoFenceLocation location) {
        this.location = location;
    }

    public long getLocationKey() {
        return locationKey;
    }

    public void setLocationKey(long locationKey) {
        this.locationKey = locationKey;
    }

    public boolean isLocationProfile() {

        if (location != null)
            return true;
        else
            return false;
    }

    public boolean isUseStartDefault() {
        return useStartDefault;
    }

    public void setUseStartDefault(boolean useStartDefault) {
        this.useStartDefault = useStartDefault;
    }

    public boolean isUseEndDefault() {
        return useEndDefault;
    }

    public void setUseEndDefault(boolean useEndDefault) {
        this.useEndDefault = useEndDefault;
    }
    /*****************************************************/
	/*                   Private Methods                 */

    /*****************************************************/
    private void initDaysOfWeek() {

        for (int i = 0; i < 7; i++)
            daysOfTheWeek.add(i, true);
    }

    private void calculateAlarmId() {

        String str = "" + this.id;
        int uid = str.hashCode();
        String filterStr = "" + uid;
        str = filterStr.replaceAll("-", "");
        alarmId = Integer.parseInt(str);

    }

    protected Profile(Parcel in) {
        id = (UUID) in.readValue(UUID.class.getClassLoader());
        title = in.readString();
        enabled = in.readByte() != 0x00;
        startVolumeType = in.readInt();
        endVolumeType = in.readInt();
        startRingVolume = in.readInt();
        endRingVolume = in.readInt();
        previousVolumeType = in.readInt();
        previousRingVolume = in.readInt();
        long tmpStartDate = in.readLong();
        startDate = tmpStartDate != -1 ? new Date(tmpStartDate) : null;
        long tmpEndDate = in.readLong();
        endDate = tmpEndDate != -1 ? new Date(tmpEndDate) : null;
        alarmId = in.readInt();
        if (in.readByte() == 0x01) {
            daysOfTheWeek = new ArrayList<Boolean>();
            in.readList(daysOfTheWeek, Boolean.class.getClassLoader());
        } else {
            daysOfTheWeek = null;
        }
        inAlarm = in.readByte() != 0x00;
        location = (GeoFenceLocation) in.readValue(GeoFenceLocation.class.getClassLoader());
        locationKey = in.readLong();
        useStartDefault = in.readByte() != 0x00;
        useEndDefault = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeString(title);
        dest.writeByte((byte) (enabled ? 0x01 : 0x00));
        dest.writeInt(startVolumeType);
        dest.writeInt(endVolumeType);
        dest.writeInt(startRingVolume);
        dest.writeInt(endRingVolume);
        dest.writeInt(previousVolumeType);
        dest.writeInt(previousRingVolume);
        dest.writeLong(startDate != null ? startDate.getTime() : -1L);
        dest.writeLong(endDate != null ? endDate.getTime() : -1L);
        dest.writeInt(alarmId);
        if (daysOfTheWeek == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(daysOfTheWeek);
        }
        dest.writeByte((byte) (inAlarm ? 0x01 : 0x00));
        dest.writeValue(location);
        dest.writeLong(locationKey);
        dest.writeByte((byte) (useStartDefault ? 0x01 : 0x00));
        dest.writeByte((byte) (useEndDefault ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Profile> CREATOR = new Parcelable.Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
}