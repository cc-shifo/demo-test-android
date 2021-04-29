package com.demo.demopaymodule.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class AssetsUtils {
    public static String getJson(String fileName, Context context) {
        AssetManager assetManager = context.getAssets();
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));){
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static Properties getProperties(String fileName, Context context) {
        AssetManager assetManager = context.getAssets();

        Properties properties = new Properties();
        try (InputStream inputStream = assetManager.open(fileName)) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }
}
