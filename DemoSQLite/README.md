# SQLiteDemo

<p align="left" style="">
  <img src="https://img.shields.io/github/last-commit/hongyoudan/SQLiteDemo"></img>
	<img src="https://img.shields.io/github/languages/count/hongyoudan/SQLiteDemo"></img>
<img src="https://img.shields.io/github/stars/hongyoudan/SQLiteDemo?style=social"></img>
<img src="https://img.shields.io/github/watchers/hongyoudan/SQLiteDemo?style=social"></img>
</p>

**整理不易，欢迎 `Star` 和 `Fork` ^_^ ，谢谢~~**



## 前言

Android 为了让我们能够更加方便地管理数据库，专门提供了一个 SQLiteOpenHelper 帮助
类，借助这个类就可以经常简单地对数据库进⾏创建和升级。

首先我们要知道 SQLiteOpenHelper 是一个抽象类，这意味着如果我们想要使用它的话，就需要创建一个自己的帮助类去继承它。 SQLiteOpenHelper 中有两个抽象方法，分别是 `onCreate()` 和 `onUpgrade()` ，我们必须在自己的帮助类里面重写这两个方法，然后分别在这两个方法中去实现创建、升级数据库的逻辑。

SQLiteOpenHelper 中还有两个非常重要的实例方法 ，getReadableDatabase() 和 getWritableDatabase() 。这两个方法都可以创建或打开一个现有的数据库（如果数据库已存在则直接打开，否则创建一个新的数据库），并返回一个可对数据库进行读写操作的对象。

不同的是，当数据库不可写入的时候（如磁盘空间已满） `getReadableDatabase()` 方法返回的对象将以只读的方式去打开数据库，而 `getWritableDatabase()` 方法则将出现异常。SQLiteOpenHelper 中有两个构造方法可供重写。

这个构造方法中接收四个参数：

- `Context`，这个没什么好说的，必须要有它才能对数据库进行操作

- 数据库名，创建数据库时使用的就是这个指定的名称

- 允许我们在查询数据的时候返回一个自定义的`Cursor`，一般都是传回null

- 表示当前数据库的版本号， 可用于对数据库进行升级操作

构建出 SQLiteOpenHelper 的实例之后，再调用它的`getReadableDatabase()`或`getWritableDatabase()`方法就能够创建数据库了，数据库文件会存放在`/data/data/<package name>/databases/`目录下。此时， 重写的`onCreate()`方法也会得到执行， 所以通常会在这里去处理一些创建表的逻辑。



## 案例一



### 项目截图

| <img src="https://img-blog.csdnimg.cn/a3514cc4b9a442acad04cf1c4fc787c8.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NDAyMTg0,size_16,color_FFFFFF,t_70#pic_center"></img> | <img src="https://img-blog.csdnimg.cn/b3653d100ce547578ea2248b2621cfe7.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NDAyMTg0,size_16,color_FFFFFF,t_70#pic_center"></img> | <img src="https://img-blog.csdnimg.cn/8d746ba69da0450cb5feecc415ec5998.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NDAyMTg0,size_16,color_FFFFFF,t_70#pic_center"></img> |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="https://img-blog.csdnimg.cn/c830e6a608404deca0f87019a5aa209e.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NDAyMTg0,size_16,color_FFFFFF,t_70#pic_center"></img> | <img src="https://img-blog.csdnimg.cn/3cccfc64a68c44ab9d181162f7c95bff.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NDAyMTg0,size_16,color_FFFFFF,t_70#pic_center"></img> | <img src="https://img-blog.csdnimg.cn/ef20af9a47c048fea670b73f78e36030.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NDAyMTg0,size_16,color_FFFFFF,t_70#pic_center"></img> |



### 主要功能

1. 新建文本内容
2. 长按条目选择编辑或删除



## 案例二

**运用了Android SQLite的经典使用方法，这里直接贴上代码**



MyDatabaseHelper.java：

```java
public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_BOOK = "create table book ("
            + "id integer primary key autoincrement, "
            + "author text, "
            + "price real, "
            + "pages integer, "
            + "name text)";
    public static final String CREATE_CATEGORY = "create table Category("
            + "id integer primary key autoincrement,"
            + "category_name text,"
            + "category_code integer)";
    private Context mContext;
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
        db.execSQL(CREATE_CATEGORY);
        Toast.makeText(mContext, "创建book数据表成功！", Toast.LENGTH_SHORT).show();
        Toast.makeText(mContext, "创建Category表成功！", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists book");
        db.execSQL("drop table if exists Category");
        onCreate(db);
    }
}
```

 MainActivity.java：

```java
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
        createDatabase = (Button) findViewById(R.id.create_database);
        add_data = (Button) findViewById(R.id.add_data);
        update_btn = (Button) findViewById(R.id.update_data);
        delete_btn = (Button) findViewById(R.id.delete_data);
        query_btn = (Button) findViewById(R.id.query_data);
        replace_btn= (Button) findViewById(R.id.replace_data);
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
                        Log.d("MyLog","--------------------");
                        Log.d("MyLog","书名是："+name);
                        Log.d("MyLog","作者是："+author);
                        Log.d("MyLog","编号是："+pages);
                        Log.d("MyLog","价格是："+price);
                        Log.d("MyLog","--------------------");
                    }while (cursor.moveToNext());
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
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    database.endTransaction();//结束事务
                }
            }
        });
    }
}
```

main_activity.xml：

```
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    <Button
        android:id="@+id/create_database"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="创建数据库" />
    <Button
        android:id="@+id/add_data"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="添加数据" />
    <Button
        android:id="@+id/update_data"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="更新数据" />
    <Button
        android:id="@+id/delete_data"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="删除数据" />
    <Button
        android:id="@+id/query_data"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="查询数据" />
    <Button
        android:id="@+id/replace_data"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="替换数据（事务）"
        />
</LinearLayout>
```

