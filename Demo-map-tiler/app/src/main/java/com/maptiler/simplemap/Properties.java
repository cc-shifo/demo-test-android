package com.maptiler.simplemap;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Properties {
    @SerializedName("ref")
    private String ref;
    @SerializedName("country_code")
    private String countryCode;
    // @SerializedName("kind")
    // private String kind;
    // @SerializedName("place_type_name")
    // private List<String> placeTypeName;


    public String getRef(){
        return ref;
    }

    public void setRef(String ref){
        this.ref=ref;
    }

    public String getCountryCode(){
        return countryCode;
    }

    public void setCountryCode(String countryCode){
        this.countryCode=countryCode;
    }

    // public String getKind(){
    //     return kind;
    // }
    //
    // public void setKind(String kind){
    //     this.kind=kind;
    // }
    //
    // public List<String> getPlaceTypeName(){
    //     return placeTypeName;
    // }
    //
    // public void setPlaceTypeName(List<String> placeTypeName){
    //     this.placeTypeName=placeTypeName;
    // }
}
