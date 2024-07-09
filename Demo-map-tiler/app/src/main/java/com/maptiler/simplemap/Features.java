package com.maptiler.simplemap;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Features {
    @SerializedName("id")
    private String id;

    @SerializedName("text")
    private String text;

    @SerializedName("type")
    private String type;

    @SerializedName("properties")
    private Properties properties;

    @SerializedName("geometry")
    private Geometry geometry;

    @SerializedName("bbox")
    private List<Double> bbox;

    @SerializedName("center")
    private List<Double> center;

    @SerializedName("place_name")
    private String placeName;

    @SerializedName("place_type")
    private List<String> placeType;

    @SerializedName("place_type_name")
    private List<String> placeTypeName;

    @SerializedName("relevance")
    private float relevance;

    @SerializedName("context")
    private List<Context> context;

    // @SerializedName("text_zh")
    // private String textZh;
    // @SerializedName("place_name_zh")
    // private String placeNameZh;
    // @SerializedName("text_en")
    // private String textEn;
    // @SerializedName("place_name_en")
    // private String placeNameEn;


    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type=type;
    }

    public Properties getProperties(){
        return properties;
    }

    public void setProperties(Properties properties){
        this.properties=properties;
    }

    public Geometry getGeometry(){
        return geometry;
    }

    public void setGeometry(Geometry geometry){
        this.geometry=geometry;
    }

    public List<Double> getBbox(){
        return bbox;
    }

    public void setBbox(List<Double> bbox){
        this.bbox=bbox;
    }

    public List<Double> getCenter(){
        return center;
    }

    public void setCenter(List<Double> center){
        this.center=center;
    }

    public String getPlaceName(){
        return placeName;
    }

    public void setPlaceName(String placeName){
        this.placeName=placeName;
    }

    public List<String> getPlaceType(){
        return placeType;
    }

    public void setPlaceType(List<String> placeType){
        this.placeType=placeType;
    }

    public float getRelevance(){
        return relevance;
    }

    public void setRelevance(float relevance){
        this.relevance=relevance;
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

    public List<String> getPlaceTypeName(){
        return placeTypeName;
    }

    public void setPlaceTypeName(List<String> placeTypeName){
        this.placeTypeName=placeTypeName;
    }

    public List<Context> getContext(){
        return context;
    }

    public void setContext(List<Context> context){
        this.context=context;
    }

    // public String getTextZh(){
    //     return textZh;
    // }
    //
    // public void setTextZh(String textZh){
    //     this.textZh=textZh;
    // }
    //
    // public String getPlaceNameZh(){
    //     return placeNameZh;
    // }
    //
    // public void setPlaceNameZh(String placeNameZh){
    //     this.placeNameZh=placeNameZh;
    // }
    //
    // public String getTextEn(){
    //     return textEn;
    // }
    //
    // public void setTextEn(String textEn){
    //     this.textEn=textEn;
    // }
    //
    // public String getPlaceNameEn(){
    //     return placeNameEn;
    // }
    //
    // public void setPlaceNameEn(String placeNameEn){
    //     this.placeNameEn=placeNameEn;
    // }
}
