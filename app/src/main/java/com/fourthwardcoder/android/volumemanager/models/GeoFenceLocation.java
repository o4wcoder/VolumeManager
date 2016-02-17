package com.fourthwardcoder.android.volumemanager.models;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

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
