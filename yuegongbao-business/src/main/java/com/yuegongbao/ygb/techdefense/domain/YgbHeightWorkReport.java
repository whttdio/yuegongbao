package com.yuegongbao.ygb.techdefense.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * 高处作业申报报备对象 t_height_work_report
 *
 * @author yuegongbao
 */
public class YgbHeightWorkReport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "报备ID")
    private Long reportId;

    @Excel(name = "报备编号")
    private String reportNo;

    private Long enterpriseId;

    @Excel(name = "作业单位/个人")
    private String enterpriseName;

    private String reporterType;

    @Excel(name = "填报人")
    private String applicantName;

    @Excel(name = "填报人联系电话")
    private String applicantPhone;

    @Excel(name = "作业地点", width = 30)
    private String workLocation;

    private BigDecimal longitude;

    private BigDecimal latitude;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "计划开始时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "计划结束时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @Excel(name = "作业高度(米)")
    private Integer workHeightM;

    @Excel(name = "作业人数")
    private Integer workerCount;

    @Excel(name = "监护人")
    private String guardianName;

    @Excel(name = "监护人电话")
    private String guardianPhone;

    private String safetyMeasuresJson;

    private List<String> safetyMeasures;

    @Excel(name = "证书核验状态", readConverterExp = "0=全部有效,1=部分失效,2=全部失效")
    private String certValidStatus;

    @Excel(name = "有效证书人数")
    private Integer certValidCount;

    @Excel(name = "无效证书人数")
    private Integer certInvalidCount;

    @Excel(name = "报备状态", readConverterExp = "0=已报备,1=已结束")
    private String reportStatus;

    @Excel(name = "来源模式")
    private String sourceMode;

    @Excel(name = "来源平台")
    private String sourcePlatform;

    private String sourceSerialNo;

    private String voucherToken;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actualEndTime;

    private String endPhotoUrl;

    private List<YgbHeightWorkReportWorker> workerList;

    private String regionCode;

    private String startTimeBegin;

    private String startTimeEnd;

    public Long getReportId()
    {
        return reportId;
    }

    public void setReportId(Long reportId)
    {
        this.reportId = reportId;
    }

    public String getReportNo()
    {
        return reportNo;
    }

    public void setReportNo(String reportNo)
    {
        this.reportNo = reportNo;
    }

    public Long getEnterpriseId()
    {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }

    @NotBlank(message = "作业单位/个人不能为空")
    public String getEnterpriseName()
    {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName)
    {
        this.enterpriseName = enterpriseName;
    }

    @NotBlank(message = "报备主体类型不能为空")
    public String getReporterType()
    {
        return reporterType;
    }

    public void setReporterType(String reporterType)
    {
        this.reporterType = reporterType;
    }

    @NotBlank(message = "填报人姓名不能为空")
    public String getApplicantName()
    {
        return applicantName;
    }

    public void setApplicantName(String applicantName)
    {
        this.applicantName = applicantName;
    }

    @NotBlank(message = "填报人联系电话不能为空")
    public String getApplicantPhone()
    {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone)
    {
        this.applicantPhone = applicantPhone;
    }

    @NotBlank(message = "作业地点不能为空")
    public String getWorkLocation()
    {
        return workLocation;
    }

    public void setWorkLocation(String workLocation)
    {
        this.workLocation = workLocation;
    }

    @DecimalMin(value = "-180.000000", message = "经度超出范围")
    @DecimalMax(value = "180.000000", message = "经度超出范围")
    public BigDecimal getLongitude()
    {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude)
    {
        this.longitude = longitude;
    }

    @DecimalMin(value = "-90.000000", message = "纬度超出范围")
    @DecimalMax(value = "90.000000", message = "纬度超出范围")
    public BigDecimal getLatitude()
    {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude)
    {
        this.latitude = latitude;
    }

    @NotNull(message = "计划开始时间不能为空")
    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    @NotNull(message = "计划结束时间不能为空")
    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    @NotNull(message = "作业高度不能为空")
    @Min(value = 2, message = "高处作业高度不能低于2米")
    public Integer getWorkHeightM()
    {
        return workHeightM;
    }

    public void setWorkHeightM(Integer workHeightM)
    {
        this.workHeightM = workHeightM;
    }

    public Integer getWorkerCount()
    {
        return workerCount;
    }

    public void setWorkerCount(Integer workerCount)
    {
        this.workerCount = workerCount;
    }

    @NotBlank(message = "监护人姓名不能为空")
    public String getGuardianName()
    {
        return guardianName;
    }

    public void setGuardianName(String guardianName)
    {
        this.guardianName = guardianName;
    }

    @NotBlank(message = "监护人电话不能为空")
    public String getGuardianPhone()
    {
        return guardianPhone;
    }

    public void setGuardianPhone(String guardianPhone)
    {
        this.guardianPhone = guardianPhone;
    }

    public String getSafetyMeasuresJson()
    {
        return safetyMeasuresJson;
    }

    public void setSafetyMeasuresJson(String safetyMeasuresJson)
    {
        this.safetyMeasuresJson = safetyMeasuresJson;
    }

    @NotEmpty(message = "请至少勾选一项安全措施")
    public List<String> getSafetyMeasures()
    {
        return safetyMeasures;
    }

    public void setSafetyMeasures(List<String> safetyMeasures)
    {
        this.safetyMeasures = safetyMeasures;
    }

    public String getCertValidStatus()
    {
        return certValidStatus;
    }

    public void setCertValidStatus(String certValidStatus)
    {
        this.certValidStatus = certValidStatus;
    }

    public Integer getCertValidCount()
    {
        return certValidCount;
    }

    public void setCertValidCount(Integer certValidCount)
    {
        this.certValidCount = certValidCount;
    }

    public Integer getCertInvalidCount()
    {
        return certInvalidCount;
    }

    public void setCertInvalidCount(Integer certInvalidCount)
    {
        this.certInvalidCount = certInvalidCount;
    }

    public String getReportStatus()
    {
        return reportStatus;
    }

    public void setReportStatus(String reportStatus)
    {
        this.reportStatus = reportStatus;
    }

    public String getSourceMode()
    {
        return sourceMode;
    }

    public void setSourceMode(String sourceMode)
    {
        this.sourceMode = sourceMode;
    }

    public String getSourcePlatform()
    {
        return sourcePlatform;
    }

    public void setSourcePlatform(String sourcePlatform)
    {
        this.sourcePlatform = sourcePlatform;
    }

    public String getSourceSerialNo()
    {
        return sourceSerialNo;
    }

    public void setSourceSerialNo(String sourceSerialNo)
    {
        this.sourceSerialNo = sourceSerialNo;
    }

    public String getVoucherToken()
    {
        return voucherToken;
    }

    public void setVoucherToken(String voucherToken)
    {
        this.voucherToken = voucherToken;
    }

    public Date getActualEndTime()
    {
        return actualEndTime;
    }

    public void setActualEndTime(Date actualEndTime)
    {
        this.actualEndTime = actualEndTime;
    }

    public String getEndPhotoUrl()
    {
        return endPhotoUrl;
    }

    public void setEndPhotoUrl(String endPhotoUrl)
    {
        this.endPhotoUrl = endPhotoUrl;
    }

    @Valid
    @NotEmpty(message = "作业人员清单不能为空")
    public List<YgbHeightWorkReportWorker> getWorkerList()
    {
        return workerList;
    }

    public void setWorkerList(List<YgbHeightWorkReportWorker> workerList)
    {
        this.workerList = workerList;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    public String getStartTimeBegin()
    {
        return startTimeBegin;
    }

    public void setStartTimeBegin(String startTimeBegin)
    {
        this.startTimeBegin = startTimeBegin;
    }

    public String getStartTimeEnd()
    {
        return startTimeEnd;
    }

    public void setStartTimeEnd(String startTimeEnd)
    {
        this.startTimeEnd = startTimeEnd;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("reportId", getReportId())
            .append("reportNo", getReportNo())
            .append("enterpriseId", getEnterpriseId())
            .append("enterpriseName", getEnterpriseName())
            .append("reportStatus", getReportStatus())
            .append("certValidStatus", getCertValidStatus())
            .toString();
    }
}
