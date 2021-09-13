// IServerEndCaller.aidl
package com.whty.smartpos.qbcommapp;

// Declare any non-default types here with import statements
import com.whty.smartpos.qbcommapp.IMessageListener;
interface IServerEndCaller {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    /*void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);*/

    void registerMessageListener(IMessageListener listener);
    void unregisterMessageListener(IMessageListener listener);
}