package com.piesat.mobile.android.lib.business.netservice.entity;

import com.piesat.mobile.android.lib.business.netservice.database.CommonDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;



//任务图斑附件表
@Table(database = CommonDataBase.class)
public class nrrms_task_patch_data extends BaseModel implements Serializable {

    @Column
    @NotNull
    @PrimaryKey
    public String f_data_id;//主键id

    @Column
    public String f_data_name;//文件名称

    @Column
    public String f_data_size;//文件大小

    @Column
    public String f_data_update_time;//文件创建时间

    @Column
    public String f_data_type;//文件类型：1 图片，2 音频，3 视频。目前可接受的文件类型：1、图片：jpg,jpeg,png,bmp、3、视频：mp4

    @Column
    public String f_data_path;//文件路径

    @Column
    public String f_belong_id;//任务id，或任务图斑表id

    @Column
    public int f_attribute_type;//1.下发、2.app上传

    @Column
    public int f_audio_time;//录音文件的时间

    public nrrms_task_patch_data() {
    }
}
