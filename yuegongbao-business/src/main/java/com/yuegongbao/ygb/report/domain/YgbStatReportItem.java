package com.yuegongbao.ygb.report.domain;

import java.math.BigDecimal;

public class YgbStatReportItem
{
    private Long itemId;

    private Long reportId;

    private String itemCategory;

    private String itemName;

    private String itemDimension;

    private Integer metricCount;

    private BigDecimal metricValue;

    private BigDecimal metricRate;

    private Integer sortNo;

    public Long getItemId()
    {
        return itemId;
    }

    public void setItemId(Long itemId)
    {
        this.itemId = itemId;
    }

    public Long getReportId()
    {
        return reportId;
    }

    public void setReportId(Long reportId)
    {
        this.reportId = reportId;
    }

    public String getItemCategory()
    {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory)
    {
        this.itemCategory = itemCategory;
    }

    public String getItemName()
    {
        return itemName;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public String getItemDimension()
    {
        return itemDimension;
    }

    public void setItemDimension(String itemDimension)
    {
        this.itemDimension = itemDimension;
    }

    public Integer getMetricCount()
    {
        return metricCount;
    }

    public void setMetricCount(Integer metricCount)
    {
        this.metricCount = metricCount;
    }

    public BigDecimal getMetricValue()
    {
        return metricValue;
    }

    public void setMetricValue(BigDecimal metricValue)
    {
        this.metricValue = metricValue;
    }

    public BigDecimal getMetricRate()
    {
        return metricRate;
    }

    public void setMetricRate(BigDecimal metricRate)
    {
        this.metricRate = metricRate;
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
