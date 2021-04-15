package com.demo.myhttp.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GsonUtils {
    private static final String TAG = "GsonUtils";
    /**
     * 将Json数据转化为对象;
     *
     * @param jsonString Json数据;
     * @param cls        转换后的类;
     */
    public static <T> T getObject(String jsonString, Class<T> cls) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, cls);
        } catch (Exception e) {
            Log.e(TAG, "getObject: " + e);
        }
        return t;
    }

    /**
     * 将Json数据转化成List<Object>集合;
     *
     * @param json Json数据;
     */
    public static <T> List<T> asList(String json) {
        List<T> list = new ArrayList<>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(json, new TypeToken<List<T>>(){}.getType());
        } catch (Exception e) {
            Log.e(TAG, "asList: " + e);
        }
        return list;
    }

    public static <T> String toJsonString(T object) {
        try {
            Gson gson = new Gson();
            return gson.toJson(object);
        } catch (Exception e) {
            Log.d(TAG, "toJsonStringTest: " + e);
        }

        return null;
    }

    /**
     * 将Json数据转化成Map<String, Object>对象;
     *
     * @param jsonString Json数据;
     */
    public static Map<String, Object> objKeyMaps(String jsonString) {
        Map<String, Object> map = new HashMap<>();
        try {
            Gson gson = new Gson();
            map = gson.fromJson(jsonString, new TypeToken<Map<String, Object>>(){}.getType());
        } catch (Exception e) {
            Log.e(TAG, "objKeyMaps: " + e);
        }
        return map;
    }
}
