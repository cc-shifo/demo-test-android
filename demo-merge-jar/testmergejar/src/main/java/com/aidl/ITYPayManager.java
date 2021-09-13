/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.aidl;
public interface ITYPayManager extends android.os.IInterface
{
  /** Default implementation for ITYPayManager. */
  public static class Default implements ITYPayManager
  {
    /**
         * Demonstrates some basic types that you can use as parameters
         * and return values in AIDL.
         */
    @Override
    public void setPosTradeListener(IPosTradeListener posTradeListener) throws android.os.RemoteException
    {
    }
    @Override
    public void releasePosTradeListener(IPosTradeListener posTradeListener) throws android.os.RemoteException
    {
    }
    @Override
    public void doTrans(String params) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements ITYPayManager
  {
    private static final String DESCRIPTOR = "com.whty.smartpos.pay.aidl.ITYPayManager";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.whty.smartpos.pay.aidl.ITYPayManager interface,
     * generating a proxy if needed.
     */
    public static ITYPayManager asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof ITYPayManager))) {
        return ((ITYPayManager)iin);
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
        case TRANSACTION_setPosTradeListener:
        {
          data.enforceInterface(descriptor);
          IPosTradeListener _arg0;
          _arg0 = IPosTradeListener.Stub.asInterface(data.readStrongBinder());
          this.setPosTradeListener(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_releasePosTradeListener:
        {
          data.enforceInterface(descriptor);
          IPosTradeListener _arg0;
          _arg0 = IPosTradeListener.Stub.asInterface(data.readStrongBinder());
          this.releasePosTradeListener(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_doTrans:
        {
          data.enforceInterface(descriptor);
          String _arg0;
          _arg0 = data.readString();
          this.doTrans(_arg0);
          reply.writeNoException();
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements ITYPayManager
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
      public void setPosTradeListener(IPosTradeListener posTradeListener) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((posTradeListener!=null))?(posTradeListener.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setPosTradeListener, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setPosTradeListener(posTradeListener);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override
      public void releasePosTradeListener(IPosTradeListener posTradeListener) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((posTradeListener!=null))?(posTradeListener.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_releasePosTradeListener, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().releasePosTradeListener(posTradeListener);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override
      public void doTrans(String params) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(params);
          boolean _status = mRemote.transact(Stub.TRANSACTION_doTrans, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().doTrans(params);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      public static ITYPayManager sDefaultImpl;
    }
    static final int TRANSACTION_setPosTradeListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_releasePosTradeListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_doTrans = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    public static boolean setDefaultImpl(ITYPayManager impl) {
      if (Proxy.sDefaultImpl == null && impl != null) {
        Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static ITYPayManager getDefaultImpl() {
      return Proxy.sDefaultImpl;
    }
  }
  /**
       * Demonstrates some basic types that you can use as parameters
       * and return values in AIDL.
       */
  public void setPosTradeListener(IPosTradeListener posTradeListener) throws android.os.RemoteException;
  public void releasePosTradeListener(IPosTradeListener posTradeListener) throws android.os.RemoteException;
  public void doTrans(String params) throws android.os.RemoteException;
}
