package com.pax.helloworld;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppInfoStorageCache {
    private static final String PACKAGE_CACHE_FILE_NAME = "packages.json";
    private File mCache;

    public static AppInfoStorageCache getInstance() {
        return Singleton.instance;
    }

    public List<AppInfo> getPackages() {
        List<AppInfo> apps = new ArrayList<>();
        if (!isExternalStorageWritable()) {
            LogUtil.e(new IOException("License: No write permission for external storage"));
            return apps;
        }

//        String path = HelloWorldApp.getApplication().getFilesDir().getAbsolutePath();
        String path = Objects.requireNonNull(HelloWorldApp.getApplication()
                .getExternalFilesDir(null)).getAbsolutePath();
        mCache = new File(path + File.separator + PACKAGE_CACHE_FILE_NAME);
        if (!mCache.exists()) {
            apps.add(new AppInfo("com.pax.launcher", new AppInfo
                    .LauncherActivity("com.pax.launcher.setting.SettingActivity",
                    R.mipmap.icon_settings, Tool.getString(R.string.setting))));
            if (!updatePackagesCache(apps)) {
                apps.remove(0);
            }

            return apps;
        }

        StringBuilder builder = new StringBuilder();
        try (FileInputStream input = new FileInputStream(mCache);
             BufferedReader reader = new BufferedReader(new InputStreamReader(input,
                     StandardCharsets.UTF_8))) {
            char[] chars = new char[1024];
            int n;
            while ((n = reader.read(chars)) != -1) {
                //builder.append((char)v);
                builder.append(chars, 0, n);
            }
        } catch (IOException e) {
            LogUtil.e(e);
            return apps;
        }

        Gson gson = new Gson();
        String s = builder.toString();
        return gson.fromJson(s, new TypeToken<List<AppInfo>>() {
        }.getType());
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean updatePackagesCache(@NonNull List<AppInfo> apps) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<AppInfo>>() {
        }.getType();
        String s = gson.toJson(apps, listType);
        try (OutputStream output = new FileOutputStream(mCache);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output,
                     StandardCharsets.UTF_8))
        ) {
            writer.write(s);
        } catch (IOException e) {
            LogUtil.e(e);
            return false;
        }
        return true;
    }

    private static class Singleton {
        private static AppInfoStorageCache instance = new AppInfoStorageCache();
        private Singleton() {
            //do nothing
        }
    }
}
