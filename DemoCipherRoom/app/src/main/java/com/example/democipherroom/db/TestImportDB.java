package com.example.democipherroom.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.democipherroom.db.testdao.TaskDao;
import com.example.democipherroom.db.testentry.nrrms_task;
import com.example.democipherroom.db.testentry.nrrms_task_patch;
import com.example.democipherroom.db.testentry.nrrms_task_patch_data;
import com.example.democipherroom.db.testentry.nrrms_task_patch_phase;

import timber.log.Timber;

@Database(entities = {nrrms_task.class, nrrms_task_patch.class, nrrms_task_patch_data.class,
        nrrms_task_patch_phase.class}, version = 1)
public abstract class TestImportDB extends RoomDatabase {
    private static TestImportDB mInstance = null;

    public abstract TaskDao getTaskDao();
    public static synchronized void importData(@NonNull Context context) {
        if (mInstance == null) {
            mInstance = Room.databaseBuilder(context.getApplicationContext(), TestImportDB.class,
                    "test-in.db")
                    .createFromAsset("sqlite.db")
                    .build();

            try {
                mInstance.getTaskDao().getTask();
            } catch (Exception e) {
                Timber.e(e);
            }
            Timber.d("%s", mInstance == null ? "null" : "not null");
        }

    }
}
