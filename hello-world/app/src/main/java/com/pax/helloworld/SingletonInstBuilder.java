package com.pax.helloworld;

import java.util.HashMap;

public class SingletonInstBuilder {
    private static HashMap<java.lang.reflect.Type, Object> mHashMap = new HashMap<>();

    /**
     * 让构造函数为 private，这样该类就不会被实例化
     */
    public SingletonInstBuilder() {
        //nothing
    }

    public static <T> T getInstance(Class<T> tClass){
        Object tClassOb = mHashMap.get(tClass);
        if (tClassOb == null){
            synchronized (SingletonInstBuilder.class){
                try {
                    tClassOb = tClass.newInstance();
                    //以键值对存储在HashMap中，class类作为key值，class类对象作为value值
                    mHashMap.put(tClass,tClassOb);
                } catch (Exception e) {
                    LogUtil.e(e);
                }
            }
        }

        return (T)tClassOb;
    }

    public <T> void removeInstance(Class<T> tClass){
        mHashMap.remove(tClass);
    }
}
