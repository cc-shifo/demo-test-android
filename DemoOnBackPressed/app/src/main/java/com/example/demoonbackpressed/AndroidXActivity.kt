package com.example.demoonbackpressed

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import android.window.OnBackInvokedCallback

import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AndroidXActivity : AppCompatActivity() {
    private val TAG = "AndroidXActivity"
    private lateinit var onBackInvokedCallback: OnBackInvokedCallback

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_android_xactivity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        //     onBackInvokedDispatcher.registerOnBackInvokedCallback(
        //         OnBackInvokedDispatcher.PRIORITY_DEFAULT
        //     ) {
        //         Log.d(TAG, "onBackInvoked: ")
        //         Toast.makeText(this@AndroidXActivity, "Hello back pressed", Toast.LENGTH_LONG)
        //             .show()
        //         onBackPressedDispatcher.dispatchOnBackCancelled()
        //     }
        // }

        // 新 API 的回调
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedCallback = OnBackInvokedCallback {
                // 如果添加toast，将影响到导航栏的按下效果，导致一直处于按下状态。
                // 即使调用了onBackPressedDispatcher.dispatchOnBackCancelled还是不能恢复正常状态效果
                // Toast.makeText(this@AndroidXActivity, "Hello back pressed", Toast.LENGTH_LONG)
                //     .show()
                if (!handleBack()) {
                    onBackPressedDispatcher.onBackPressed()
                } else {
                    // 如果添加了toast，没有效果。导航栏的一直处于按下状态效果。不区分PRIORITY_DEFAULT，PRIORITY_DEFAULT
                    // onBackPressedDispatcher.dispatchOnBackCancelled()

                    // 如果添加了toast，有效果
                    // showDialog()

                    // 如果添加了toast，没有效果。导航栏的一直处于按下状态效果
                    // window.decorView.post {
                    //     window.decorView.performClick()
                    // }
                }

            }
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                onBackInvokedCallback
            )
        } else {
            // 旧 API 的回调
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Toast.makeText(this@AndroidXActivity, "Hello back pressed", Toast.LENGTH_LONG)
                    //     .show()
                    if (!handleBack()) {
                        // isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                        // isEnabled = true
                    }
                }
            })
        }

    }

    private fun handleBack(): Boolean {
        // 你的自定义返回处理逻辑
        return true
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // 重置导航键状态
            window.decorView.post {
                window.decorView.performClick()
            }
        }
    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(this@AndroidXActivity)
            .setTitle("Demo Pressed")
            .setPositiveButton("确定", null)
            .setNegativeButton("取消", null)
        dialog.show()
    }
}