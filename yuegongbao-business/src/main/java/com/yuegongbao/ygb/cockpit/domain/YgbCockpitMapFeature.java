package com.yuegongbao.ygb.cockpit.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuegongbao.common.core.domain.BaseEntity;

public class YgbCockpitMapFeature extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long featureId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date statDate;

    private String regionCode;

    private String featureType;

    private String featureName;

    private String geometryType;

    private String geometryJson;

    private String featureStatus;

    private String sourceMode;

    private String propertiesJson;

    private Integer sortNo;

    public Long getFeatureId()
    {
        return featureId;
    }

    public void setFeatureId(Long featureId)
    {
        this.featureId = featureId;
    }

    public Date getStatDate()
    {
        return statDate;
    }

    public void setStatDate(Date statDate)
    {
        this.statDate = statDate;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
    }

    public String getFeatureType()
    {
        return featureType;
    }

    public void setFeatureType(String featureType)
    {
        this.featureType = featureType;
    }

    public String getFeatureName()
    {
        return featureName;
    }

    public void setFeatureName(String featureName)
    {
        this.featureName = featureName;
    }

    public String getGeometryType()
    {
        return geometryType;
    }

    public void setGeometryType(String geometryType)
    {
        this.geometryType = geometryType;
    }

    public String getGeometryJson()
    {
        return geometryJson;
    }

    public void setGeometryJson(String geometryJson)
    {
        this.geometryJson = geometryJson;
    }

    public String getFeatureStatus()
    {
        return featureStatus;
    }

    public void setFeatureStatus(String featureStatus)
    {
        this.featureStatus = featureStatus;
    }

    public String getSourceMode()
    {
        return sourceMode;
    }

    public void setSourceMode(String sourceMode)
    {
        this.sourceMode = sourceMode;
    }

    public String getPropertiesJson()
    {
        return propertiesJson;
    }

    public void setPropertiesJson(String propertiesJson)
    {
        this.propertiesJson = propertiesJson;
    }

    public Integer getSortNo()
    {
        return sortNo;
    }

    public void setSortNo(Integer sortNo)
    {
        this.sortNo = sortNo;
    }
}
