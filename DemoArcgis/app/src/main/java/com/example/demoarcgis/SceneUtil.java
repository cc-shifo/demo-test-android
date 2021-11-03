package com.example.demoarcgis;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SceneUtil {
    public static class TrackObj implements Parcelable {
        private double mLatitude;
        private double mLongitude;
        private double mHeight;

        public TrackObj(double latitude, double longitude, double height) {
            mLatitude = latitude;
            mLongitude = longitude;
            mHeight = height;
        }

        public double getLatitude() {
            return mLatitude;
        }

        public void setLatitude(double latitude) {
            mLatitude = latitude;
        }

        public double getLongitude() {
            return mLongitude;
        }

        public void setLongitude(double longitude) {
            mLongitude = longitude;
        }

        public double getHeight() {
            return mHeight;
        }

        public void setHeight(double height) {
            mHeight = height;
        }

        private TrackObj(@NonNull Parcel source) {
            mLatitude = source.readDouble();
            mLongitude = source.readDouble();
            mHeight = source.readDouble();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(mLatitude);
            dest.writeDouble(mLongitude);
            dest.writeDouble(mHeight);
        }

        public static final Parcelable.Creator<TrackObj> CREATOR = new Parcelable
                .Creator<TrackObj>() {
            @Override
            public TrackObj createFromParcel(Parcel source) {
                return new TrackObj(source);
            }

            @Override
            public TrackObj[] newArray(int size) {
                return new TrackObj[size];
            }
        };

    }
}
