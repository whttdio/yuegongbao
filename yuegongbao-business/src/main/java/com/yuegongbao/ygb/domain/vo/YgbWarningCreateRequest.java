package com.yuegongbao.ygb.domain.vo;

/**
 * 统一预警创建请求。
 *
 * @author yuegongbao
 */
public class YgbWarningCreateRequest
{
    private String warnLevel;

    private String warnType;

    private String sourceModule;

    private Long targetObjectId;

    private String targetType;

    private Long enterpriseId;

    private String enterpriseName;

    private String regionCode;

    private String content;

    private String evidenceUrl;

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
}
