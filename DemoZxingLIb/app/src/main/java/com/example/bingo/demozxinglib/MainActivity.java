package com.example.bingo.demozxinglib;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bingo.barcode.BarcodeUtil;
import com.google.zxing.BarcodeFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageView mImageView;
    private TextView mTextView;
    private Button mBtnBarcode1DEAN13;
    private Button mBtnBarcode1DEAN8;
    private Button mBtnBarcode1D128;
    private Button mBtnBarcode2D;
    private EditText mEditText;
    private int mContentLen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }

        mImageView = findViewById(R.id.image_bar_code);
        mTextView = findViewById(R.id.tx_bar_code_content);
        mEditText = findViewById(R.id.ed_text_on_editor_action_listener);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && v.getText().length() != 0) {
                    Log.d(TAG, "onEditorAction: ");
                    testonEditorAction();
                    return true;
                }
                return false;
            }
        });
        testEAN13();
        testEAN8();
        testCode128();
        testQRCode();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager
                        .PERMISSION_GRANTED) {
                    Toast.makeText(this, "Need Storage Write Permission",
                            Toast.LENGTH_LONG).show();
                }
                break;
            default:
                Log.d(TAG, "onRequestPermissionsResult: ");
        }
    }

    private void testonEditorAction() {
        StringBuilder content = new StringBuilder();
        mContentLen = 12;
        int a = 0;
        int b = 0;
        int c = 0;
        for (int i = 1; i <= mContentLen; i++) {
            Random random = new Random();
            int r = random.nextInt(10);
            content.append(r);
            if (i % 0x02 == 0) {
                b += r;
            } else {
                a += r;
            }
        }

        c = 10 - (a + b * 3) % 10;
        if (c == 10) {
            c = 0;
        }
        content.append(c);
        Log.d(TAG, "onClick: " + a + " " + b + " " + c + " " + content.toString());
        mTextView.setText(content.toString());
        Bitmap bitmap = BarcodeUtil.createBarCode(content.toString(), BarcodeFormat.EAN_13,
                300, 120);
        mImageView.setImageBitmap(bitmap);
        startCodeActivity(bitmap, content.toString());
    }

    private void testEAN13() {
        mBtnBarcode1DEAN13 = findViewById(R.id.btn_bar_code_EAN13);
        mBtnBarcode1DEAN13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RandomStringUtils;
                StringBuilder content = new StringBuilder();
                mContentLen = 12;
                int a = 0;
                int b = 0;
                int c = 0;
                for (int i = 1; i <= mContentLen; i++) {
                    Random random = new Random();
                    int r = random.nextInt(10);
                    content.append(r);
                    if (i % 0x02 == 0) {
                        b += r;
                    } else {
                        a += r;
                    }
                }

                c = 10 - (a + b * 3) % 10;
                if (c == 10) {
                    c = 0;
                }
                content.append(c);
                Log.d(TAG, "onClick: " + a + " " + b + " " + c + " " + content.toString());
                mTextView.setText(content.toString());
                Bitmap bitmap = BarcodeUtil.createBarCode(content.toString(), BarcodeFormat.EAN_13,
                        300, 120);
                mImageView.setImageBitmap(bitmap);
                startCodeActivity(bitmap, content.toString());
            }
        });
    }

    private void testEAN8() {
        mBtnBarcode1DEAN8 = findViewById(R.id.btn_bar_code_EAN8);
        mBtnBarcode1DEAN8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RandomStringUtils;
                StringBuilder content = new StringBuilder();
                mContentLen = 7;
                int a = 0;
                int b = 0;
                int c = 0;
                for (int i = 1; i <= mContentLen; i++) {
                    Random random = new Random();
                    int r = random.nextInt(10);
                    if (i == 1) {
                        content.append(8);
                    } else if (i == 2) {
                        content.append(6);
                    } else {
                        content.append(r);
                    }
                    if (i % 0x02 == 0) {
                        b += r;
                    } else {
                        a += r;
                    }
                }

                c = 10 - (a + b * 3) % 10;
                if (c == 10) {
                    c = 0;
                }
                Log.d(TAG, "onClick: " + a + " " + b + " " + c + " " + content.toString());
                Bitmap bitmap = BarcodeUtil.createBarCode(content.toString(), BarcodeFormat.EAN_8,
                        300, 120);
                //do not pass checksum to Zxing lib.
                content.append(c);
                mTextView.setText(content.toString());
                mImageView.setImageBitmap(bitmap);
                startCodeActivity(bitmap, content.toString());
            }
        });
    }

    private void testCode128() {
        mBtnBarcode1D128 = findViewById(R.id.btn_bar_code_128);
        mBtnBarcode1D128.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RandomStringUtils;
                final String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                StringBuilder content = new StringBuilder();
                mContentLen = 10;
                for (int i = 0; i < mContentLen; i++) {
                    Random random = new Random();
                    int r = random.nextInt(36);
                    content.append(alpha.charAt(r));
                }
                //211232231131221213131222212222221224121213121211321132331112
                mTextView.setText(content.toString());
                Log.d(TAG, "onClick: " + content.toString());
                Bitmap bitmap = BarcodeUtil.createBarCode(content.toString(), BarcodeFormat
                                .CODE_128,
                        300, 120);
                mImageView.setImageBitmap(bitmap);
                startCodeActivity(bitmap, content.toString());
            }
        });
    }

    private void testQRCode() {
        mBtnBarcode2D = findViewById(R.id.btn_qr_code);
        mBtnBarcode2D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Alpha =
                        "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwzyz1234567890";
                mContentLen = 40;
                StringBuilder content = new StringBuilder();
                for (int i = 0; i < mContentLen; i++) {
                    Random random = new Random();
                    int r = random.nextInt(82);
                    content.append(Alpha.charAt(r));
                }

                mTextView.setText(content.toString());
                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                Bitmap bitmap = BarcodeUtil.createQRCode(content.toString(), logo, BarcodeUtil
                        .QR_SIZE_300);
                saveBitmapToFile(bitmap);
                //mImageView.setImageBitmap(bitmap);
                //startCodeActivity(bitmap, content.toString());
            }
        });
    }

    private void startCodeActivity(Bitmap bitmap, String barcode) {
        Intent intent = new Intent(MainActivity.this, CodeActivity.class);
        intent.putExtra("bitmap", bitmap);
        intent.putExtra("text", barcode);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        }
    }

    private void saveBitmapToFile(Bitmap bitmap) {
        String path = getApplication().getFilesDir() + File.separator +
                "barcode" + File.separator;
        File dir = new File(path);
        if (!dir.exists()) {
            boolean r = dir.mkdirs();
            if (!r) {
                Log.d(TAG, "saveBitmapToFile: " + r);
                return;
            }
        }

        File file = new File(dir, "bitmap.jpeg");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Log.d(TAG, "saveBitmapToFile: " + e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.d(TAG, "saveBitmapToFile: " + e);
                }
            }
        }
    }

}

