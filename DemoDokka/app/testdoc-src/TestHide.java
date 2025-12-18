/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.example.demodokka.aidl.testdoc;

/**
 * test @hide
 * <ul>
 * <li>{@link Error#SUCCESS} 成功返回码</li>
 * <li>{@link Error#FAILED} 失败。</li>
 * </p>
 * </ul>
 */
public interface TestHide {
    /**
     * Default implementation for TestHide.
     * @hide
     */
    public static class Default {

        /**
         * Input PIN and get result through callback.
         *
         * @param param    the parameter for inputting PIN entry.
         * @param callback the callback for getting result of inputting action.
         */
        @Override
        public java.lang.String toString() {
            return "TestHide.Default";
        }

    }

    /**
     * Local-side IPC implementation stub class.
     * @hide
     */
    public static abstract class Stub extends android.os.Binder {
        private static final java.lang.String DESCRIPTOR = "com.example.demodokka.aidl.testdoc.TestHide";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an com.example.demodokka.aidl.testdoc.TestHide interface,
         * generating a proxy if needed.
         */
        public static com.example.demodokka.aidl.testdoc.TestHide asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof com.example.demodokka.aidl.testdoc.TestHide))) {
                return ((com.example.demodokka.aidl.testdoc.TestHide) iin);
            }
            return new com.example.demodokka.aidl.testdoc.TestHide.Stub.Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags)
                throws android.os.RemoteException {
            java.lang.String descriptor = DESCRIPTOR;
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
            }
        }

        /**
         * @hide
         */
        private static class Proxy {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            /**
             * Input PIN and get result through callback.
             *
             * @param param    the parameter for inputting PIN entry.
             * @param callback the callback for getting result of inputting action.
             * @hide
             */

            public void helloWorld(com.example.demodokka.aidl.testdoc.ParamPinPad param,
                    com.example.demodokka.aidl.testdoc.IPinPadCallback callback) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((param != null)) {
                        _data.writeInt(1);
                        param.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder((((callback != null)) ? (callback.asBinder()) : (null)));
                    boolean _status = mRemote.transact(Stub.TRANSACTION_startInputPIN, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().startInputPIN(param, callback);
                        return;
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public static com.example.demodokka.aidl.testdoc.TestHide sDefaultImpl;
        }

        static final int TRANSACTION_startInputPIN = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);

        public static boolean setDefaultImpl(com.example.demodokka.aidl.testdoc.TestHide impl) {
            // Only one user of this interface can use this function
            // at a time. This is a heuristic to detect if two different
            // users in the same process use this function.
            if (Stub.Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Stub.Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static com.example.demodokka.aidl.testdoc.TestHide getDefaultImpl() {
            return Stub.Proxy.sDefaultImpl;
        }
    }

    /**
     * test hello world
     *
     * @param param    the parameter for testing hello world
     * @param callback the callback for getting result of testing.
     */
    public void testHelloWorld(com.example.demodokka.aidl.testdoc.Param param,
            com.example.demodokka.aidl.testdoc.ICallback callback) throws android.os.RemoteException;
}
