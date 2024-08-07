package com.example.democipherroom.db.testentry;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


//任务图斑表
@Entity(tableName = nrrms_task_patch.TABLE_NAME)
public class nrrms_task_patch {
    public static final String TABLE_NAME = "nrrms_task_patch";

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "f_patch_id")
    public String fpatchId;//主键id

    @ColumnInfo(name = "f_task_id")
    public String ftaskId;//任务主键id

    @ColumnInfo(name = "f_patch_code")
    public String fpatchCode;//图斑编号

    @ColumnInfo(name = "f_patch_area")
    public String fpatchArea;//图斑面积（亩）

    @ColumnInfo(name = "f_patch_type")
    public String fpatchType;//图斑类型

    @ColumnInfo(name = "f_patch_variation_type")
    public String fpatchVariationType;//变化类型

    @ColumnInfo(name = "f_patch_admin_region_code")
    public String fpatchAdminRegionCode;//行政区划编号

    @ColumnInfo(name = "f_patch_admin_region_name")
    public String fpatchAdminRegionName;//行政区划名称

    @ColumnInfo(name = "f_patch_data_source")
    public String fpatchDataSource;//数据来源：1.遥感监测图斑、2.新增上报图斑

    @ColumnInfo(name = "f_patch_distribution_status")
    public int fpatchDistributionStatus;//当前状态-分配：1.未分配(未导出)、2.已分配(已导出)、3.已接收（导出时）

    @ColumnInfo(name = "f_patch_survey_status")
    public int fpatchSurveyStatus;//当前状态-调查：1.未核查、2.已核查、3.已提交（导出时）

    @ColumnInfo(name = "f_patch_check_status")
    public int fatchCheckStatus;//当前状态-审核：1.审核通过、2.退回

    @ColumnInfo(name = "f_patch_investigator_id")
    public String fpatchInvestigatorId;//调查人员id

    @ColumnInfo(name = "f_patch_investigator_name")
    public String fpatchInvestigatorName;//调查人员名称

    @ColumnInfo(name = "f_patch_description")
    public String fpatchDescription;//描述 1.选中,0 未选中

    @ColumnInfo(name = "f_patch_note")
    public String fpatchNote;//备注

    @ColumnInfo(name = "f_patch_wkt")
    public String fpatchWkt;//图斑空间属性

    @ColumnInfo(name = "f_patch_update_time")
    public String fpatchUpdateTime;//图斑更新时间

    @ColumnInfo(name = "f_after_phase_type")
    public int fafterPhaseType;//后时相地类类型

    @ColumnInfo(name = "f_after_phase_name")
    public String fafterPhaseName;//后时相地类名称


    @ColumnInfo(name = "f_patch_distribution_time")
    public String fpatchDistributionTime;//图斑的最新更新时间

    @ColumnInfo(name = "f_is_download")
    public Boolean fisDownload=false;//图斑多媒体信息是否下载  true 已下载 false  未下载

    @ColumnInfo(name = "f_after_phase_dlmc_url")
    public String afterPhaseDlmcUrl;//后时相图片路径

    @ColumnInfo(name = "f_before_phase_dlmc_url")
    public String beforePhaseDlmcUrl ;//前时相图片路径

    @ColumnInfo(name = "f_patch_create_time")
    public String fpatchCreateTime ;//创建时间

    public nrrms_task_patch(@NonNull String fpatchId, String ftaskId, String fpatchCode,
                            String fpatchArea, String fpatchType, String fpatchVariationType,
                            String fpatchAdminRegionCode, String fpatchAdminRegionName,
                            String fpatchDataSource, int fpatchDistributionStatus,
                            int fpatchSurveyStatus, int fatchCheckStatus,
                            String fpatchInvestigatorId, String fpatchInvestigatorName,
                            String fpatchDescription, String fpatchNote, String fpatchWkt,
                            String fpatchUpdateTime, int fafterPhaseType, String fafterPhaseName,
                            String fpatchDistributionTime, Boolean fisDownload,
                            String afterPhaseDlmcUrl, String beforePhaseDlmcUrl,
                            String fpatchCreateTime/*, String afterPhaseDlmc*/) {
        this.fpatchId = fpatchId;
        this.ftaskId = ftaskId;
        this.fpatchCode = fpatchCode;
        this.fpatchArea = fpatchArea;
        this.fpatchType = fpatchType;
        this.fpatchVariationType = fpatchVariationType;
        this.fpatchAdminRegionCode = fpatchAdminRegionCode;
        this.fpatchAdminRegionName = fpatchAdminRegionName;
        this.fpatchDataSource = fpatchDataSource;
        this.fpatchDistributionStatus = fpatchDistributionStatus;
        this.fpatchSurveyStatus = fpatchSurveyStatus;
        this.fatchCheckStatus = fatchCheckStatus;
        this.fpatchInvestigatorId = fpatchInvestigatorId;
        this.fpatchInvestigatorName = fpatchInvestigatorName;
        this.fpatchDescription = fpatchDescription;
        this.fpatchNote = fpatchNote;
        this.fpatchWkt = fpatchWkt;
        this.fpatchUpdateTime = fpatchUpdateTime;
        this.fafterPhaseType = fafterPhaseType;
        this.fafterPhaseName = fafterPhaseName;
        this.fpatchDistributionTime = fpatchDistributionTime;
        this.fisDownload = fisDownload;
        this.afterPhaseDlmcUrl = afterPhaseDlmcUrl;
        this.beforePhaseDlmcUrl = beforePhaseDlmcUrl;
        this.fpatchCreateTime = fpatchCreateTime;
        // this.afterPhaseDlmc = afterPhaseDlmc;
    }
}
