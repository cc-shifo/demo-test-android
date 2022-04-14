package com.example.democipherroom.db.testentry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


//任务表
@Entity(tableName = nrrms_task.TABLE_NAME)
public class nrrms_task  {
    public static final String TABLE_NAME = "nrrms_task";

    @PrimaryKey
    @NonNull
    public String f_task_id;//任务主键id

    public String f_task_name;//任务名称

    public String f_task_code;//任务编号

    public String f_task_type;//任务类型

    public String f_task_plan_finish_time;//计划完成时间

    public String f_task_real_finish_time;//实际完成时间

    public int f_task_status;//任务状态（任务状态：未分配、部分未分配、已分配、已完成）

    public nrrms_task(@NonNull String f_task_id, String f_task_name, String f_task_code,
                      String f_task_type, String f_task_plan_finish_time,
                      String f_task_real_finish_time, int f_task_status) {
        this.f_task_id = f_task_id;
        this.f_task_name = f_task_name;
        this.f_task_code = f_task_code;
        this.f_task_type = f_task_type;
        this.f_task_plan_finish_time = f_task_plan_finish_time;
        this.f_task_real_finish_time = f_task_real_finish_time;
        this.f_task_status = f_task_status;
    }
}
