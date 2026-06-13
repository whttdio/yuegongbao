package com.yuegongbao.ygb.warning.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

/**
 * 预警工单对象 t_warning。
 *
 * @author yuegongbao
 */
public class YgbWarning extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "预警ID")
    private Long warnId;

    @Excel(name = "预警级别", readConverterExp = "1=提示,2=黄警,3=红警")
    private String warnLevel;

    @Excel(name = "预警类型")
    private String warnType;

    @Excel(name = "来源模块")
    private String sourceModule;

    private Long targetObjectId;

    @Excel(name = "对象类型", readConverterExp = "1=企业,2=设备,3=人员")
    private String targetType;

    @Excel(name = "企业ID")
    private Long enterpriseId;

    @Excel(name = "企业名称")
    private String enterpriseName;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "预警内容", width = 30)
    private String content;

    private String evidenceUrl;

    @Excel(name = "状态", readConverterExp = "0=待处理,1=处理中,2=已办结,3=误报,4=已升级")
    private String warnStatus;

    private Long assignTo;

    @Excel(name = "处置人")
    private String assignName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 22, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "办结时间", width = 22, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date resolveTime;

    public Long getWarnId()
    {
        return warnId;
    }

    public void setWarnId(Long warnId)
    {
        this.warnId = warnId;
    }

    public String getWarnLevel()
    {
        return warnLevel;
    }

    public void setWarnLevel(String warnLevel)
    {
        this.warnLevel = warnLevel;
    }

    public String getWarnType()
    {
        return warnType;
    }

    public void setWarnType(String warnType)
    {
        this.warnType = warnType;
    }

    public String getSourceModule()
    {
        return sourceModule;
    }

    public void setSourceModule(String sourceModule)
    {
        this.sourceModule = sourceModule;
    }

    public Long getTargetObjectId()
    {
        return targetObjectId;
    }

    public void setTargetObjectId(Long targetObjectId)
    {
        this.targetObjectId = targetObjectId;
    }

    public String getTargetType()
    {
        return targetType;
    }

    public void setTargetType(String targetType)
    {
        this.targetType = targetType;
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

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getEvidenceUrl()
    {
        return evidenceUrl;
    }

    public void setEvidenceUrl(String evidenceUrl)
    {
        this.evidenceUrl = evidenceUrl;
    }

    public String getWarnStatus()
    {
        return warnStatus;
    }

    public void setWarnStatus(String warnStatus)
    {
        this.warnStatus = warnStatus;
    }

    public Long getAssignTo()
    {
        return assignTo;
    }

    public void setAssignTo(Long assignTo)
    {
        this.assignTo = assignTo;
    }

    public String getAssignName()
    {
        return assignName;
    }

    public void setAssignName(String assignName)
    {
        this.assignName = assignName;
    }

    @Override
    public Date getCreateTime()
    {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getResolveTime()
    {
        return resolveTime;
    }

    public void setResolveTime(Date resolveTime)
    {
        this.resolveTime = resolveTime;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("warnId", getWarnId())
            .append("warnLevel", getWarnLevel())
            .append("warnType", getWarnType())
            .append("sourceModule", getSourceModule())
            .append("targetObjectId", getTargetObjectId())
            .append("targetType", getTargetType())
            .append("enterpriseId", getEnterpriseId())
            .append("enterpriseName", getEnterpriseName())
            .append("regionCode", getRegionCode())
            .append("content", getContent())
            .append("evidenceUrl", getEvidenceUrl())
            .append("warnStatus", getWarnStatus())
            .append("assignTo", getAssignTo())
            .append("assignName", getAssignName())
            .append("createTime", getCreateTime())
            .append("resolveTime", getResolveTime())
            .toString();
    }
}
