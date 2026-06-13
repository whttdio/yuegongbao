package com.yuegongbao.ygb.regulation.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

/**
 * 假外包识别对象 t_fake_outsourcing_record。
 *
 * @author yuegongbao
 */
public class YgbFakeOutsourcingRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "记录ID")
    private Long recordId;

    @Excel(name = "统计月份")
    private String statMonth;

    private Long enterpriseId;

    @Excel(name = "企业名称")
    private String enterpriseName;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "考勤管理分")
    private Integer attendanceScore;

    @Excel(name = "排班管理分")
    private Integer scheduleScore;

    @Excel(name = "奖惩管理分")
    private Integer rewardScore;

    @Excel(name = "培训管理分")
    private Integer trainingScore;

    @Excel(name = "综合分")
    private Integer totalScore;

    @Excel(name = "疑似标志", readConverterExp = "0=否,1=是")
    private String suspectedFlag;

    @Excel(name = "预警状态", readConverterExp = "0=未预警,1=已预警")
    private String warningStatus;

    @Excel(name = "证据摘要", width = 30)
    private String evidenceSummary;

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public String getStatMonth()
    {
        return statMonth;
    }

    public void setStatMonth(String statMonth)
    {
        this.statMonth = statMonth;
    }

    public Long getEnterpriseId()
    {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName()
    {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName)
    {
        this.enterpriseName = enterpriseName;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    public Integer getAttendanceScore()
    {
        return attendanceScore;
    }

    public void setAttendanceScore(Integer attendanceScore)
    {
        this.attendanceScore = attendanceScore;
    }

    public Integer getScheduleScore()
    {
        return scheduleScore;
    }

    public void setScheduleScore(Integer scheduleScore)
    {
        this.scheduleScore = scheduleScore;
    }

    public Integer getRewardScore()
    {
        return rewardScore;
    }

    public void setRewardScore(Integer rewardScore)
    {
        this.rewardScore = rewardScore;
    }

    public Integer getTrainingScore()
    {
        return trainingScore;
    }

    public void setTrainingScore(Integer trainingScore)
    {
        this.trainingScore = trainingScore;
    }

    public Integer getTotalScore()
    {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore)
    {
        this.totalScore = totalScore;
    }

    public String getSuspectedFlag()
    {
        return suspectedFlag;
    }

    public void setSuspectedFlag(String suspectedFlag)
    {
        this.suspectedFlag = suspectedFlag;
    }

    public String getWarningStatus()
    {
        return warningStatus;
    }

    public void setWarningStatus(String warningStatus)
    {
        this.warningStatus = warningStatus;
    }

    public String getEvidenceSummary()
    {
        return evidenceSummary;
    }

    public void setEvidenceSummary(String evidenceSummary)
    {
        this.evidenceSummary = evidenceSummary;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("statMonth", getStatMonth())
            .append("enterpriseName", getEnterpriseName())
            .append("totalScore", getTotalScore())
            .append("suspectedFlag", getSuspectedFlag())
            .toString();
    }
}
