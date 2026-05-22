package com.example.demosetlanguage;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.demosetlanguage.databinding.ActivityChangeBinding;
import com.example.demosetlanguage.util.ViewUtil;
import com.example.demosetlanguage.widget.AppTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;



public class SetLanguageActivity extends AppCompatActivity {
    private ActivityChangeBinding mBinding;
    private AppTextView mSetText;
    private AppTextView mSetTextId;
    private AppTextView mGlobalText;
    private AppTextView mGlobalTextId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityChangeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册EventBus
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onStringEvent(ClassEvent event) {
        Log.d("test","LangeChangeActivity got message:" +  event);
        ViewUtil.updateViewLanguage(findViewById(android.R.id.content));
    }

    private void switchLanguage(Locale locale) {
        Configuration config = getResources().getConfiguration();// 获得设置对象
        Resources resources = getResources();// 获得res资源对象
        DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        config.locale = locale; // 简体中文
        resources.updateConfiguration(config, dm);
    }

    private void initView() {
        mSetText = findViewById(R.id.set_text);
        mSetTextId = findViewById(R.id.set_text_id);
        mGlobalText = findViewById(R.id.global_set_txt);
        mGlobalTextId = findViewById(R.id.global_set_txt_id);

        // 原理：不reCreate Activity，切换Local，主动刷新txt的显示。
        // 结论
        // 文本显示方式1：通过传递R.string id给View, 展示的文本，可以正常刷新
        // 文本显示方式2：通过传递string内容给View, 展示的文本，无法刷新。因为找不到资源对应的id，无法重新获取资源。不区分文本内容是
        // 通过App的Context还是Activity的Context获取
        // 获取文本的方式：文本是通过App获取的情况，在切换语言后，获取的文本依旧是旧语言的格式；文本是通过Activity获取的情况，
        // 在切换语言后，获取的文本是新语言格式的；
        // 注意：不可以使用App的context来获取语言。

        mSetText.setTextById(R.string.set_text);
        mSetTextId.setTextWithString(getString(R.string.set_text));
        mGlobalText.setTextById(R.string.global_text);
        mGlobalTextId.setTextWithString(MyApp.getApp().getString(R.string.global_text));
        mBinding.btnChinese.setOnClickListener(v -> {
            switchLanguage(Locale.CHINESE);
            notifyLngChanged();
        });
        mBinding.btnEnglish.setOnClickListener(v -> {
            switchLanguage(Locale.ENGLISH);
            notifyLngChanged();
        });
    }

    private void notifyLngChanged() {
        ClassEvent event = new ClassEvent();
        event.msg = getString(R.string.do_it); // 语言切换后，内容的语言格式也变换；
        // event.msg = MyApp.getApp().getString(R.string.do_it); // 语言切换后，内容格式依旧是旧语言格式。
        EventBus.getDefault().post(event);

        Toast.makeText(this, event.msg + "!!!!!!!!!",Toast.LENGTH_SHORT).show();
    }
}
