package com.example.demoarcgis;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscription;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
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
import io.reactivex.rxjava3.functions.Action;
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
    private Disposable mConvDisposable;
    private Disposable mLoadDisposable;
    private String mPath;

    private final MutableLiveData<Boolean> mReadStatus;
    private final MutableLiveData<Boolean> mWriteStatus;
    private final MutableLiveData<Boolean> mConvertStatus;
    private final MutableLiveData<HistoricalTrack> mHistTrack;


    public MainActivityViewModel(@NonNull @NotNull Application application) {
        super(application);
        mLiveData = new MutableLiveData<>();
        mReadStatus = new MutableLiveData<>();
        mWriteStatus = new MutableLiveData<>();
        mConvertStatus = new MutableLiveData<>();
        mHistTrack = new MutableLiveData<>();
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

    public MutableLiveData<Boolean> getConvertStatus() {
        return mConvertStatus;
    }

    public void rawNavFileTxtToBin() {
        mConvDisposable = Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Boolean> emitter) throws Throwable {

                boolean status = TrackRecUtil.getInstance().create(getApplication(), "" +
                                "/sdcard/Android/data/com.rgspace.cocapture/files/hisTrackTest",
                        false,
                        true);

                if (!status) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(false);
                        emitter.onComplete();
                    }
                    return;
                }

                try (InputStream inputStream =
                             getApplication().getResources().openRawResource(R.raw.nav);
                     BufferedSource source = Okio.buffer(Okio.source(inputStream))) {
                    while (!source.exhausted()) {
                        String line = source.readUtf8Line();
                        if (line != null) {
                            String[] data = line.split("\\s+");
                            status = TrackRecUtil.getInstance().write(Double.parseDouble(data[1]),
                                    Double.parseDouble(data[2]), 1000);
                            if (!status) {
                                if (!emitter.isDisposed()) {
                                    emitter.onNext(false);
                                    emitter.onComplete();
                                }
                                return;
                            }
                        }
                    }

                    if (TrackRecUtil.getInstance().isCached()) {
                        TrackRecUtil.getInstance().flush();
                    }

                } catch (Exception e) {
                    Log.e("readNavTestData", "readNavTestData: ", e);
                    if (!emitter.isDisposed()) {
                        emitter.onNext(false);
                        emitter.onComplete();
                        return;
                    }
                }

                TrackRecUtil.getInstance().close();

                if (!emitter.isDisposed()) {
                    emitter.onNext(true);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Throwable {
                        mConvertStatus.setValue(aBoolean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.e(TAG, "accept: ", throwable);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Throwable {

                    }
                });

    }


    public LiveData<HistoricalTrack> observeHisTrack() {
        return mHistTrack;
    }

    private PointCollection collection;
    public void loadHistTrack(@NonNull String path) {
        mLoadDisposable = Observable.create(new ObservableOnSubscribe<HistoricalTrack>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<HistoricalTrack> emitter) throws Throwable {
                collection = new PointCollection(SpatialReferences.getWgs84());
                boolean r = TrackRecUtil.getInstance().open(
                        "/sdcard/Android/data/com.rgspace.cocapture/files/hisTrackTest",
                        true, false);
                if (!r) {
                    if (!emitter.isDisposed()) {
                        emitter.onError(new Exception("open track record failed"));
                    }
                    return;
                }
                while (!TrackRecUtil.getInstance().isExhausted()) {
                    double[][] lines = TrackRecUtil.getInstance().read();
                    if (lines.length == 0) {
                        break;
                    }
                    for (double[] line : lines) {
                        Point wgs = new Point(line[1], line[0], 0, SpatialReferences.getWgs84());
                        Point point = (Point) GeometryEngine.project(wgs,
                                SpatialReferences.getWebMercator());
                        collection.add(point);
                    }
                }

                TrackRecUtil.getInstance().close();

                if (!emitter.isDisposed()) {
                    emitter.onNext(new HistoricalTrack(new Polyline(collection),
                            new Viewpoint(collection.get(0).getY(), collection.get(0).getX(),
                                    10000)));
                    emitter.onComplete();
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HistoricalTrack>() {
                    @Override
                    public void accept(HistoricalTrack aBoolean) throws Throwable {
                        mHistTrack.setValue(aBoolean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.e(TAG, "accept: ", throwable);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Throwable {

                    }
                });


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
        // return loadTextManuallyInputData1();
        // return loadTextManuallyInputData2();
        return loadTextManuallyInputData3();
    }

    // when using ByteBuffer together with Okio, it can't deal with double type rightly.
    private boolean loadTextManuallyInputData1() {
        Sink sink;
        try {
            // emulator
            // sink = Okio.sink(new File("/storage/emulated/0/Android/data/com.example
            // .demoarcgis/cache/m_bin_raw"), true);
            // device
            sink = Okio.sink(new File("/sdcard/Android/data/com.example" +
                    ".demoarcgis/files/track/m_bin_raw"), true);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "openTrackRecord: path isn't exists");
            return false;
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        Log.e(TAG, "loadTextManuallyInputData: allocate pos=" + byteBuffer.position()
                + ", l=" + byteBuffer.limit() + ", remain=" + byteBuffer.remaining());
        int x = 0;
        int y = 0;
        long z = 0;
        long p = 0;
        try (GzipSink gzipSink = new GzipSink(sink);
             BufferedSink bufferedSink = Okio.buffer(gzipSink)
             // BufferedSink bufferedSink = Okio.buffer(sink)
        ) {
            for (int i = 0; i < 4096 * 2; i++) {
                Log.e(TAG, "loadTextManuallyInputData: 0 remaining=" + byteBuffer.remaining()
                        + ", l=" + byteBuffer.limit() + ", p=" + p + ", i=" + i + ", pos=" + byteBuffer.position());

                // 复杂度：利用了小循环，复杂度=(总行数/容量 + 1)次的小循环； 总行数=4096*2，容量等于4096
                if (byteBuffer.position() < byteBuffer.capacity()) {
                    byteBuffer.putDouble(10000 + i);
                    byteBuffer.putDouble(20000 + i);
                    p += 2;
                    continue;
                }

                Log.e(TAG, "loadTextManuallyInputData: remaining=" + byteBuffer.remaining()
                        + ", l=" + byteBuffer.limit() + ", p=" + p + ", i=" + i + ", pos=" + byteBuffer.position());
                byteBuffer.rewind();
                int l = byteBuffer.limit();
                int n = bufferedSink.write(byteBuffer);
                if (n == 0 || n != l) {
                    Log.e(TAG, "loadTextManuallyInputData: bufferedSink.write2 n= " + n);
                    return false;
                }
                bufferedSink.flush();
                byteBuffer.clear();
                z += n;
                y++;
                x++;
                Log.e(TAG, "loadTextManuallyInputData: position full, n=" + n + ", y=" + y);

                byteBuffer.putDouble(10240f);
                byteBuffer.putDouble(20480f);
                p += 2;
            }
            Log.e(TAG, "loadTextManuallyInputData: bufferedSink.flush1 times x=" + x
                    + ", y = " + y + ", z = " + z);

            // 循环不是2的倍数时
            if (byteBuffer.position() != 0) {
                int pNull0 = byteBuffer.position();
                byteBuffer.flip();
                int n = bufferedSink.write(byteBuffer);
                if (n == 0) {
                    Log.e(TAG, "loadTextManuallyInputData: bufferedSink.write2 = 0");
                    return false;
                }
                bufferedSink.flush();
                byteBuffer.clear();
                z += n;
                y++;
                Log.e(TAG, "loadTextManuallyInputData: position != 0, n=" + n + ", y=" + y
                        + ", pos=" + pNull0);
            }
        } catch (Exception e) {
            Log.e(TAG, "loadTextManuallyInputData: ", e);
            return false;
        }

        // x = 8192, y = 32, z = 131072
        Log.e(TAG, "loadTextManuallyInputData: bufferedSink.flush2 times x=" + x
                + ", y = " + y + ", z = " + z);
        return true;
    }

    // when using ByteBuffer together with Okio, it can't deal with double type rightly.
    private boolean loadTextManuallyInputData2() {
        Sink sink;
        try {
            // emulator
            // sink = Okio.sink(new File("/storage/emulated/0/Android/data/com.example
            // .demoarcgis/cache/m_bin_raw"), true);
            // device
            sink = Okio.sink(new File("/sdcard/Android/data/com.example" +
                    ".demoarcgis/files/track/m_bin_raw"), true);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "openTrackRecord: path isn't exists");
            return false;
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        Log.e(TAG, "loadTextManuallyInputData: allocate pos=" + byteBuffer.position()
                + ", l=" + byteBuffer.limit() + ", remain=" + byteBuffer.remaining());
        int x = 0;
        int y = 0;
        long z = 0;
        long p = 0;
        try (GzipSink gzipSink = new GzipSink(sink);
             BufferedSink bufferedSink = Okio.buffer(gzipSink)) {
            for (int i = 0; i < 4096 * 2; i++) {
                Log.e(TAG, "loadTextManuallyInputData: 0 remaining=" + byteBuffer.remaining()
                        + ", l=" + byteBuffer.limit() + ", p=" + p + ", i=" + i + ", pos=" + byteBuffer.position());

                if (byteBuffer.position() < byteBuffer.capacity()) {
                    byteBuffer.putDouble(10000 + i);
                    byteBuffer.putDouble(20000 + i);
                    p += 2;
                }

                // 复杂度8192次比较
                Log.e(TAG, "loadTextManuallyInputData: remaining=" + byteBuffer.remaining()
                        + ", l=" + byteBuffer.limit() + ", p=" + p + ", i=" + i + ", pos=" + byteBuffer.position());
                if (byteBuffer.position() == byteBuffer.capacity()) {
                    byteBuffer.rewind();
                    int l = byteBuffer.limit();
                    int n = bufferedSink.write(byteBuffer);
                    if (n == 0 || n != l) {
                        Log.e(TAG, "loadTextManuallyInputData: bufferedSink.write2 n= " + n);
                        return false;
                    }
                    Log.e(TAG, "loadTextManuallyInputData: n=" + n);
                    bufferedSink.flush();
                    byteBuffer.clear();
                    z += n;
                    y++;
                }

                x++;
            }

            if (byteBuffer.position() != 0) {
                byteBuffer.flip();
                int l = byteBuffer.limit();
                int n = bufferedSink.write(byteBuffer);
                if (n == 0 || n != l) {
                    Log.e(TAG, "loadTextManuallyInputData: bufferedSink.write2 n= " + n);
                    return false;
                }
                Log.e(TAG, "loadTextManuallyInputData: n=" + n);
                bufferedSink.flush();
                byteBuffer.clear();
                z += n;
                y++;
            }
            Log.e(TAG, "loadTextManuallyInputData: bufferedSink.flush1 times x=" + x
                    + ", y = " + y + ", z = " + z);
        } catch (Exception e) {
            Log.e(TAG, "loadTextManuallyInputData: ", e);
            return false;
        }

        // x = 8192, y = 32, z = 131072
        Log.e(TAG, "loadTextManuallyInputData: bufferedSink.flush2 times x=" + x
                + ", y = " + y + ", z = " + z);
        return true;
    }

    // use byte[]
    private boolean loadTextManuallyInputData3() {
        Sink sink;
        try {
            // emulator
            // sink = Okio.sink(new File("/storage/emulated/0/Android/data/com.example
            // .demoarcgis/cache/m_bin_raw"), true);
            // device
            sink = Okio.sink(new File("/sdcard/Android/data/com.example" +
                    ".demoarcgis/files/track/m_bin_raw"), true);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "openTrackRecord: path isn't exists");
            return false;
        }

        byte[] bytes = new byte[4096];
        int j = 0;
        int x = 0;
        int y = 0;
        long z = 0;
        long p = 0;
        try (GzipSink gzipSink = new GzipSink(sink);
             BufferedSink bufferedSink = Okio.buffer(gzipSink)) {
            for (int i = 0; i < 4096 * 2; i++) {
                Log.e(TAG, "loadTextManuallyInputData: 0 remaining="
                        + ", l=" + ", p=" + p + ", i=" + i);
                if (p < bytes.length) {
                    long v = Double.doubleToRawLongBits(10000.1f + i);
                    // Double.doubleToLongBits(10000 + i);
                    bytes[j++] = (byte) ((v & 0xff00000000000000L) >>> 56);
                    bytes[j++] = (byte) ((v & 0x00ff000000000000L) >>> 48);
                    bytes[j++] = (byte) ((v & 0x0000ff0000000000L) >>> 40);
                    bytes[j++] = (byte) ((v & 0x000000ff00000000L) >>> 32);
                    bytes[j++] = (byte) ((v & 0x00000000ff000000L) >>> 24);
                    bytes[j++] = (byte) ((v & 0x0000000000ff0000L) >>> 16);
                    bytes[j++] = (byte) ((v & 0x000000000000ff00L) >>> 8);
                    bytes[j++] = (byte) ((v & 0x00000000000000ffL));
                    v = Double.doubleToRawLongBits(20000.33f + i);
                    bytes[j++] = (byte) ((v & 0xff00000000000000L) >>> 56);
                    bytes[j++] = (byte) ((v & 0x00ff000000000000L) >>> 48);
                    bytes[j++] = (byte) ((v & 0x0000ff0000000000L) >>> 40);
                    bytes[j++] = (byte) ((v & 0x000000ff00000000L) >>> 32);
                    bytes[j++] = (byte) ((v & 0x00000000ff000000L) >>> 24);
                    bytes[j++] = (byte) ((v & 0x0000000000ff0000L) >>> 16);
                    bytes[j++] = (byte) ((v & 0x000000000000ff00L) >>> 8);
                    bytes[j++] = (byte) ((v & 0x00000000000000ffL));
                }

                // 复杂度8192次比较
                Log.e(TAG, "loadTextManuallyInputData: remaining=" + (bytes.length - j)
                        + ", i=" + i + ", pos=" + j);
                if (j == bytes.length) {
                    bufferedSink.write(bytes);
                    j = 0;
                    z += bytes.length;
                    y++;
                }

                x++;
            }

            if (j != 0) {
                bufferedSink.write(bytes, 0, j);
                z += j;
                y++;
            }
            Log.e(TAG, "loadTextManuallyInputData: bufferedSink.flush1 times x=" + x
                    + ", y = " + y + ", z = " + z);
        } catch (Exception e) {
            Log.e(TAG, "loadTextManuallyInputData: ", e);
            return false;
        }

        // x = 8192, y = 32, z = 131072
        Log.e(TAG, "loadTextManuallyInputData: bufferedSink.flush2 times x=" + x
                + ", y = " + y + ", z = " + z);
        return true;
    }

    // use BigDecimal
    private boolean loadTextManuallyInputData4() {
        Sink sink;
        try {
            // emulator
            // sink = Okio.sink(new File("/storage/emulated/0/Android/data/com.example
            // .demoarcgis/cache/m_bin_raw"), true);
            // device
            sink = Okio.sink(new File("/sdcard/Android/data/com.example" +
                    ".demoarcgis/files/track/m_bin_raw"), true);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "openTrackRecord: path isn't exists");
            return false;
        }

        byte[] bytes = new byte[4096];
        int j = 0;
        int x = 0;
        int y = 0;
        long z = 0;
        long p = 0;
        try (GzipSink gzipSink = new GzipSink(sink);
             BufferedSink bufferedSink = Okio.buffer(gzipSink)) {
            for (int i = 0; i < 4096 * 2; i++) {
                Log.e(TAG, "loadTextManuallyInputData: 0 remaining="
                        + ", l=" + ", p=" + p + ", i=" + i);
                if (p < bytes.length) {
                    long v = Double.doubleToRawLongBits(10000.1f + i);
                    // Double.doubleToLongBits(10000 + i);
                    bytes[j++] = (byte) ((v & 0xff00000000000000L) >>> 56);
                    bytes[j++] = (byte) ((v & 0x00ff000000000000L) >>> 48);
                    bytes[j++] = (byte) ((v & 0x0000ff0000000000L) >>> 40);
                    bytes[j++] = (byte) ((v & 0x000000ff00000000L) >>> 32);
                    bytes[j++] = (byte) ((v & 0x00000000ff000000L) >>> 24);
                    bytes[j++] = (byte) ((v & 0x0000000000ff0000L) >>> 16);
                    bytes[j++] = (byte) ((v & 0x000000000000ff00L) >>> 8);
                    bytes[j++] = (byte) ((v & 0x00000000000000ffL));
                    v = Double.doubleToRawLongBits(20000.33f + i);
                    bytes[j++] = (byte) ((v & 0xff00000000000000L) >>> 56);
                    bytes[j++] = (byte) ((v & 0x00ff000000000000L) >>> 48);
                    bytes[j++] = (byte) ((v & 0x0000ff0000000000L) >>> 40);
                    bytes[j++] = (byte) ((v & 0x000000ff00000000L) >>> 32);
                    bytes[j++] = (byte) ((v & 0x00000000ff000000L) >>> 24);
                    bytes[j++] = (byte) ((v & 0x0000000000ff0000L) >>> 16);
                    bytes[j++] = (byte) ((v & 0x000000000000ff00L) >>> 8);
                    bytes[j++] = (byte) ((v & 0x00000000000000ffL));
                }

                // 复杂度8192次比较
                Log.e(TAG, "loadTextManuallyInputData: remaining=" + (bytes.length - j)
                        + ", i=" + i + ", pos=" + j);
                if (j == bytes.length) {
                    bufferedSink.write(bytes);
                    j = 0;
                    z += bytes.length;
                    y++;
                }

                x++;
            }

            if (j != 0) {
                bufferedSink.write(bytes, 0, j);
                z += j;
                y++;
            }
            Log.e(TAG, "loadTextManuallyInputData: bufferedSink.flush1 times x=" + x
                    + ", y = " + y + ", z = " + z);
        } catch (Exception e) {
            Log.e(TAG, "loadTextManuallyInputData: ", e);
            return false;
        }

        // x = 8192, y = 32, z = 131072
        Log.e(TAG, "loadTextManuallyInputData: bufferedSink.flush2 times x=" + x
                + ", y = " + y + ", z = " + z);
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
        // return loadBinaryManuallyInputData1();
        // return loadBinaryManuallyInputData2();
        return loadBinaryManuallyInputData3();
    }

    // when using ByteBuffer together with Okio, it can't deal with double type rightly.
    private boolean loadBinaryManuallyInputData1() {
        // Source source;
        // try {
        //     source = Okio.source(new File("/sdcard/Android/data/com.example" +
        //             ".demoarcgis/files/track/m_bin_raw"));
        // } catch (FileNotFoundException e) {
        //     Log.e(TAG, "openTrackRecord: path isn't exists");
        //     return false;
        // }
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        // PointCollection collection = new PointCollection((Iterable<Point>) null,
        //         SpatialReferences.getWebMercator());

        List<Double> list = new ArrayList<>();
        List<String> listText = new ArrayList<>();
        long index = 0;
        long x = 0;
        long y = 0;
        long total = 0;
        try (// used for emulator
             // Source source = Okio.source(new File("/storage/emulated/0/Android/data/com
             // .example" +
             //         ".demoarcgis/cache/m_bin_raw"));

             // used for device.
             Source source = Okio.source(new File("/sdcard/Android/data/com.example" +
                     ".demoarcgis/files/track/m_bin_raw"));
             BufferedSource bufferedSource = Okio.buffer(new GzipSource(source))) {
            NumberFormat format = NumberFormat.getInstance();

            format.setGroupingUsed(false);
            format.setMaximumFractionDigits(6);
            // BufferedSource bufferedSource = Okio.buffer(source);
            while (!bufferedSource.exhausted()) {
                int n = bufferedSource.read(byteBuffer);
                if (n != -1) {
                    total += n;
                }

                if (n % 8 != 0) {
                    Log.e(TAG, "loadBinaryManuallyInputData1 n=" + n + ", x=" + x
                            + ", l=" + byteBuffer.limit());
                }

                x++;

                // 读取数据不全，读取的不一定是8的倍数。
                // 如果调用了，当遇到读取数据长度不是8的倍数时就会出现数据丢失情况。
                // rewind:position ->0 mark discard
                int remainder = byteBuffer.limit() & 0x07;
                if (remainder != 0) {
                    // 读取数据不全，读取的不一定是8的倍数。
                    int part = byteBuffer.limit() - remainder;
                    while (byteBuffer.position() < part) {
                        // 读取不全，缓存里不一定有第二个数
                        // collection.add(doubleBuffer.get(), doubleBuffer.get(), 0);//

                        double d = byteBuffer.getDouble();
                        list.add(d);
                        listText.add(format.format(d));
                        Log.e(TAG, "loadBinaryManuallyInputData: p=" + byteBuffer.position() + "," +
                                " " +
                                "l=" + byteBuffer.limit() + ", part=" + part);
                        // list.add(doubleBuffer.get());
                        y++;
                    }
                    byteBuffer.compact();
                    continue;
                }

                byteBuffer.rewind();
                while (byteBuffer.position() < byteBuffer.limit()) {
                    // 读取不全，缓存里不一定有第二个数
                    // collection.add(byteBuffer.get(), byteBuffer.get(), 0);//
                    double d = byteBuffer.getDouble();
                    list.add(d);
                    listText.add(format.format(d));
                    Log.e(TAG, "loadBinaryManuallyInputData: p=" + byteBuffer.position() + ", " +
                            "l=" + byteBuffer.limit());
                    y++;
                }

                byteBuffer.clear();
                Log.e(TAG, "\n\n\n\n<--p=  x= " + x + "\n\n\n\n");

            }
        } catch (Exception e) {
            Log.e(TAG, "readTrackRecord[" + index + "]: ", e);
            return false;
        }

        // y = 8192
        Log.e(TAG,
                "loadBinaryManuallyInputData1 [x=" + (x - 1) + "], [y=" + (y - 1) + "]"
                        + "size=" + list.size() + ", total=" + total);
        for (int i = 1; i <= (listText.size() / 2); i += 2) {
            Log.e(TAG, "loadBinaryManuallyInputData1: point1=" + listText.get(i - 1)
                    + ", point2=" + listText.get(i));
        }

        if (listText.size() % 2 != 0) {
            Log.e(TAG, "loadBinaryManuallyInputData1: point1=" + listText.get(listText.size() - 1));
        }

        Sink sink;
        try {
            // emulator
            // sink = Okio.sink(new File("/storage/emulated/0/Android/data/com.example
            // .demoarcgis/cache/m_text_raw"));
            // device
            sink = Okio.sink(new File("/sdcard/Android/data/com.example" +
                    ".demoarcgis/files/track/m_text_raw"));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "openTrackRecord: path isn't exists");
            return false;
        }


        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        format.setMaximumFractionDigits(10);
        format.setMinimumFractionDigits(10);
        long z = 0;
        try (BufferedSink bufferedSink = Okio.buffer(sink)) {
            StringBuilder builder = new StringBuilder();
            for (int n = 0; n < list.size(); n++) {
                builder.append(format.format(list.get(n)));
                if ((n & 0x01) == 1) {
                    builder.append("\n");
                } else {
                    builder.append("\t");
                }


                // builder.append(Double.toString(collection.get(n).getY()));
                // builder.append("\t");
                // builder.append(Double.toString(collection.get(n).getX()));
                // builder.append("\n");
                if (builder.length() >= 4096) {
                    bufferedSink.writeString(builder.toString(), StandardCharsets.UTF_8);
                    bufferedSink.flush();
                    builder.setLength(0);
                    z++;
                }
            }

            if (builder.length() > 0) {
                bufferedSink.writeString(builder.toString(), StandardCharsets.UTF_8);
                bufferedSink.flush();
                builder.setLength(0);
                z++;
            }
        } catch (Exception e) {
            Log.e(TAG, "loadBinaryManuallyInputData: ", e);
            return false;
        }

        // z = 4
        Log.e(TAG, "loadBinaryManuallyInputData2 [z=" + z + "]");

        return true;
    }

    // when using ByteBuffer together with Okio, it can't deal with double type rightly.
    private boolean loadBinaryManuallyInputData2() {
        // Source source;
        // try {
        //     source = Okio.source(new File("/sdcard/Android/data/com.example" +
        //             ".demoarcgis/files/track/m_bin_raw"));
        // } catch (FileNotFoundException e) {
        //     Log.e(TAG, "openTrackRecord: path isn't exists");
        //     return false;
        // }
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        // PointCollection collection = new PointCollection((Iterable<Point>) null,
        //         SpatialReferences.getWebMercator());

        List<Double> list = new ArrayList<>();
        long index = 0;
        long x = 0;
        long y = 0;
        long total = 0;
        try {
            // used for emulator
            // Source source = Okio.source(new File("/storage/emulated/0/Android/data/com.example" +
            //         ".demoarcgis/cache/m_bin_raw"));

            // used for device.
            Source source = Okio.source(new File("/sdcard/Android/data/com.example" +
                    ".demoarcgis/files/track/m_bin_raw"));
            BufferedSource bufferedSource = Okio.buffer(new GzipSource(source));
            while (!bufferedSource.exhausted()) {
                int n = bufferedSource.read(byteBuffer);
                if (n != -1) {
                    total += n;
                }

                if (n % 8 != 0) {
                    Log.e(TAG, "loadBinaryManuallyInputData1 n=" + n + ", x=" + x
                            + ", l=" + byteBuffer.limit());
                }

                x++;

                // 读取数据不全，读取的不一定是8的倍数。
                // 如果调用了，当遇到读取数据长度不是8的倍数时就会出现数据丢失情况。
                // rewind:position ->0 mark discard
                int remainder = byteBuffer.limit() & 0x07;
                if (remainder != 0) {
                    // 读取数据不全，读取的不一定是8的倍数。
                    int part = byteBuffer.limit() - remainder;
                    while (byteBuffer.position() < part) {
                        // 读取不全，缓存里不一定有第二个数
                        // collection.add(doubleBuffer.get(), doubleBuffer.get(), 0);//
                        list.add(byteBuffer.getDouble());
                        Log.e(TAG, "loadBinaryManuallyInputData: p=" + byteBuffer.position() + "," +
                                " " +
                                "l=" + byteBuffer.limit() + ", part=" + part);
                        // list.add(doubleBuffer.get());
                        y++;
                    }
                    byteBuffer.compact();
                    continue;
                }

                byteBuffer.rewind();
                while (byteBuffer.position() < byteBuffer.limit()) {
                    // 读取不全，缓存里不一定有第二个数
                    // collection.add(byteBuffer.get(), byteBuffer.get(), 0);//
                    list.add(byteBuffer.getDouble());
                    Log.e(TAG, "loadBinaryManuallyInputData: p=" + byteBuffer.position() + ", " +
                            "l=" + byteBuffer.limit());
                    y++;
                }

                byteBuffer.clear();
                Log.e(TAG, "\n\n\n\n<--p=  x= " + x + "\n\n\n\n");

            }
        } catch (Exception e) {
            Log.e(TAG, "readTrackRecord[" + index + "]: ", e);
            return false;
        }

        // y = 8192
        Log.e(TAG,
                "loadBinaryManuallyInputData1 [x=" + (x - 1) + "], [y=" + (y - 1) + "]"
                        + "size=" + list.size() + ", total=" + total);

        Sink sink;
        try {
            // emulator
            // sink = Okio.sink(new File("/storage/emulated/0/Android/data/com.example
            // .demoarcgis/cache/m_text_raw"));
            // device
            sink = Okio.sink(new File("/sdcard/Android/data/com.example" +
                    ".demoarcgis/files/track/m_text_raw"));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "openTrackRecord: path isn't exists");
            return false;
        }


        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        format.setMaximumFractionDigits(10);
        format.setMinimumFractionDigits(10);
        long z = 0;
        try (BufferedSink bufferedSink = Okio.buffer(sink)) {
            StringBuilder builder = new StringBuilder();
            for (int n = 0; n < list.size(); n++) {
                builder.append(format.format(list.get(n)));
                if ((n & 0x01) == 1) {
                    builder.append("\n");
                } else {
                    builder.append("\t");
                }


                // builder.append(Double.toString(collection.get(n).getY()));
                // builder.append("\t");
                // builder.append(Double.toString(collection.get(n).getX()));
                // builder.append("\n");
                if (builder.length() >= 4096) {
                    bufferedSink.writeString(builder.toString(), StandardCharsets.UTF_8);
                    bufferedSink.flush();
                    builder.setLength(0);
                    z++;
                }
            }

            if (builder.length() > 0) {
                bufferedSink.writeString(builder.toString(), StandardCharsets.UTF_8);
                bufferedSink.flush();
                builder.setLength(0);
                z++;
            }
        } catch (Exception e) {
            Log.e(TAG, "loadBinaryManuallyInputData: ", e);
            return false;
        }

        // z = 4
        Log.e(TAG, "loadBinaryManuallyInputData2 [z=" + z + "]");

        return true;
    }

    // use byte[]
    private boolean loadBinaryManuallyInputData3() {
        // Source source;
        // try {
        //     source = Okio.source(new File("/sdcard/Android/data/com.example" +
        //             ".demoarcgis/files/track/m_bin_raw"));
        // } catch (FileNotFoundException e) {
        //     Log.e(TAG, "openTrackRecord: path isn't exists");
        //     return false;
        // }
        byte[] bytes = new byte[4096];
        int offset = 0;
        // PointCollection collection = new PointCollection((Iterable<Point>) null,
        //         SpatialReferences.getWebMercator());


        List<Double> list = new ArrayList<>();
        long index = 0;
        long x = 0;
        long y = 0;
        long total = 0;
        try (// used for emulator
             // Source source = Okio.source(new File("/storage/emulated/0/Android/data/com
             // .example" +
             //         ".demoarcgis/cache/m_bin_raw"));

             // used for device.
             Source source = Okio.source(new File("/sdcard/Android/data/com.example" +
                     ".demoarcgis/files/track/m_bin_raw"));
             BufferedSource bufferedSource = Okio.buffer(new GzipSource(source))) {

            while (!bufferedSource.exhausted()) {
                int n = bufferedSource.read(bytes, offset, bytes.length - offset);
                if (n != -1) {
                    total += n;
                }

                offset += n;
                if (offset % 8 != 0) {
                    Log.e(TAG, "loadBinaryManuallyInputData1 n=" + n + ", x=" + x);
                    continue;
                }

                x++;

                // 读取数据不全，读取的不一定是8的倍数。
                // 如果调用了，当遇到读取数据长度不是8的倍数时就会出现数据丢失情况。
                // rewind:position ->0 mark discard
                // 读取数据不全，读取的不一定是8的倍数。
                for (int pti = 0; pti < offset; pti += 8) {
                    // 读取不全，缓存里不一定有第二个数
                    // collection.add(doubleBuffer.get(), doubleBuffer.get(), 0);//
                    long l = ((bytes[pti] & 0xffL) << 56)
                            | ((bytes[pti + 1] & 0xffL) << 48)
                            | ((bytes[pti + 2] & 0xffL) << 40)
                            | ((bytes[pti + 3] & 0xffL) << 32)
                            | ((bytes[pti + 4] & 0xffL) << 24)
                            | ((bytes[pti + 5] & 0xffL) << 16)
                            | ((bytes[pti + 6] & 0xffL) << 8)
                            | (bytes[pti + 7] & 0xffL);
                    list.add(Double.longBitsToDouble(l));
                    Log.e(TAG, "loadBinaryManuallyInputData: pos=" + pti);
                    // list.add(doubleBuffer.get());
                    y++;
                }

                offset = 0;
                Log.e(TAG, "\n\n\n\n<--p=  x= " + x + "\n\n\n\n");
            }
        } catch (Exception e) {
            Log.e(TAG, "readTrackRecord[" + index + "]: ", e);
            return false;
        }

        // y = 8192
        Log.e(TAG,
                "loadBinaryManuallyInputData1 [x=" + (x - 1) + "], [y=" + (y - 1) + "]"
                        + "size=" + list.size() + ", total=" + total);

        Sink sink;
        try {
            // emulator
            // sink = Okio.sink(new File("/storage/emulated/0/Android/data/com.example
            // .demoarcgis/cache/m_text_raw"));
            // device
            sink = Okio.sink(new File("/sdcard/Android/data/com.example" +
                    ".demoarcgis/files/track/m_text_raw"));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "openTrackRecord: path isn't exists");
            return false;
        }


        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        format.setMaximumFractionDigits(10);
        long z = 0;
        try (BufferedSink bufferedSink = Okio.buffer(sink)) {
            StringBuilder builder = new StringBuilder();
            for (int n = 0; n < list.size(); n++) {
                builder.append(format.format(list.get(n)));
                if ((n & 0x01) == 1) {
                    builder.append("\n");
                } else {
                    builder.append("\t");
                }
                Log.e(TAG, "loadBinaryManuallyInputData3: " + builder.toString());


                // builder.append(Double.toString(collection.get(n).getY()));
                // builder.append("\t");
                // builder.append(Double.toString(collection.get(n).getX()));
                // builder.append("\n");
                if (builder.length() >= 4096) {
                    bufferedSink.writeString(builder.toString(), StandardCharsets.UTF_8);
                    bufferedSink.flush();
                    builder.setLength(0);
                    z++;
                }
            }

            if (builder.length() > 0) {
                Log.e(TAG, "loadBinaryManuallyInputData3: " + builder.toString());
                bufferedSink.writeString(builder.toString(), StandardCharsets.UTF_8);
                bufferedSink.flush();
                builder.setLength(0);
                z++;
            }
        } catch (Exception e) {
            Log.e(TAG, "loadBinaryManuallyInputData: ", e);
            return false;
        }

        // z = 4
        Log.e(TAG, "loadBinaryManuallyInputData2 [z=" + z + "]");

        return true;
    }

    // use BigDecimal
    private boolean loadBinaryManuallyInputData4() {
        // Source source;
        // try {
        //     source = Okio.source(new File("/sdcard/Android/data/com.example" +
        //             ".demoarcgis/files/track/m_bin_raw"));
        // } catch (FileNotFoundException e) {
        //     Log.e(TAG, "openTrackRecord: path isn't exists");
        //     return false;
        // }
        byte[] bytes = new byte[4096];
        int offset = 0;
        // PointCollection collection = new PointCollection((Iterable<Point>) null,
        //         SpatialReferences.getWebMercator());


        List<Double> list = new ArrayList<>();
        long index = 0;
        long x = 0;
        long y = 0;
        long total = 0;
        try (// used for emulator
             // Source source = Okio.source(new File("/storage/emulated/0/Android/data/com
             // .example" +
             //         ".demoarcgis/cache/m_bin_raw"));

             // used for device.
             Source source = Okio.source(new File("/sdcard/Android/data/com.example" +
                     ".demoarcgis/files/track/m_bin_raw"));
             BufferedSource bufferedSource = Okio.buffer(new GzipSource(source))) {

            while (!bufferedSource.exhausted()) {
                int n = bufferedSource.read(bytes, offset, bytes.length - offset);
                if (n != -1) {
                    total += n;
                }

                offset += n;
                if (offset % 8 != 0) {
                    Log.e(TAG, "loadBinaryManuallyInputData1 n=" + n + ", x=" + x);
                    continue;
                }

                x++;

                // 读取数据不全，读取的不一定是8的倍数。
                // 如果调用了，当遇到读取数据长度不是8的倍数时就会出现数据丢失情况。
                // rewind:position ->0 mark discard
                // 读取数据不全，读取的不一定是8的倍数。
                for (int pti = 0; pti < offset; pti += 8) {
                    // 读取不全，缓存里不一定有第二个数
                    // collection.add(doubleBuffer.get(), doubleBuffer.get(), 0);//
                    long l = ((bytes[pti] & 0xffL) << 56)
                            | ((bytes[pti + 1] & 0xffL) << 48)
                            | ((bytes[pti + 2] & 0xffL) << 40)
                            | ((bytes[pti + 3] & 0xffL) << 32)
                            | ((bytes[pti + 4] & 0xffL) << 24)
                            | ((bytes[pti + 5] & 0xffL) << 16)
                            | ((bytes[pti + 6] & 0xffL) << 8)
                            | (bytes[pti + 7] & 0xffL);
                    list.add(Double.longBitsToDouble(l));
                    Log.e(TAG, "loadBinaryManuallyInputData: pos=" + pti);
                    // list.add(doubleBuffer.get());
                    y++;
                }

                offset = 0;
                Log.e(TAG, "\n\n\n\n<--p=  x= " + x + "\n\n\n\n");
            }
        } catch (Exception e) {
            Log.e(TAG, "readTrackRecord[" + index + "]: ", e);
            return false;
        }

        // y = 8192
        Log.e(TAG,
                "loadBinaryManuallyInputData1 [x=" + (x - 1) + "], [y=" + (y - 1) + "]"
                        + "size=" + list.size() + ", total=" + total);

        Sink sink;
        try {
            // emulator
            // sink = Okio.sink(new File("/storage/emulated/0/Android/data/com.example
            // .demoarcgis/cache/m_text_raw"));
            // device
            sink = Okio.sink(new File("/sdcard/Android/data/com.example" +
                    ".demoarcgis/files/track/m_text_raw"));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "openTrackRecord: path isn't exists");
            return false;
        }


        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        format.setMaximumFractionDigits(10);
        long z = 0;
        try (BufferedSink bufferedSink = Okio.buffer(sink)) {
            StringBuilder builder = new StringBuilder();
            for (int n = 0; n < list.size(); n++) {
                builder.append(format.format(list.get(n)));
                if ((n & 0x01) == 1) {
                    builder.append("\n");
                } else {
                    builder.append("\t");
                }
                Log.e(TAG, "loadBinaryManuallyInputData3: " + builder.toString());


                // builder.append(Double.toString(collection.get(n).getY()));
                // builder.append("\t");
                // builder.append(Double.toString(collection.get(n).getX()));
                // builder.append("\n");
                if (builder.length() >= 4096) {
                    bufferedSink.writeString(builder.toString(), StandardCharsets.UTF_8);
                    bufferedSink.flush();
                    builder.setLength(0);
                    z++;
                }
            }

            if (builder.length() > 0) {
                Log.e(TAG, "loadBinaryManuallyInputData3: " + builder.toString());
                bufferedSink.writeString(builder.toString(), StandardCharsets.UTF_8);
                bufferedSink.flush();
                builder.setLength(0);
                z++;
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

        if (mConvDisposable != null && !mConvDisposable.isDisposed()) {
            mConvDisposable.dispose();
            mConvDisposable = null;
        }

        if (mLoadDisposable != null && !mLoadDisposable.isDisposed()) {
            mLoadDisposable.dispose();
            mLoadDisposable = null;
        }
    }

}
