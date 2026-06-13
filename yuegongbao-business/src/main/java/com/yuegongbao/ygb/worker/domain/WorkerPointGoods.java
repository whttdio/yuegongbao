package com.yuegongbao.ygb.worker.domain;

import java.math.BigDecimal;
import com.yuegongbao.common.core.domain.BaseEntity;

public class WorkerPointGoods extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long goodsId;

    private String goodsKey;

    private String goodsName;

    private String goodsDesc;

    private BigDecimal requiredScore;

    private String goodsType;

    private String status;

    private Integer stockCount;

    private Integer sortNum;

    private String delFlag;

    public Long getGoodsId()
    {
        return goodsId;
    }

    public void setGoodsId(Long goodsId)
    {
        this.goodsId = goodsId;
    }

    public String getGoodsKey()
    {
        return goodsKey;
    }

    public void setGoodsKey(String goodsKey)
    {
        this.goodsKey = goodsKey;
    }

    public String getGoodsName()
    {
        return goodsName;
    }

    public void setGoodsName(String goodsName)
    {
        this.goodsName = goodsName;
    }

    public String getGoodsDesc()
    {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc)
    {
        this.goodsDesc = goodsDesc;
    }

    public BigDecimal getRequiredScore()
    {
        return requiredScore;
    }

    public void setRequiredScore(BigDecimal requiredScore)
    {
        this.requiredScore = requiredScore;
    }

    public String getGoodsType()
    {
        return goodsType;
    }

    public void setGoodsType(String goodsType)
    {
        this.goodsType = goodsType;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Integer getStockCount()
    {
        return stockCount;
    }

    public void setStockCount(Integer stockCount)
    {
        this.stockCount = stockCount;
    }

    public Integer getSortNum()
    {
        return sortNum;
    }

    public void setSortNum(Integer sortNum)
    {
        this.sortNum = sortNum;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }
}
