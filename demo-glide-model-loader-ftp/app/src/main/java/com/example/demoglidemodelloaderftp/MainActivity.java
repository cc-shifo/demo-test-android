package com.example.demoglidemodelloaderftp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.DisplayMetrics;

import com.bumptech.glide.Glide;
import com.example.demoglidemodelloaderftp.databinding.ActivityMainBinding;
import com.example.demoglidemodelloaderftp.glide.FtpUrl;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        loadLadybugPic();
    }

    private void loadLadybugPic() {
        final FtpUrl url = new FtpUrl("ftp://192.168.143.1/test.png");
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels / 2;
        int h = dm.heightPixels / 2;
        Glide.with(this)
                .load(url)
                .override(w, h)
                // .skipMemoryCache(false)
                // .diskCacheStrategy(DiskCacheStrategy.NONE)
                .signature(url.getSignatureKey(true))
                .into(mBinding.demoIv);
    }
}