package com.piesat.mobile.android.lib.business.netservice.entity;

import com.piesat.mobile.android.lib.business.netservice.database.CommonDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;


//任务表
@Table(database = CommonDataBase.class)
public class nrrms_task extends BaseModel implements Serializable {
    @NotNull
    @Column
    @PrimaryKey
    public String f_task_id;//任务主键id

    @Column
    public String f_task_name;//任务名称

    @Column
    public String f_task_code;//任务编号

    @Column
    public String f_task_type;//任务类型

    @Column
    public String f_task_plan_finish_time;//计划完成时间

    @Column
    public String f_task_real_finish_time;//实际完成时间

    @Column
    public int f_task_status;//任务状态（任务状态：未分配、部分未分配、已分配、已完成）

    public nrrms_task() {
    }

    public nrrms_task(String f_task_id, String f_task_name, String f_task_code,
                      String f_task_type, String f_task_plan_finish_time, String f_task_real_finish_time, int f_task_status) {
        this.f_task_id = f_task_id;
        this.f_task_name = f_task_name;
        this.f_task_code = f_task_code;
        this.f_task_type = f_task_type;
        this.f_task_plan_finish_time = f_task_plan_finish_time;
        this.f_task_real_finish_time = f_task_real_finish_time;
        this.f_task_status = f_task_status;
    }

    public TaskEntity toTaskEntity() {
        return new TaskEntity(this.f_task_id, this.f_task_name, this.f_task_code, this.f_task_type, this.f_task_plan_finish_time, this.f_task_real_finish_time, this.f_task_status);
    }
}
