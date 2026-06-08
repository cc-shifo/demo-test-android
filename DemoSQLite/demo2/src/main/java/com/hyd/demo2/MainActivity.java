package com.hyd.demo2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private Button createDatabase, add_data, update_btn,
            delete_btn, query_btn, replace_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //实例化一个dbHelper对象
        dbHelper = new MyDatabaseHelper(this, "BookStore.db", null, 3);
        //初始化UI控件
        createDatabase = findViewById(R.id.create_database);
        add_data = findViewById(R.id.add_data);
        update_btn = findViewById(R.id.update_data);
        delete_btn = findViewById(R.id.delete_data);
        query_btn = findViewById(R.id.query_data);
        replace_btn = findViewById(R.id.replace_data);
        //按钮createDatabase的监听响应事件
        createDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.getWritableDatabase();
            }
        });
        //添加数据表数据
        add_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                //开始组装第一条数据
                values.put("name", "The Yaowen Code");
                values.put("author", "YaoWen");
                values.put("pages", "454");
                values.put("price", "19.50");
                //插入第一条数据
                db.insert("book", null, values);
                //开始组装第二条数据
                values.put("name", "The Siny Code");
                values.put("author", "Siny");
                values.put("pages", "459");
                values.put("price", "39.50");
                //插入第二条数据
                db.insert("book", null, values);
                //开始组装第三条数据
                values.put("name", "The HTML5 Code");
                values.put("author", "Siny");
                values.put("pages", "678");
                values.put("price", "59.50");
                //插入第三条数据
                db.insert("book", null, values);
                //开始组装第四条数据
                values.put("name", "The C# Code");
                values.put("author", "HelloWorld");
                values.put("pages", "897");
                values.put("price", "29.50");
                //插入第四条数据
                db.insert("book", null, values);
            }
        });
        //更新数据表数据
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                //更新第一条数据
                contentValues.put("name", "The Java Code");
                db.update("book", contentValues, "author=?", new String[]{
                        "Siny"
                });
                //更新第二条数据
                contentValues.put("name", "The Android Code");
                db.update("book", contentValues, "author=?", new String[]{
                        "YaoWen"
                });
            }
        });
        //删除数据表数据
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                database.delete("book", "pages>?", new String[]{"500"});
            }
        });
        //查询数据表数据
        query_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                //查询book数据表里的所有数据；
                Cursor cursor = database.query("book", null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        //遍历Cursor对象，取出数据并打印
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        String author = cursor.getString(cursor.getColumnIndex("author"));
                        int pages = cursor.getInt(cursor.getColumnIndex("pages"));
                        double price = cursor.getDouble(cursor.getColumnIndex("price"));
                        Log.d("MyLog", "--------------------");
                        Log.d("MyLog", "书名是：" + name);
                        Log.d("MyLog", "作者是：" + author);
                        Log.d("MyLog", "编号是：" + pages);
                        Log.d("MyLog", "价格是：" + price);
                        Log.d("MyLog", "--------------------");
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        });
        //测试数据库的事务功能
        replace_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                database.beginTransaction();//开启事务
                try {
                    database.delete("book", null, null);
                    if (true) {
                        //这里抛出一个异常，让事务失败
                        throw new NullPointerException();
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("name", "Game of Thrones");
                    contentValues.put("author", "HelloWorld");
                    contentValues.put("pages", 720);
                    contentValues.put("price", 21.85);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();//结束事务
                }
            }
        });
    }
}