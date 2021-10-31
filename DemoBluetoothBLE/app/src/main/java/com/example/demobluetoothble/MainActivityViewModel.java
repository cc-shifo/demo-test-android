package com.example.demobluetoothble;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

public class MainActivityViewModel extends AndroidViewModel {
    private static final String TAG = "MainActivityViewModel";
    private final MutableLiveData<List<BluetoothDevice>> mDevices;
    private final MutableLiveData<String> mResult;
    private final MutableLiveData<String> mSendData;
    private final MutableLiveData<String> mRecvData;

    private final HashMap<String, BluetoothDevice> mMap = new HashMap<>();
    private final CompositeDisposable mDisposable;

    private final BluetoothAdapter mBluetoothAdapter;
    private final BluetoothLeScanner mScanner;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mWriteCharacteristic;
    private BluetoothGattCharacteristic mReadCharacteristic;
    private Disposable mTxRxDisposable;
    private final BluetoothGattCallback mClientCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // intentAction = ACTION_GATT_CONNECTED;
                // connectionState = STATE_CONNECTED;
                // broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());
                mResult.setValue("Connected to GATT server.");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // intentAction = ACTION_GATT_DISCONNECTED;
                // connectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                // broadcastUpdate(intentAction);
                mResult.setValue("Disconnected from server.");
                mReadCharacteristic = null;
                mWriteCharacteristic = null;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                List<BluetoothDevice> devices = gatt.getConnectedDevices();
                // List<BluetoothGattService> devices = gatt.getService();
                for (BluetoothDevice device : devices) {
                    Log.d(TAG, "onServicesDiscovered: " + device.getName());
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicRead: status=" + status);
            mResult.setValue("on Characteristic Read Properties: " + characteristic.getProperties());
            if (BluetoothGatt.GATT_SUCCESS == status) {
                mResult.setValue("on Characteristic Write");
                mReadCharacteristic = characteristic;
                recv();
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d(TAG, "onCharacteristicWrite: status=" + status);
            if (BluetoothGatt.GATT_SUCCESS == status) {
                mResult.setValue("on Characteristic Write");
                mWriteCharacteristic = characteristic;
            }

        }
    };
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.d(TAG, "onScanResult: " + result.getDevice().getName());

            mMap.put(result.getDevice().getName(), result.getDevice());
            mDevices.setValue(new ArrayList<>(mMap.values()));
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d(TAG, "onBatchScanResults: thread id=" + Thread.currentThread().getId());
            for (ScanResult result : results) {
                mMap.put(result.getDevice().getName(), result.getDevice());
            }
            mDevices.setValue(new ArrayList<>(mMap.values()));
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d(TAG, "onScanFailed: " + errorCode);
        }
    };

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        mDevices = new MutableLiveData<>();
        mResult = new MutableLiveData<>();
        mSendData = new MutableLiveData<>();
        mRecvData = new MutableLiveData<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mDisposable = new CompositeDisposable();
    }

    public static byte[] hexToByte(String hex) {
        int m = 0, n = 0;
        int byteLen = hex.length() / 2; // 每两个字符描述一个字节
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = (byte) intVal;
        }
        return ret;
    }

    public static String byteToHex(byte[] bytes) {
        String strHex = "";
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            strHex = Integer.toHexString(aByte & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
        }
        return sb.toString().trim();
    }

    /**
     * scanning duration: 12s
     */
    public void scanLeDevice() {
        mDisposable.add(Observable.intervalRange(0, 0, 12, 12, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Throwable {
                        stopScanning();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.e(TAG, "stopScanning accept: ", throwable);
                    }
                })
        );
        mScanner.startScan(mScanCallback);
    }

    public void stopScanning() {
        mScanner.stopScan(mScanCallback);
        mScanCallback = null;
        mDevices.setValue(new ArrayList<>(mMap.values()));
    }

    public void connect(@NonNull BluetoothDevice device) {
        // 31 requires connection permission
        mBluetoothGatt = device.connectGatt(getApplication(), true, mClientCallback);
    }

    public void disconnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }
    }

    public void send(String values) {
        if (mWriteCharacteristic != null) {
            byte[] data = hexToByte(values);
            mWriteCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            mWriteCharacteristic.setValue(data);
            mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
        }
    }

    private void recv() {
        if (mReadCharacteristic != null) {
            mReadCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<String> emitter) throws Throwable {
                    byte[] data = mReadCharacteristic.getValue();
                    if (data != null) {
                        String v = byteToHex(data);
                        if (!emitter.isDisposed()) {
                            emitter.onNext(v);
                            emitter.onComplete();
                        }
                    }

                }
            })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                            mTxRxDisposable = d;
                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull String o) {
                            mRecvData.setValue(o);
                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            Log.e(TAG, "onError: ", e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    public void destroy() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
        }
        mDisposable.clear();
    }
}
