package com.example.demologcat

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.demologcat.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val TAG: String = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main)

        // val btnStart: Button = findViewById(R.id.btn_start)
        // // 必须设置点击监听器，点击才会回调
        // btnStart.setOnClickListener {
        //     Log.d("MainActivity", "btn_start 被点击了")
        // }
        //
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        // setContentView(R.layout.activity_main)
        setContentView(binding.root)
        val root = findViewById<View>(R.id.main)
        // val test = findViewById<View>(R.id.btn_start)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // test.setOnClickListener {
        //     Log.d(TAG, "findViewById btn_start clicked - Starting logcat capture")
        //     Toast.makeText(this@MainActivity, "Start clicked (findViewById)", Toast.LENGTH_SHORT).show()
        // }

        // val date: Date = Date(System.currentTimeMillis())
        // val simpleDate = SimpleDateFormat("yyyyMMdd_HHmmss").format(date)
        // // 初始化日志捕获器
        // val path = filesDir.absolutePath + "/logcat_" + simpleDate + ".txt"
        // val path = getExternalFilesDir(null)?.absolutePath + "/logcat.txt"
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val path = File(getExternalFilesDir(null), "logcat_${timestamp}.txt").absolutePath
        val ktLogcat = KtLogcat()
        // val ktLogcat = LogcatCapture()
        binding.btnStart.setOnClickListener {
            Log.d(TAG, "binding.btnStart clicked - Starting logcat capture")
            Toast.makeText(this, "Start clicked (binding)", Toast.LENGTH_SHORT).show()
            // ktLogcat.start(path)
            ktLogcat.start(path, )
        }
        //
        // val altStart = findViewById<Button>(R.id.btn_start)
        // Log.d(TAG, "findViewById btn_start view: id=${altStart.id}, visibility=${altStart.visibility}, clickable=${altStart.isClickable}, enabled=${altStart.isEnabled}")
        //
        // altStart.setOnClickListener {
        //     Log.d(TAG, "findViewById btn_start clicked - Starting logcat capture")
        //     Toast.makeText(this, "Start clicked (findViewById)", Toast.LENGTH_SHORT).show()
        //     ktLogcat.start(path)
        // }
        //
        binding.btnStop.setOnClickListener {
            Log.d(TAG, "stop clicked - Stopping logcat capture")
            Toast.makeText(this, "Stop clicked", Toast.LENGTH_SHORT).show()
            ktLogcat.stop()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.d(TAG, "dispatchTouchEvent: action=${ev.action}, x=${ev.x}, y=${ev.y}")
        return super.dispatchTouchEvent(ev)
    }
}