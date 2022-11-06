/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demoh5jsnativecomm.jsinterface;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.preference.Preference;

import java.util.Set;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * A data store interface to be implemented and provided to the {@link Preference} framework.
 * This can be used to replace the default {@link android.content.SharedPreferences}, if needed.
 *
 * <p>In most cases you want to use {@link android.content.SharedPreferences} as it is
 * automatically backed up and migrated to new devices. However, providing custom data store to
 * preferences can be useful if your app stores its preferences in a local database, cloud, or
 * they are device specific like "Developer settings". It might be also useful when you want to
 * use the preferences UI but the data is not supposed to be stored at all because they are only
 * valid per session.
 *
 * <p>Once a put method is called it is the full responsibility of the data store implementation
 * to safely store the given values. Time expensive operations need to be done in the background
 * to prevent from blocking the UI. You also need to have a plan on how to serialize the data in
 * case the activity holding this object gets destroyed.
 *
 * <p>By default, all "put" methods throw {@link UnsupportedOperationException}.
 */
@SuppressWarnings("unused")
public class NativeAPISpUtil {
    private static final String TAG = "SpDataStoreUtil";
    private static final String DS_PREFERENCES_NAME = "my.sp";
    private static final String API_OBJ_NAME = "NativeAPISpUtil";
    private static NativeAPISpUtil mInstance;

    private static RxDataStore<Preferences> mRxPrefDataStore;

    private NativeAPISpUtil() {
        // nothing
    }

    public static synchronized NativeAPISpUtil getInstance() {
        if (mInstance == null) {
            mInstance = new NativeAPISpUtil();
        }

        return mInstance;
    }

    public synchronized void init(@NonNull Context context) {
        if (mRxPrefDataStore == null) {
            mRxPrefDataStore = new RxPreferenceDataStoreBuilder(
                    context.getApplicationContext(), DS_PREFERENCES_NAME)
                    .setIoScheduler(Schedulers.newThread())
                    .build();
        }
    }

    /**
     * register all js API
     *
     * @param webView the web view
     */
    @SuppressLint("JavascriptInterface")
    public void registerAllJsAPI(@NonNull WebView webView) {
        webView.addJavascriptInterface(this, API_OBJ_NAME);
    }

    /**
     * Sets a {@link String} value to the data store.
     *
     * <p>Once the value is set the data store is responsible for holding it.
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     */
    @JavascriptInterface
    public boolean putString(String key, @Nullable String value) {
        if (key == null) {
            Log.e(TAG, "putString: ", new IllegalArgumentException("key must be nonnull"));
            return false;
        }

        if (value == null) {
            value = "";
        }
        writeString(key, value);

        return true;
    }

    /**
     * Sets a set of {@link String}s to the data store.
     *
     * <p>Once the value is set the data store is responsible for holding it.
     *
     * @param key    The name of the preference to modify
     * @param values The set of new values for the preference
     * @see #getStringSet(String, Set)
     */
    public boolean putStringSet(String key, @Nullable Set<String> values) {
        if (key == null) {
            Log.e(TAG, "putString: ", new IllegalArgumentException("key must be nonnull"));
            return false;
        }

        if (values == null) {
            values = new ArraySet<>(0);
        }
        writeStringSet(key, values);

        return true;
    }

    /**
     * Sets an {@link Integer} value to the data store.
     *
     * <p>Once the value is set the data store is responsible for holding it.
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     * @see #getInt(String)
     */
    public boolean putInt(String key, int value) {
        if (key == null) {
            Log.e(TAG, "putString: ", new IllegalArgumentException("key must be nonnull"));
            return false;
        }

        writeInt(key, value);

        return true;
    }

    /**
     * Sets a {@link Long} value to the data store.
     *
     * <p>Once the value is set the data store is responsible for holding it.
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     * @see #getLong(String)
     */
    public boolean putLong(String key, long value) {
        if (key == null) {
            Log.e(TAG, "putString: ", new IllegalArgumentException("key must be nonnull"));
            return false;
        }
        writeLong(key, value);

        return true;
    }

    /**
     * Sets a {@link Float} value to the data store.
     *
     * <p>Once the value is set the data store is responsible for holding it.
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     * @see #getFloat(String)
     */
    public boolean putFloat(String key, float value) {
        if (key == null) {
            Log.e(TAG, "putString: ", new IllegalArgumentException("key must be nonnull"));
            return false;
        }
        writeFloat(key, value);
        return true;
    }

    /**
     * Sets a {@link Boolean} value to the data store.
     *
     * <p>Once the value is set the data store is responsible for holding it.
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     * @see #getBoolean(String)
     */
    public boolean putBoolean(String key, boolean value) {
        if (key == null) {
            Log.e(TAG, "putString: ", new IllegalArgumentException("key must be nonnull"));
            return false;
        }
        writeBoolean(key, value);

        return true;
    }

    /**
     * Retrieves a {@link String} value from the data store.
     *
     * @param key The name of the preference to retrieve
     * @return The value from the data store or the default return value
     * @see #putString(String, String)
     */
    @Nullable
    @JavascriptInterface
    public String getString(String key) {
        String defValue = "";
        if (key == null) {
            Log.e(TAG, "getString: ", new IllegalArgumentException("key must be nonnull"));
            return defValue;
        }

        return readString(key, defValue);
    }

    /**
     * Retrieves a set of Strings from the data store.
     *
     * @param key       The name of the preference to retrieve
     * @param defValues Values to return if this preference does not exist in the storage
     * @return The values from the data store or the default return values
     * @see #putStringSet(String, Set)
     */
    @Nullable
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        if (key == null) {
            throw new IllegalArgumentException("key must be nonnull");
        }
        if (defValues == null) {
            defValues = new ArraySet<>(0);
        }
        return readStringSet(key, defValues);
    }

    /**
     * Retrieves an {@link Integer} value from the data store.
     *
     * @param key The name of the preference to retrieve
     * @return The value from the data store or the default return value
     * @see #putInt(String, int)
     */
    @JavascriptInterface
    public int getInt(String key) {
        int defValue = 0;
        if (key == null) {
            Log.e(TAG, "getInt: ", new IllegalArgumentException("key must be nonnull"));
            return defValue;
        }
        return readInt(key, defValue);
    }

    /**
     * Retrieves a {@link Long} value from the data store.
     *
     * @param key The name of the preference to retrieve
     * @return The value from the data store or the default return value
     * @see #putLong(String, long)
     */
    @JavascriptInterface
    public long getLong(String key) {
        long defValue = 0;
        if (key == null) {
            Log.e(TAG, "getInt: ", new IllegalArgumentException("key must be nonnull"));
            return defValue;
        }
        return readLong(key, defValue);
    }

    /**
     * Retrieves a {@link Float} value from the data store.
     *
     * @param key The name of the preference to retrieve
     * @return The value from the data store or the default return value
     * @see #putFloat(String, float)
     */
    @JavascriptInterface
    public float getFloat(String key) {
        float defValue = 0;
        if (key == null) {
            Log.e(TAG, "getFloat: ", new IllegalArgumentException("key must be nonnull"));
            return defValue;
        }
        return readFloat(key, defValue);
    }

    /**
     * Retrieves a {@link Boolean} value from the data store.
     *
     * @param key The name of the preference to retrieve
     * @return the value from the data store or the default return value
     * @see #getBoolean(String)
     */
    @JavascriptInterface
    public boolean getBoolean(String key) {
        if (key == null) {
            Log.e(TAG, "getInt: ", new IllegalArgumentException("key must be nonnull"));
            return false;
        }
        return readBoolean(key, false);
    }

    public void destroy() {
        if (mRxPrefDataStore != null && !mRxPrefDataStore.isDisposed()) {
            mRxPrefDataStore.dispose();
        }
    }

    private int readInt(@NonNull final String key, final int defaultValue) {
        return mRxPrefDataStore.data().map(preferences -> {
            Log.d(TAG, "Thread name: " + Thread.currentThread().getName());
            Preferences.Key<Integer> k = PreferencesKeys.intKey(key);
            Integer value = preferences.get(k);
            return value != null ? value : defaultValue;
        }).blockingFirst(defaultValue);
    }

    private long readLong(@NonNull final String key, final long defaultValue) {
        Preferences.Key<Long> k = PreferencesKeys.longKey(key);
        return mRxPrefDataStore.data().map(preferences -> {
            Long value = preferences.get(k);
            return value != null ? value : defaultValue;
        }).blockingFirst(defaultValue);
    }

    private boolean readBoolean(@NonNull final String key, @NonNull final Boolean defaultValue) {
        return mRxPrefDataStore.data().map(preferences -> {
            Preferences.Key<Boolean> k = PreferencesKeys.booleanKey(key);
            Boolean value = preferences.get(k);
            return value != null ? value : defaultValue;
        }).blockingFirst(defaultValue);
    }

    private float readFloat(@NonNull final String key, final float defaultValue) {
        return mRxPrefDataStore.data().map(preferences -> {
            Preferences.Key<Float> k = PreferencesKeys.floatKey(key);
            Float value = preferences.get(k);
            return value != null ? value : defaultValue;
        }).blockingFirst(defaultValue);
    }

    private double readDouble(@NonNull final String key, final double defaultValue) {
        return mRxPrefDataStore.data().map(preferences -> {
            Preferences.Key<Double> k = PreferencesKeys.doubleKey(key);
            Double value = preferences.get(k);
            return value != null ? value : defaultValue;
        }).blockingFirst(defaultValue);
    }

    private String readString(@NonNull final String key, @NonNull final String defaultValue) {
        Log.d(TAG, "readString mRxPrefDataStore: " + mRxPrefDataStore.toString());
        String v = mRxPrefDataStore.data().map(preferences -> {
            Log.d(TAG, "readString Thread name: " + Thread.currentThread().getName());
            Preferences.Key<String> k = PreferencesKeys.stringKey(key);
            Log.d(TAG, "apply SystemClock.sleep: 3000");
            String value = preferences.get(k);
            return value != null ? value : defaultValue;
        }).blockingFirst(defaultValue);

        Log.d(TAG, "readString: SystemClock.sleep");

        return v;
    }

    private Set<String> readStringSet(@NonNull final String key,
                                      @NonNull final Set<String> defaultValue) {
        return mRxPrefDataStore.data().map(preferences -> {
            Preferences.Key<Set<String>> k = PreferencesKeys.stringSetKey(key);
            Set<String> value = preferences.get(k);
            return value != null ? value : defaultValue;
        }).blockingFirst(defaultValue);
    }

    private void writeInt(@NonNull final String key, final int defaultValue) {
        mRxPrefDataStore.updateDataAsync(preferences -> {
            final Preferences.Key<Integer> k = PreferencesKeys.intKey(key);
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.set(k, defaultValue);
            return Single.just(mutablePreferences);
        }).blockingSubscribe();
    }

    private void writeLong(@NonNull final String key, final long defaultValue) {
        mRxPrefDataStore.updateDataAsync(preferences -> {
            final Preferences.Key<Long> k = PreferencesKeys.longKey(key);
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.set(k, defaultValue);
            return Single.just(mutablePreferences);
        }).blockingSubscribe(preferences -> Log.d(TAG, "write " + key + " success"),
                throwable -> Log.e(TAG, "write " + key + " failed: ", throwable));
    }

    private void writeBoolean(@NonNull final String key, @NonNull final Boolean defaultValue) {
        mRxPrefDataStore.updateDataAsync(preferences -> {
            Preferences.Key<Boolean> k = PreferencesKeys.booleanKey(key);
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.set(k, defaultValue);
            return Single.just(mutablePreferences);
        }).blockingSubscribe(preferences -> Log.d(TAG, "write " + key + " success"),
                throwable -> Log.e(TAG, "write " + key + " failed: ", throwable));
    }

    private void writeFloat(@NonNull final String key, final float defaultValue) {
        mRxPrefDataStore.updateDataAsync(preferences -> {
            Preferences.Key<Float> k = PreferencesKeys.floatKey(key);
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.set(k, defaultValue);
            return Single.just(mutablePreferences);
        }).blockingSubscribe(preferences -> Log.d(TAG, "write " + key + " success"),
                throwable -> Log.e(TAG, "write " + key + " failed: ", throwable));
    }

    private void writeDouble(@NonNull final String key, final double defaultValue) {
        mRxPrefDataStore.updateDataAsync(preferences -> {
            Preferences.Key<Double> k = PreferencesKeys.doubleKey(key);
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.set(k, defaultValue);
            return Single.just(mutablePreferences);
        }).blockingSubscribe(preferences -> Log.d(TAG, "write " + key + " success"),
                throwable -> Log.e(TAG, "write " + key + " failed: ", throwable));
    }

    private void writeString(@NonNull final String key, final @NonNull String defaultValue) {
        mRxPrefDataStore.updateDataAsync(preferences -> {
            Log.d(TAG, "writeString mRxPrefDataStore: " + mRxPrefDataStore.toString() + ", " +
                    "key=" + key + ", value=" + defaultValue);
            Log.d(TAG, "writeString Thread name: " + Thread.currentThread().getName());
            final Preferences.Key<String> k = PreferencesKeys.stringKey(key);
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.set(k, defaultValue);
            return Single.just(mutablePreferences);
        }).blockingSubscribe(preferences -> Log.d(TAG, "write " + key + " success"),
                throwable -> Log.e(TAG, "write " + key + " failed: ", throwable));
    }

    private void writeStringSet(@NonNull final String key, final @NonNull Set<String> value) {
        mRxPrefDataStore.updateDataAsync(preferences -> {
            Preferences.Key<Set<String>> k = PreferencesKeys.stringSetKey(key);
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.set(k, value);
            return Single.just(mutablePreferences);
        }).blockingSubscribe(preferences -> Log.d(TAG, "write " + key + " success"),
                throwable -> Log.e(TAG, "write " + key + " failed: ", throwable));

    }
}

