package moduleIPC;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.demo.basemodule.IPCWithAppModule;


@Route(path = "/appModule/serviceApp")
public class AppSaleServiceImpl implements IPCWithAppModule {
    private static final String TAG = "AppSaleServiceImpl";

    @Override
    public void init(Context context) {
        Log.d(TAG, "init: ");
    }

    @Override
    public String printPackName(@NonNull Context context) {
        Log.d(TAG, "getPackName: " + context.getPackageName());
        Log.d(TAG, "getPackageCodePath: " + context.getPackageCodePath());

        return this.toString();
    }
}
