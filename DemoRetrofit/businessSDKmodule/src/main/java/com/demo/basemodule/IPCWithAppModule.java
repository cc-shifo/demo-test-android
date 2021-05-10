package com.demo.basemodule;

import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;

public interface IPCWithAppModule extends IProvider {
    String printPackName(Context context);
}
