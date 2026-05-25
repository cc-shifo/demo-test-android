package com.example.demosetlanguage

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.demosetlanguage.databinding.ActivityMainBinding
import com.example.demosetlanguage.util.ViewUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var mBinding: ActivityMainBinding

    // 内部无需重启Activity进行语言切换要求：
    // 1、显示功能：尽量引用资源id，引用字符串时，要求提供对照表不同语言的
    // 2、必须通过Activity的Context来引用资源。
    // 3、完全自定义各个UI，要求提供显示资源id的保存，重新绘制功能。
    // 4、建议提供封装了语言切换的Application上下文，Resource


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // mBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        // setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 按键上文本语言改变不了，因为没有重新绘制。
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        mBinding.testBtn.setOnClickListener {
            switchLanguage()
        }
        mBinding.btnNext.setOnClickListener {
            startActivity(Intent(this@MainActivity, TestActivity::class.java))
        }

        // buttonCh = (Button) findViewById(R.id.btn_switch_cn);
        // buttonCh.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         switchLanguage();
        //     }
        // });
        EventBus.getDefault().register(this)

        val l = listOf<Int>(1, 2, 3, 4, 5, 6, 7)
        l.filter({ item: Int -> item % 2 == 1 })
        l.filter({ it -> it % 2 == 1 })
        l.filter({ it % 2 == 1 })
        l.filter { it % 2 == 1 }
        l.let { }
        l.forEach { }

        val mutL1 = MutableList(5) {}
        val mutL2 = mutableListOf<Int>(1, 2, 3, 4, 5, 6, 7)

        // 完整显式匿名函数写法
        val predicates: List<(Int) -> Boolean> = listOf(
            fun(num: Int): Boolean { return num % 2 == 0 },
            fun(num: Int): Boolean { return num % 2 == 1 }
        )

        val nfn1 = fun(a: Int): Boolean { // 显示指定返回类型
            return a % 2 == 0
        }
        val nfn2 = fun(a: Int) { // 省略了返回类型
            val tmp = a % 2 == 1
            return
        }
        val nfn3: (Int) -> Boolean = { a: Int ->
            // 省略了返回类型
            a % 2 == 1
        }

        // 显示匿名函数转Lambda 表达式，把所有能省的都省掉：

        object : Thread() {
            override fun run() {
                Thread.sleep(3000)
                println("使用Thread对象表达式：${Thread.currentThread().id}")
            }
        }.start()



        val t1 = Thread(object : Runnable {
            override fun run() {
                Thread.sleep(3000)
                println("使用Thread对象表达式：${Thread.currentThread().id}")
            }
        })
        // SAM接口转Lambda
        val t2 = Thread({ ->
            Thread.sleep(3000)
            println("使用Thread对象表达式：${Thread.currentThread().id}")
        })

        val t3 = Thread{
            Thread.sleep(3000)
            println("使用Thread对象表达式：${Thread.currentThread().id}")
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Log.d(TAG, "onWindowFocusChanged: $hasFocus")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val l: Locale = newConfig.locales.get(0)
        Log.d(TAG, "onConfigurationChanged: $l, country=${l.country}, language=${l.language}")
        Toast.makeText(this@MainActivity, "current language: $l", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this) // 反注册EventBus
    }

    private fun switchLanguage() {
        val resources = getResources()
        val configuration = resources.getConfiguration()
        val locale = configuration.getLocales().get(0)
        val displayCountry = locale.getDisplayCountry() // United States, China
        Log.d(TAG, "switchLanguage: displayCountry: $displayCountry")
        val displayLanguage = locale.displayLanguage // English, Chinese
        Log.d(TAG, "switchLanguage: displayLanguage: $displayLanguage")

        val country = locale.country // CN, US
        Log.d(TAG, "switchLanguage: country: $country")
        val language = locale.language // zh, en
        Log.d(TAG, "switchLanguage: language: $language")
        Log.d(TAG, "switchLanguage: Locale.CHINA.getDisplayName(): " + Locale.CHINA.displayName)
        if (language.contains("zh")) {
            configuration.setLocale(Locale.ENGLISH)
            // localeString = Locale.ENGLISH.getDisplayLanguage();
        } else {
            configuration.setLocale(Locale.CHINA)
            // localeString = Locale.CHINA.getDisplayName();
        }
        resources.updateConfiguration(configuration, resources.displayMetrics)
        // getApplicationContext().createConfigurationContext(configuration);

        val event: ClassEvent = ClassEvent()
        event.msg = MyApp.getApp().getString(R.string.switch_txt)
        event.msg = getString(R.string.switch_txt)
        EventBus.getDefault().post(event)
    }

    @Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
    fun onStringEvent(event: ClassEvent) {
        Log.d(TAG, "MainActivity got message: ${event.toString()}")
        ViewUtil.updateViewLanguage(findViewById(android.R.id.content))
        Toast.makeText(this@MainActivity, event.msg, Toast.LENGTH_SHORT).show()
    }
}