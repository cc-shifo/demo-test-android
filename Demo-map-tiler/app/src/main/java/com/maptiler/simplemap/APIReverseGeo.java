package com.maptiler.simplemap;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
}
