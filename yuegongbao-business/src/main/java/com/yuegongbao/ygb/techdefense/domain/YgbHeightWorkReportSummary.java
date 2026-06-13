package com.yuegongbao.ygb.techdefense.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YgbHeightWorkReportSummary
{
    private Integer totalCount;

    private Integer activeCount;

    private Integer finishedCount;

    private Integer invalidWorkerCount;

    private Integer importedCount;

    private Integer allInvalidCount;

    private Integer partialInvalidCount;

    private List<Map<String, Object>> ygbExplanation = new ArrayList<>();

    private List<Map<String, Object>> azbExplanation = new ArrayList<>();

    public Integer getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }

    public Integer getActiveCount()
    {
        return activeCount;
    }

    public void setActiveCount(Integer activeCount)
    {
        this.activeCount = activeCount;
    }

    public Integer getFinishedCount()
    {
        return finishedCount;
    }

    public void setFinishedCount(Integer finishedCount)
    {
        this.finishedCount = finishedCount;
    }

    public Integer getInvalidWorkerCount()
    {
        return invalidWorkerCount;
    }

    public void setInvalidWorkerCount(Integer invalidWorkerCount)
    {
        this.invalidWorkerCount = invalidWorkerCount;
    }

    public Integer getImportedCount()
    {
        return importedCount;
    }

    public void setImportedCount(Integer importedCount)
    {
        this.importedCount = importedCount;
    }

    public Integer getAllInvalidCount()
    {
        return allInvalidCount;
    }

    public void setAllInvalidCount(Integer allInvalidCount)
    {
        this.allInvalidCount = allInvalidCount;
    }

    public Integer getPartialInvalidCount()
    {
        return partialInvalidCount;
    }

    public void setPartialInvalidCount(Integer partialInvalidCount)
    {
        this.partialInvalidCount = partialInvalidCount;
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
