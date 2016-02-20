package com.fourthwardcoder.android.volumemanager.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

import com.fourthwardcoder.android.volumemanager.data.ProfileContract;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Chris Hare on 2/6/2016.
 */
public class GeoFenceLocation implements Constants, Parcelable {

    private LatLng latLng;
    private String address;
    private String city;
    private float fenceRadius;

    public GeoFenceLocation(LatLng latLng) {

        this.latLng = latLng;
        fenceRadius = GEOFENCE_RADIUS_DEFAULT;
    }

    public GeoFenceLocation(Cursor cursor) {

        double lat = cursor.getDouble(ProfileContract.COL_LOCATION_LATITUDE);
        double lng = cursor.getDouble(ProfileContract.COL_LOCATION_LONGITUDE);
        this.latLng = new LatLng(lat,lng);
        this.address = cursor.getString(ProfileContract.COL_LOCATION_ADDRESS);
        this.city = cursor.getString(ProfileContract.COL_LOCATION_CITY);
        this.fenceRadius = cursor.getFloat(ProfileContract.COL_LOCATION_RADIUS);

    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public float getFenceRadius() {
        return fenceRadius;
    }

    public void setFenceRadius(float fenceRadius) {
        this.fenceRadius = fenceRadius;
    }

    public String getFullAddress() {
        return this.address + " " + this.city;
    }

    @Override
    public String toString() {

        return "Location: " + this.latLng + "\n" +
                "Address: " + this.address + "\n" +
                "City:    " + this.city + "\n" +
                "Radius:  " + this.fenceRadius;

    }

    public ContentValues getContentValues() {

        ContentValues profileValues = new ContentValues();
        profileValues.put(ProfileContract.LocationEntry.COLUMN_LONGITUDE,this.latLng.longitude);
        profileValues.put(ProfileContract.LocationEntry.COLUMN_LATITUDE,this.latLng.latitude);
        profileValues.put(ProfileContract.LocationEntry.COLUMN_ADDRESS,this.address);
        profileValues.put(ProfileContract.LocationEntry.COLUMN_CITY,this.city);
        profileValues.put(ProfileContract.LocationEntry.COLUMN_RADIUS,this.fenceRadius);

        return profileValues;
    }
    protected GeoFenceLocation(Parcel in) {
        latLng = (LatLng) in.readValue(LatLng.class.getClassLoader());
        address = in.readString();
        city = in.readString();
        fenceRadius = in.readFloat();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(latLng);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeFloat(fenceRadius);

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GeoFenceLocation> CREATOR = new Parcelable.Creator<GeoFenceLocation>() {
        @Override
        public GeoFenceLocation createFromParcel(Parcel in) {
            return new GeoFenceLocation(in);
        }

        @Override
        public GeoFenceLocation[] newArray(int size) {
            return new GeoFenceLocation[size];
        }
    };
}
