package com.yuegongbao.ygb.safety.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotBlank;

/**
 * 工伤预防项目对象 t_prevention_project。
 *
 * @author yuegongbao
 */
public class YgbPreventionProject extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "项目ID")
    private Long projectId;

    @Excel(name = "项目名称")
    private String projectName;

    @Excel(name = "项目类型", readConverterExp = "1=宣传,2=培训,3=AI建设,4=隐患排查")
    private String projectType;

    private Long enterpriseId;

    @Excel(name = "企业名称")
    private String enterpriseName;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "预算金额")
    private BigDecimal budgetAmount;

    @Excel(name = "实际金额")
    private BigDecimal actualAmount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期", width = 18, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束日期", width = 18, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    @Excel(name = "项目状态", readConverterExp = "0=申报,1=立项,2=实施,3=验收,4=结项")
    private String projectStatus;

    @Excel(name = "评估分")
    private Integer evaluationScore;

    @Excel(name = "评估报告", width = 24)
    private String evaluationReport;

    public Long getProjectId()
    {
        return projectId;
    }

    public void setProjectId(Long projectId)
    {
        this.projectId = projectId;
    }

    @NotBlank(message = "项目名称不能为空")
    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public String getProjectType()
    {
        return projectType;
    }

    public void setProjectType(String projectType)
    {
        this.projectType = projectType;
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

    public BigDecimal getBudgetAmount()
    {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount)
    {
        this.budgetAmount = budgetAmount;
    }

    public BigDecimal getActualAmount()
    {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount)
    {
        this.actualAmount = actualAmount;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public String getProjectStatus()
    {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus)
    {
        this.projectStatus = projectStatus;
    }

    public Integer getEvaluationScore()
    {
        return evaluationScore;
    }

    public void setEvaluationScore(Integer evaluationScore)
    {
        this.evaluationScore = evaluationScore;
    }

    public String getEvaluationReport()
    {
        return evaluationReport;
    }

    public void setEvaluationReport(String evaluationReport)
    {
        this.evaluationReport = evaluationReport;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("projectId", getProjectId())
            .append("projectName", getProjectName())
            .append("projectType", getProjectType())
            .append("enterpriseName", getEnterpriseName())
            .append("projectStatus", getProjectStatus())
            .toString();
    }
}
