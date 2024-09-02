package com.maptiler.simplemap;


import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIReverseGeo {
    String LNG = "en";
    List<String> TYPES = Collections.singletonList("address");
    int LIMIT = 1;

    // lat,lng.json?key=apiKey
    // https://api.maptiler.com/geocoding/114.420270,30.425054.json?key=QqNtsRF5xtXnAoAaCSmC
    @GET("geocoding/{lng},{lat}.json?")/*key={key}*/
    Observable<SearchJsonObj> getAddress(@Path("lng") double lng, @Path("lat") double lat,
            @Query("key") String key, /*@Query("limit") int limit,
            @Query("language") String en, */@Query("types") List<String> types,
             @Query("excludeTypes") boolean excludeTypes);

    // https://geocode.maps.co/reverse?lat=30.425001&lon=114.42021113&api_key=66d196e166266586258252klbf15ac1
    @GET("reverse/")
    Observable<GeoMapPlaceObj> getAddressGeoMap(
            @Query("lat") double lat, @Query("lon") double lon,
            @Query("api_key") @NonNull String key);
}
