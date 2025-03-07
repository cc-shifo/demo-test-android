package com.example.democropbitmap;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private ImageView mTestImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mTestImageView = findViewById(R.id.iv_test);

        Button button = findViewById(R.id.btn_hello_world);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = PictureUtil.fillPicture(MainActivity.this, R.drawable
                        .ic_bg_1920x1080, R.drawable.ic_fg_1080x1080);
                if (bitmap == null) {
                    Toast.makeText(MainActivity.this, "null bitmap", Toast.LENGTH_LONG).show();
                    return;
                }

                mTestImageView.setImageBitmap(bitmap);
            }
        });
    }
}