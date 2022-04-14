package com.example.democipherroom.db.testentry;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

//任务图斑时相表
@Entity(tableName = nrrms_task_patch_phase.TABLE_NAME)
public class nrrms_task_patch_phase {
    public static final String TABLE_NAME = "nrrms_task_patch_phase";
    @PrimaryKey
    @NonNull
    public String f_phase_id;//主键id

    public String f_task_id;//任务id

    public String f_patch_id;//图斑id

    public String f_phase_create_time;//创建时间

    public String f_patch_variation_type;//变化类型

    public int f_phase_type;//时相类型（1.前时相、2.后时相、3.其他）

    public String f_phase_land_type;//时相地类

    public String f_phase_path;//时相路径

    // add these in 2022-04-12
    public String f_phase_dlbm;
    public String f_phase_dlmc;
    public String f_phase_code;
    public String f_phase_time;

    public nrrms_task_patch_phase(@NonNull String f_phase_id, String f_task_id, String f_patch_id
            , String f_phase_create_time, String f_patch_variation_type, int f_phase_type,
                                  String f_phase_land_type, String f_phase_path,
                                  String f_phase_dlbm, String f_phase_dlmc, String f_phase_code,
                                  String f_phase_time) {
        this.f_phase_id = f_phase_id;
        this.f_task_id = f_task_id;
        this.f_patch_id = f_patch_id;
        this.f_phase_create_time = f_phase_create_time;
        this.f_patch_variation_type = f_patch_variation_type;
        this.f_phase_type = f_phase_type;
        this.f_phase_land_type = f_phase_land_type;
        this.f_phase_path = f_phase_path;
        this.f_phase_dlbm = f_phase_dlbm;
        this.f_phase_dlmc = f_phase_dlmc;
        this.f_phase_code = f_phase_code;
        this.f_phase_time = f_phase_time;
    }
}
