package com.yuegongbao.ygb.compliance.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;

public class YgbContractTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "模板ID")
    private Long templateId;

    @Excel(name = "模板编码")
    private String templateCode;

    @Excel(name = "模板名称")
    private String templateName;

    @Excel(name = "模板版本")
    private String templateVersion;

    @Excel(name = "合同类型")
    private String templateType;

    @Excel(name = "适用范围")
    private String applicableScope;

    @Excel(name = "审核状态")
    private String reviewStatus;

    @Excel(name = "启停状态")
    private String status;

    @Excel(name = "区域编码")
    private String regionCode;

    @Excel(name = "模板文件")
    private String templateFileUrl;

    private String contentText;

    private String reviewBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

    private String reviewRemark;

    public Long getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(Long templateId)
    {
        this.templateId = templateId;
    }

    public String getTemplateCode()
    {
        return templateCode;
    }

    public void setTemplateCode(String templateCode)
    {
        this.templateCode = templateCode;
    }

    public String getTemplateName()
    {
        return templateName;
    }

    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    public String getTemplateVersion()
    {
        return templateVersion;
    }

    public void setTemplateVersion(String templateVersion)
    {
        this.templateVersion = templateVersion;
    }

    public String getTemplateType()
    {
        return templateType;
    }

    public void setTemplateType(String templateType)
    {
        this.templateType = templateType;
    }

    public String getApplicableScope()
    {
        return applicableScope;
    }

    public void setApplicableScope(String applicableScope)
    {
        this.applicableScope = applicableScope;
    }

    public String getReviewStatus()
    {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus)
    {
        this.reviewStatus = reviewStatus;
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

    public String getTemplateFileUrl()
    {
        return templateFileUrl;
    }

    public void setTemplateFileUrl(String templateFileUrl)
    {
        this.templateFileUrl = templateFileUrl;
    }

    public String getContentText()
    {
        return contentText;
    }

    public void setContentText(String contentText)
    {
        this.contentText = contentText;
    }

    public String getReviewBy()
    {
        return reviewBy;
    }

    public void setReviewBy(String reviewBy)
    {
        this.reviewBy = reviewBy;
    }

    public Date getReviewTime()
    {
        return reviewTime;
    }

    public void setReviewTime(Date reviewTime)
    {
        this.reviewTime = reviewTime;
    }

    public String getReviewRemark()
    {
        return reviewRemark;
    }

    public void setReviewRemark(String reviewRemark)
    {
        this.reviewRemark = reviewRemark;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("templateId", getTemplateId())
            .append("templateCode", getTemplateCode())
            .append("templateName", getTemplateName())
            .append("templateVersion", getTemplateVersion())
            .append("templateType", getTemplateType())
            .append("applicableScope", getApplicableScope())
            .append("reviewStatus", getReviewStatus())
            .append("status", getStatus())
            .append("regionCode", getRegionCode())
            .append("templateFileUrl", getTemplateFileUrl())
            .append("contentText", getContentText())
            .append("reviewBy", getReviewBy())
            .append("reviewTime", getReviewTime())
            .append("reviewRemark", getReviewRemark())
            .append("remark", getRemark())
            .toString();
    }
}
