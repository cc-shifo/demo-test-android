package com.example.bingo.demozxinglib;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class CodeActivity extends AppCompatActivity {
    private static final String TAG = "CodeActivity";
    Bitmap mBitmap;
    String mCodeContent;
    ImageView mImageView;
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        Intent intent = getIntent();
        if (intent != null) {
            mBitmap = intent.getParcelableExtra("bitmap");
            mCodeContent = intent.getStringExtra("text");
        }


        mImageView = findViewById(R.id.image_bar_code);
        mTextView = findViewById(R.id.tx_bar_code_content);
        mTextView.setText(mCodeContent);
        Bitmap pager = getBarCodeBitmap();
        mImageView.setImageBitmap(pager);
    }

    private Bitmap getBarCodeBitmap() {
        final int pagerW = 384;
        final int pagerH = 384;

        Bitmap pager = null;
        try {
            int w = mBitmap.getWidth();
            int h = mBitmap.getHeight();
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            pager = Bitmap.createBitmap(pagerW, pagerH, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(pager);
            canvas.drawRect(new Rect(0, 0, pager.getWidth(), pager.getHeight()), paint);
            //canvas.drawBitmap(pager, 0, 0, paint);
            //canvas.save();
            canvas.drawBitmap(mBitmap, (pagerW - w)>>1, (pagerH - h)>>1, null);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            Log.d(TAG, "getBarCodeBitmap: " +e);
        }
        return pager;
    }
}
