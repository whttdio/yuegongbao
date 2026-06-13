package com.yuegongbao.ygb.portal.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.annotation.Excel;
import com.yuegongbao.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotBlank;

/**
 * 门户网站 CMS 内容。
 */
public class YgbPortalContent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long contentId;

    @Excel(name = "门户")
    private String portalCode;

    @Excel(name = "栏目")
    private String sectionCode;

    @Excel(name = "分类")
    private String categoryCode;

    @Excel(name = "标题")
    private String title;

    @Excel(name = "摘要")
    private String summary;

    private String content;

    private String coverUrl;

    private String linkUrl;

    @Excel(name = "来源")
    private String sourceName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    private Integer sortOrder;

    @Excel(name = "状态", readConverterExp = "0=发布,1=停用")
    private String status;

    private String extraJson;

    public Long getContentId()
    {
        return contentId;
    }

    public void setContentId(Long contentId)
    {
        this.contentId = contentId;
    }

    @NotBlank(message = "门户编码不能为空")
    public String getPortalCode()
    {
        return portalCode;
    }

    public void setPortalCode(String portalCode)
    {
        this.portalCode = portalCode;
    }

    @NotBlank(message = "栏目编码不能为空")
    public String getSectionCode()
    {
        return sectionCode;
    }

    public void setSectionCode(String sectionCode)
    {
        this.sectionCode = sectionCode;
    }

    public String getCategoryCode()
    {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode)
    {
        this.categoryCode = categoryCode;
    }

    @NotBlank(message = "标题不能为空")
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getCoverUrl()
    {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl)
    {
        this.coverUrl = coverUrl;
    }

    public String getLinkUrl()
    {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl)
    {
        this.linkUrl = linkUrl;
    }

    public String getSourceName()
    {
        return sourceName;
    }

    public void setSourceName(String sourceName)
    {
        this.sourceName = sourceName;
    }

    public Date getPublishTime()
    {
        return publishTime;
    }

    public void setPublishTime(Date publishTime)
    {
        this.publishTime = publishTime;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getExtraJson()
    {
        return extraJson;
    }

    public void setExtraJson(String extraJson)
    {
        this.extraJson = extraJson;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("contentId", getContentId())
            .append("portalCode", getPortalCode())
            .append("sectionCode", getSectionCode())
            .append("categoryCode", getCategoryCode())
            .append("title", getTitle())
            .append("summary", getSummary())
            .append("status", getStatus())
            .toString();
    }
}
