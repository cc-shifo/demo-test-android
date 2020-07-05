/*
 * = COPYRIGHT
 *          Wuhan Tianyu
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20200616 	        liujian                  Create
 */

package com.example.democircleprogressbar.dialog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class BasicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayoutId());
        initData();
        initView();
        afterViewInit();
    }

    /**
     * firstly, set ui for this activity
     */
    protected abstract int setLayoutId();

    /**
     * secondly, initialize data for UI
     */
    protected abstract void initData();

    /**
     * thirdly, update UI.
     */
    protected abstract void initView();

    protected void afterViewInit() {
        //nothing
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
