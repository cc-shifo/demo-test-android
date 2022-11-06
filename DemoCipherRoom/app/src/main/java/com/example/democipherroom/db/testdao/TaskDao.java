package com.example.democipherroom.db.testdao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.democipherroom.db.testentry.nrrms_task;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM " + nrrms_task.TABLE_NAME)
    List<nrrms_task> getTask();
}
