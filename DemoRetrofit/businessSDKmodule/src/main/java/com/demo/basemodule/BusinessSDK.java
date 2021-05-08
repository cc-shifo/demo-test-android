package com.demo.basemodule;

import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;

public interface BusinessSDK extends IProvider {
    String printPackName(Context context);
}
