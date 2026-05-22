package com.example.demosetlanguage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.demosetlanguage.util.ViewUtil;
import com.example.demosetlanguage.widget.AppTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class TestActivity extends AppCompatActivity {
    private AppTextView mSetText;
    private AppTextView mSetTextId;
    private AppTextView mGlobalText;
    private AppTextView mGlobalTextId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        EventBus.getDefault().register(this);

        findViewById(R.id.btn).setOnClickListener(
                v -> startActivity(new Intent(TestActivity.this, SetLanguageActivity.class)));

        mSetText = findViewById(R.id.txt_set_text);
        mSetTextId = findViewById(R.id.txt_set_text_id);
        mGlobalText = findViewById(R.id.txt_global_set_txt);
        mGlobalTextId = findViewById(R.id.txt_global_set_txt_id);

        mSetText.setTextById(R.string.set_text);
        mSetTextId.setTextWithString(getString(R.string.set_text));
        mGlobalText.setTextById(R.string.global_text);
        mGlobalTextId.setTextWithString(MyApp.getApp().getString(R.string.global_text));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);// 反注册EventBus
    }

    @Subscribe(threadMode = ThreadMode.MAIN) // 在ui线程执行
    public void onStringEvent(ClassEvent event) {
        Log.d("test", "TestActivity got message:" + event);
        ViewUtil.updateViewLanguage(findViewById(android.R.id.content));
    }
}
