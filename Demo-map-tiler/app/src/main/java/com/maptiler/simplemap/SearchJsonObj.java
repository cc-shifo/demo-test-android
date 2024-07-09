package com.maptiler.simplemap;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchJsonObj {
    @SerializedName("type")
    private String type;
    @SerializedName("features")
    private List<Features> features;
    @SerializedName("query")
    private List<String> query;
    @SerializedName("attribution")
    private String attribution;


    @Nullable
    public String getType(){
        return type;
    }

    public void setType(@Nullable String type){
        this.type=type;
    }

    @Nullable
    public List<Features> getFeatures(){
        return features;
    }

    public void setFeatures(@Nullable List<Features> features){
        this.features=features;
    }

    public List<String> getQuery(){
        return query;
    }

    public void setQuery(@Nullable List<String> query){
        this.query=query;
    }

    @Nullable
    public String getAttribution(){
        return attribution;
    }

    public void setAttribution(@Nullable String attribution){
        this.attribution=attribution;
    }
}
