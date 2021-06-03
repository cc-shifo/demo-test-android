package com.example.sliderecyclerview;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView view = findViewById(R.id.recycler_view);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add(String.valueOf(i));
        }

        MyAdapter adapter = new MyAdapter(list);
        LinearLayoutManager manager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        view.setLayoutManager(manager);
        int d = getResources().getDimensionPixelSize(R.dimen.item_child_width);
        view.addOnItemTouchListener(new ProcessMenuTouchHelper(d));
        view.setAdapter(adapter);
    }

    private float dp2px(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }
}