package com.example.demo_framelayout_layout_weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyTextureView extends TextureView {
    public MyTextureView(@NonNull Context context) {
        super(context);
    }

    public MyTextureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyTextureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr
            , int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


}
