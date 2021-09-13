/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.aidl;
// Declare any non-default types here with import statements

public interface IPosTradeListener extends android.os.IInterface
{
  /** Default implementation for IPosTradeListener. */
  public static class Default implements IPosTradeListener
  {
    /**
         * Demonstrates some basic types that you can use as parameters
         * and return values in AIDL.
         */
    @Override
    public void onResult(String msg) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements IPosTradeListener
  {
    private static final String DESCRIPTOR = "com.whty.smartpos.pay.aidl.IPosTradeListener";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.whty.smartpos.pay.aidl.IPosTradeListener interface,
     * generating a proxy if needed.
     */
    public static IPosTradeListener asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof IPosTradeListener))) {
        return ((IPosTradeListener)iin);
      }
      return new Proxy(obj);
    }
    @Override
    public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override
    public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_onResult:
        {
          data.enforceInterface(descriptor);
          String _arg0;
          _arg0 = data.readString();
          this.onResult(_arg0);
          reply.writeNoException();
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements IPosTradeListener
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override
      public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      /**
           * Demonstrates some basic types that you can use as parameters
           * and return values in AIDL.
           */
      @Override
      public void onResult(String msg) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(msg);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onResult, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onResult(msg);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      public static IPosTradeListener sDefaultImpl;
    }
    static final int TRANSACTION_onResult = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    public static boolean setDefaultImpl(IPosTradeListener impl) {
      if (Proxy.sDefaultImpl == null && impl != null) {
        Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static IPosTradeListener getDefaultImpl() {
      return Proxy.sDefaultImpl;
    }
  }
  /**
       * Demonstrates some basic types that you can use as parameters
       * and return values in AIDL.
       */
  public void onResult(String msg) throws android.os.RemoteException;
}
