package com.example.demoarcgis;

import android.app.Application;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.Viewpoint;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscription;

import java.io.InputStream;
import java.util.zip.DataFormatException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okio.BufferedSource;
import okio.Okio;

public class MainActivityViewModel extends AndroidViewModel {
    private static final String TAG = "MainViewModel";
    private final MutableLiveData<TrackData> mLiveData;
    /**
     * unit of milliseconds
     */
    private long mPrevTime;
    private PolylineBuilder mPolylineBuilder;
    private double mLastLatitude;
    private double mLastLongitude;
    private double mLastHeightLevel;
    private TrackData mTrack;
    private Viewpoint mViewpoint;
    private Subscription mDisposable;


    public MainActivityViewModel(@NonNull @NotNull Application application) {
        super(application);
        mLiveData = new MutableLiveData<>();
    }


    public LiveData<TrackData> startTrack() {
        init();

        Flowable.create(new FlowableOnSubscribe<TrackData>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull
                                          FlowableEmitter<TrackData> emitter) throws Throwable {
                readNavTestData(emitter);
            }
        }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<TrackData>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Subscription s) {
                        mDisposable = s;
                        s.request(1);
                    }

                    @Override
                    public void onNext(TrackData track) {
                        mLiveData.setValue(track);
                        mDisposable.request(1);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");

                    }
                });

        return mLiveData;
    }

    private void init() {
        mPrevTime = 0;
        mTrack = new TrackData();
        mPolylineBuilder = new PolylineBuilder(SpatialReferences.getWgs84());
        mLastLatitude = 0;
        mLastLongitude = 0;
        mLastHeightLevel = 0;
        mViewpoint = null;
    }

    public void stopTrack() {
        recycle();
    }

    /**
     * set the buffer of navigation coordinates to null so that the memory can be collected
     */
    public void recycle() {
        mPrevTime = 0;
        mPolylineBuilder = null;
        mLastLatitude = 0;
        mLastLongitude = 0;
        mLastHeightLevel = 0;
        mViewpoint = null;
        mTrack = null;
    }


    private void readNavTestData(FlowableEmitter<TrackData> emitter) {
        try (InputStream inputStream = getApplication().getResources().openRawResource(R.raw.nav);
             BufferedSource source = Okio.buffer(Okio.source(inputStream))) {
            while (!source.exhausted()) {
                String line = source.readUtf8Line();
                if (line != null) {
                    String[] data = line.split("\\s+");
                    if (data.length == 3) {
                        sycNavTrack(Double.parseDouble(data[1]),
                                Double.parseDouble(data[2]), 1000);
                        if (!emitter.isCancelled()) {
                            emitter.onNext(mTrack);
                        }
                    } else {
                        if (!emitter.isCancelled()) {
                            emitter.onError(new DataFormatException(" data error" + data.toString()));
                        }
                        break;
                    }
                }
            }

            if (!emitter.isCancelled()) {
                emitter.onComplete();
            }
        } catch (Exception e) {
            Log.e("readNavTestData", "readNavTestData: ", e);
            if (!emitter.isCancelled()) {
                emitter.onError(e);
            }
        }
    }

    /**
     * Cache navigation coordinates a period of time which should be defined dependent upon
     * the movement speed of LIDAR.
     */
    private TrackData sycNavTrack(double latitude, double longitude, double height) {
        Log.d("sycNavTrack", "sycNavTrack: latitude=" + latitude + "longitude=" + longitude);

        if (mPrevTime <= 0) {
            mPrevTime = SystemClock.elapsedRealtime();
            // one point
            addTrackPoint(latitude, longitude, height);
            return mTrack;
        }

        long time = SystemClock.elapsedRealtime();
        if (time - mPrevTime >= 2000) {
            mPrevTime = time;
            if (!isApproximatelyEqual(latitude,
                    longitude, height)) {
                addTrackPoint(latitude, longitude, height);
            }
        }

        return mTrack;
    }

    /**
     * add one LIDAR coordinate into the internal PolylineBuilder cache.
     *
     * @param latitude    WGS84 coordinate
     * @param longitude   WGS84 coordinate
     * @param heightLevel WGS84 coordinate
     */
    private void addTrackPoint(double latitude, double longitude, double heightLevel) {
        mLastLatitude = latitude;
        mLastLongitude = longitude;
        mLastHeightLevel = heightLevel;
        mViewpoint = new Viewpoint(new Point(latitude, longitude, heightLevel), 10000);
        mPolylineBuilder.addPoint(latitude, longitude, heightLevel);
        mTrack.setPolyline(mPolylineBuilder.toGeometry());
        mTrack.setViewpoint(mViewpoint);
        mTrack.setLatitude(latitude);
        mTrack.setLongitude(longitude);
        mTrack.setHeight(heightLevel);
    }

    private boolean isApproximatelyEqual(double latitude, double longitude, double heightLevel) {
        return (Double.compare(mLastLatitude - latitude, 0.000001) == 0
                && Double.compare(mLastLongitude - longitude, 0.000001) == 0
                && Double.compare(mLastHeightLevel - heightLevel, 0.000001) == 0);
    }

    public static class TrackData {
        private Polyline mPolyline;
        private double mLatitude;
        private double mLongitude;
        private double mHeight;

        private Viewpoint mViewpoint;

        public TrackData() {
            // nothing
        }

        public Polyline getPolyline() {
            return mPolyline;
        }

        public void setPolyline(Polyline polyline) {
            mPolyline = polyline;
        }

        public double getLatitude() {
            return mLatitude;
        }

        public void setLatitude(double latitude) {
            mLatitude = latitude;
        }

        public double getLongitude() {
            return mLongitude;
        }

        public void setLongitude(double longitude) {
            mLongitude = longitude;
        }

        public double getHeight() {
            return mHeight;
        }

        public void setHeight(double height) {
            mHeight = height;
        }

        public Viewpoint getViewpoint() {
            return mViewpoint;
        }

        public void setViewpoint(Viewpoint viewpoint) {
            mViewpoint = viewpoint;
        }
    }
}
