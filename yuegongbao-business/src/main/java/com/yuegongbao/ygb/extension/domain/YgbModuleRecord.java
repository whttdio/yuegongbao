package com.yuegongbao.ygb.extension.domain;

import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

public class YgbModuleRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "记录ID")
    private Long recordId;

    private String recordType;

    @Excel(name = "记录名称")
    private String recordName;

    @Excel(name = "分类编码")
    private String categoryCode;

    @Excel(name = "统计月份")
    private String statMonth;

    @Excel(name = "门户编码")
    private String portalCode;

    @Excel(name = "处理状态")
    private String workflowStatus;

    @Excel(name = "启停状态")
    private String status;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "企业ID")
    private Long enterpriseId;

    @Excel(name = "企业名称")
    private String enterpriseName;

    @Excel(name = "人员ID")
    private Long personId;

    @Excel(name = "人员姓名")
    private String personName;

    @Excel(name = "关联ID")
    private Long relatedId;

    @Excel(name = "关联编码")
    private String relatedCode;

    @Excel(name = "排序")
    private Integer sortOrder;

    @Excel(name = "来源标签")
    private String sourceLabel;

    private String payloadJson;

    private String platformName;

    private String industryType;

    private String warningLevel;

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public String getRecordType()
    {
        return recordType;
    }

    public void setRecordType(String recordType)
    {
        this.recordType = recordType;
    }

    public String getRecordName()
    {
        return recordName;
    }

    public void setRecordName(String recordName)
    {
        this.recordName = recordName;
    }

    public String getCategoryCode()
    {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode)
    {
        this.categoryCode = categoryCode;
    }

    public String getStatMonth()
    {
        return statMonth;
    }

    public void setStatMonth(String statMonth)
    {
        this.statMonth = statMonth;
    }

    public String getPortalCode()
    {
        return portalCode;
    }

    public void setPortalCode(String portalCode)
    {
        this.portalCode = portalCode;
    }

    public String getWorkflowStatus()
    {
        return workflowStatus;
    }

    public void setWorkflowStatus(String workflowStatus)
    {
        this.workflowStatus = workflowStatus;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
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

    public Long getPersonId()
    {
        return personId;
    }

    public void setPersonId(Long personId)
    {
        this.personId = personId;
    }

    public String getPersonName()
    {
        return personName;
    }

    public void setPersonName(String personName)
    {
        this.personName = personName;
    }

    public Long getRelatedId()
    {
        return relatedId;
    }

    public void setRelatedId(Long relatedId)
    {
        this.relatedId = relatedId;
    }

    public String getRelatedCode()
    {
        return relatedCode;
    }

    public void setRelatedCode(String relatedCode)
    {
        this.relatedCode = relatedCode;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public String getSourceLabel()
    {
        return sourceLabel;
    }

    public void setSourceLabel(String sourceLabel)
    {
        this.sourceLabel = sourceLabel;
    }

    public String getPayloadJson()
    {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson)
    {
        this.payloadJson = payloadJson;
    }

    public String getPlatformName()
    {
        return platformName;
    }

    public void setPlatformName(String platformName)
    {
        this.platformName = platformName;
    }

    public String getIndustryType()
    {
        return industryType;
    }

    public void setIndustryType(String industryType)
    {
        this.industryType = industryType;
    }

    public String getWarningLevel()
    {
        return warningLevel;
    }

    public void setWarningLevel(String warningLevel)
    {
        this.warningLevel = warningLevel;
    }
}
