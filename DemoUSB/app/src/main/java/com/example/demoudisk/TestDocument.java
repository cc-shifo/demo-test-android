package com.example.demoudisk;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demoudisk.databinding.ActivityTestDocumentBinding;

public class TestDocument extends AppCompatActivity {
    ActivityTestDocumentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTestDocumentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnCreateDoc.setOnClickListener(v -> {
            // 创建 Documents/example.txt
            DocumentFileHelper.createDocumentFile(TestDocument.this, "example.txt", "text/plain");
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DocumentFileHelper.handleActivityResult(this, requestCode, resultCode, data);
    }
}