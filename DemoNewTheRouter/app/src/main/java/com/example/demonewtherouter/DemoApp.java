package com.example.demonewtherouter;

import android.app.Application;
import android.content.Context;

import com.therouter.TheRouter;
import com.therouter.TheRouterKt;

import cn.therouter.BuildConfig;


public class DemoApp extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        TheRouterKt.setTheRouterUseAutoInit(false);
        TheRouter.setDebug(BuildConfig.DEBUG);
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TheRouter.init(this);
    }
}
