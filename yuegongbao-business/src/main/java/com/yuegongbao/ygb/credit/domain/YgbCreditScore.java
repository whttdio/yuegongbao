package com.yuegongbao.ygb.credit.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

/**
 * 企业信用评分对象 t_credit_score。
 *
 * @author yuegongbao
 */
public class YgbCreditScore extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "评分ID")
    private Long scoreId;

    @Excel(name = "统计月份")
    private String statMonth;

    private Long enterpriseId;

    @Excel(name = "企业名称")
    private String enterpriseName;

    @Excel(name = "区域编码")
    private String regionCode;

    private String regionName;

    @Excel(name = "企业类型", readConverterExp = "1=派遣单位,2=用工单位,3=服务机构,4=监管单位")
    private String enterpriseType;

    @Excel(name = "合同备案得分")
    private BigDecimal contractScore;

    @Excel(name = "考勤归集得分")
    private BigDecimal attendanceScore;

    @Excel(name = "工资发放得分")
    private BigDecimal salaryScore;

    @Excel(name = "社税合规得分")
    private BigDecimal socialTaxScore;

    @Excel(name = "安全保障得分")
    private BigDecimal safetyScore;

    @Excel(name = "预警治理得分")
    private BigDecimal governanceScore;

    @Excel(name = "信用总分")
    private BigDecimal totalScore;

    @Excel(name = "信用等级")
    private String creditLevel;

    @Excel(name = "红黄绿码")
    private String colorCode;

    @Excel(name = "排名")
    private Integer rankNo;

    private String factorJson;

    @Excel(name = "摘要", width = 40)
    private String summaryText;

    @Excel(name = "预警状态", readConverterExp = "0=未预警,1=已预警")
    private String warningStatus;

    private String sourceMode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "评估时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date evaluateTime;

    public Long getScoreId()
    {
        return scoreId;
    }

    public void setScoreId(Long scoreId)
    {
        this.scoreId = scoreId;
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

    public String getRegionName()
    {
        return regionName;
    }

    public void setRegionName(String regionName)
    {
        this.regionName = regionName;
    }

    public String getEnterpriseType()
    {
        return enterpriseType;
    }

    public void setEnterpriseType(String enterpriseType)
    {
        this.enterpriseType = enterpriseType;
    }

    public BigDecimal getContractScore()
    {
        return contractScore;
    }

    public void setContractScore(BigDecimal contractScore)
    {
        this.contractScore = contractScore;
    }

    public BigDecimal getAttendanceScore()
    {
        return attendanceScore;
    }

    public void setAttendanceScore(BigDecimal attendanceScore)
    {
        this.attendanceScore = attendanceScore;
    }

    public BigDecimal getSalaryScore()
    {
        return salaryScore;
    }

    public void setSalaryScore(BigDecimal salaryScore)
    {
        this.salaryScore = salaryScore;
    }

    public BigDecimal getSocialTaxScore()
    {
        return socialTaxScore;
    }

    public void setSocialTaxScore(BigDecimal socialTaxScore)
    {
        this.socialTaxScore = socialTaxScore;
    }

    public BigDecimal getSafetyScore()
    {
        return safetyScore;
    }

    public void setSafetyScore(BigDecimal safetyScore)
    {
        this.safetyScore = safetyScore;
    }

    public BigDecimal getGovernanceScore()
    {
        return governanceScore;
    }

    public void setGovernanceScore(BigDecimal governanceScore)
    {
        this.governanceScore = governanceScore;
    }

    public BigDecimal getTotalScore()
    {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore)
    {
        this.totalScore = totalScore;
    }

    public String getCreditLevel()
    {
        return creditLevel;
    }

    public void setCreditLevel(String creditLevel)
    {
        this.creditLevel = creditLevel;
    }

    public String getColorCode()
    {
        return colorCode;
    }

    public void setColorCode(String colorCode)
    {
        this.colorCode = colorCode;
    }

    public Integer getRankNo()
    {
        return rankNo;
    }

    public void setRankNo(Integer rankNo)
    {
        this.rankNo = rankNo;
    }

    public String getFactorJson()
    {
        return factorJson;
    }

    public void setFactorJson(String factorJson)
    {
        this.factorJson = factorJson;
    }

    public String getSummaryText()
    {
        return summaryText;
    }

    public void setSummaryText(String summaryText)
    {
        this.summaryText = summaryText;
    }

    public String getWarningStatus()
    {
        return warningStatus;
    }

    public void setWarningStatus(String warningStatus)
    {
        this.warningStatus = warningStatus;
    }

    public String getSourceMode()
    {
        return sourceMode;
    }

    public void setSourceMode(String sourceMode)
    {
        this.sourceMode = sourceMode;
    }

    public Date getEvaluateTime()
    {
        return evaluateTime;
    }

    public void setEvaluateTime(Date evaluateTime)
    {
        this.evaluateTime = evaluateTime;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("scoreId", getScoreId())
            .append("statMonth", getStatMonth())
            .append("enterpriseId", getEnterpriseId())
            .append("enterpriseName", getEnterpriseName())
            .append("regionCode", getRegionCode())
            .append("enterpriseType", getEnterpriseType())
            .append("totalScore", getTotalScore())
            .append("creditLevel", getCreditLevel())
            .append("colorCode", getColorCode())
            .append("rankNo", getRankNo())
            .append("warningStatus", getWarningStatus())
            .append("evaluateTime", getEvaluateTime())
            .append("summaryText", getSummaryText())
            .toString();
    }
}
