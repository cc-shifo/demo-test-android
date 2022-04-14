package com.example.democipherroom.db.testentry;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


//任务图斑附件表
@Entity(tableName = nrrms_task_patch_data.TABLE_NAME)
public class nrrms_task_patch_data {

    public static final String TABLE_NAME = "nrrms_task_patch_data";
    @PrimaryKey
    @NonNull
    public String f_data_id;//主键id

    public String f_data_name;//文件名称

    public String f_data_size;//文件大小

    public String f_data_update_time;//文件创建时间

    public String f_data_type;//文件类型：1 图片，2 音频，3 视频。目前可接受的文件类型：1、图片：jpg,jpeg,png,bmp、3、视频：mp4

    public String f_data_path;//文件路径

    public String f_belong_id;//任务id，或任务图斑表id

    public int f_attribute_type;//1.下发、2.app上传

    public int f_audio_time;//录音文件的时间

    public nrrms_task_patch_data(@NonNull String f_data_id, String f_data_name,
                                 String f_data_size, String f_data_update_time,
                                 String f_data_type, String f_data_path, String f_belong_id,
                                 int f_attribute_type, int f_audio_time) {
        this.f_data_id = f_data_id;
        this.f_data_name = f_data_name;
        this.f_data_size = f_data_size;
        this.f_data_update_time = f_data_update_time;
        this.f_data_type = f_data_type;
        this.f_data_path = f_data_path;
        this.f_belong_id = f_belong_id;
        this.f_attribute_type = f_attribute_type;
        this.f_audio_time = f_audio_time;
    }
}
