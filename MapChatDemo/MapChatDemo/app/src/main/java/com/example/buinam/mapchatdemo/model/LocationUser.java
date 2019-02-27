package com.example.buinam.mapchatdemo.model;

/**
 * Created by buinam on 12/6/16.
 */

public class LocationUser {
    private String longitudeUser;
    private String latitudeUser;
    private String addressUser;

    public LocationUser() {
    }

    public String getAddressUser() {
        return addressUser;
    }

    public void setAddressUser(String addressUser) {
        this.addressUser = addressUser;
    }

    public String getLatitudeUser() {
        return latitudeUser;
    }

    public void setLatitudeUser(String latitudeUser) {
        this.latitudeUser = latitudeUser;
    }

    public String getLongitudeUser() {
        return longitudeUser;
    }

    public void setLongitudeUser(String longitudeUser) {
        this.longitudeUser = longitudeUser;
    }
}
