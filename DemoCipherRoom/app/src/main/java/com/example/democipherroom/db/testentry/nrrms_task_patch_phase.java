package com.piesat.mobile.android.lib.business.netservice.entity;

import com.piesat.mobile.android.lib.business.netservice.database.CommonDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import java.io.Serializable;

//任务图斑时相表
@Table(database = CommonDataBase.class)
public class nrrms_task_patch_phase extends BaseModel implements Serializable {

    @NotNull
    @Column
    @PrimaryKey
    public String f_phase_id;//主键id

    @Column
    public String f_task_id;//任务id

    @Column
    public String f_patch_id;//图斑id

    @Column
    public String f_phase_create_time;//创建时间

    @Column
    public String f_patch_variation_type;//变化类型

    @Column
    public int f_phase_type;//时相类型（1.前时相、2.后时相、3.其他）

    @Column
    public String f_phase_land_type;//时相地类

    @Column
    public String f_phase_path;//时相路径

    public nrrms_task_patch_phase() {
    }
}
