package com.example.democipherroom.db.entry;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TB_person")
public class Person {
    public static final String TABLE_NAME = "TB_person";

    @PrimaryKey
    @ColumnInfo(name = "id")
    private long mId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "age")
    private int mAge;

    @ColumnInfo(name = "gender")
    private int mGender;

    public Person(long id, String name, int age, int gender) {
        mId = id;
        mName = name;
        mAge = age;
        mGender = gender;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int age) {
        mAge = age;
    }

    public int getGender() {
        return mGender;
    }

    public void setGender(int gender) {
        mGender = gender;
    }
}
