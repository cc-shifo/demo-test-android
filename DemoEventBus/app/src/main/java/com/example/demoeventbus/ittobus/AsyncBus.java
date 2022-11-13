/*
 * Copyright (C) 2012 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demoeventbus.ittobus;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Maintains a singleton instance for obtaining the bus. Ideally this would be replaced with a
 * more efficient means
 * such as through injection directly into interested classes.
 */
public final class AsyncBus extends Bus {
    private static final String TAG = "AsyncBus";
    private Handler mHandler;

    public static AsyncBus getInstance() {
        return Holder.BUS;
    }

    private AsyncBus(ThreadEnforcer enforcer, String identifier) {
        super(enforcer, identifier);
        initAsyncHandler();
    }

    @Override
    public void post(Object event) {
        mHandler.post(() -> AsyncBus.super.post(event));
    }

    private static class Holder {
        private static final AsyncBus BUS = new AsyncBus(ThreadEnforcer.ANY, "[otto]");

        private Holder() {
            // nothing
        }
    }

    @Override
    public void unregister(Object object) {
        super.unregister(object);
        mHandler.getLooper().quit();
    }

    private void initAsyncHandler() {
        HandlerThread thread = new HandlerThread("itto");
        thread.start();
        Log.d(TAG, "initAsyncHandler: tid=" + thread.getId());
        mHandler = new Handler(thread.getLooper());
        // ExecutorService executorService = Executors.newSingleThreadExecutor();
        // ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
        //         0L, TimeUnit.MILLISECONDS,
        //         new LinkedBlockingQueue<Runnable>());
        // threadPoolExecutor.execute(new Runnable() {
        //     @Override
        //     public void run() {
        //         Looper.prepare();
        //         Handler handler = new Handler(Looper.myLooper());
        //         Looper.loop();
        //     }
        // });
    }
}
