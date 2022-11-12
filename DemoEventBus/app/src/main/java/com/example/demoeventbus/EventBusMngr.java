/*
 * = COPYRIGHT
 *          xxxx
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                    Author                  Action
 * 2022-11-11              LiuJian                 Create
 */

package com.example.demoeventbus;

import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * live data bus管理。
 */
public class EventBusMngr {

    private EventBusMngr() {
        // nothing
    }

    public static EventBusMngr getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 非自动清除监听。所有状态都可以收到事件。
     */
    public void init() {
        // TODO lifecycleObserverAlwaysActive false和autoClear为true是否影响sticky模式待测试。
        LiveEventBus.config()
                .lifecycleObserverAlwaysActive(false)
                .enableLogger(false)
                .autoClear(false);
    }

    private final ExecutorService         mExecutorService = Executors.newCachedThreadPool();

    public void observeOn() {
        // mExecutorService
        //         .execute(() -> observerWrapper.onChanged(tValueWrapper.value));
    }

    private static class Holder {
        private static final EventBusMngr INSTANCE = new EventBusMngr();
    }
}
