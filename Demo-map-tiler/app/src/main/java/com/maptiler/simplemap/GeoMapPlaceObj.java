/*
 * = COPYRIGHT
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date				Author				 Action
 * 20240902			liujian				Create
 */

package com.maptiler.simplemap;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * nominatim.org
 * https://geocode.maps.co/reverse?lat=30.42500162664262&lon=114.42021113115743&api_key=66d196e166266586258252klbf15ac1
 */
public class GeoMapPlaceObj {
    @SerializedName("place_id")
    private int mPlaceId;
    @SerializedName("licence")
    private String mLicence;
    @SerializedName("osm_type")
    private String mOsmType;
    @SerializedName("osm_id")
    private int mOsmId;
    @SerializedName("lat")
    private String mLat;
    @SerializedName("lon")
    private String mLon;
    // 沿湖社区, 藏龙岛办事处, 江夏区, 武汉市, 湖北省, 中国
    @SerializedName("display_name")
    private String mDisplayName;
    @SerializedName("address")
    private Address mAddress;
    @SerializedName("boundingBox")
    private List<String> mBoundingBox;

    public int getPlaceId() {
        return mPlaceId;
    }

    public void setPlaceId(int placeId) {
        mPlaceId = placeId;
    }

    public String getLicence() {
        return mLicence;
    }

    public void setLicence(String licence) {
        mLicence = licence;
    }

    public String getOsmType() {
        return mOsmType;
    }

    public void setOsmType(String osmType) {
        mOsmType = osmType;
    }

    public int getOsmId() {
        return mOsmId;
    }

    public void setOsmId(int osmId) {
        mOsmId = osmId;
    }

    public String getLat() {
        return mLat;
    }

    public void setLat(String lat) {
        mLat = lat;
    }

    public String getLon() {
        return mLon;
    }

    public void setLon(String lon) {
        mLon = lon;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public Address getAddress() {
        return mAddress;
    }

    public void setAddress(Address address) {
        mAddress = address;
    }

    public List<String> getBoundingBox() {
        return mBoundingBox;
    }

    public void setBoundingBox(List<String> boundingBox) {
        mBoundingBox = boundingBox;
    }

    public static class Address {
        @SerializedName("quarter")
        private String mQuarter;
        @SerializedName("suburb")
        private String mSuburb;
        @SerializedName("city")
        private String mCity;
        @SerializedName("state")
        private String mState;
        @SerializedName("ISO3166-2-lvl4")
        private String city_district;
        @SerializedName("country")
        private String mCountry;
        @SerializedName("country_code")
        private String mCountryCode;

        public String getQuarter() {
            return mQuarter;
        }

        public void setQuarter(String quarter) {
            mQuarter = quarter;
        }

        public String getSuburb() {
            return mSuburb;
        }

        public void setSuburb(String suburb) {
            mSuburb = suburb;
        }

        public String getCity() {
            return mCity;
        }

        public void setCity(String city) {
            mCity = city;
        }

        public String getState() {
            return mState;
        }

        public void setState(String state) {
            mState = state;
        }

        public String getCity_district() {
            return city_district;
        }

        public void setCity_district(String city_district) {
            this.city_district = city_district;
        }

        public String getCountry() {
            return mCountry;
        }

        public void setCountry(String country) {
            mCountry = country;
        }

        public String getCountryCode() {
            return mCountryCode;
        }

        public void setCountryCode(String countryCode) {
            mCountryCode = countryCode;
        }
    }
}
