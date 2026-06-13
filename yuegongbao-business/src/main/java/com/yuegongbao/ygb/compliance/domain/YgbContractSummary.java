package com.yuegongbao.ygb.compliance.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YgbContractSummary
{
    private int totalCount;

    private int filedCount;

    private int pendingCount;

    private int rejectedCount;

    private int expiredCount;

    private int expiringSoonCount;

    private int unfiledCount;

    private int ocrFailedCount;

    private int clauseMissingCount;

    private List<Map<String, Object>> ygbExplanation = new ArrayList<>();

    public int getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public int getFiledCount()
    {
        return filedCount;
    }

    public void setFiledCount(int filedCount)
    {
        this.filedCount = filedCount;
    }

    public int getPendingCount()
    {
        return pendingCount;
    }

    public void setPendingCount(int pendingCount)
    {
        this.pendingCount = pendingCount;
    }

    public int getRejectedCount()
    {
        return rejectedCount;
    }

    public void setRejectedCount(int rejectedCount)
    {
        this.rejectedCount = rejectedCount;
    }

    public int getExpiredCount()
    {
        return expiredCount;
    }

    public void setExpiredCount(int expiredCount)
    {
        this.expiredCount = expiredCount;
    }

    public int getOcrFailedCount()
    {
        return ocrFailedCount;
    }

    public void setOcrFailedCount(int ocrFailedCount)
    {
        this.ocrFailedCount = ocrFailedCount;
    }

    public int getClauseMissingCount()
    {
        return clauseMissingCount;
    }

    public void setClauseMissingCount(int clauseMissingCount)
    {
        this.clauseMissingCount = clauseMissingCount;
    }

    public int getExpiringSoonCount()
    {
        return expiringSoonCount;
    }

    public void setExpiringSoonCount(int expiringSoonCount)
    {
        this.expiringSoonCount = expiringSoonCount;
    }

    public int getUnfiledCount()
    {
        return unfiledCount;
    }

    public void setUnfiledCount(int unfiledCount)
    {
        this.unfiledCount = unfiledCount;
    }

    public List<Map<String, Object>> getYgbExplanation()
    {
        return ygbExplanation;
    }

    public void setYgbExplanation(List<Map<String, Object>> ygbExplanation)
    {
        this.ygbExplanation = ygbExplanation;
    }
}
