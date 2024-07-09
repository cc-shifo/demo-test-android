package com.maptiler.simplemap;

import com.google.gson.annotations.SerializedName;

public class Context {
    @SerializedName("ref")
    private String ref;
    @SerializedName("country_code")
    private String countryCode;
    @SerializedName("id")
    private String id;
    @SerializedName("text")
    private String text;
    @SerializedName("kind")
    private String kind;
    @SerializedName("osm:place_type")
    private String osmPlaceType;
    @SerializedName("text_zh")
    private String textZh;
    @SerializedName("text_en")
    private String textEn;


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

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id=id;
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text=text;
    }

    public String getKind(){
        return kind;
    }

    public void setKind(String kind){
        this.kind=kind;
    }

    public String getOsmPlaceType(){
        return osmPlaceType;
    }

    public void setOsmPlaceType(String osmPlaceType){
        this.osmPlaceType=osmPlaceType;
    }

    public String getTextZh(){
        return textZh;
    }

    public void setTextZh(String textZh){
        this.textZh=textZh;
    }

    public String getTextEn(){
        return textEn;
    }

    public void setTextEn(String textEn){
        this.textEn=textEn;
    }
}
