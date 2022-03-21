package com.example.demoh5jsnativecomm.jsinterface;

public class NativeAPILocationResult {
    @NativeAPIResultCode
    private int mResultCode;
    private Location mLocation;

    public NativeAPILocationResult(int resultCode, Location location) {
        mResultCode = resultCode;
        mLocation = location;
    }

    public int getResultCode() {
        return mResultCode;
    }

    public void setResultCode(int resultCode) {
        mResultCode = resultCode;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public static class Location {
        private double mLon;
        private double mLat;
        private double altitude;

        public Location(double lon, double lat, double altitude) {
            mLon = lon;
            mLat = lat;
            this.altitude = altitude;
        }

        public double getLon() {
            return mLon;
        }

        public void setLon(double lon) {
            mLon = lon;
        }

        public double getLat() {
            return mLat;
        }

        public void setLat(double lat) {
            mLat = lat;
        }

        public double getAltitude() {
            return altitude;
        }

        public void setAltitude(double altitude) {
            this.altitude = altitude;
        }
    }


}
