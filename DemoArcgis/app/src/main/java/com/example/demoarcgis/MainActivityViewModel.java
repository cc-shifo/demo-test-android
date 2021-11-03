package com.example.demoarcgis;

import android.app.Application;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.Viewpoint;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscription;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.GzipSink;
import okio.GzipSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

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

    private Disposable mReadDisposable;
    private Disposable mWriteDisposable;
    private String mPath;

    private final MutableLiveData<Boolean> mReadStatus;
    private final MutableLiveData<Boolean> mWriteStatus;


    public MainActivityViewModel(@NonNull @NotNull Application application) {
        super(application);
        mLiveData = new MutableLiveData<>();
        mReadStatus = new MutableLiveData<>();
        mWriteStatus = new MutableLiveData<>();
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


    public LiveData<Boolean> observeReadStatus() {
        return mReadStatus;
    }

    public LiveData<Boolean> observeWriteStatus() {
        return mWriteStatus;
    }

    public void textToBinaryTest() {
        mReadDisposable = Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Boolean> emitter) throws Throwable {
                // boolean r1 = loadTextData();
                boolean r1 = loadTextManuallyInputData();
                // boolean r1= loadTextHello();
                // boolean r1= textLoadTextHello();
                if (!emitter.isDisposed()) {
                    emitter.onNext(r1);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Throwable {
                        mReadStatus.setValue(aBoolean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.d(TAG, "accept: ", throwable);
                    }
                });
    }

    private boolean loadTextData() {
        File file = new File("/sdcard/Android/data/com.example.demoarcgis/files/track/bin_raw");
        if (file.exists()) {
            file.delete();
        }

        boolean result = OkioBufferUtil.getInstance().createBuffedTrackRecord(getApplication(),
                "/sdcard/Android/data/com.example.demoarcgis/files/track/bin_raw");
        if (!result) {
            return false;
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        try (InputStream inputStream = getApplication().getResources().openRawResource(R.raw.nav);
             BufferedSource source = Okio.buffer(Okio.source(inputStream))) {
            while (!source.exhausted()) {
                String line = source.readUtf8Line();
                if (line != null) {
                    String[] data = line.split("\\s+");
                    if (data.length == 3) {
                        if (byteBuffer.position() <= byteBuffer.capacity() - 16) {
                            byteBuffer.putDouble(Double.parseDouble(data[1]));
                            byteBuffer.putDouble(Double.parseDouble(data[2]));
                        }

                        if (byteBuffer.position() == byteBuffer.capacity()) {
                            OkioBufferUtil.getInstance().write2Buffer(byteBuffer);
                            OkioBufferUtil.getInstance().flushTrackBuffer();
                            byteBuffer.clear();
                        }
                    } else {
                        Log.e(TAG, "readTest: data error" + data.toString());
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("readTest", "readNavTestData: ", e);
            return false;
        }

        mPath = OkioBufferUtil.getInstance().getTrackRecordPath();

        OkioBufferUtil.getInstance().closeTrackBuffer();
        return true;
    }

    private boolean loadTextManuallyInputData() {
        Sink sink;
        try {
            sink = Okio.sink(new File("/storage/emulated/0/Android/data/com.example" +
                    ".demoarcgis/cache/m_bin_raw"), true);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "openTrackRecord: path isn't exists");
            return false;
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
        int x = 0;
        int y = 0;
        long z = 0;
        try (GzipSink gzipSink = new GzipSink(sink);
             BufferedSink bufferedSink = Okio.buffer(gzipSink)) {
            for (int i = 0; i < 4096 * 2; i++) {
                if (doubleBuffer.position() < doubleBuffer.capacity()) {
                    doubleBuffer.put(i + 1000000f);
                    doubleBuffer.put(i + 2000000f);
                }

                if (doubleBuffer.position() == doubleBuffer.capacity()) {
                    doubleBuffer.rewind();
                    byteBuffer.rewind();
                    int n = bufferedSink.write(byteBuffer);
                    if (n == 0) {
                        Log.e(TAG, "bufferedSink.write2 = 0");
                        return false;
                    }
                    bufferedSink.flush();
                    doubleBuffer.clear();
                    byteBuffer.clear();
                    z += n;
                    y++;
                }
                x++;
            }

            if (doubleBuffer.position() != 0) {
                doubleBuffer.rewind();
                byteBuffer.rewind();
                int n = bufferedSink.write(byteBuffer);
                if (n == 0) {
                    Log.e(TAG, "bufferedSink.write2 = 0");
                    return false;
                }
                bufferedSink.flush();
                doubleBuffer.clear();
                byteBuffer.clear();
                z += n;
                y++;
            }
        } catch (Exception e) {
            Log.e(TAG, "loadTextManuallyInputData: ", e);
            return false;
        }

        // x = 8192, y = 32, z = 131072
        Log.e(TAG, "loadTextManuallyInputData: bufferedSink.flush times x=" + x + ", y = " + y
                + ", z = " + z);
        return true;
    }

    public void binaryToTextTest() {
        mReadDisposable = Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Boolean> emitter) throws Throwable {
                // boolean r = loadBinary();
                boolean r = loadBinaryManuallyInputData();
                // boolean r = loadBinaryHello();
                // boolean r = textLoadBinaryHello();
                if (!emitter.isDisposed()) {
                    emitter.onNext(r);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Throwable {
                        mWriteStatus.setValue(aBoolean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.d(TAG, "accept: ", throwable);
                    }
                });
    }


    private boolean loadBinary() {
        File file = new File("/sdcard/Android/data/com.example.demoarcgis/files/track/text_raw");
        if (file.exists()) {
            file.delete();
        }

        boolean b = OkioBufferUtil.getInstance().openBuffedTrackRecord("/sdcard/Android/data/com" +
                ".example" +
                ".demoarcgis/files/track/bin_raw");
        if (!b) {
            return false;
        }

        PointCollection collection = OkioBufferUtil.getInstance().readRecord();
        OkioBufferUtil.getInstance().closeTrackBuffer();
        if (collection.isEmpty()) {
            Log.e(TAG, "binaryToTextTest: PointCollection empty");
            return false;
        }


        b = OkioBufferUtil.getInstance().createBuffedTrackRecord(getApplication(),
                "/sdcard/Android/data/com.example.demoarcgis/files/track/text_raw");
        if (!b) {
            return false;
        }

        StringBuilder builder = new StringBuilder();
        for (int n = 0; n < collection.size(); n++) {
            builder.append(collection.get(n).getY());
            builder.append("\t");
            builder.append(collection.get(n).getX());
            builder.append("\n");
            if ((n + 1) % 4096 == 0) {
                b = OkioBufferUtil.getInstance().writeTrack2Buffer(builder.toString());
                if (!b) {
                    OkioBufferUtil.getInstance().closeTrackBuffer();
                    return false;
                }
                OkioBufferUtil.getInstance().flushTrackBuffer();
                builder.setLength(0);
            }
        }

        if (builder.length() > 0) {
            b = OkioBufferUtil.getInstance().writeTrack2Buffer(builder.toString());
            if (!b) {
                OkioBufferUtil.getInstance().closeTrackBuffer();
                return false;
            }
            OkioBufferUtil.getInstance().flushTrackBuffer();
        }
        OkioBufferUtil.getInstance().closeTrackBuffer();

        return true;
    }

    private boolean loadBinaryManuallyInputData() {
        // Source source;
        // try {
        //     source = Okio.source(new File("/sdcard/Android/data/com.example" +
        //             ".demoarcgis/files/track/m_bin_raw"));
        // } catch (FileNotFoundException e) {
        //     Log.e(TAG, "openTrackRecord: path isn't exists");
        //     return false;
        // }
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
        // PointCollection collection = new PointCollection((Iterable<Point>) null,
        //         SpatialReferences.getWebMercator());

        List<Double> list = new ArrayList<>();
        long index = 0;
        long x = 0;
        long y = 0;
        try {
            Source source = Okio.source(new File("/storage/emulated/0/Android/data/com.example" +
                    ".demoarcgis/cache/m_bin_raw"));
            BufferedSource bufferedSource = Okio.buffer(new GzipSource(source));
            while (!bufferedSource.exhausted()) {
                int n = bufferedSource.read(byteBuffer);

                x++;
                byteBuffer.flip();
                doubleBuffer.position(0);
                doubleBuffer.limit(byteBuffer.limit() >>> 3);// limit = byte limit / 8
                // int limit = n - (n % 16);
                while (doubleBuffer.position() < doubleBuffer.limit()) {
                    // collection.add(doubleBuffer.get(), doubleBuffer.get(), 0);
                    list.add(doubleBuffer.get());
                    Log.e(TAG, "loadBinaryManuallyInputData: p=" + doubleBuffer.position() + ", " +
                            "l=" + doubleBuffer.limit());
                    list.add(doubleBuffer.get());
                    y++;
                }
                doubleBuffer.clear();
                byteBuffer.clear();
                doubleBuffer.limit(doubleBuffer.capacity());
                byteBuffer.limit(byteBuffer.capacity());
                // n = bufferedSource.read(byteBuffer);
                // if (n == 0) {
                //     break;
                // }
                //
                // x++;
                // byteBuffer.flip();
                // int limit = n - (n % 16);
                // while (byteBuffer.position() < limit) {
                //     collection.add(byteBuffer.getDouble(), byteBuffer.getDouble(), 0);
                //     y++;
                // }
                // byteBuffer.clear();

                Log.e(TAG, "\n\n\n\n<--p=  x= " + x + "\n\n\n\n");

            }
        } catch (Exception e) {
            Log.e(TAG, "readTrackRecord[" + index + "]: ", e);
            return false;
        }

        // x = 32, y = 8192
        Log.e(TAG,
                "loadBinaryManuallyInputData1 [x=" + (x - 1) + "], [y=" + (y - 1) + "]" + "size=" + list.size());

        Sink sink;
        try {
            sink = Okio.sink(new File("/storage/emulated/0/Android/data/com.example" +
                    ".demoarcgis/cache/m_text_raw"));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "openTrackRecord: path isn't exists");
            return false;
        }


        long z = 0;
        try (BufferedSink bufferedSink = Okio.buffer(sink)) {
            StringBuilder builder = new StringBuilder();
            for (int n = 0; n < list.size(); n += 2) {
                builder.append(list.get(n));
                builder.append("\t");
                builder.append(list.get(n + 1));
                builder.append("\n");

                // builder.append(Double.toString(collection.get(n).getY()));
                // builder.append("\t");
                // builder.append(Double.toString(collection.get(n).getX()));
                // builder.append("\n");
                if ((n + 1) % 256 == 0) {
                    bufferedSink.writeString(builder.toString(), StandardCharsets.UTF_8);
                    bufferedSink.flush();
                    builder.setLength(0);
                    z++;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "loadBinaryManuallyInputData: ", e);
            return false;
        }

        // z = 4
        Log.e(TAG, "loadBinaryManuallyInputData2 [z=" + z + "]");

        return true;
    }

    private boolean loadTextHello() {
        Sink sink;
        try {
            sink = Okio.sink(new File("/sdcard/Android/data/com.example" +
                    ".demoarcgis/files/track/m_bin_raw"), true);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "openTrackRecord: path isn't exists");
            return false;
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
        int x = 0;
        int y = 0;
        long z = 0;
        try (GzipSink gzipSink = new GzipSink(sink);
             BufferedSink bufferedSink = Okio.buffer(gzipSink)) {
            for (int i = 0; i < 1; i++) {
                if (doubleBuffer.position() < doubleBuffer.capacity()) {
                    doubleBuffer.put(i + 1000000f);
                    doubleBuffer.put(i + 2000000f);
                }

                if (doubleBuffer.position() == doubleBuffer.capacity()) {
                    doubleBuffer.rewind();
                    byteBuffer.rewind();
                    int n = bufferedSink.write(byteBuffer);
                    if (n == 0) {
                        Log.e(TAG, "bufferedSink.write2 = 0");
                        return false;
                    }
                    bufferedSink.flush();
                    doubleBuffer.clear();
                    byteBuffer.clear();
                    z += n;
                    y++;
                }
                x++;
            }

            if (doubleBuffer.position() != 0) {
                doubleBuffer.rewind();
                byteBuffer.rewind();
                int n = bufferedSink.write(byteBuffer);
                if (n == 0) {
                    Log.e(TAG, "bufferedSink.write2 = 0");
                    return false;
                }
                bufferedSink.flush();
                doubleBuffer.clear();
                byteBuffer.clear();
                z += n;
                y++;
            }
        } catch (Exception e) {
            Log.e(TAG, "loadTextManuallyInputData: ", e);
            return false;
        }

        // x = 8192, y = 32, z = 131072
        Log.e(TAG, "loadTextManuallyInputData: bufferedSink.flush times x=" + x + ", y = " + y
                + ", z = " + z);
        return true;
    }

    private boolean loadBinaryHello() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        PointCollection collection = new PointCollection((Iterable<Point>) null,
                SpatialReferences.getWebMercator());
        long index = 0;
        long x = 0;
        long y = 0;
        try {
            Source source = Okio.source(new File("/sdcard/Android/data/com.example" +
                    ".demoarcgis/files/track/m_bin_raw"));
            BufferedSource bufferedSource = Okio.buffer(source);
            // BufferedSource bufferedSource = Okio.buffer(new GzipSource(source));
            int n = 0;
            while (!bufferedSource.exhausted()) {
                n = bufferedSource.read(byteBuffer);
                if (n == 0) {
                    break;
                }

                x++;
                byteBuffer.rewind();
                int limit = n - (n % 16);
                while (byteBuffer.position() < limit) {
                    collection.add(byteBuffer.getDouble(), byteBuffer.getDouble(), 0);
                    y++;
                }
                byteBuffer.clear();
            }
        } catch (Exception e) {
            Log.e(TAG, "readTrackRecord[" + index + "]: ", e);
            return false;
        }

        // x = 32, y = 256
        Log.e(TAG, "loadBinaryManuallyInputData1 [x=" + x + "], [y=" + y + "]");

        Sink sink;
        try {
            sink = Okio.sink(new File("/sdcard/Android/data/com.example" +
                    ".demoarcgis/files/track/m_text_raw"));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "openTrackRecord: path isn't exists");
            return false;
        }


        long z = 0;
        try (BufferedSink bufferedSink = Okio.buffer(sink)) {
            StringBuilder builder = new StringBuilder();
            for (int n = 0; n < collection.size(); n++) {
                builder.append(collection.get(n).getY());
                builder.append("\t");
                builder.append(collection.get(n).getX());
                builder.append("\n");
                if ((n + 1) % 256 == 0) {
                    bufferedSink.writeString(builder.toString(), StandardCharsets.UTF_8);
                    bufferedSink.flush();
                    builder.setLength(0);
                    z++;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "loadBinaryManuallyInputData: ", e);
            return false;
        }

        // z = 4
        Log.e(TAG, "loadBinaryManuallyInputData2 [z=" + z + "]");

        return true;
    }

    private boolean textLoadTextHello() {
        Context context = getApplication().getApplicationContext();
        String path = context.getExternalCacheDir().getAbsolutePath();
        Sink sink;
        try {
            sink = Okio.sink(new File("/storage/emulated/0/Android/data/com.example" +
                    ".demoarcgis/cache/m_bin_gzip"), true);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "openTrackRecord: path isn't exists");
            return false;
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        try (GzipSink gzipSink = new GzipSink(sink);
             BufferedSink bufferedSink = Okio.buffer(gzipSink)) {
            for (int i = 0; i < 1; i++) {
                byteBuffer.put((byte) 0x03);
                byteBuffer.put((byte) 0x04);
                byteBuffer.flip();
                bufferedSink.write(byteBuffer);
                bufferedSink.flush();
                byteBuffer.clear();
            }
        } catch (Exception e) {
            Log.e(TAG, "loadTextManuallyInputData: ", e);
            return false;
        }

        return true;
    }

    private boolean textLoadBinaryHello() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);

        byte[] bytes = new byte[0];
        try {
            // Source source = Okio.source(new File("/sdcard/Android/data/com.example
            // .demoarcgis/files/m_bin_gzip"));
            Source source = Okio.source(new File("/storage/emulated/0/Android/data/com.example" +
                    ".demoarcgis/cache/m_bin_gzip"));
            BufferedSource bufferedSource = Okio.buffer(new GzipSource(source));
            int n = 0;
            while (!bufferedSource.exhausted()) {
                n = bufferedSource.read(byteBuffer);
                if (n == 0) {
                    break;
                }


                byteBuffer.flip();// just change position, don't change limit
                bytes = new byte[byteBuffer.limit()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = byteBuffer.get();
                }
                byteBuffer.clear();
            }
        } catch (Exception e) {
            Log.e(TAG, "textLoadBinaryHello: ", e);
            return false;
        }

        if (bytes.length != 0) {
            Log.e(TAG, "textLoadBinaryHello: " + bytes.toString());
        }

        Sink sink;
        try {
            // sink = Okio.sink(new File("/sdcard/Android/data/com.example
            // .demoarcgis/files/track/m_text"));
            sink = Okio.sink(new File("/storage/emulated/0/Android/data/com.example" +
                    ".demoarcgis/cache/m_text"));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "textLoadBinaryHello: path isn't exists");
            return false;
        }


        try (BufferedSink bufferedSink = Okio.buffer(sink)) {
            StringBuilder builder = new StringBuilder();
            for (int n = 0; n < bytes.length; n++) {
                int b1 = (bytes[n] & 0xf0) >>> 4;
                int b2 = bytes[n] & 0x0f;
                builder.append(b1).append(b2);
                builder.append("\n");
            }
            bufferedSink.writeString(builder.toString(), StandardCharsets.UTF_8);
            bufferedSink.flush();
            builder.setLength(0);
        } catch (Exception e) {
            Log.e(TAG, "loadBinaryManuallyInputData: ", e);
            return false;
        }

        return true;
    }


    public void readTest() {

    }

    public void writeTest() {

    }


    public void destroy() {
        if (mDisposable != null) {
            mDisposable.cancel();
            mDisposable = null;
        }
        if (mReadDisposable != null && !mReadDisposable.isDisposed()) {
            mReadDisposable.dispose();
            mReadDisposable = null;
        }
        if (mWriteDisposable != null && !mWriteDisposable.isDisposed()) {
            mWriteDisposable.dispose();
            mWriteDisposable = null;
        }
    }

}
