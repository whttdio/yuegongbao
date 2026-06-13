package com.yuegongbao.ygb.safety.domain;

import java.util.List;
import java.util.Map;

public class YgbDeviceSummary
{
    private Integer totalCount;

    private Integer onlineCount;

    private Integer lockedOrFaultCount;

    private Integer authDeniedCount;

    private Integer unauthorizedCount;

    private Integer chipDeviceCount;

    private Integer aiCameraCount;

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

    public Integer getOnlineCount()
    {
        return onlineCount;
    }

    public void setOnlineCount(Integer onlineCount)
    {
        this.onlineCount = onlineCount;
    }

    public Integer getLockedOrFaultCount()
    {
        return lockedOrFaultCount;
    }

    public void setLockedOrFaultCount(Integer lockedOrFaultCount)
    {
        this.lockedOrFaultCount = lockedOrFaultCount;
    }

    public Integer getAuthDeniedCount()
    {
        return authDeniedCount;
    }

    public void setAuthDeniedCount(Integer authDeniedCount)
    {
        this.authDeniedCount = authDeniedCount;
    }

    public Integer getUnauthorizedCount()
    {
        return unauthorizedCount;
    }

    public void setUnauthorizedCount(Integer unauthorizedCount)
    {
        this.unauthorizedCount = unauthorizedCount;
    }

    public Integer getChipDeviceCount()
    {
        return chipDeviceCount;
    }

    public void setChipDeviceCount(Integer chipDeviceCount)
    {
        this.chipDeviceCount = chipDeviceCount;
    }

    public Integer getAiCameraCount()
    {
        return aiCameraCount;
    }

    public void setAiCameraCount(Integer aiCameraCount)
    {
        this.aiCameraCount = aiCameraCount;
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
