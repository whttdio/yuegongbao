package com.yuegongbao.ygb.cockpit.domain;

import java.util.List;
import java.util.Map;

public class YgbGeoJsonFeatureCollection
{
    private String type = "FeatureCollection";

    private String statDate;

    private List<Map<String, Object>> features;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getStatDate()
    {
        return statDate;
    }

    public void setStatDate(String statDate)
    {
        this.statDate = statDate;
    }

    public List<Map<String, Object>> getFeatures()
    {
        return features;
    }

    public void setFeatures(List<Map<String, Object>> features)
    {
        this.features = features;
    }
}
