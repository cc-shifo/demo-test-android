package com.hyd.sqlitedemo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hyd.sqlitedemo.database.model.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hayden
 * @create 2021/8/12 12:35 下午
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // 数据库版本
    private static final int DATABASE_VERSION = 1;

    // 数据库名称
    private static final String DATABASE_NAME = "notes_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 创建表
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Note.CREATE_TABLE);
    }

    // 升级数据库
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 如果存在，则删除旧表
        db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NAME);

        // 再次创建表
        onCreate(db);
    }

    public long insertNote(String note) {
        // 获取可写数据库，因为我们要写入数据
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` 和 `timestamp` 会自动插入
        // 无需添加它们
        values.put(Note.COLUMN_NOTE, note);

        // 插入行
        long id = db.insert(Note.TABLE_NAME, null, values);

        // 关闭数据库连接
        db.close();

        // 返回新插入的行 id
        return id;
    }

    public Note getNote(long id) {
        // 获取可读的数据库，因为我们没有插入任何内容
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Note.TABLE_NAME,
                new String[]{Note.COLUMN_ID, Note.COLUMN_NOTE, Note.COLUMN_TIMESTAMP},
                Note.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // 准备笔记对象
        Note note = new Note(
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

        // 关闭数据库连接
        cursor.close();

        return note;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();

        // 全局查询
        String selectQuery = "SELECT  * FROM " + Note.TABLE_NAME + " ORDER BY " +
                Note.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // 循环遍历所有行并添加到列表
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // 关闭数据库连接
        db.close();

        // 返回笔记列表
        return notes;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + Note.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // 返回 count
        return count;
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_NOTE, note.getNote());

        // 更新行
        return db.update(Note.TABLE_NAME, values, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
}
