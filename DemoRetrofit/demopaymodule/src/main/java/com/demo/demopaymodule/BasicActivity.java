/*
 * = COPYRIGHT
 *          Wuhan Tianyu
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20200616 	        liujian                  Create
 */

package com.demo.demopaymodule;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;

import com.demo.demopaymodule.utils.ActivityCollector;


public abstract class BasicActivity<T extends ViewDataBinding> extends AppCompatActivity {
    protected T mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor();
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        mBinding = bindContentView();
        initData();
        initView();
    }

    private void setStatusBarColor() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.color_activity_custom_status_bg,
                null));
    }

    /**
     * firstly, set ui for this activity
     */
    protected abstract T bindContentView();

    /**
     * secondly, initialize data for UI
     */
    protected abstract void initData();

    /**
     * thirdly, update UI.
     */
    protected abstract void initView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
