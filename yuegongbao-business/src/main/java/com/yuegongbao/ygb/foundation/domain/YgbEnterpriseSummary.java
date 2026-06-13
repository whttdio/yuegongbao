package com.yuegongbao.ygb.foundation.domain;

import java.util.List;
import java.util.Map;

public class YgbEnterpriseSummary
{
    private Integer totalCount;
    private Integer normalCount;
    private Integer syncErrorCount;
    private Integer employerCount;
    private Integer dispatchCount;
    private Integer serviceOrgCount;
    private Integer disabledCount;
    private List<Map<String, Object>> ygbExplanation;
    private List<Map<String, Object>> azbExplanation;

    public Integer getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }

    public Integer getNormalCount()
    {
        return normalCount;
    }

    public void setNormalCount(Integer normalCount)
    {
        this.normalCount = normalCount;
    }

    public Integer getSyncErrorCount()
    {
        return syncErrorCount;
    }

    public void setSyncErrorCount(Integer syncErrorCount)
    {
        this.syncErrorCount = syncErrorCount;
    }

    public Integer getEmployerCount()
    {
        return employerCount;
    }

    public void setEmployerCount(Integer employerCount)
    {
        this.employerCount = employerCount;
    }

    public Integer getDispatchCount()
    {
        return dispatchCount;
    }

    public void setDispatchCount(Integer dispatchCount)
    {
        this.dispatchCount = dispatchCount;
    }

    public Integer getServiceOrgCount()
    {
        return serviceOrgCount;
    }

    public void setServiceOrgCount(Integer serviceOrgCount)
    {
        this.serviceOrgCount = serviceOrgCount;
    }

    public Integer getDisabledCount()
    {
        return disabledCount;
    }

    public void setDisabledCount(Integer disabledCount)
    {
        this.disabledCount = disabledCount;
    }

    public List<Map<String, Object>> getYgbExplanation()
    {
        return ygbExplanation;
    }

    public void setYgbExplanation(List<Map<String, Object>> ygbExplanation)
    {
        this.ygbExplanation = ygbExplanation;
    }

    public List<Map<String, Object>> getAzbExplanation()
    {
        return azbExplanation;
    }

    public void setAzbExplanation(List<Map<String, Object>> azbExplanation)
    {
        this.azbExplanation = azbExplanation;
    }
}
