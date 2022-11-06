package com.example.democipherroom.db.entry;

import androidx.room.ColumnInfo;

public class ProjectionPersonName {
    @ColumnInfo(name = "name")
    private String mName;

    public ProjectionPersonName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
